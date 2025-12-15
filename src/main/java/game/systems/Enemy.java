package game.systems;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy {

    private double x, y;
    private double vx = 60;
    private static final double SIZE = 28;
    private boolean alive = true;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isAlive() {
        return alive;
    }

    public double getX() {
        return x;
    }

    public void setVelocityX(double vx) {
        this.vx = vx;
    }

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) {
        if (!alive) return false;

        return px < x + SIZE &&
               px + pw > x &&
               py < y + SIZE &&
               py + ph > y;
    }

    public void update(double dt, TileMap map) {
        x += vx * dt;

        // Simple edge turn: if there is no ground slightly ahead, flip direction
        if (!map.isSolidAt(x + Math.signum(vx) * (SIZE / 2.0), y + SIZE + 2)) {
            vx = -vx;
        }
    }

    public void render(GraphicsContext gc, Camera camera) {
        if (!alive) return;

        double screenX = x - camera.getOffsetX();
        double screenY = y - camera.getOffsetY();

        // Body
        gc.setFill(Color.web("#C62828")); // red shell
        gc.fillRoundRect(screenX, screenY, SIZE, SIZE, 8, 8);

        // Outline
        gc.setStroke(Color.web("#4E0B0B"));
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(screenX, screenY, SIZE, SIZE, 8, 8);

        // Simple "eyes"
        gc.setFill(Color.WHITE);
        gc.fillOval(screenX + 6, screenY + 6, 6, 8);
        gc.fillOval(screenX + SIZE - 12, screenY + 6, 6, 8);

        gc.setFill(Color.BLACK);
        gc.fillOval(screenX + 8, screenY + 9, 3, 4);
        gc.fillOval(screenX + SIZE - 10, screenY + 9, 3, 4);
    }

    public void kill() {
        alive = false;
    }
}
