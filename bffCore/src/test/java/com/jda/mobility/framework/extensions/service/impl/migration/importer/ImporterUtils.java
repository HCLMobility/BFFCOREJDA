package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;

class ImporterUtils {

    static List<String> invoke(BasicImporter<?> importer, byte[] contents, String fileName) {
        ImportContext context = contextualInvoke(importer, contents, fileName);
        return context.getErrors().values().stream()
                .flatMap(Collection::stream).collect(Collectors.toList());
    }

    static ImportContext contextualInvoke(BasicImporter<?> importer, Resource resource) {
        return contextualInvoke(importer, read(resource), resource.getFilename());
    }

    static List<String> invoke(BasicImporter<?> importer, Resource resource) {
        return invoke(importer, read(resource), resource.getFilename());
    }

    private static ImportContext contextualInvoke(BasicImporter<?> importer, byte[] contents, String fileName) {
        ImportContext context = new ImportContext(false, "test", RENAME_NEW);
        context.run(fileName, () -> importer.doImport(contents, Path.of(fileName), context));
        return context;
    }

    static byte[] read(Resource resource) {
        try {
            return resource.getInputStream().readAllBytes();
        }
        catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
