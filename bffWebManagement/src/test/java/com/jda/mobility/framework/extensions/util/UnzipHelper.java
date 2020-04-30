package com.jda.mobility.framework.extensions.util;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class UnzipHelper {

    public static Map<String, JsonNode> unzip(byte[] bytes) throws IOException {
        Path zipFile = Files.createTempDirectory("unzip").resolve("test-data.zip");
        Files.write(zipFile, bytes);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, JsonNode> nodes = new HashMap<>();

        try (FileSystem fs = FileSystems.newFileSystem(zipFile, null)) {
            try (DirectoryStream<Path> files = Files.newDirectoryStream(fs.getPath("/"))) {
                for (Path file : files) {
                    Path name = file.getFileName();
                    nodes.put(name.toString(), mapper.readTree(Files.readAllBytes(file)));
                }
            }
        }
        return nodes;
    }
}
