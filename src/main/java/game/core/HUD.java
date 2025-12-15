package game.core;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class HUD {

    private final Text scoreText;

    public HUD() {
        scoreText = new Text(20, 30, "Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(20));
    }

    public Text getScoreText() {
        return scoreText;
    }

    public void setScore(int score) {
        scoreText.setText("Score: " + score);
    }
}
