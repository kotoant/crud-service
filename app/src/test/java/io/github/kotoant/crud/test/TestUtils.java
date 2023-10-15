package io.github.kotoant.crud.test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TestUtils {

    public static String loadFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveFile(Path path, String data) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, data.replaceAll("\\n", System.lineSeparator()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private TestUtils() {
    }
}

