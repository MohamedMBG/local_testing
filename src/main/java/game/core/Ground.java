package game.core; // Declares the package for this source file.

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Ground { // Defines a class.

    private final Rectangle rectangle;

    public Ground(double x, double y, double w, double h) { // Begins a method or constructor with its signature.
        rectangle = new Rectangle(x, y, w, h); // Executes: rectangle = new Rectangle(x, y, w, h);
        // Soft desert sand tone to match the new environment theme
        rectangle.setFill(Color.web("#D8C18F")); // Executes: rectangle.setFill(Color.web("#D8C18F"));
    } // Closes a code block.

    public Rectangle getRectangle() { return rectangle; } // Executes: public Rectangle getRectangle() { return rectangle; }
    public double getY() { return rectangle.getY(); } // Executes: public double getY() { return rectangle.getY(); }

    public void applyTheme(Color color) { // Begins a method or constructor with its signature.
        rectangle.setFill(color); // Executes: rectangle.setFill(color);
    } // Closes a code block.
} // Closes a code block.
