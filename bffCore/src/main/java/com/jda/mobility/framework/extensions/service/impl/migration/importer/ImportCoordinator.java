package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.jda.mobility.framework.extensions.service.ImportService;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 * This service is responsible for importing entities (passed in
 * a zipped up file) into the database.
 * <p>
 * Depending on the type of entity being imported, this may involve
 * updating existing entities or wholesale replacement of them.
 * <p>
 * If executed as a {@code dryRun} all database changes are rolled
 * back at the end of the import regardless of whether or not the
 * import process errored.
 */
@Service
public class ImportCoordinator implements ImportService {

    private final Set<BasicImporter<?>> basicImporters;
    private final FormFlowImporter formflowImporter;
    private final TransactionTemplate transaction;

    public ImportCoordinator(Set<BasicImporter<?>> basicImporters,
                             FormFlowImporter formflowImporter,
                             TransactionTemplate transaction) {
        this.basicImporters = basicImporters;
        this.formflowImporter = formflowImporter;
        this.transaction = transaction;
    }

    @Override
    public Map<String, JsonNode> importFile(MultipartFile multipartFile,
                                            ImportContext context) {

        Map<String, JsonNode> diff = Collections.emptyMap();

        try {
            Path zipFile = Files.createTempDirectory("imports").resolve(multipartFile.getName() + ".zip");
            multipartFile.transferTo(zipFile);

            try (FileSystem fs = FileSystems.newFileSystem(zipFile, null)) {
                List<Pair<Path, FileSuffix>> imports = new ArrayList<>();
                // Loop over all the files in the zip. If they are named appropriately, include
                // them in the list of imports to process.
                try (DirectoryStream<Path> files = Files.newDirectoryStream(fs.getPath("/"))) {
                    for (Path file : files) {
                        Path name = file.getFileName();

                        // If we can create a FileSuffix from the name, we should have
                        // an importer available to handle this file.
                        FileSuffix.from(name).ifPresentOrElse(
                                suffix -> imports.add(Pair.of(file, suffix)),
                                () -> context.warn("Encountered unexpected file " + name + " in zip"));
                    }
                }

                // The values of FileSuffixes are in dependency order (when necessary)
                // We want to make sure we run the imports in the right order as well.
                // e.g. ApiRegistryImporter runs before ApiImporter.
                imports.sort(Comparator.comparing(Pair::getRight));

                diff = importFilesInTransaction(imports, context);
            }
        }
        catch (IOException e) {
            context.raise("An unexpected error occurred while trying to import files", e);
        }

        return diff;
    }

    private Map<String, JsonNode> importFilesInTransaction(List<Pair<Path, FileSuffix>> imports,
                                                           ImportContext context) {
        // We're going to manually create a transaction here to wrap the
        // import execution. It's an easy way to ensure we rollback
        // in either a dryRun or error scenario.
        return transaction.execute(status -> {
            Map<String, JsonNode> diff = importFiles(imports, context);

            if (context.isDryRun()) {
                LOGGER.info("Import was executed as a dry run. Rolling back any db changes. Encountered {} issues.", context.getErrors().size());
                status.setRollbackOnly();
            }
            else if (context.hasErrors()) {
                LOGGER.error("Encountered {} issues while importing {}. Rolling back any changes.", context.getErrors().size(), imports);
                status.setRollbackOnly();
            }

            return diff;
        });
    }

    private Map<String, JsonNode> importFiles(List<Pair<Path, FileSuffix>> imports,
                                              ImportContext context) {
        List<Pair<Path, FileSuffix>> unhandled = new ArrayList<>();

        for (Pair<Path, FileSuffix> pair : imports) {
            Path fileToImport = pair.getLeft();

            nextImport: {
                for (BasicImporter<?> importer : basicImporters) {
                    if (importer.canImport(pair.getRight())) {
                        context.run(fileToImport.toString(), () -> {
                            byte[] bytes = readFile(fileToImport, context);
                            if (bytes != null) {
                                doBasicImport(importer, bytes, fileToImport, context);
                            }
                        });
                        // We found an importer, so break out of this loop
                        // and move on to the next import. This skips the
                        // unhandled.add(pair) logic below.
                        break nextImport;
                    }
                }

                // We couldn't find a basic importer to handle this import.
                // Likely, this is a flow or form import. We'll handle those
                // below.
                unhandled.add(pair);
            }
        }

        // Make sure that only form or flow imports are left.
        unhandled.removeIf(pair -> {
            if (notFlowOrForm(pair.getRight())) {
                context.warn("Unimportable file encountered: " + pair.getLeft());
                return true;
            }
            return false;
        });

        return context.run("form-flow",
                () -> unhandled.isEmpty() ? Collections.emptyMap() : importFormFlow(unhandled, context));
    }

    private void doBasicImport(BasicImporter<?> importer, byte[] bytes, Path fileToImport, ImportContext context) {
        try {
            importer.doImport(bytes, fileToImport, context);
        }
        catch (DataAccessException e) {
            context.raise("An error occurred while importing", e);
        }
    }

    private Map<String, JsonNode> importFormFlow(List<Pair<Path, FileSuffix>> files,
                                                 ImportContext context) {
        // Split the list into 2 partitions. One for forms and one for the flow.
        Map<FileSuffix, List<Pair<Path, FileSuffix>>> partitions =
                files.stream().collect(Collectors.groupingBy(Pair::getRight));

        long errorCount = context.countErrors();

        byte[] flowBytes = getFlowBytes(partitions, context);
        List<byte[]> formBytes = getFormsBytes(partitions, context);

        if (context.countErrors() > errorCount) {
            return Collections.emptyMap();
        }

        try {
            return formflowImporter.doImport(flowBytes, formBytes, context);
        }
        catch (DataAccessException e) {
            context.raise("Unable to import flow/forms due to data issues", e);
        }

        return Collections.emptyMap();
    }

    private byte[] getFlowBytes(Map<FileSuffix, List<Pair<Path, FileSuffix>>> partitions,
                                ImportContext context) {
        List<Pair<Path, FileSuffix>> flows = partitions.get(FileSuffix.FLOW);
        int flowCount = CollectionUtils.size(flows);

        if (flowCount != 1) {
            context.raise("Expected to find one *.flow.json file, but found " + flowCount);
            return null;
        }
        return readFile(flows.get(0).getLeft(), context);
    }

    private List<byte[]> getFormsBytes(Map<FileSuffix, List<Pair<Path, FileSuffix>>> partitions,
                                       ImportContext context) {
        List<Pair<Path, FileSuffix>> forms = partitions.get(FileSuffix.FORM);
        if (CollectionUtils.isEmpty(forms)) {
            context.raise("Expected to find at least one *.form.json file, but found none.");
            return null;
        }
        return forms.stream()
                    .map(Pair::getLeft)
                    .map(path -> readFile(path, context))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    private byte[] readFile(Path file, ImportContext context) {
        try {
            return Files.readAllBytes(file);
        }
        catch (IOException e) {
            context.raise("Unable to read file " + file.toString());
            return null;
        }
    }

    private boolean notFlowOrForm(FileSuffix suffix) {
        return suffix != FileSuffix.FLOW && suffix != FileSuffix.FORM;
    }

    private static final Logger LOGGER = LogManager.getLogger();


}
