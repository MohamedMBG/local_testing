package game.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ground {

    private final Rectangle rectangle;

    public Ground(double x, double y, double w, double h) {
        rectangle = new Rectangle(x, y, w, h);
        // Soft desert sand tone to match the new environment theme
        rectangle.setFill(Color.web("#D8C18F"));
    }

    public Rectangle getRectangle() { return rectangle; }
    public double getY() { return rectangle.getY(); }

    public void applyTheme(Color color) {
        rectangle.setFill(color);
    }
}
