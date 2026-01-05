package game.systems; // Declares the package for this source file.

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameOverScreen { // Defines a class.

    private final Group node;
    private final Runnable onRetry;

    public GameOverScreen(double width, double height, Runnable onRetry) { // Begins a method or constructor with its signature.
        this.onRetry = onRetry; // Executes: this.onRetry = onRetry;

        Rectangle dim = new Rectangle(width, height);
        dim.setFill(Color.rgb(0, 0, 0, 0.7)); // Executes: dim.setFill(Color.rgb(0, 0, 0, 0.7));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE); // Executes: title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48)); // Executes: title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        title.setEffect(new DropShadow(8, Color.BLACK)); // Executes: title.setEffect(new DropShadow(8, Color.BLACK));

        Text hint = new Text("Press R or click Retry");
        hint.setFill(Color.LIGHTGRAY); // Executes: hint.setFill(Color.LIGHTGRAY);
        hint.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20)); // Executes: hint.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));

        Button retryButton = new Button("Retry");
        retryButton.setFont(Font.font("Arial", FontWeight.BOLD, 22)); // Executes: retryButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        retryButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 10 26; -fx-background-radius: 8;"); // Executes: retryButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 10 26; -fx-background-radius: 8;");
        retryButton.setOnAction(e -> { // Begins a method or constructor with its signature.
            if (onRetry != null) { // Begins a method or constructor with its signature.
                onRetry.run(); // Executes: onRetry.run();
            } // Closes a code block.
        }); // Executes: });

        VBox content = new VBox(14, title, retryButton, hint);
        content.setAlignment(Pos.CENTER); // Executes: content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(dim, content);
        root.setPrefSize(width, height); // Executes: root.setPrefSize(width, height);

        node = new Group(root); // Executes: node = new Group(root);
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
} // Closes a code block.
