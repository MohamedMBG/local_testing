package game.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {

    // -------------------------
    // Visual
    // -------------------------
    private final Rectangle rectangle;

    // -------------------------
    // Position & size
    // -------------------------
    private double x;
    private double y;
    private final double width = 36;   // slightly narrower
    private final double height = 36;  // slightly shorter to "lower" collisions

    // -------------------------
    // Physics
    // -------------------------
    private double velocityX = 0;
    private double velocityY = 0;

    private boolean onGround = false;

    // -------------------------
    // Tunable constants (feel)
    // -------------------------
    private static final double MOVE_SPEED = 280;     // faster
    private static final double GRAVITY = 1100;       // a bit lighter fall
    private static final double JUMP_FORCE = -620;    // higher jump

    // -------------------------
    // Constructor
    // -------------------------
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        rectangle = new Rectangle(width, height);
        rectangle.setFill(Color.RED);
        rectangle.setStroke(Color.DARKRED);
        rectangle.setStrokeWidth(2);

        syncRect();
    }

    // -------------------------
    // Update (called every frame)
    // -------------------------
    public void update(double dt) {
        x += velocityX * dt;
        y += velocityY * dt;
        syncRect();
    }

    // -------------------------
    // Movement input
    // -------------------------
    public void moveLeft() {
        velocityX = -MOVE_SPEED;
    }

    public void moveRight() {
        velocityX = MOVE_SPEED;
    }

    public void stopX() {
        velocityX = 0;
    }

    public void jump() {
        if (onGround) {
            velocityY = JUMP_FORCE;
            onGround = false;
        }
    }

    // -------------------------
    // World bounds (WORLD width, not screen)
    // -------------------------
    public void constrainToBounds(double worldWidth) {
        if (x < 0) {
            x = 0;
        }
        if (x + width > worldWidth) {
            x = worldWidth - width;
        }
        syncRect();
    }

    // -------------------------
    // Collision helpers
    // -------------------------
    public void landOn(double groundY) {
        y = groundY - height;
        velocityY = 0;
        onGround = true;
        syncRect();
    }

    public void hitCeiling(double ceilingY) {
        y = ceilingY;
        velocityY = 0;
        syncRect();
    }

    // -------------------------
    // Sync visual
    // -------------------------
    private void syncRect() {
        rectangle.setX(x);
        rectangle.setY(y);
    }

    // -------------------------
    // Getters
    // -------------------------
    public Rectangle getRectangle() {
        return rectangle;
    }

    public void applyGravity(double dt) {
        velocityY += GRAVITY * dt;
    }

    public double getPlayerX() {
        return x;
    }

    public double getPlayerY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    // -------------------------
    // Setters (USED BY PHYSICS & RESPAWN)
    // -------------------------
    public void setPlayerX(double x) {
        this.x = x;
        syncRect();
    }

    public void setPlayerY(double y) {
        this.y = y;
        syncRect();
    }

    public void setVelocityX(double vx) {
        this.velocityX = vx;
    }

    public void setVelocityY(double vy) {
        this.velocityY = vy;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
