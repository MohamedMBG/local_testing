package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.utils.Theme;

public class Spike { // Defines a class.

    private double x, y;
    public static final double SIZE = 32; // match tile size so spike is clearly visible // Executes: public static final double SIZE = 32; // match tile size so spike is clearly visible
    private boolean active = true;

    public Spike(double x, double y) { // Begins a method or constructor with its signature.
        this.x = x; // Executes: this.x = x;
        this.y = y; // Executes: this.y = y;
    } // Closes a code block.

    public boolean isActive() { // Begins a method or constructor with its signature.
        return active;
    } // Closes a code block.

    public double getX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public double getY() { // Begins a method or constructor with its signature.
        return y;
    } // Closes a code block.

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) { // Begins a method or constructor with its signature.
        if (!active) return false; // Evaluates a conditional branch.

        return px < x + SIZE && // Returns a value from the method.
               px + pw > x && // Executes: px + pw > x &&
               py < y + SIZE && // Executes: py < y + SIZE &&
               py + ph > y; // Executes: py + ph > y;
    } // Closes a code block.

    public void render(GraphicsContext gc, Camera camera, Theme theme) { // Begins a method or constructor with its signature.
        if (!active) return; // Evaluates a conditional branch.

        double screenX = x - camera.getOffsetX();
        double screenY = y - camera.getOffsetY();

        // Draw a small dark base rectangle so the triangular spike stands out
        Color base = theme != null ? theme.getSpikeBase() : Color.web("#3E2723");
        Color fill = theme != null ? theme.getSpikeFill() : Color.web("#D32F2F");
        Color outline = theme != null ? theme.getSpikeOutline() : Color.web("#5D0E0E");

        gc.setFill(base); // Executes: gc.setFill(base);
        gc.fillRect(screenX + 4, screenY + SIZE - 6, SIZE - 8, 6); // Executes: gc.fillRect(screenX + 4, screenY + SIZE - 6, SIZE - 8, 6);

        // Draw spike as a triangle pointing upward
        double[] xPoints = { // Executes: double[] xPoints = {
            screenX + SIZE / 2.0,  // tip // Executes: screenX + SIZE / 2.0,  // tip
            screenX + 2,           // left // Executes: screenX + 2,           // left
            screenX + SIZE - 2     // right // Executes: screenX + SIZE - 2     // right
        }; // Executes: };
        double[] yPoints = { // Executes: double[] yPoints = {
            screenY + 4,                // tip a bit down from top // Executes: screenY + 4,                // tip a bit down from top
            screenY + SIZE - 6,         // bottom left aligned with base // Executes: screenY + SIZE - 6,         // bottom left aligned with base
            screenY + SIZE - 6          // bottom right // Executes: screenY + SIZE - 6          // bottom right
        }; // Executes: };

        gc.setFill(fill); // visible red // Executes: gc.setFill(fill); // visible red
        gc.fillPolygon(xPoints, yPoints, 3); // Executes: gc.fillPolygon(xPoints, yPoints, 3);

        // Outline
        gc.setStroke(outline); // Executes: gc.setStroke(outline);
        gc.setLineWidth(1.5); // Executes: gc.setLineWidth(1.5);
        gc.strokePolygon(xPoints, yPoints, 3); // Executes: gc.strokePolygon(xPoints, yPoints, 3);
    } // Closes a code block.

    public void deactivate() { // Begins a method or constructor with its signature.
        active = false; // Executes: active = false;
    } // Closes a code block.
} // Closes a code block.
