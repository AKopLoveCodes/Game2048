package com.cz.game2048super;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PackagingConfigTest {

    @Test
    void pomConfigIncludesStableWindowsUpgradeUuid() throws IOException {
        String pom = Files.readString(Path.of("pom.xml"));

        assertTrue(pom.contains("--win-upgrade-uuid"));
    }
}
