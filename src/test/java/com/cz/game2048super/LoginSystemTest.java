package com.cz.game2048super;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LoginSystemTest {

    private static final Path REGISTRY = Path.of("src/main/resources/assets/data/UserRegistry.txt");

    @AfterEach
    void cleanup() {
        LoginSystem.setUserRegistryPath(REGISTRY);
    }

    @Test
    void createVisitorUserDoesNotDependOnInputFields() {
        User visitor = LoginSystem.createVisitorUser();

        assertTrue(visitor.isVisitor());
        assertEquals("", visitor.getUsername());
        assertEquals("", visitor.getPassword());
    }

    @Test
    void saveUserToRegistryDoesNotPersistRawPassword() throws IOException {
        Path tempRegistry = Files.createTempFile("user-registry", ".txt");
        try {
            LoginSystem.setUserRegistryPath(tempRegistry);

            User user = new User("alice", "secret");
            LoginSystem.SaveUserToRegistry(user);

            String savedLine = Files.readString(tempRegistry, StandardCharsets.UTF_8).trim();
            assertTrue(savedLine.startsWith("alice,"));
            assertFalse(savedLine.contains("secret"));
            assertTrue(LoginSystem.CheckUser("alice", "secret"));
        } finally {
            Files.deleteIfExists(tempRegistry);
        }
    }

    @Test
    void checkUserAcceptsLegacyPlaintextEntries() throws IOException {
        Path tempRegistry = Files.createTempFile("user-registry-legacy", ".txt");
        try {
            Files.writeString(tempRegistry, "bob,legacySecret\n", StandardCharsets.UTF_8);
            LoginSystem.setUserRegistryPath(tempRegistry);

            assertTrue(LoginSystem.CheckUser("bob", "legacySecret"));
        } finally {
            Files.deleteIfExists(tempRegistry);
        }
    }
}
