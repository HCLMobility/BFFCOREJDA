package com.jda.mobility.framework.extensions.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public final class ZipHelper {

    public static void zipToStream(Map<String, JsonNode> contents, ObjectMapper mapper, OutputStream out)
            throws IOException {
        ObjectWriter writer = mapper
                .writer(new DefaultPrettyPrinter()
                        .withRootSeparator("")
                        .withoutSpacesInObjectEntries());

        try (BufferedOutputStream buffer = new BufferedOutputStream(out);
             ZipOutputStream zip = new ZipOutputStream(buffer)) {
            for (Entry<String, JsonNode> entry : contents.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                writer.writeValue(zip, entry.getValue());
            }
        }
    }
}
