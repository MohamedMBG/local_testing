package game.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {

    private final Rectangle rectangle;

    private double x, y;
    private final double width = 40;
    private final double height = 40;

    private double vx = 0;
    private double vy = 0;

    private boolean onGround = false;

    private static final double GRAVITY = 2200;
    private static final double MOVE_SPEED = 260;
    private static final double JUMP_SPEED = -750;

    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(Color.RED);
        rectangle.setStroke(Color.DARKRED);
        rectangle.setStrokeWidth(2);
    }

    public Rectangle getRectangle() { return rectangle; }

    public double getPlayerX() { return x; }
    public double getPlayerY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }

    public double getVx() { return vx; }
    public double getVy() { return vy; }
    public boolean isOnGround() { return onGround; }

    public void setPlayerX(double x) { this.x = x; rectangle.setX(x); }
    public void setPlayerY(double y) { this.y = y; rectangle.setY(y); }
    public void setVx(double vx) { this.vx = vx; }
    public void setVy(double vy) { this.vy = vy; }
    public void setOnGround(boolean v) { this.onGround = v; }

    public void moveLeft() { vx = -MOVE_SPEED; }
    public void moveRight() { vx = MOVE_SPEED; }
    public void stopX() { vx = 0; }

    public void jump() {
        if (onGround) {
            vy = JUMP_SPEED;
            onGround = false;
        }
    }

    public void applyGravity(double dt) {
        vy += GRAVITY * dt;
    }

    public void integrate(double dt) {
        setPlayerX(x + vx * dt);
        setPlayerY(y + vy * dt);
    }

    public void constrainToBounds(double windowWidth) {
        if (x < 0) setPlayerX(0);
        if (x + width > windowWidth) setPlayerX(windowWidth - width);
    }
}
