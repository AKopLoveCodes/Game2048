package com.cz.game2048super;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public final class ResourceLoader {
    private ResourceLoader() {
    }

    public static URL resourceUrl(String resourcePath) {
        String normalizedPath = normalizeResourcePath(resourcePath);
        URL resourceUrl = ResourceLoader.class.getResource(normalizedPath);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Missing classpath resource: " + normalizedPath);
        }
        return resourceUrl;
    }

    /**
     * Returns the external-form URL of a classpath CSS resource,
     * suitable for {@code Scene.getStylesheets().add(...)}.
     */
    public static String stylesheet(String resourcePath) {
        return resourceUrl(resourcePath).toExternalForm();
    }

    public static InputStream open(String resourcePath) {
        String normalizedPath = normalizeResourcePath(resourcePath);
        InputStream inputStream = ResourceLoader.class.getResourceAsStream(normalizedPath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Missing classpath resource: " + normalizedPath);
        }
        return inputStream;
    }

    private static String normalizeResourcePath(String resourcePath) {
        Objects.requireNonNull(resourcePath, "resourcePath");
        if (resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath must not be blank");
        }
        return resourcePath.startsWith("/") ? resourcePath : "/" + resourcePath;
    }
}
