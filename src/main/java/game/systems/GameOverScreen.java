package game.systems;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class GameOverScreen {

    private final Group node;

    public GameOverScreen(double width, double height) {
        Rectangle dim = new Rectangle(width, height);
        dim.setFill(Color.rgb(0, 0, 0, 0.7));

        Text title = new Text("GAME OVER");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 48));
        title.setEffect(new DropShadow(8, Color.BLACK));

        Text hint = new Text("Press R to restart");
        hint.setFill(Color.LIGHTGRAY);
        hint.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));

        StackPane box = new StackPane(title);
        box.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(dim, box);
        root.setPrefSize(width, height);

        node = new Group(root);
        node.setVisible(false);
    }

    public Group getNode() {
        return node;
    }

    public void show() {
        node.setVisible(true);
    }
}
