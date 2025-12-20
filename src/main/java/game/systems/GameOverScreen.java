package game.systems;

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

public class GameOverScreen {

    private final Group node;
    private final Runnable onRetry;

    public GameOverScreen(double width, double height, Runnable onRetry) {
        this.onRetry = onRetry;

        Rectangle dim = new Rectangle(width, height);
        dim.setFill(Color.rgb(0, 0, 0, 0.7));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        title.setEffect(new DropShadow(8, Color.BLACK));

        Text hint = new Text("Press R or click Retry");
        hint.setFill(Color.LIGHTGRAY);
        hint.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));

        Button retryButton = new Button("Retry");
        retryButton.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        retryButton.setStyle("-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-padding: 10 26; -fx-background-radius: 8;");
        retryButton.setOnAction(e -> {
            if (onRetry != null) {
                onRetry.run();
            }
        });

        VBox content = new VBox(14, title, retryButton, hint);
        content.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(dim, content);
        root.setPrefSize(width, height);

        node = new Group(root);
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
}
