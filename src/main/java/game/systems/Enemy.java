package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.utils.Theme;

public class Enemy { // Defines a class.

    private double x, y;
    private double vx = 60;
    public static final double SIZE = 30;
    private boolean alive = true;

    public Enemy(double x, double y) { // Begins a method or constructor with its signature.
        this.x = x; // Executes: this.x = x;
        this.y = y; // Executes: this.y = y;
    } // Closes a code block.

    public boolean isAlive() { // Begins a method or constructor with its signature.
        return alive;
    } // Closes a code block.

    public double getX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public void setVelocityX(double vx) { // Begins a method or constructor with its signature.
        this.vx = vx; // Executes: this.vx = vx;
    } // Closes a code block.

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) { // Begins a method or constructor with its signature.
        if (!alive) return false; // Evaluates a conditional branch.

        return px < x + SIZE && // Returns a value from the method.
               px + pw > x && // Executes: px + pw > x &&
               py < y + SIZE && // Executes: py < y + SIZE &&
               py + ph > y; // Executes: py + ph > y;
    } // Closes a code block.

    public void update(double dt, TileMap map) { // Begins a method or constructor with its signature.
        x += vx * dt; // Executes: x += vx * dt;

        // Simple edge turn: if there is no ground slightly ahead, flip direction
        if (!map.isSolidAt(x + Math.signum(vx) * (SIZE / 2.0), y + SIZE + 2)) { // Begins a method or constructor with its signature.
            vx = -vx; // Executes: vx = -vx;
        } // Closes a code block.
    } // Closes a code block.

    public void render(GraphicsContext gc, Camera camera, Theme theme) { // Begins a method or constructor with its signature.
        if (!alive) return; // Evaluates a conditional branch.

        double screenX = x - camera.getOffsetX();
        double screenY = y - camera.getOffsetY();

        // Body
        Color fill = theme != null ? theme.getEnemyFill() : Color.web("#C62828");
        Color outline = theme != null ? theme.getEnemyOutline() : Color.web("#4E0B0B");

        gc.setFill(fill); // Executes: gc.setFill(fill);
        gc.fillRoundRect(screenX, screenY, SIZE, SIZE, 8, 8); // Executes: gc.fillRoundRect(screenX, screenY, SIZE, SIZE, 8, 8);

        // Outline
        gc.setStroke(outline); // Executes: gc.setStroke(outline);
        gc.setLineWidth(1.5); // Executes: gc.setLineWidth(1.5);
        gc.strokeRoundRect(screenX, screenY, SIZE, SIZE, 8, 8); // Executes: gc.strokeRoundRect(screenX, screenY, SIZE, SIZE, 8, 8);

        // Simple "eyes"
        gc.setFill(Color.WHITE); // Executes: gc.setFill(Color.WHITE);
        gc.fillOval(screenX + 6, screenY + 6, 6, 8); // Executes: gc.fillOval(screenX + 6, screenY + 6, 6, 8);
        gc.fillOval(screenX + SIZE - 12, screenY + 6, 6, 8); // Executes: gc.fillOval(screenX + SIZE - 12, screenY + 6, 6, 8);

        gc.setFill(Color.BLACK); // Executes: gc.setFill(Color.BLACK);
        gc.fillOval(screenX + 8, screenY + 9, 3, 4); // Executes: gc.fillOval(screenX + 8, screenY + 9, 3, 4);
        gc.fillOval(screenX + SIZE - 10, screenY + 9, 3, 4); // Executes: gc.fillOval(screenX + SIZE - 10, screenY + 9, 3, 4);
    } // Closes a code block.

    public void kill() { // Begins a method or constructor with its signature.
        alive = false; // Executes: alive = false;
    } // Closes a code block.
} // Closes a code block.
