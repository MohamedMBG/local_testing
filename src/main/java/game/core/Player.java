package game.core;

import game.utils.Theme;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player {

    // -------------------------
    // Visual
    // -------------------------
    private final Group node;
    private final Rectangle collisionBox;
    private final ImageView imageView;
    private final Image standingImage;
    private final Image runningImage;
    private final Image jumpingImage;

    // -------------------------
    // Position & size
    // -------------------------
    private double x;
    private double y;
    private final double width = 74;   // bigger and clearer silhouette
    private final double height = 74;  // taller box for more readable collisions

    // -------------------------
    // Physics
    // -------------------------
    private double velocityX = 0;
    private double velocityY = 0;

    private boolean onGround = false;

    // -------------------------
    // Tunable constants (feel)
    // -------------------------
    private static final double BASE_MOVE_SPEED = 280;     // faster
    private static final double BASE_GRAVITY = 1100;       // a bit lighter fall
    private static final double BASE_JUMP_FORCE = -620;    // higher jump

    private double moveSpeed = BASE_MOVE_SPEED;
    private double gravity = BASE_GRAVITY;
    private double jumpForce = BASE_JUMP_FORCE;

    // Jump leniency / buffering
    private static final double COYOTE_TIME = 0.14;
    private static final double JUMP_BUFFER = 0.16;
    private double coyoteTimer = 0;
    private double jumpBufferTimer = 0;

    // -------------------------
    // Constructor
    // -------------------------
    public Player(double startX, double startY) {
        this.x = startX;
        this.y = startY;

        collisionBox = new Rectangle(width, height);
        collisionBox.setFill(Color.TRANSPARENT);
        collisionBox.setStroke(Color.TRANSPARENT);

        // Load images
        standingImage = new Image(getClass().getResourceAsStream("/assets/standing.png"));
        runningImage = new Image(getClass().getResourceAsStream("/assets/running.png"));
        jumpingImage = new Image(getClass().getResourceAsStream("/assets/jumping.png"));

        // Initialize ImageView
        imageView = new ImageView(standingImage);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        node = new Group();
        node.getChildren().addAll(collisionBox, imageView);

        syncRect();
    }



    // -------------------------
    // Update (called every frame)
    // -------------------------
    public void update(double dt) {
        // Animation logic: switch sprites based on state
        if (!onGround) {
            imageView.setImage(jumpingImage);
        } else if (Math.abs(velocityX) > 10) {
            imageView.setImage(runningImage);
            imageView.setScaleX(velocityX > 0 ? 1 : -1);
        } else {
            imageView.setImage(standingImage);
            imageView.setScaleX(1);
        }

        syncRect();
    }

    // -------------------------
    // Movement input
    // -------------------------
    public void moveLeft() {
        velocityX = -moveSpeed;
    }

    public void moveRight() {
        velocityX = moveSpeed;
    }

    public void stopX() {
        velocityX = 0;
    }

    public void jump() {
        jumpBufferTimer = JUMP_BUFFER;
        tryConsumeBufferedJump();
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
        node.setLayoutX(x);
        node.setLayoutY(y);
        collisionBox.setX(0);
        collisionBox.setY(0);
        // Center the image view on the collision box
        imageView.setX(0);
        imageView.setY(0);
    }

    // -------------------------
    // Getters
    // -------------------------
    public Rectangle getRectangle() {
        return collisionBox;
    }

    public Group getNode() {
        return node;
    }

    public void applyGravity(double dt) {
        velocityY += gravity * dt;
    }

    public void tick(double dt) {
        if (onGround) {
            coyoteTimer = COYOTE_TIME;
        } else {
            coyoteTimer = Math.max(0, coyoteTimer - dt);
        }

        if (jumpBufferTimer > 0) {
            jumpBufferTimer = Math.max(0, jumpBufferTimer - dt);
        }

        if (Math.abs(velocityX) < 10 && onGround) {
            velocityX = 0;
        }

        tryConsumeBufferedJump();
        update(dt);
    }

    public void applyTheme(Theme theme) {
        moveSpeed = BASE_MOVE_SPEED * theme.getMoveScale();
        gravity = BASE_GRAVITY * theme.getGravityScale();
        jumpForce = BASE_JUMP_FORCE * theme.getJumpScale();
    }

    private void tryConsumeBufferedJump() {
        if (jumpBufferTimer > 0 && (onGround || coyoteTimer > 0)) {
            velocityY = jumpForce;
            onGround = false;
            coyoteTimer = 0;
            jumpBufferTimer = 0;
        }
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
