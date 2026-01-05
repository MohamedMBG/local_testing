package game.core; // Declares the package for this source file.

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HUD { // Defines a class.

    private final Text scoreText;

    public HUD() { // Begins a method or constructor with its signature.
        scoreText = new Text(20, 30, "Score: 0"); // Executes: scoreText = new Text(20, 30, "Score: 0");
        scoreText.setFill(Color.WHITE); // Executes: scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(20)); // Executes: scoreText.setFont(Font.font(20));
    } // Closes a code block.

    public Text getScoreText() { // Begins a method or constructor with its signature.
        return scoreText;
    } // Closes a code block.

    public void setScore(int score) { // Begins a method or constructor with its signature.
        scoreText.setText("Score: " + score); // Executes: scoreText.setText("Score: " + score);
    } // Closes a code block.
} // Closes a code block.
