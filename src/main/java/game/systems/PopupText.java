package game.systems;

import javafx.scene.canvas.GraphicsContext;

public class PopupText {
    private String text;
    private double x, y;
    private double duration;
    private double elapsed;

    public PopupText(String text, double x, double y, double duration) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.duration = duration;
        this.elapsed = 0;
    }

    public void update(double deltaTime) {
        elapsed += deltaTime;
        y -= 20 * deltaTime; // slight upward movement
    }

    public void render(GraphicsContext gc) {
        gc.fillText(text, x, y);
    }

    public boolean isFinished() {
        return elapsed >= duration;
    }
}
