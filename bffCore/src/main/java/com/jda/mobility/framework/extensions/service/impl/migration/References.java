package com.jda.mobility.framework.extensions.service.impl.migration;

import java.util.HashSet;
import java.util.Set;

/**
 * This class tracks identifying references to various entities
 * that are encountered during an export of a flow.
 * <p>
 * These identifiers represent dependencies that must be included
 * in an export for a flow.
 */
public class References {
    public final Set<String> globalVars = new HashSet<>();
    public final Set<String> contextVals = new HashSet<>();
    public final Set<String> resourceBundleKeys = new HashSet<>();
    public final Set<ApiKey> apis = new HashSet<>();
    public final Set<String> customComponentIds = new HashSet<>();
    public final Set<String> menuIds = new HashSet<>();
}
