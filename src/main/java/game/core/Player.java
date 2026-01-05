package game.core; // Declares the package for this source file.

import game.utils.Theme;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Player { // Defines a class.

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
    private final double width = 74;   // bigger and clearer silhouette // Executes: private final double width = 74;   // bigger and clearer silhouette
    private final double height = 74;  // taller box for more readable collisions // Executes: private final double height = 74;  // taller box for more readable collisions

    // -------------------------
    // Physics
    // -------------------------
    private double velocityX = 0;
    private double velocityY = 0;

    private boolean onGround = false;

    // -------------------------
    // Tunable constants (feel)
    // -------------------------
    private static final double BASE_MOVE_SPEED = 280;     // faster // Executes: private static final double BASE_MOVE_SPEED = 280;     // faster
    private static final double BASE_GRAVITY = 1100;       // a bit lighter fall // Executes: private static final double BASE_GRAVITY = 1100;       // a bit lighter fall
    private static final double BASE_JUMP_FORCE = -620;    // higher jump // Executes: private static final double BASE_JUMP_FORCE = -620;    // higher jump

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
    public Player(double startX, double startY) { // Begins a method or constructor with its signature.
        this.x = startX; // Executes: this.x = startX;
        this.y = startY; // Executes: this.y = startY;

        collisionBox = new Rectangle(width, height); // Executes: collisionBox = new Rectangle(width, height);
        collisionBox.setFill(Color.TRANSPARENT); // Executes: collisionBox.setFill(Color.TRANSPARENT);
        collisionBox.setStroke(Color.TRANSPARENT); // Executes: collisionBox.setStroke(Color.TRANSPARENT);

        // Load images
        standingImage = new Image(getClass().getResourceAsStream("/assets/standing.png")); // Executes: standingImage = new Image(getClass().getResourceAsStream("/assets/standing.png"));
        runningImage = new Image(getClass().getResourceAsStream("/assets/running.png")); // Executes: runningImage = new Image(getClass().getResourceAsStream("/assets/running.png"));
        jumpingImage = new Image(getClass().getResourceAsStream("/assets/jumping.png")); // Executes: jumpingImage = new Image(getClass().getResourceAsStream("/assets/jumping.png"));

        // Initialize ImageView
        imageView = new ImageView(standingImage); // Executes: imageView = new ImageView(standingImage);
        imageView.setFitWidth(width); // Executes: imageView.setFitWidth(width);
        imageView.setFitHeight(height); // Executes: imageView.setFitHeight(height);
        imageView.setPreserveRatio(true); // Executes: imageView.setPreserveRatio(true);
        imageView.setSmooth(true); // Executes: imageView.setSmooth(true);

        node = new Group(); // Executes: node = new Group();
        node.getChildren().addAll(collisionBox, imageView); // Executes: node.getChildren().addAll(collisionBox, imageView);

        syncRect(); // Executes: syncRect();
    } // Closes a code block.



    // -------------------------
    // Update (called every frame)
    // -------------------------
    public void update(double dt) { // Begins a method or constructor with its signature.
        // Animation logic: switch sprites based on state
        if (!onGround) { // Begins a method or constructor with its signature.
            imageView.setImage(jumpingImage); // Executes: imageView.setImage(jumpingImage);
        } else if (Math.abs(velocityX) > 10) { // Begins a method or constructor with its signature.
            imageView.setImage(runningImage); // Executes: imageView.setImage(runningImage);
            imageView.setScaleX(velocityX > 0 ? 1 : -1); // Executes: imageView.setScaleX(velocityX > 0 ? 1 : -1);
        } else { // Executes: } else {
            imageView.setImage(standingImage); // Executes: imageView.setImage(standingImage);
            imageView.setScaleX(1); // Executes: imageView.setScaleX(1);
        } // Closes a code block.

        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    // -------------------------
    // Movement input
    // -------------------------
    public void moveLeft() { // Begins a method or constructor with its signature.
        velocityX = -moveSpeed; // Executes: velocityX = -moveSpeed;
    } // Closes a code block.

    public void moveRight() { // Begins a method or constructor with its signature.
        velocityX = moveSpeed; // Executes: velocityX = moveSpeed;
    } // Closes a code block.

    public void stopX() { // Begins a method or constructor with its signature.
        velocityX = 0; // Executes: velocityX = 0;
    } // Closes a code block.

    public void jump() { // Begins a method or constructor with its signature.
        jumpBufferTimer = JUMP_BUFFER; // Executes: jumpBufferTimer = JUMP_BUFFER;
        tryConsumeBufferedJump(); // Attempts operations that might throw exceptions.
    } // Closes a code block.

    // -------------------------
    // World bounds (WORLD width, not screen)
    // -------------------------
    public void constrainToBounds(double worldWidth) { // Begins a method or constructor with its signature.
        if (x < 0) { // Begins a method or constructor with its signature.
            x = 0; // Executes: x = 0;
        } // Closes a code block.
        if (x + width > worldWidth) { // Begins a method or constructor with its signature.
            x = worldWidth - width; // Executes: x = worldWidth - width;
        } // Closes a code block.
        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    // -------------------------
    // Collision helpers
    // -------------------------
    public void landOn(double groundY) { // Begins a method or constructor with its signature.
        y = groundY - height; // Executes: y = groundY - height;
        velocityY = 0; // Executes: velocityY = 0;
        onGround = true; // Executes: onGround = true;
        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    public void hitCeiling(double ceilingY) { // Begins a method or constructor with its signature.
        y = ceilingY; // Executes: y = ceilingY;
        velocityY = 0; // Executes: velocityY = 0;
        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    // -------------------------
    // Sync visual
    // -------------------------
    private void syncRect() { // Begins a method or constructor with its signature.
        node.setLayoutX(x); // Executes: node.setLayoutX(x);
        node.setLayoutY(y); // Executes: node.setLayoutY(y);
        collisionBox.setX(0); // Executes: collisionBox.setX(0);
        collisionBox.setY(0); // Executes: collisionBox.setY(0);
        // Center the image view on the collision box
        imageView.setX(0); // Executes: imageView.setX(0);
        imageView.setY(0); // Executes: imageView.setY(0);
    } // Closes a code block.

    // -------------------------
    // Getters
    // -------------------------
    public Rectangle getRectangle() { // Begins a method or constructor with its signature.
        return collisionBox;
    } // Closes a code block.

    public Group getNode() { // Begins a method or constructor with its signature.
        return node;
    } // Closes a code block.

    public void applyGravity(double dt) { // Begins a method or constructor with its signature.
        velocityY += gravity * dt; // Executes: velocityY += gravity * dt;
    } // Closes a code block.

    public void tick(double dt) { // Begins a method or constructor with its signature.
        if (onGround) { // Begins a method or constructor with its signature.
            coyoteTimer = COYOTE_TIME; // Executes: coyoteTimer = COYOTE_TIME;
        } else { // Executes: } else {
            coyoteTimer = Math.max(0, coyoteTimer - dt); // Executes: coyoteTimer = Math.max(0, coyoteTimer - dt);
        } // Closes a code block.

        if (jumpBufferTimer > 0) { // Begins a method or constructor with its signature.
            jumpBufferTimer = Math.max(0, jumpBufferTimer - dt); // Executes: jumpBufferTimer = Math.max(0, jumpBufferTimer - dt);
        } // Closes a code block.

        if (Math.abs(velocityX) < 10 && onGround) { // Begins a method or constructor with its signature.
            velocityX = 0; // Executes: velocityX = 0;
        } // Closes a code block.

        tryConsumeBufferedJump(); // Attempts operations that might throw exceptions.
        update(dt); // Executes: update(dt);
    } // Closes a code block.

    public void applyTheme(Theme theme) { // Begins a method or constructor with its signature.
        moveSpeed = BASE_MOVE_SPEED * theme.getMoveScale(); // Executes: moveSpeed = BASE_MOVE_SPEED * theme.getMoveScale();
        gravity = BASE_GRAVITY * theme.getGravityScale(); // Executes: gravity = BASE_GRAVITY * theme.getGravityScale();
        jumpForce = BASE_JUMP_FORCE * theme.getJumpScale(); // Executes: jumpForce = BASE_JUMP_FORCE * theme.getJumpScale();
    } // Closes a code block.

    private void tryConsumeBufferedJump() { // Begins a method or constructor with its signature.
        if (jumpBufferTimer > 0 && (onGround || coyoteTimer > 0)) { // Begins a method or constructor with its signature.
            velocityY = jumpForce; // Executes: velocityY = jumpForce;
            onGround = false; // Executes: onGround = false;
            coyoteTimer = 0; // Executes: coyoteTimer = 0;
            jumpBufferTimer = 0; // Executes: jumpBufferTimer = 0;
        } // Closes a code block.
    } // Closes a code block.

    public double getPlayerX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public double getPlayerY() { // Begins a method or constructor with its signature.
        return y;
    } // Closes a code block.

    public double getWidth() { // Begins a method or constructor with its signature.
        return width;
    } // Closes a code block.

    public double getHeight() { // Begins a method or constructor with its signature.
        return height;
    } // Closes a code block.

    public double getVelocityX() { // Begins a method or constructor with its signature.
        return velocityX;
    } // Closes a code block.

    public double getVelocityY() { // Begins a method or constructor with its signature.
        return velocityY;
    } // Closes a code block.

    public boolean isOnGround() { // Begins a method or constructor with its signature.
        return onGround;
    } // Closes a code block.

    // -------------------------
    // Setters (USED BY PHYSICS & RESPAWN)
    // -------------------------
    public void setPlayerX(double x) { // Begins a method or constructor with its signature.
        this.x = x; // Executes: this.x = x;
        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    public void setPlayerY(double y) { // Begins a method or constructor with its signature.
        this.y = y; // Executes: this.y = y;
        syncRect(); // Executes: syncRect();
    } // Closes a code block.

    public void setVelocityX(double vx) { // Begins a method or constructor with its signature.
        this.velocityX = vx; // Executes: this.velocityX = vx;
    } // Closes a code block.

    public void setVelocityY(double vy) { // Begins a method or constructor with its signature.
        this.velocityY = vy; // Executes: this.velocityY = vy;
    } // Closes a code block.

    public void setOnGround(boolean onGround) { // Begins a method or constructor with its signature.
        this.onGround = onGround; // Executes: this.onGround = onGround;
    } // Closes a code block.
} // Closes a code block.
