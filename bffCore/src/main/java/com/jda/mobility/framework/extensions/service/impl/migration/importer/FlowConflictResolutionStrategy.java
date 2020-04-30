package com.jda.mobility.framework.extensions.service.impl.migration.importer;

public enum FlowConflictResolutionStrategy {
    /**
     * Change the name of the new flow and leave the existing one alone.
     * <p>
     * The new name of the flow will be globally unique and should no
     * longer conflict with any flow that doesn't have the same uid.
     */
    RENAME_NEW,
    /**
     * Just ignore the new flow and don't import it.
     */
    IGNORE_NEW
}
