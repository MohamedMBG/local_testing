package game.systems;

import javafx.scene.canvas.GraphicsContext;

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

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) {
        if (!alive) return false;

        boolean hit =
                px < x + SIZE &&
                        px + pw > x &&
                        py < y + SIZE &&
                        py + ph > y;

        return hit;
    }

    public void update(double dt, TileMap map) {
        x += vx * dt;

        // Simple edge turn
        if (map.isSolidAt(x, y + SIZE + 2) == false) {
            vx = -vx;
        }
    }

    public void render(GraphicsContext gc, Camera camera) {
        if (!alive) return;

        gc.fillRect(
                x - camera.getOffsetX(),
                y - camera.getOffsetY(),
                SIZE,
                SIZE
        );
    }

    public void kill() {
        alive = false;
    }
}
