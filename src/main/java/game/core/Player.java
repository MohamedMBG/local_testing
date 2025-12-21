package game.core;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Player {

    // -------------------------
    // Visual
    // -------------------------
    private final Group node;
    private final Rectangle collisionBox;

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

        collisionBox = new Rectangle(width, height);
        collisionBox.setFill(Color.TRANSPARENT);
        collisionBox.setStroke(Color.TRANSPARENT);

        node = new Group();
        node.getChildren().add(collisionBox);

        buildAvatar();

        syncRect();
    }

    private void buildAvatar() {
        double headRadius = width * 0.18;
        double faceCenterX = width / 2.0;
        double faceCenterY = headRadius + 2;

        Circle face = new Circle(faceCenterX, faceCenterY, headRadius);
        face.setFill(Color.web("#F4CDA5"));
        face.setStroke(Color.web("#D09A6A"));

        Rectangle hair = new Rectangle(faceCenterX - headRadius - 2, 2, headRadius * 2 + 4, headRadius * 0.8);
        hair.setArcWidth(6);
        hair.setArcHeight(6);
        hair.setFill(Color.web("#5B3A2E"));

        Rectangle hatCrown = new Rectangle(faceCenterX - headRadius, 0, headRadius * 2, headRadius * 0.9);
        hatCrown.setArcWidth(6);
        hatCrown.setArcHeight(6);
        hatCrown.setFill(Color.web("#B03030"));
        hatCrown.setStroke(Color.web("#7D0E0E"));

        Rectangle hatBrim = new Rectangle(faceCenterX - headRadius - 4, headRadius * 0.7, headRadius * 2.4, headRadius * 0.3);
        hatBrim.setArcWidth(4);
        hatBrim.setArcHeight(4);
        hatBrim.setFill(Color.web("#8E1B1B"));

        double torsoY = headRadius * 2 + 4;
        double torsoHeight = height * 0.52;

        Rectangle robe = new Rectangle(4, torsoY, width - 8, torsoHeight);
        robe.setArcWidth(10);
        robe.setArcHeight(10);
        robe.setFill(Color.web("#2E7FA1"));
        robe.setStroke(Color.web("#1C4F69"));

        Rectangle robeTrim = new Rectangle(width / 2.0 - 3, torsoY + 2, 6, torsoHeight - 4);
        robeTrim.setFill(Color.web("#D8B464"));

        Rectangle sash = new Rectangle(6, torsoY + torsoHeight * 0.55, width - 12, 4);
        sash.setFill(Color.web("#C2873C"));

        Rectangle strap = new Rectangle(robe.getX() + robe.getWidth() - 9, torsoY + 6, 5, torsoHeight - 12);
        strap.setFill(Color.web("#8B5E3C"));

        Rectangle satchel = new Rectangle(robe.getX() + robe.getWidth() - 15, torsoY + torsoHeight - 18, 12, 14);
        satchel.setArcWidth(4);
        satchel.setArcHeight(4);
        satchel.setFill(Color.web("#B07A46"));
        satchel.setStroke(Color.web("#7A4F2B"));

        double pantsY = torsoY + torsoHeight - 2;
        double pantsHeight = height - pantsY - 4;
        Rectangle pants = new Rectangle(6, pantsY, width - 12, pantsHeight);
        pants.setArcWidth(6);
        pants.setArcHeight(6);
        pants.setFill(Color.web("#F0E5D8"));
        pants.setStroke(Color.web("#C4B8A6"));

        Rectangle leftBoot = new Rectangle(6, height - 6, 12, 6);
        leftBoot.setArcWidth(4);
        leftBoot.setArcHeight(4);
        leftBoot.setFill(Color.web("#7B4A24"));
        leftBoot.setStroke(Color.web("#5C3519"));

        Rectangle rightBoot = new Rectangle(width - 18, height - 6, 12, 6);
        rightBoot.setArcWidth(4);
        rightBoot.setArcHeight(4);
        rightBoot.setFill(Color.web("#7B4A24"));
        rightBoot.setStroke(Color.web("#5C3519"));

        Rectangle leftSleeve = new Rectangle(robe.getX() - 4, torsoY + 6, 6, torsoHeight * 0.38);
        leftSleeve.setArcWidth(6);
        leftSleeve.setArcHeight(6);
        leftSleeve.setFill(Color.web("#2E7FA1"));

        Rectangle rightSleeve = new Rectangle(robe.getX() + robe.getWidth() - 2, torsoY + 8, 6, torsoHeight * 0.36);
        rightSleeve.setArcWidth(6);
        rightSleeve.setArcHeight(6);
        rightSleeve.setFill(Color.web("#2E7FA1"));

        Rectangle collar = new Rectangle(robe.getX() + 6, torsoY - 4, robe.getWidth() - 12, 6);
        collar.setArcWidth(6);
        collar.setArcHeight(6);
        collar.setFill(Color.web("#2E7FA1"));
        collar.setStroke(Color.web("#D8B464"));

        node.getChildren().addAll(
                hatBrim, hatCrown, hair, face,
                robe, robeTrim, collar, sash, strap, satchel,
                leftSleeve, rightSleeve,
                pants, leftBoot, rightBoot
        );
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
        node.setLayoutX(x);
        node.setLayoutY(y);
        collisionBox.setX(0);
        collisionBox.setY(0);
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
