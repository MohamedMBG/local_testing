package game.utils; // Declares the package for this source file.

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Small utility to persist and retrieve the highest score locally.
 */
public class HighScoreDatabase { // Defines a class.

    private final Path dbFile;

    public HighScoreDatabase() { // Begins a method or constructor with its signature.
        Path directory = Path.of(System.getProperty("user.home"), ".bb-mario");
        dbFile = directory.resolve("highscore.db"); // Executes: dbFile = directory.resolve("highscore.db");
        try { // Attempts operations that might throw exceptions.
            Files.createDirectories(directory); // Executes: Files.createDirectories(directory);
            if (!Files.exists(dbFile)) { // Begins a method or constructor with its signature.
                Files.createFile(dbFile); // Executes: Files.createFile(dbFile);
                Files.writeString(dbFile, "0", StandardCharsets.UTF_8); // Executes: Files.writeString(dbFile, "0", StandardCharsets.UTF_8);
            } // Closes a code block.
        } catch (IOException ignored) { // Begins a method or constructor with its signature.
            // If we can't create the file, we'll fallback to in-memory only.
        } // Closes a code block.
    } // Closes a code block.

    public int loadHighScore() { // Begins a method or constructor with its signature.
        try { // Attempts operations that might throw exceptions.
            if (Files.exists(dbFile)) { // Begins a method or constructor with its signature.
                String content = Files.readString(dbFile, StandardCharsets.UTF_8).trim();
                return Integer.parseInt(content); // Returns a value from the method.
            } // Closes a code block.
        } catch (IOException | NumberFormatException ignored) { // Begins a method or constructor with its signature.
            // Ignore and return default
        } // Closes a code block.
        return 0; // Returns a value from the method.
    } // Closes a code block.

    public void saveHighScore(int score) { // Begins a method or constructor with its signature.
        try { // Attempts operations that might throw exceptions.
            Files.writeString(dbFile, Integer.toString(score), StandardCharsets.UTF_8); // Executes: Files.writeString(dbFile, Integer.toString(score), StandardCharsets.UTF_8);
        } catch (IOException ignored) { // Begins a method or constructor with its signature.
            // Failing to persist should not crash the game
        } // Closes a code block.
    } // Closes a code block.
} // Closes a code block.
