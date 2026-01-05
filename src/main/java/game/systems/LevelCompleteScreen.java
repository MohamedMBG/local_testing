package game.systems; // Declares the package for this source file.

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

public class LevelCompleteScreen { // Defines a class.

    private StackPane node;
    private Runnable onNextLevelAction;

    public LevelCompleteScreen(double width, double height, Runnable onNextLevel) { // Begins a method or constructor with its signature.
        this.onNextLevelAction = onNextLevel; // Executes: this.onNextLevelAction = onNextLevel;

        node = new StackPane(); // Executes: node = new StackPane();
        node.setPrefSize(width, height); // Executes: node.setPrefSize(width, height);
        node.setVisible(false); // Hidden by default // Executes: node.setVisible(false); // Hidden by default

        // Semi-transparent background
        Rectangle bg = new Rectangle(width, height);
        bg.setFill(Color.rgb(0, 0, 0, 0.7)); // Executes: bg.setFill(Color.rgb(0, 0, 0, 0.7));

        // Layout
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER); // Executes: content.setAlignment(Pos.CENTER);

        // Title
        Label title = new Label("LEVEL COMPLETE!");
        title.setTextFill(Color.LIMEGREEN); // Executes: title.setTextFill(Color.LIMEGREEN);
        title.setFont(Font.font("Arial", 40)); // Executes: title.setFont(Font.font("Arial", 40));
        title.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);"); // Executes: title.setStyle("-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, black, 2, 1.0, 0, 0);");

        // Next Level Button
        Button nextBtn = new Button("Next Level >>");
        nextBtn.setStyle("-fx-font-size: 20px; -fx-background-color: #ffffff; -fx-text-fill: #333; -fx-padding: 10 20;"); // Executes: nextBtn.setStyle("-fx-font-size: 20px; -fx-background-color: #ffffff; -fx-text-fill: #333; -fx-padding: 10 20;");
        nextBtn.setOnAction(e -> { // Begins a method or constructor with its signature.
            hide(); // Executes: hide();
            if (onNextLevelAction != null) { // Begins a method or constructor with its signature.
                onNextLevelAction.run(); // Executes: onNextLevelAction.run();
            } // Closes a code block.
        }); // Executes: });

        content.getChildren().addAll(title, nextBtn); // Executes: content.getChildren().addAll(title, nextBtn);
        node.getChildren().addAll(bg, content); // Executes: node.getChildren().addAll(bg, content);
    } // Closes a code block.

    public StackPane getNode() { // Begins a method or constructor with its signature.
        return node;
    } // Closes a code block.

    public void show() { // Begins a method or constructor with its signature.
        node.setVisible(true); // Executes: node.setVisible(true);
        node.toFront(); // Executes: node.toFront();
    } // Closes a code block.

    public void hide() { // Begins a method or constructor with its signature.
        node.setVisible(false); // Executes: node.setVisible(false);
    } // Closes a code block.
} // Closes a code block.
