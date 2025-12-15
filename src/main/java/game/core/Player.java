package game.core;

import game.systems.Camera;
import game.systems.PowerUpType;
import javafx.scene.canvas.GraphicsContext;

public class Player {

    private double x;
    private double y;
    private double width;
    private double height;

    public Player(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // Called every frame by GameWorld
    public void update(double dt) {
        // Person A will implement movement/physics here later
    }

    // Called every frame by GameWorld
    public void render(GraphicsContext gc, Camera camera) {
        double screenX = x - camera.getX();
        double screenY = y - camera.getY();
        gc.fillRect(screenX, screenY, width, height);
    }

    // Power-up hook (safe stub for now)
    public void applyPowerUp(PowerUpType type) {
        // Person A can implement effects later
    }

    // --- Bounds for collisions ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    // Optional setters (useful later)
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
