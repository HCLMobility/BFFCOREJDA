package com.jda.mobility.framework.extensions.service.impl.migration;

import java.nio.file.Path;
import java.util.Optional;

/**
 * Defines the file suffixes used by the export and import logic.
 * <p>
 * Files that do not have one of the following suffixes will not
 * be considered an export or import file.
 * <p>
 * The order these suffixes are defined in is important. When importing,
 * files with suffixes higher in the list must be imported before files
 * with suffixes lower in the list.
 */
public enum FileSuffix {
    API_REGISTRY(".registries.json"),
    API(".apis.json"),
    RESOURCE_BUNDLE(".resource-bundles.json"),
    APP_CONFIG(".app-configs.json"),
    GLOBAL_VAR(".global-vars.json"),
    CONTEXT_VAL(".context-vals.json"),
    CUSTOM_COMPONENT(".custom-components.json"),
    FLOW(".flow.json"),
    FORM(".form.json"),
    MENU(".menus.json");

    private final String suffix;

    FileSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String suffix() {
        return suffix;
    }

    /**
     * Prepends the name to the suffix
     * <p>
     * Useful for creating file names.
     * @param name The name of a file
     * @return a file name ending with the suffix.
     */
    public String prepend(String name) {
        return name + suffix;
    }

    public static Optional<FileSuffix> from(String fileName) {
        for (FileSuffix value : values()) {
            if (fileName.endsWith(value.suffix)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    public static Optional<FileSuffix> from(Path path) {
        return from(path.getFileName().toString());
    }


}
