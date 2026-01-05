package game.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small utility to persist and retrieve the highest score locally.
 */
public class HighScoreDatabase {

    private final Path dbFile;

    public HighScoreDatabase() {
        Path directory = Path.of(System.getProperty("user.home"), ".bb-mario");
        dbFile = directory.resolve("highscore.db");
        try {
            Files.createDirectories(directory);
            if (!Files.exists(dbFile)) {
                Files.createFile(dbFile);
                Files.writeString(dbFile, "0", StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {
            // If we can't create the file, we'll fallback to in-memory only.
        }
    }

    public int loadHighScore() {
        try {
            if (Files.exists(dbFile)) {
                String content = Files.readString(dbFile, StandardCharsets.UTF_8).trim();
                return Integer.parseInt(content);
            }
        } catch (IOException | NumberFormatException ignored) {
            // Ignore and return default
        }
        return 0;
    }

    public void saveHighScore(int score) {
        try {
            Files.writeString(dbFile, Integer.toString(score), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
            // Failing to persist should not crash the game
        }
    }
}
