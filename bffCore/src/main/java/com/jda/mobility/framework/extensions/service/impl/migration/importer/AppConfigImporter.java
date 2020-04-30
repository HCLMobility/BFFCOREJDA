package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.AppConfigDetail;
import com.jda.mobility.framework.extensions.entity.AppConfigMaster;
import com.jda.mobility.framework.extensions.repository.AppConfigMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import static com.jda.mobility.framework.extensions.service.impl.migration.exporter.AppConfigExporter.isApplicationConfig;
import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.DEFAULT_FLOW_KEY;
import static com.jda.mobility.framework.extensions.utils.BffAdminConstantsUtils.HOME_FLOW_KEY;

/**
 * Imports {@link AppConfigMaster app config} entities.
 * <p>
 * This importer can handle configs found in {@code *.app-configs.json},
 * {@code *.global-vars.json} and {@code *.context-vals.json} files.
 * <p>
 * AppConfig master (and {@link AppConfigDetail detail}) {@code UUID}s
 * are not included in import files and are not used to find existing
 * configs as the {@code UUID}s may vary across systems.
 * <p>
 * The data model supports multiple detail records tied to a single
 * master, but at most <em>one</em> of those detail records should
 * be exported and imported. Detail records may be associated with
 * sessions to store global or context values for that session. Those
 * details should never be included in an import. This importer
 * enforces that either no detail is included in the import or only
 * a single detail is present for each config.
 */
@Service
public class AppConfigImporter implements BasicImporter<AppConfigMaster> {

    private final AppConfigMasterRepository repo;
    private final ObjectMapper mapper;

    public AppConfigImporter(AppConfigMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }


    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.GLOBAL_VAR
                || suffix == FileSuffix.CONTEXT_VAL
                || suffix == FileSuffix.APP_CONFIG;
    }

    @Override
    public TypeReference<List<AppConfigMaster>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        deserialize(contents, context)
                .forEach(config -> {
                    String key = config.getConfigName() + "/" + config.getConfigType();
                    context.run(key, () -> saveConfig(context, config));
                });
    }

    private void saveConfig(ImportContext context, AppConfigMaster config) {
        List<AppConfigDetail> importedDetails = config.getAppConfigDetails();
        String configName = config.getConfigName();
        if (CollectionUtils.size(importedDetails) > 1) {
            context.raise("Multiple details found for config " + configName);
            return;
        }

        AppConfigMaster dbConfig =
                repo.findByConfigNameAndConfigType(configName, config.getConfigType());

        AppConfigDetail importedDetail =
                CollectionUtils.isNotEmpty(importedDetails) ? importedDetails.get(0) : null;

        if (dbConfig == null) {
            // The config is new. Save it and we're done.
            repo.save(config);
            recordHomeAndDefaultFlowChanges(configName, importedDetail, null, context);
        }
        else if (importedDetail != null) {
            // The config is not new and we have a single detail that
            // defines the config's value. This list cannot have more than
            // one item in it. See check at the top of the method.
            AppConfigDetail dbDetail = getDbApplicationDetail(dbConfig);
            String oldValue = null;
            if (dbDetail != null) {
                oldValue = dbDetail.getConfigValue();
                // There is already a detail record in the db. So we
                // can just update its value.
                dbDetail.setConfigValue(importedDetail.getConfigValue());
            }
            else {
                // There is no detail record in the db, so we need to
                // add the new detail to the existing master record.
                dbConfig.getAppConfigDetails().add(dbDetail);
            }
            recordHomeAndDefaultFlowChanges(configName, importedDetail, oldValue, context);
            repo.save(dbConfig);
        }
    }

    private void recordHomeAndDefaultFlowChanges(String configName,
                                                 AppConfigDetail importedDetail,
                                                 String oldValue,
                                                 ImportContext context) {


        if (DEFAULT_FLOW_KEY.equalsIgnoreCase(configName) || HOME_FLOW_KEY.equalsIgnoreCase(configName)) {
            String newValue = importedDetail == null ? null : importedDetail.getConfigValue();
            context.note(String.format(DEFAULT_OR_HOME_FLOW_UPDATE_NOTE, configName, newValue, oldValue));
        }
    }

    private static final TypeReference<List<AppConfigMaster>> TYPE_REFERENCE = new TypeReference<>() {};

    private AppConfigDetail getDbApplicationDetail(AppConfigMaster config) {
        List<AppConfigDetail> details = config.getAppConfigDetails();
        if (CollectionUtils.isNotEmpty(details) && isApplicationConfig(config)) {
            return details.get(0);
        }
        return null;
    }

    static final String DEFAULT_OR_HOME_FLOW_UPDATE_NOTE =
            "Updated the application %s to %s from %s. This will cause problems if the new flow does not exist.";

}
