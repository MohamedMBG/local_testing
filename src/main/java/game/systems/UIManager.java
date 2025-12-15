package game.systems;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UIManager {

    private final Group node;

    private final Text scoreText;
    private final Text coinsText;
    private final HBox heartsBox;

    private int score = 0;
    private int coins = 0;
    private int lives = 3;

    public UIManager(double x, double y) {

        // Panel background
        Rectangle bg = new Rectangle(240, 78);
        bg.setArcWidth(16);
        bg.setArcHeight(16);
        bg.setFill(Color.rgb(0, 0, 0, 0.35));
        bg.setStroke(Color.rgb(255, 255, 255, 0.35));

        // Text style
        Font font = Font.font("Arial", FontWeight.EXTRA_BOLD, 18);
        DropShadow shadow = new DropShadow(3, Color.rgb(0, 0, 0, 0.7));

        scoreText = new Text("SCORE  0");
        scoreText.setFont(font);
        scoreText.setFill(Color.WHITE);
        scoreText.setEffect(shadow);
        scoreText.setStroke(Color.rgb(0, 0, 0, 0.35));
        scoreText.setStrokeWidth(0.7);

        coinsText = new Text("x  0");
        coinsText.setFont(font);
        coinsText.setFill(Color.WHITE);
        coinsText.setEffect(shadow);
        coinsText.setStroke(Color.rgb(0, 0, 0, 0.35));
        coinsText.setStrokeWidth(0.7);

        // Hearts (lives)
        heartsBox = new HBox(6);
        heartsBox.setAlignment(Pos.CENTER_LEFT);
        rebuildHearts();

        // Small coin icon next to the coins counter
        Region coinIcon = new Region();
        coinIcon.setPrefSize(18, 18);
        coinIcon.setBackground(new Background(new BackgroundFill(
                Color.web("#FFCA28"),
                new CornerRadii(9),
                Insets.EMPTY
        )));
        coinIcon.setBorder(new Border(new BorderStroke(
                Color.web("#D68600"),
                BorderStrokeStyle.SOLID,
                new CornerRadii(9),
                new BorderWidths(1.5)
        )));
        coinIcon.setEffect(new DropShadow(2, Color.rgb(0, 0, 0, 0.6)));

        HBox coinsRow = new HBox(6, coinIcon, coinsText);
        coinsRow.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(6, scoreText, coinsRow);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(18, left, heartsBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));

        StackPane panel = new StackPane(bg, row);
        panel.setLayoutX(x);
        panel.setLayoutY(y);

        node = new Group(panel);
    }

    private void rebuildHearts() {
        heartsBox.getChildren().clear();
        for (int i = 0; i < lives; i++) {
            heartsBox.getChildren().add(makeHeart());
        }
    }

    private Region makeHeart() {
        // Simple heart using a colored rounded rect (easy + clean)
        Region heart = new Region();
        heart.setPrefSize(18, 18);
        heart.setBackground(new Background(new BackgroundFill(Color.RED, new CornerRadii(6), Insets.EMPTY)));
        heart.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 255, 255, 0.25),
                BorderStrokeStyle.SOLID,
                new CornerRadii(6),
                new BorderWidths(1)
        )));
        heart.setEffect(new DropShadow(2, Color.rgb(0, 0, 0, 0.6)));
        return heart;
    }

    // --- Public API ---

    public void setScore(int score) {
        this.score = Math.max(0, score);
        scoreText.setText("SCORE  " + this.score);
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
        coinsText.setText("COINS  " + this.coins);
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
        rebuildHearts();
    }

    // If you want one call from GameWorld:
    public void setAll(int score, int coins, int lives) {
        setScore(score);
        setCoins(coins);
        setLives(lives);
    }

    public Group getNode() {
        return node;
    }
}
