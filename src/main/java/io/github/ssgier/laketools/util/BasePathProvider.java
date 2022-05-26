package io.github.ssgier.laketools.util;

import java.util.Objects;

public class BasePathProvider {
    public static String getBasePath() {
        return Objects.requireNonNull(System.getProperty("basePath"), "Pass base path via -DbasePath=...");
    }
}
