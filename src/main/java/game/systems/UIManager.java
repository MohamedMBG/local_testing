package game.systems; // Declares the package for this source file.

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

public class UIManager { // Defines a class.

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

    public UIManager(double x, double y, Theme theme) { // Begins a method or constructor with its signature.

        this.theme = theme; // Executes: this.theme = theme;

        // Panel background
        panelBackground = new Rectangle(380, 124); // Executes: panelBackground = new Rectangle(380, 124);
        panelBackground.setArcWidth(20); // Executes: panelBackground.setArcWidth(20);
        panelBackground.setArcHeight(20); // Executes: panelBackground.setArcHeight(20);

        // Text style
        Font font = Font.font("Inter", FontWeight.EXTRA_BOLD, 18);
        shadow = new DropShadow(6, Color.rgb(0, 0, 0, 0.55)); // Executes: shadow = new DropShadow(6, Color.rgb(0, 0, 0, 0.55));

        scoreText = new Text("SCORE  0"); // Executes: scoreText = new Text("SCORE  0");
        scoreText.setFont(font); // Executes: scoreText.setFont(font);
        scoreText.setEffect(shadow); // Executes: scoreText.setEffect(shadow);

        coinsText = new Text("COINS  0"); // Executes: coinsText = new Text("COINS  0");
        coinsText.setFont(font); // Executes: coinsText.setFont(font);
        coinsText.setEffect(shadow); // Executes: coinsText.setEffect(shadow);

        bestText = new Text("BEST  0"); // Executes: bestText = new Text("BEST  0");
        bestText.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 16)); // Executes: bestText.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 16));
        bestText.setEffect(shadow); // Executes: bestText.setEffect(shadow);

        themeText = new Text("Theme: " + theme.getDisplayName()); // Executes: themeText = new Text("Theme: " + theme.getDisplayName());
        themeText.setFont(Font.font("Inter", FontWeight.BOLD, 14)); // Executes: themeText.setFont(Font.font("Inter", FontWeight.BOLD, 14));
        themeText.setEffect(shadow); // Executes: themeText.setEffect(shadow);

        // Hearts (lives)
        heartsBox = new HBox(6); // Executes: heartsBox = new HBox(6);
        heartsBox.setAlignment(Pos.CENTER_LEFT); // Executes: heartsBox.setAlignment(Pos.CENTER_LEFT);

        // Small coin icon next to the coins counter
        coinIcon = new Region(); // Executes: coinIcon = new Region();
        coinIcon.setPrefSize(18, 18); // Executes: coinIcon.setPrefSize(18, 18);

        HBox coinsRow = new HBox(8, coinIcon, coinsText);
        coinsRow.setAlignment(Pos.CENTER_LEFT); // Executes: coinsRow.setAlignment(Pos.CENTER_LEFT);

        VBox left = new VBox(6, scoreText, coinsRow, bestText, themeText);
        left.setAlignment(Pos.CENTER_LEFT); // Executes: left.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(18, left, heartsBox);
        row.setAlignment(Pos.CENTER_LEFT); // Executes: row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 14, 12, 14)); // Executes: row.setPadding(new Insets(12, 14, 12, 14));

        StackPane panel = new StackPane(panelBackground, row);
        panel.setLayoutX(x); // Executes: panel.setLayoutX(x);
        panel.setLayoutY(y); // Executes: panel.setLayoutY(y);
        panel.setBorder(new Border(new BorderStroke( // Executes: panel.setBorder(new Border(new BorderStroke(
                Color.rgb(255, 255, 255, 0.25), // Executes: Color.rgb(255, 255, 255, 0.25),
                BorderStrokeStyle.SOLID, // Executes: BorderStrokeStyle.SOLID,
                new CornerRadii(20), // Executes: new CornerRadii(20),
                new BorderWidths(1.5) // Executes: new BorderWidths(1.5)
        ))); // Executes: )));

        node = new Group(panel); // Executes: node = new Group(panel);

        applyTheme(theme); // Executes: applyTheme(theme);
        rebuildHearts(); // Executes: rebuildHearts();
    } // Closes a code block.

    private void rebuildHearts() { // Begins a method or constructor with its signature.
        heartsBox.getChildren().clear(); // Executes: heartsBox.getChildren().clear();
        for (int i = 0; i < lives; i++) { // Begins a method or constructor with its signature.
            heartsBox.getChildren().add(makeHeart()); // Executes: heartsBox.getChildren().add(makeHeart());
        } // Closes a code block.
    } // Closes a code block.

    private Region makeHeart() { // Begins a method or constructor with its signature.
        Region heart = new Region();
        heart.setPrefSize(18, 18); // Executes: heart.setPrefSize(18, 18);
        Color heartColor = theme.getCoinMid().deriveColor(0, 1, 0.95, 1);
        heart.setBackground(new Background(new BackgroundFill(heartColor, new CornerRadii(7), Insets.EMPTY))); // Executes: heart.setBackground(new Background(new BackgroundFill(heartColor, new CornerRadii(7), Insets.EMPTY)));
        heart.setBorder(new Border(new BorderStroke( // Executes: heart.setBorder(new Border(new BorderStroke(
                theme.getCoinOutline().deriveColor(0, 1, 1, 0.5), // Executes: theme.getCoinOutline().deriveColor(0, 1, 1, 0.5),
                BorderStrokeStyle.SOLID, // Executes: BorderStrokeStyle.SOLID,
                new CornerRadii(7), // Executes: new CornerRadii(7),
                new BorderWidths(1.2) // Executes: new BorderWidths(1.2)
        ))); // Executes: )));
        heart.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.6))); // Executes: heart.setEffect(new DropShadow(3, Color.rgb(0, 0, 0, 0.6)));
        return heart;
    } // Closes a code block.

    // --- Public API ---

    public void setScore(int score) { // Begins a method or constructor with its signature.
        this.score = Math.max(0, score); // Executes: this.score = Math.max(0, score);
        scoreText.setText("SCORE  " + this.score); // Executes: scoreText.setText("SCORE  " + this.score);
    } // Closes a code block.

    public void setCoins(int coins) { // Begins a method or constructor with its signature.
        this.coins = Math.max(0, coins); // Executes: this.coins = Math.max(0, coins);
        coinsText.setText("COINS  " + this.coins); // Executes: coinsText.setText("COINS  " + this.coins);
    } // Closes a code block.

    public void setLives(int lives) { // Begins a method or constructor with its signature.
        this.lives = Math.max(0, lives); // Executes: this.lives = Math.max(0, lives);
        rebuildHearts(); // Executes: rebuildHearts();
    } // Closes a code block.

    public void setBestScore(int bestScore) { // Begins a method or constructor with its signature.
        this.bestScore = Math.max(0, bestScore); // Executes: this.bestScore = Math.max(0, bestScore);
        bestText.setText("BEST  " + this.bestScore); // Executes: bestText.setText("BEST  " + this.bestScore);
    } // Closes a code block.

    public void setThemeName(String theme) { // Begins a method or constructor with its signature.
        themeText.setText("Theme: " + theme); // Executes: themeText.setText("Theme: " + theme);
    } // Closes a code block.

    // If you want one call from GameWorld:
    public void setAll(int score, int coins, int lives) { // Begins a method or constructor with its signature.
        setScore(score); // Executes: setScore(score);
        setCoins(coins); // Executes: setCoins(coins);
        setLives(lives); // Executes: setLives(lives);
        setBestScore(bestScore); // Executes: setBestScore(bestScore);
        setThemeName(theme.getDisplayName()); // Executes: setThemeName(theme.getDisplayName());
    } // Closes a code block.

    public Group getNode() { // Begins a method or constructor with its signature.
        return node;
    } // Closes a code block.

    public void setTheme(Theme theme) { // Begins a method or constructor with its signature.
        this.theme = theme; // Executes: this.theme = theme;
        applyTheme(theme); // Executes: applyTheme(theme);
        rebuildHearts(); // Executes: rebuildHearts();
    } // Closes a code block.

    private void applyTheme(Theme theme) { // Begins a method or constructor with its signature.
        LinearGradient glass = new LinearGradient( // Executes: LinearGradient glass = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE, // Executes: 0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, theme.getBackground().deriveColor(0, 1, 1, 0.78)), // Executes: new Stop(0, theme.getBackground().deriveColor(0, 1, 1, 0.78)),
                new Stop(1, theme.getBackgroundBottom().deriveColor(0, 1, 1, 0.82)) // Executes: new Stop(1, theme.getBackgroundBottom().deriveColor(0, 1, 1, 0.82))
        ); // Executes: );
        panelBackground.setFill(glass); // Executes: panelBackground.setFill(glass);
        panelBackground.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.32)); // Executes: panelBackground.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.32));
        panelBackground.setStrokeWidth(1.8); // Executes: panelBackground.setStrokeWidth(1.8);

        Color brightText = theme.getTileHighlight().desaturate().deriveColor(0, 1, 1.1, 1);
        Color labelText = theme.getTileBase().deriveColor(0, 1, 1.2, 0.9);
        Color softGlow = theme.getBackgroundBottom().interpolate(Color.WHITE, 0.55);

        scoreText.setFill(brightText); // Executes: scoreText.setFill(brightText);
        scoreText.setStroke(Color.rgb(0, 0, 0, 0.35)); // Executes: scoreText.setStroke(Color.rgb(0, 0, 0, 0.35));

        coinsText.setFill(brightText); // Executes: coinsText.setFill(brightText);
        coinsText.setStroke(Color.rgb(0, 0, 0, 0.35)); // Executes: coinsText.setStroke(Color.rgb(0, 0, 0, 0.35));

        bestText.setFill(softGlow); // Executes: bestText.setFill(softGlow);
        bestText.setStroke(Color.rgb(0, 0, 0, 0.35)); // Executes: bestText.setStroke(Color.rgb(0, 0, 0, 0.35));

        themeText.setFill(labelText); // Executes: themeText.setFill(labelText);
        themeText.setStroke(Color.rgb(0, 0, 0, 0.25)); // Executes: themeText.setStroke(Color.rgb(0, 0, 0, 0.25));

        coinIcon.setBackground(new Background(new BackgroundFill( // Executes: coinIcon.setBackground(new Background(new BackgroundFill(
                new LinearGradient( // Executes: new LinearGradient(
                        0, 0, 0, 1, true, CycleMethod.NO_CYCLE, // Executes: 0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                        new Stop(0, theme.getCoinLight()), // Executes: new Stop(0, theme.getCoinLight()),
                        new Stop(1, theme.getCoinShadow()) // Executes: new Stop(1, theme.getCoinShadow())
                ), // Executes: ),
                new CornerRadii(9), // Executes: new CornerRadii(9),
                Insets.EMPTY // Executes: Insets.EMPTY
        ))); // Executes: )));
        coinIcon.setBorder(new Border(new BorderStroke( // Executes: coinIcon.setBorder(new Border(new BorderStroke(
                theme.getCoinOutline(), // Executes: theme.getCoinOutline(),
                BorderStrokeStyle.SOLID, // Executes: BorderStrokeStyle.SOLID,
                new CornerRadii(9), // Executes: new CornerRadii(9),
                new BorderWidths(1.5) // Executes: new BorderWidths(1.5)
        ))); // Executes: )));
        coinIcon.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.7))); // Executes: coinIcon.setEffect(new DropShadow(4, Color.rgb(0, 0, 0, 0.7)));
    } // Closes a code block.
} // Closes a code block.
