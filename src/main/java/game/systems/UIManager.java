package game.systems;

import game.utils.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class UIManager {

    private final Group node;

    private final Text scoreText;
    private final Text coinsText;
    private final Text bestText;
    private final Text themeText;
    private final HBox heartsBox;
    private final Rectangle panelBackground;
    private final Region coinIcon;
    private final DropShadow shadow;

    private int score = 0;
    private int coins = 0;
    private int lives = 3;
    private int bestScore = 0;
    private Theme theme;

    public UIManager(double x, double y, Theme theme) {

        this.theme = theme;

        // Panel background
        panelBackground = new Rectangle(380, 124);
        panelBackground.setArcWidth(20);
        panelBackground.setArcHeight(20);

        // Text style
        Font font = Font.font("Inter", FontWeight.EXTRA_BOLD, 18);
        shadow = new DropShadow(6, Color.rgb(0, 0, 0, 0.55));

        scoreText = new Text("SCORE  0");
        scoreText.setFont(font);
        scoreText.setEffect(shadow);

        coinsText = new Text("COINS  0");
        coinsText.setFont(font);
        coinsText.setEffect(shadow);

        bestText = new Text("BEST  0");
        bestText.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 16));
        bestText.setEffect(shadow);

        themeText = new Text("Theme: " + theme.getDisplayName());
        themeText.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        themeText.setEffect(shadow);

        // Hearts (lives)
        heartsBox = new HBox(6);
        heartsBox.setAlignment(Pos.CENTER_LEFT);

        // Small coin icon next to the coins counter
        coinIcon = new Region();
        coinIcon.setPrefSize(18, 18);

        HBox coinsRow = new HBox(8, coinIcon, coinsText);
        coinsRow.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(6, scoreText, coinsRow, bestText, themeText);
        left.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(18, left, heartsBox);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14));

        StackPane panel = new StackPane(panelBackground, row);
        panel.setLayoutX(x);
        panel.setLayoutY(y);
        panel.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 255, 255, 0.25),
                BorderStrokeStyle.SOLID,
                new CornerRadii(20),
                new BorderWidths(1.5)
        )));

        node = new Group(panel);

        applyTheme(theme);
        rebuildHearts();
    }

    private void rebuildHearts() {
        heartsBox.getChildren().clear();
        for (int i = 0; i < lives; i++) {
            heartsBox.getChildren().add(makeHeart());
        }
    }

    private Region makeHeart() {
        Region heart = new Region();
        heart.setPrefSize(18, 18);
        Color heartColor = theme.getCoinMid().deriveColor(0, 1, 0.95, 1);
        heart.setBackground(new Background(new BackgroundFill(heartColor, new CornerRadii(7), Insets.EMPTY)));
        heart.setBorder(new Border(new BorderStroke(
                theme.getCoinOutline().deriveColor(0, 1, 1, 0.5),
                BorderStrokeStyle.SOLID,
                new CornerRadii(7),
                new BorderWidths(1.2)
        )));
        heart.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.6)));
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

    public void setBestScore(int bestScore) {
        this.bestScore = Math.max(0, bestScore);
        bestText.setText("BEST  " + this.bestScore);
    }

    public void setThemeName(String theme) {
        themeText.setText("Theme: " + theme);
    }

    // If you want one call from GameWorld:
    public void setAll(int score, int coins, int lives) {
        setScore(score);
        setCoins(coins);
        setLives(lives);
        setBestScore(bestScore);
        setThemeName(theme.getDisplayName());
    }

    public Group getNode() {
        return node;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        applyTheme(theme);
        rebuildHearts();
    }

    private void applyTheme(Theme theme) {
        LinearGradient glass = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, theme.getBackground().deriveColor(0, 1, 1, 0.78)),
                new Stop(1, theme.getBackgroundBottom().deriveColor(0, 1, 1, 0.82))
        );
        panelBackground.setFill(glass);
        panelBackground.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.32));
        panelBackground.setStrokeWidth(1.8);

        Color brightText = theme.getTileHighlight().desaturate().deriveColor(0, 1, 1.1, 1);
        Color labelText = theme.getTileBase().deriveColor(0, 1, 1.2, 0.9);
        Color softGlow = theme.getBackgroundBottom().interpolate(Color.WHITE, 0.55);

        scoreText.setFill(brightText);
        scoreText.setStroke(Color.rgb(0, 0, 0, 0.35));

        coinsText.setFill(brightText);
        coinsText.setStroke(Color.rgb(0, 0, 0, 0.35));

        bestText.setFill(softGlow);
        bestText.setStroke(Color.rgb(0, 0, 0, 0.35));

        themeText.setFill(labelText);
        themeText.setStroke(Color.rgb(0, 0, 0, 0.25));

        coinIcon.setBackground(new Background(new BackgroundFill(
                new LinearGradient(
                        0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, theme.getCoinLight()),
                        new Stop(1, theme.getCoinShadow())
                ),
                new CornerRadii(9),
                Insets.EMPTY
        )));
        coinIcon.setBorder(new Border(new BorderStroke(
                theme.getCoinOutline(),
                BorderStrokeStyle.SOLID,
                new CornerRadii(9),
                new BorderWidths(1.5)
        )));
        coinIcon.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.7)));
    }
}
