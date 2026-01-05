package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;

public class PopupText { // Defines a class.
    private String text;
    private double x, y;
    private double duration;
    private double elapsed;

    public PopupText(String text, double x, double y, double duration) { // Begins a method or constructor with its signature.
        this.text = text; // Executes: this.text = text;
        this.x = x; // Executes: this.x = x;
        this.y = y; // Executes: this.y = y;
        this.duration = duration; // Executes: this.duration = duration;
        this.elapsed = 0; // Executes: this.elapsed = 0;
    } // Closes a code block.

    public void update(double deltaTime) { // Begins a method or constructor with its signature.
        elapsed += deltaTime; // Executes: elapsed += deltaTime;
        y -= 20 * deltaTime; // slight upward movement // Executes: y -= 20 * deltaTime; // slight upward movement
    } // Closes a code block.

    public void render(GraphicsContext gc) { // Begins a method or constructor with its signature.
        gc.fillText(text, x, y); // Executes: gc.fillText(text, x, y);
    } // Closes a code block.

    public boolean isFinished() { // Begins a method or constructor with its signature.
        return elapsed >= duration; // Returns a value from the method.
    } // Closes a code block.
} // Closes a code block.
