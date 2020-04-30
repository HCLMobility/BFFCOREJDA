package com.jda.mobility.framework.extensions.service.impl.migration.importer;


import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Contains the information about a particular import request.
 * <p>
 * The context is passed down through the various importer
 * classes and updated when appropriate to indicate error
 * states, warnings or just details the person requesting
 * the import might find useful.
 */
@Getter
@RequiredArgsConstructor
@Log4j2
public class ImportContext {

    private final Map<String, List<String>> errors = new LinkedHashMap<>();
    private final Map<String, List<String>> warnings = new LinkedHashMap<>();
    private final Map<String, List<String>> notes = new LinkedHashMap<>();
    private final boolean dryRun;
    private final String name;
    private final FlowConflictResolutionStrategy flowConflictResolutionStrategy;

    @Getter(AccessLevel.NONE)
    private final LinkedList<String> stack = new LinkedList<>(List.of("$"));

    @Getter(AccessLevel.NONE)
    private String currentKey = stack.peek();

    private void push(String context) {
        stack.addLast(context);
        currentKey = String.join(".", stack);
    }

    private void pop() {
        stack.removeLast();
        currentKey = String.join(".", stack);
    }

    /**
     * Executes the specified function in the named context.
     * <p>
     * Named contexts may be nested to an arbitrarily deep level.
     * When an error, warning, or note is added to the context
     * it is tied to the fully qualified context name path.
     * @param contextName identifies the local context the
     * block of code should run in.
     * @param fn some logic to run within the context.
     * @param <T> The type of data that will be returned
     * from the run method. This is the type returned by the
     * specified function.
     * @return The value returned by the function passed to
     * this method.
     */
    <T> T run(String contextName, Supplier<T> fn) {
        try {
            push(contextName);
            return fn.get();
        }
        finally {
            pop();
        }
    }

    /**
     * Executes the specified function in the named context.
     * <p>
     * Named contexts may be nested to an arbitrarily deep level.
     * When an error, warning, or note is added to the context
     * it is tied to the fully qualified context name path.
     * @param contextName identifies the local context the
     * block of code should run in.
     * @param runnable some logic to run within the context.
     */
    void run(String contextName, Runnable runnable) {
        run(contextName, () -> {
            runnable.run();
            return null;
        });
    }

    /**
     * Adds the error message to the context and logs
     * it.
     * <p>
     * Note: This does not throw an exception or abort
     * the current logic. If you need to eject, you
     * have to do that yourself.
     * @param error The error to save/log.
     * @param throwable An exception to include with the log
     */
    public void raise(String error, Throwable throwable) {
        errors.computeIfAbsent(currentKey, k -> new ArrayList<>()).add(error);
        log.error(LOG_TEMPLATE, name, currentKey, error, throwable);
    }

    /**
     * Adds the error message to the context and logs
     * it.
     * <p>
     * Note: This does not throw an exception or abort
     * the current logic. If you need to eject, you
     * have to do that yourself.
     * @param error The error to save/log.
     */
    public void raise(String error) {
        errors.computeIfAbsent(currentKey, k -> new ArrayList<>()).add(error);
        log.error(LOG_TEMPLATE, name, currentKey, error);
    }

    /**
     * Adds a warning to the context and logs
     * @param warning The warning message to add
     */
    public void warn(String warning) {
        warnings.computeIfAbsent(currentKey, k -> new ArrayList<>()).add(warning);
        log.warn(LOG_TEMPLATE, name, currentKey, warning);
    }

    /**
     * Adds a note to the context and logs
     * @param note The note message to add
     */
    public void note(String note) {
        notes.computeIfAbsent(currentKey, k -> new ArrayList<>()).add(note);
        log.debug(LOG_TEMPLATE, name, currentKey, note);
    }

    /**
     * Counts the number of errors currently found in the context
     * (including all nested contexts).
     */
    public long countErrors() {
        return errors.values().stream()
                .mapToLong(Collection::size)
                .sum();
    }

    /**
     * Indicates if the context contains any errors.
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private static final String LOG_TEMPLATE = "{} -> {}: {}";
}
