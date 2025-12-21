package game.systems;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.utils.Theme;

public class Spike {

    private double x, y;
    public static final double SIZE = 32; // match tile size so spike is clearly visible
    private boolean active = true;

    public Spike(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActive() {
        return active;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) {
        if (!active) return false;

        return px < x + SIZE &&
               px + pw > x &&
               py < y + SIZE &&
               py + ph > y;
    }

    public void render(GraphicsContext gc, Camera camera, Theme theme) {
        if (!active) return;

        double screenX = x - camera.getOffsetX();
        double screenY = y - camera.getOffsetY();

        // Draw a small dark base rectangle so the triangular spike stands out
        Color base = theme != null ? theme.getSpikeBase() : Color.web("#3E2723");
        Color fill = theme != null ? theme.getSpikeFill() : Color.web("#D32F2F");
        Color outline = theme != null ? theme.getSpikeOutline() : Color.web("#5D0E0E");

        gc.setFill(base);
        gc.fillRect(screenX + 4, screenY + SIZE - 6, SIZE - 8, 6);

        // Draw spike as a triangle pointing upward
        double[] xPoints = {
            screenX + SIZE / 2.0,  // tip
            screenX + 2,           // left
            screenX + SIZE - 2     // right
        };
        double[] yPoints = {
            screenY + 4,                // tip a bit down from top
            screenY + SIZE - 6,         // bottom left aligned with base
            screenY + SIZE - 6          // bottom right
        };

        gc.setFill(fill); // visible red
        gc.fillPolygon(xPoints, yPoints, 3);

        // Outline
        gc.setStroke(outline);
        gc.setLineWidth(1.5);
        gc.strokePolygon(xPoints, yPoints, 3);
    }

    public void deactivate() {
        active = false;
    }
}
