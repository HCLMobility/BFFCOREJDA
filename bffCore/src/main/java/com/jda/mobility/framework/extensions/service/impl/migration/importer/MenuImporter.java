package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.MenuMaster;
import com.jda.mobility.framework.extensions.repository.MenuMasterRepository;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import org.springframework.stereotype.Service;

/**
 * Imports {@link MenuMaster menu} entities.
 * <p>
 * <strong>IMPORTANT NOTE:</strong>Importing any menus linked to
 * a form will cause all the menus linked to that form to be deleted
 * first before the new menus are imported. This is to ensure that
 * if an existing form has a menu item removed and is then re-imported,
 * that menu item is also removed in the importing system.
 */
@Service
public class MenuImporter implements BasicImporter<MenuMaster> {

    private final MenuMasterRepository repo;
    private final ObjectMapper mapper;

    public MenuImporter(MenuMasterRepository repo, ObjectMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public boolean canImport(FileSuffix suffix) {
        return suffix == FileSuffix.MENU;
    }

    @Override
    public TypeReference<List<MenuMaster>> deserializationTarget() {
        return TYPE_REFERENCE;
    }

    @Override
    public ObjectMapper objectMapper() {
        return mapper;
    }

    @Override
    public void doImport(byte[] contents, Path fileToImport, ImportContext context) {
        List<MenuMaster> menus = deserialize(contents, context);
        // First we need to remove all menus associated with the forms
        // for menus that we are importing. The import is considered
        // the final say for what should be in a form. So if menus
        // exist in this system for the same form, but don't exist
        // in the import, they must be purged now.
        menus.stream()
                .map(MenuMaster::getLinkedFormId)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(uuid -> removeMenusForForm(uuid, context));

        menus.forEach(repo::save);
    }

    private void removeMenusForForm(UUID formUid, ImportContext context) {
        // This is not as efficient as deleting the menus in a batch
        // but there are issues with permission deletions not cascading
        // properly when repo.deleteInBatch is used.
        repo.findByLinkedFormId(formUid).forEach(
                menu -> {
                    context.note("Removing existing menu with id " +
                            menu.getUid() + " tied to form with id " + formUid);
                    repo.deleteById(menu.getUid());
                });
    }

    private static final TypeReference<List<MenuMaster>> TYPE_REFERENCE = new TypeReference<>() {};
}
