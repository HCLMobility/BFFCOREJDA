package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.IGNORE_NEW;
import static com.jda.mobility.framework.extensions.service.impl.migration.importer.FlowConflictResolutionStrategy.RENAME_NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ImportContextTest {

    @Test
    void raiseErrors() {
        var context = new ImportContext(true, "import.zip", RENAME_NEW);

        assertFalse(context.hasErrors(), "Context should have no errors");

        context.raise("this is an error");

        assertTrue(context.hasErrors(), "Context should have errors");
        assertEquals(1, context.countErrors());

        context.raise("and another error");

        assertTrue(context.hasErrors(), "Context should have errors");
        assertEquals(2, context.countErrors());

        context.raise("once more with an exception", new RuntimeException());

        assertTrue(context.hasErrors(), "Context should have errors");
        assertEquals(3, context.countErrors());

        assertEquals(
                Map.of("$", List.of(
                        "this is an error",
                        "and another error",
                        "once more with an exception"
                )),
                context.getErrors()
        );
    }

    @Test
    void warn() {
        var context = new ImportContext(false, "another.zip", RENAME_NEW);
        context.warn("watch out!");
        context.warn("danger ahead!");

        assertEquals(
                Map.of("$", List.of(
                        "watch out!",
                        "danger ahead!"
                )),
                context.getWarnings()
        );
    }

    @Test
    void note() {
        var context = new ImportContext(true, "notes.zip", IGNORE_NEW);
        context.note("interesting fact");
        context.note("point of order");
        context.note("did you know?");

        assertEquals(
                Map.of("$", List.of(
                        "interesting fact",
                        "point of order",
                        "did you know?"
                )),
                context.getNotes()
        );
    }

    @Test
    void runInContextWithReturn() {
        var context = new ImportContext(false, "run-in-context-returns.zip", RENAME_NEW);

        var value = context.run("first", () -> {
            context.warn("I hope you know what you're doing");
            return context.run("second", () -> {
                context.note("note well!");
                context.raise("error!");
                return context.run("third", () -> {
                    context.warn("warning!");
                    return "value!";
                });
            });
        });

        assertEquals("value!", value);

        assertEquals(
                Map.of(
                        "$.first", List.of("I hope you know what you're doing"),
                        "$.first.second.third", List.of("warning!")
                ),
                context.getWarnings()
        );

        assertEquals(
                Map.of("$.first.second", List.of("error!")),
                context.getErrors()
        );

        assertEquals(
                Map.of("$.first.second", List.of("note well!")),
                context.getNotes()
        );
    }

    @Test
    void runInContextVoidReturn() {
        var context = new ImportContext(true, "run-in-context-void.zip", RENAME_NEW);

        context.run("hi", () -> {
            context.note("note");
            context.warn("warn");
            context.raise("error");

            context.run("there", () -> context.raise("bad"));
        });

        assertEquals(
                Map.of("$.hi", List.of("warn")),
                context.getWarnings()
        );

        assertEquals(
                Map.of(
                        "$.hi", List.of("error"),
                        "$.hi.there", List.of("bad")
                ),
                context.getErrors()
        );

        assertEquals(
                Map.of("$.hi", List.of("note")),
                context.getNotes()
        );
    }

}