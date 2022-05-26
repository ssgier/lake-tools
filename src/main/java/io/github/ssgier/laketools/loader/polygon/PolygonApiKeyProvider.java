package io.github.ssgier.laketools.loader.polygon;

import java.util.Objects;

public class PolygonApiKeyProvider {
    public static String getApiKey() {
        return Objects.requireNonNull(System.getProperty("apiKey"), "Pass API key via -DapiKey=...");
    }
}
