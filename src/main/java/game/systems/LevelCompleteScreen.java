package game.systems;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class LevelCompleteScreen {

    private StackPane node;
    private Runnable onNextLevelAction;

    public LevelCompleteScreen(double width, double height, Runnable onNextLevel) {
        this.onNextLevelAction = onNextLevel;

        node = new StackPane();
        node.setPrefSize(width, height);
        node.setVisible(false); // Hidden by default

        // Semi-transparent background
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(0, 0, 0, 0.7));

        // Layout
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("LEVEL COMPLETE!");
        title.setTextFill(Color.LIMEGREEN);
        title.setFont(Font.font("Arial", 40));
        title.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");

        // Next Level Button
        Button nextBtn = new Button("Next Level >>");
        nextBtn.setStyle("-fx-font-size: 20px; -fx-background-color: #ffffff; -fx-text-fill: #333; -fx-padding: 10 20;");
        nextBtn.setOnAction(e -> {
            hide();
            if (onNextLevelAction != null) {
                onNextLevelAction.run();
            }
        });

        content.getChildren().addAll(title, nextBtn);
        node.getChildren().addAll(bg, content);
    }

    public StackPane getNode() {
        return node;
    }

    public void show() {
        node.setVisible(true);
        node.toFront();
    }

    public void hide() {
        node.setVisible(false);
    }
}