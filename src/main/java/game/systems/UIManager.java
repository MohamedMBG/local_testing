package game.systems;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class UIManager {

    private final Text scoreText;
    private final Text livesText;
    private final Group root;

    public UIManager(double x, double y) {

        scoreText = new Text(x, y, "Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setFont(Font.font(18));

        livesText = new Text(x, y + 25, "Lives: 3");
        livesText.setFill(Color.WHITE);
        livesText.setFont(Font.font(18));

        root = new Group(scoreText, livesText);
    }

    public void set(int score, int lives) {
        scoreText.setText("Score: " + score);
        livesText.setText("Lives: " + lives);
    }

    public Group getNode() {
        return root;
    }
}
