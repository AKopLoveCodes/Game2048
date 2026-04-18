package com.cz.game2048super;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public final class AppPaths {
    public static final String DATA_DIRECTORY_PROPERTY = "game2048super.dataDir";
    private static final String DEFAULT_DATA_DIRECTORY_NAME = ".game2048super";
    private static final String WINDOWS_DATA_DIRECTORY_NAME = "Game2048super";
    private static final String GAME_DATA_FILE_NAME = "GameData.txt";

    private AppPaths() {
    }

    public static Path applicationDataDirectory() {
        String overrideDirectory = System.getProperty(DATA_DIRECTORY_PROPERTY);
        String osName = System.getProperty("os.name");
        String userHome = System.getProperty("user.home");
        return resolveApplicationDataDirectory(overrideDirectory, osName, System.getenv(), userHome);
    }

    public static Path applicationDataFile(String fileName) {
        Objects.requireNonNull(fileName, "fileName");
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("fileName must not be blank");
        }
        return applicationDataDirectory().resolve(fileName);
    }

    public static Path gameDataFile() {
        return applicationDataFile(GAME_DATA_FILE_NAME);
    }

    public static Path ensureApplicationDataDirectory() throws IOException {
        Path directory = applicationDataDirectory();
        Files.createDirectories(directory);
        return directory;
    }

    public static Path ensureGameDataFile() throws IOException {
        Path file = gameDataFile();
        Path parent = file.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        if (Files.notExists(file)) {
            Files.createFile(file);
        }
        return file;
    }

    static Path resolveApplicationDataDirectory(
            String overrideDirectory,
            String osName,
            java.util.Map<String, String> environment,
            String userHome
    ) {
        Path resolvedDirectory;
        if (overrideDirectory != null && !overrideDirectory.isBlank()) {
            resolvedDirectory = Paths.get(overrideDirectory);
        } else if (osName != null && osName.toLowerCase().contains("win")) {
            String appData = environment.get("APPDATA");
            if (appData != null && !appData.isBlank()) {
                resolvedDirectory = Paths.get(appData, WINDOWS_DATA_DIRECTORY_NAME);
            } else {
                resolvedDirectory = resolveUserHomeFallback(userHome);
            }
        } else {
            resolvedDirectory = resolveUserHomeFallback(userHome);
        }
        return resolvedDirectory.toAbsolutePath().normalize();
    }

    private static Path resolveUserHomeFallback(String userHome) {
        if (userHome == null || userHome.isBlank()) {
            throw new IllegalStateException("user.home is not configured");
        }
        return Paths.get(userHome, DEFAULT_DATA_DIRECTORY_NAME);
    }
}
