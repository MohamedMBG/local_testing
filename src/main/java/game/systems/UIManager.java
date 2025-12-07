package game.systems;

import javafx.scene.canvas.GraphicsContext;

public class UIManager {
    private double x, y;

    public UIManager(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void render(GraphicsContext gc, int coinCount){
        // Rendering logic for UI elements
        gc.fillText("Coins: " + coinCount, x, y);

    }
}
