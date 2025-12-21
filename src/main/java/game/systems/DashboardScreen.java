package game.systems;

import game.utils.Theme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.function.Consumer;

public class DashboardScreen {

    private final Group node;
    private final Text highScoreText;
    private final ToggleGroup themeToggleGroup;
    private Theme selectedTheme = Theme.SUMMER;

    public DashboardScreen(double width,
                           double height,
                           Consumer<Theme> onThemeChanged,
                           Runnable onStart) {
        Rectangle dim = new Rectangle(width, height);
        dim.setFill(Color.rgb(0, 0, 0, 0.55));

        Text title = new Text("Runner Dashboard");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 42));
        title.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.8)));

        highScoreText = new Text("Best Score: 0");
        highScoreText.setFill(Color.WHITE);
        highScoreText.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        themeToggleGroup = new ToggleGroup();
        FlowPane themesRow = new FlowPane();
        themesRow.setHgap(12);
        themesRow.setVgap(10);
        themesRow.setAlignment(Pos.CENTER);

        for (Theme theme : Theme.values()) {
            ToggleButton btn = makeThemeButton(theme);
            btn.setToggleGroup(themeToggleGroup);
            if (theme == selectedTheme) {
                btn.setSelected(true);
            }
            btn.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    selectedTheme = theme;
                    if (onThemeChanged != null) {
                        onThemeChanged.accept(theme);
                    }
                }
            });
            themesRow.getChildren().add(btn);
        }

        Button startButton = new Button("Start / Resume");
        startButton.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
        startButton.setStyle("-fx-background-color: linear-gradient(to right, #4CAF50, #81C784); -fx-text-fill: white; -fx-padding: 12 24; -fx-background-radius: 10;");
        startButton.setOnAction(e -> {
            if (onStart != null) {
                onStart.run();
            }
        });

        VBox info = new VBox(14, title, highScoreText, themesRow, startButton);
        info.setAlignment(Pos.CENTER);
        info.setPadding(new Insets(20));

        BorderPane content = new BorderPane();
        content.setCenter(info);
        content.setPadding(new Insets(20));

        StackPane container = new StackPane(dim, content);
        container.setPrefSize(width, height);

        node = new Group(container);
        node.setVisible(false);
    }

    public Group getNode() {
        return node;
    }

    public void show() {
        node.setVisible(true);
        node.toFront();
    }

    public void hide() {
        node.setVisible(false);
    }

    public Theme getSelectedTheme() {
        return selectedTheme;
    }

    public void setHighScore(int value) {
        highScoreText.setText("Best Score: " + value);
    }

    private ToggleButton makeThemeButton(Theme theme) {
        ToggleButton button = new ToggleButton(theme.getDisplayName());
        button.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        button.setTextFill(Color.BLACK);
        button.setGraphic(makeThemePreview(theme));
        button.setContentDisplay(javafx.scene.control.ContentDisplay.TOP);
        button.setStyle("-fx-background-radius: 10; -fx-padding: 10 16; -fx-background-color: white; -fx-border-color: rgba(0,0,0,0.2); -fx-border-radius: 10;");
        return button;
    }

    private HBox makeThemePreview(Theme theme) {
        Rectangle ground = new Rectangle(54, 12, theme.getGround());
        Rectangle base = new Rectangle(54, 18, theme.getTileBase());
        base.setArcWidth(8);
        base.setArcHeight(8);

        Rectangle highlight = new Rectangle(54, 6, theme.getTileHighlight());
        highlight.setArcWidth(8);
        highlight.setArcHeight(8);
        highlight.setOpacity(0.9);

        StackPane tile = new StackPane(base, highlight);
        tile.setPadding(new Insets(4, 8, 4, 8));
        tile.setStyle("-fx-background-color: " + theme.toCss() + "; -fx-background-radius: 10;");

        HBox preview = new HBox(6, tile, ground);
        preview.setAlignment(Pos.CENTER);
        return preview;
    }
}
