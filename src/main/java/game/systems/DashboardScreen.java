package game.systems; // Declares the package for this source file.

import game.utils.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DashboardScreen { // Defines a class.

    private final Group node;
    private final Text highScoreText;
    private final ToggleGroup themeToggleGroup;
    private final Map<ToggleButton, Theme> toggleThemes = new HashMap<>();
    private final Consumer<Theme> onThemeChanged;
    private final Runnable onStart;
    private Theme selectedTheme = Theme.NEUTRAL;

    public DashboardScreen(double width, // Executes: public DashboardScreen(double width,
                           double height, // Executes: double height,
                           Consumer<Theme> onThemeChanged, // Executes: Consumer<Theme> onThemeChanged,
                           Runnable onStart) { // Executes: Runnable onStart) {
        this.onThemeChanged = onThemeChanged; // Executes: this.onThemeChanged = onThemeChanged;
        this.onStart = onStart; // Executes: this.onStart = onStart;

        Rectangle dim = new Rectangle(width, height);
        dim.setFill(new LinearGradient( // Executes: dim.setFill(new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE, // Executes: 0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.rgb(10, 12, 26, 0.92)), // Executes: new Stop(0, Color.rgb(10, 12, 26, 0.92)),
                new Stop(1, Color.rgb(14, 35, 58, 0.92)) // Executes: new Stop(1, Color.rgb(14, 35, 58, 0.92))
        )); // Executes: ));

        Rectangle panel = new Rectangle(width - 160, height - 140);
        panel.setArcWidth(28); // Executes: panel.setArcWidth(28);
        panel.setArcHeight(28); // Executes: panel.setArcHeight(28);
        panel.setFill(Color.rgb(255, 255, 255, 0.07)); // Executes: panel.setFill(Color.rgb(255, 255, 255, 0.07));
        panel.setStroke(Color.rgb(255, 255, 255, 0.14)); // Executes: panel.setStroke(Color.rgb(255, 255, 255, 0.14));
        panel.setStrokeWidth(1.5); // Executes: panel.setStrokeWidth(1.5);
        panel.setEffect(new DropShadow(24, Color.rgb(0, 0, 0, 0.45))); // Executes: panel.setEffect(new DropShadow(24, Color.rgb(0, 0, 0, 0.45)));

        Text title = new Text("Runner Dashboard");
        title.setFill(Color.WHITE); // Executes: title.setFill(Color.WHITE);
        title.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 42)); // Executes: title.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 42));
        title.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.65))); // Executes: title.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.65)));

        Text subtitle = new Text("Pick your vibe, then sprint through the level with tuned movement.");
        subtitle.setFill(Color.rgb(220, 229, 245)); // Executes: subtitle.setFill(Color.rgb(220, 229, 245));
        subtitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 18)); // Executes: subtitle.setFont(Font.font("Inter", FontWeight.MEDIUM, 18));

        highScoreText = new Text("Best Score: 0"); // Executes: highScoreText = new Text("Best Score: 0");
        highScoreText.setFill(Color.rgb(255, 243, 194)); // Executes: highScoreText.setFill(Color.rgb(255, 243, 194));
        highScoreText.setFont(Font.font("Inter", FontWeight.BOLD, 20)); // Executes: highScoreText.setFont(Font.font("Inter", FontWeight.BOLD, 20));
        highScoreText.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.5))); // Executes: highScoreText.setEffect(new DropShadow(6, Color.rgb(0, 0, 0, 0.5)));

        themeToggleGroup = new ToggleGroup(); // Executes: themeToggleGroup = new ToggleGroup();
        FlowPane themesRow = new FlowPane();
        themesRow.setHgap(14); // Executes: themesRow.setHgap(14);
        themesRow.setVgap(12); // Executes: themesRow.setVgap(12);
        themesRow.setAlignment(Pos.CENTER); // Executes: themesRow.setAlignment(Pos.CENTER);
        themesRow.setPadding(new Insets(12, 6, 0, 6)); // Executes: themesRow.setPadding(new Insets(12, 6, 0, 6));

        for (Theme theme : Theme.values()) { // Begins a method or constructor with its signature.
            ToggleButton btn = makeThemeButton(theme);
            btn.setToggleGroup(themeToggleGroup); // Executes: btn.setToggleGroup(themeToggleGroup);
            toggleThemes.put(btn, theme); // Executes: toggleThemes.put(btn, theme);
            if (theme == selectedTheme) { // Begins a method or constructor with its signature.
                btn.setSelected(true); // Executes: btn.setSelected(true);
            } // Closes a code block.
            btn.selectedProperty().addListener((obs, oldVal, newVal) -> { // Begins a method or constructor with its signature.
                if (newVal) { // Begins a method or constructor with its signature.
                    selectedTheme = theme; // Executes: selectedTheme = theme;
                    refreshToggleStyles(); // Executes: refreshToggleStyles();
                    if (this.onThemeChanged != null) { // Begins a method or constructor with its signature.
                        this.onThemeChanged.accept(theme); // Executes: this.onThemeChanged.accept(theme);
                    } // Closes a code block.
                } // Closes a code block.
            }); // Executes: });
            themesRow.getChildren().add(btn); // Executes: themesRow.getChildren().add(btn);
        } // Closes a code block.
        refreshToggleStyles(); // Executes: refreshToggleStyles();

        Button startButton = new Button("Start Run");
        startButton.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 22)); // Executes: startButton.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 22));
        startButton.setTextFill(Color.WHITE); // Executes: startButton.setTextFill(Color.WHITE);
        startButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6a3d, #ff9a62); -fx-background-radius: 16; -fx-padding: 14 28; -fx-border-radius: 16; -fx-border-color: rgba(255,255,255,0.25); -fx-border-width: 1;"); // Executes: startButton.setStyle("-fx-background-color: linear-gradient(to right, #ff6a3d, #ff9a62); -fx-background-radius: 16; -fx-padding: 14 28; -fx-border-radius: 16; -fx-border-color: rgba(255,255,255,0.25); -fx-border-width: 1;");
        startButton.setEffect(new DropShadow(16, Color.rgb(0, 0, 0, 0.55))); // Executes: startButton.setEffect(new DropShadow(16, Color.rgb(0, 0, 0, 0.55)));
        startButton.setOnAction(e -> { // Begins a method or constructor with its signature.
            if (this.onStart != null) { // Begins a method or constructor with its signature.
                this.onStart.run(); // Executes: this.onStart.run();
            } // Closes a code block.
        }); // Executes: });

        HBox heroRow = new HBox(18, title, highScoreText);
        heroRow.setAlignment(Pos.CENTER); // Executes: heroRow.setAlignment(Pos.CENTER);

        VBox content = new VBox(16, // Executes: VBox content = new VBox(16,
                heroRow, // Executes: heroRow,
                subtitle, // Executes: subtitle,
                buildStatLine(), // Executes: buildStatLine(),
                themesRow, // Executes: themesRow,
                startButton); // Executes: startButton);
        content.setAlignment(Pos.CENTER); // Executes: content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(22)); // Executes: content.setPadding(new Insets(22));

        StackPane glass = new StackPane(panel, content);
        glass.setAlignment(Pos.CENTER); // Executes: glass.setAlignment(Pos.CENTER);

        StackPane container = new StackPane(dim, glass);
        container.setPrefSize(width, height); // Executes: container.setPrefSize(width, height);

        node = new Group(container); // Executes: node = new Group(container);
        node.setVisible(false); // Executes: node.setVisible(false);
    } // Closes a code block.

    public Group getNode() { // Begins a method or constructor with its signature.
        return node;
    } // Closes a code block.

    public void show() { // Begins a method or constructor with its signature.
        node.setVisible(true); // Executes: node.setVisible(true);
        node.toFront(); // Executes: node.toFront();
    } // Closes a code block.

    public void hide() { // Begins a method or constructor with its signature.
        node.setVisible(false); // Executes: node.setVisible(false);
    } // Closes a code block.

    public Theme getSelectedTheme() { // Begins a method or constructor with its signature.
        return selectedTheme;
    } // Closes a code block.

    public void setHighScore(int value) { // Begins a method or constructor with its signature.
        highScoreText.setText("Best Score: " + value); // Executes: highScoreText.setText("Best Score: " + value);
    } // Closes a code block.

    private ToggleButton makeThemeButton(Theme theme) { // Begins a method or constructor with its signature.
        ToggleButton button = new ToggleButton(theme.getDisplayName());
        button.setFont(Font.font("Inter", FontWeight.BOLD, 16)); // Executes: button.setFont(Font.font("Inter", FontWeight.BOLD, 16));
        button.setTextFill(Color.WHITE); // Executes: button.setTextFill(Color.WHITE);
        button.setGraphic(makeThemePreview(theme)); // Executes: button.setGraphic(makeThemePreview(theme));
        button.setContentDisplay(javafx.scene.control.ContentDisplay.TOP); // Executes: button.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        button.setPadding(new Insets(12, 16, 10, 16)); // Executes: button.setPadding(new Insets(12, 16, 10, 16));
        return button;
    } // Closes a code block.

    private void refreshToggleStyles() { // Begins a method or constructor with its signature.
        for (Map.Entry<ToggleButton, Theme> entry : toggleThemes.entrySet()) { // Begins a method or constructor with its signature.
            ToggleButton btn = entry.getKey();
            Theme theme = entry.getValue();
            boolean selected = btn.isSelected() && theme == selectedTheme;
            btn.setStyle(buildThemeButtonStyle(theme, selected)); // Executes: btn.setStyle(buildThemeButtonStyle(theme, selected));
        } // Closes a code block.
    } // Closes a code block.

    private String buildThemeButtonStyle(Theme theme, boolean selected) { // Begins a method or constructor with its signature.
        String accent = toRgba(theme.getTileAccent(), selected ? 1.0 : 0.75);
        String base = toRgba(theme.getTileBase(), selected ? 0.95 : 0.65);
        String highlight = toRgba(theme.getTileHighlight(), 1.0);
        double scale = selected ? 1.02 : 1.0;
        return String.join(" ", // Returns a value from the method.
                "-fx-background-radius: 14;", // Executes: "-fx-background-radius: 14;",
                "-fx-border-radius: 14;", // Executes: "-fx-border-radius: 14;",
                "-fx-background-color: linear-gradient(to bottom, " + highlight + ", " + base + ");", // Executes: "-fx-background-color: linear-gradient(to bottom, " + highlight + ", " + base + ");",
                "-fx-border-color: " + accent + ";", // Executes: "-fx-border-color: " + accent + ";",
                "-fx-border-width: " + (selected ? "2" : "1") + ";", // Executes: "-fx-border-width: " + (selected ? "2" : "1") + ";",
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 3);", // Executes: "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 12, 0, 0, 3);",
                "-fx-scale-x: " + scale + ";", // Executes: "-fx-scale-x: " + scale + ";",
                "-fx-scale-y: " + scale + ";" // Executes: "-fx-scale-y: " + scale + ";"
        ); // Executes: );
    } // Closes a code block.

    private VBox makeThemePreview(Theme theme) { // Begins a method or constructor with its signature.
        Rectangle ground = new Rectangle(74, 14, theme.getGround());
        ground.setArcWidth(10); // Executes: ground.setArcWidth(10);
        ground.setArcHeight(10); // Executes: ground.setArcHeight(10);

        Rectangle base = new Rectangle(74, 26, theme.getTileBase());
        base.setArcWidth(12); // Executes: base.setArcWidth(12);
        base.setArcHeight(12); // Executes: base.setArcHeight(12);

        Rectangle highlight = new Rectangle(74, 10, theme.getTileHighlight());
        highlight.setArcWidth(12); // Executes: highlight.setArcWidth(12);
        highlight.setArcHeight(12); // Executes: highlight.setArcHeight(12);
        highlight.setOpacity(0.9); // Executes: highlight.setOpacity(0.9);

        Rectangle accent = new Rectangle(22, 8, theme.getTileAccent());
        accent.setArcWidth(8); // Executes: accent.setArcWidth(8);
        accent.setArcHeight(8); // Executes: accent.setArcHeight(8);

        StackPane tile = new StackPane(base, highlight, accent);
        tile.setPadding(new Insets(6, 6, 0, 6)); // Executes: tile.setPadding(new Insets(6, 6, 0, 6));

        Text speed = new Text(String.format("Speed x%.2f", theme.getMoveScale()));
        speed.setFill(Color.rgb(240, 246, 255)); // Executes: speed.setFill(Color.rgb(240, 246, 255));
        speed.setFont(Font.font("Inter", FontWeight.BOLD, 12)); // Executes: speed.setFont(Font.font("Inter", FontWeight.BOLD, 12));

        Text jump = new Text(String.format("Jump x%.2f", theme.getJumpScale()));
        jump.setFill(Color.rgb(214, 226, 243)); // Executes: jump.setFill(Color.rgb(214, 226, 243));
        jump.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 12)); // Executes: jump.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 12));

        VBox preview = new VBox(6, tile, ground, speed, jump);
        preview.setAlignment(Pos.CENTER); // Executes: preview.setAlignment(Pos.CENTER);
        return preview;
    } // Closes a code block.

    private HBox buildStatLine() { // Begins a method or constructor with its signature.
        Text label = new Text("Themes tweak movement & palette — test them before running.");
        label.setFill(Color.rgb(226, 235, 250)); // Executes: label.setFill(Color.rgb(226, 235, 250));
        label.setFont(Font.font("Inter", FontWeight.MEDIUM, 14)); // Executes: label.setFont(Font.font("Inter", FontWeight.MEDIUM, 14));
        HBox row = new HBox(label);
        row.setAlignment(Pos.CENTER); // Executes: row.setAlignment(Pos.CENTER);
        return row;
    } // Closes a code block.

    private String toRgba(Color color, double alpha) { // Begins a method or constructor with its signature.
        return String.format("rgba(%d,%d,%d,%.3f)", // Returns a value from the method.
                (int) (color.getRed() * 255), // Executes: (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255), // Executes: (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255), // Executes: (int) (color.getBlue() * 255),
                alpha); // Executes: alpha);
    } // Closes a code block.
} // Closes a code block.
