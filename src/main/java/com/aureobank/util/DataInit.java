package com.aureobank.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class DataInit {
    private DataInit() {}

    public static void ensureDataFolders() {
        try {
            Files.createDirectories(Path.of("data"));
            Files.createDirectories(Path.of("logs"));
        } catch (IOException e) {
            System.err.println("Failed to create data/logs directories: " + e.getMessage());
        }
    }
}