package com.cz.game2048super;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameDataTest {
    private String originalDataDirProperty;
    private String originalUserHome;

    @BeforeEach
    void saveSystemProperties() {
        originalDataDirProperty = System.getProperty(AppPaths.DATA_DIRECTORY_PROPERTY);
        originalUserHome = System.getProperty("user.home");
    }

    @AfterEach
    void restoreSystemProperties() {
        restoreProperty(AppPaths.DATA_DIRECTORY_PROPERTY, originalDataDirProperty);
        restoreProperty("user.home", originalUserHome);
    }

    @Test
    void applicationDataDirectoryUsesSystemPropertyOverride() throws IOException {
        Path overrideDirectory = Files.createTempDirectory("game2048-data-dir");
        System.setProperty(AppPaths.DATA_DIRECTORY_PROPERTY, overrideDirectory.toString());

        assertEquals(overrideDirectory.toAbsolutePath().normalize(), AppPaths.applicationDataDirectory());
    }

    @Test
    void nonWindowsDefaultsToUserHomeSubdirectory() throws IOException {
        Path fakeHome = Files.createTempDirectory("game2048-home");

        assertEquals(
                fakeHome.resolve(".game2048super").toAbsolutePath().normalize(),
                AppPaths.resolveApplicationDataDirectory(null, "Linux", Map.of(), fakeHome.toString())
        );
    }

    @Test
    void windowsUsesRoamingAppDataDirectoryWhenAvailable() {
        Path expected = Path.of("C:/Users/test/AppData/Roaming/Game2048super").toAbsolutePath().normalize();

        assertEquals(
                expected,
                AppPaths.resolveApplicationDataDirectory(
                        null,
                        "Windows 11",
                        Map.of("APPDATA", "C:/Users/test/AppData/Roaming"),
                        "C:/Users/test"
                )
        );
    }

    @Test
    void gameDataPersistsUsingInjectedDataFile() throws Exception {
        Path dataDirectory = Files.createTempDirectory("game2048-data-file");
        Path dataFile = dataDirectory.resolve("GameData.txt");
        GameData gameData = new GameData("alice", 1, dataFile);
        int[][] grids = {
                {1, 2, 3, 4},
                {5, 6, 7, 8},
                {9, 10, 11, 12},
                {13, 14, 15, 16}
        };

        gameData.updateGameData(123, 45, grids);
        gameData.setIfHaveWon(true);
        gameData.saveGameData();

        GameData reloaded = new GameData("alice", 1, dataFile);
        assertTrue(reloaded.ifFoundUserData());
        reloaded.loadGameData();

        assertEquals(123, reloaded.getScoreLast());
        assertEquals(123, reloaded.getScoreBest());
        assertEquals(45, reloaded.getTimerLast());
        assertTrue(Arrays.deepEquals(grids, reloaded.getGridsLast()));
        assertTrue(reloaded.getIfHaveWon());
    }

    @Test
    void resourceLoaderResolvesClasspathResource() {
        assertNotNull(ResourceLoader.resourceUrl("/assets.icon/Icon.png"));
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }
}
