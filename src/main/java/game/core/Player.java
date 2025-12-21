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
    private final Group avatarContainer;
    private final Rectangle collisionBox;

    private Group idlePose;
    private Group runningPose;
    private Group jumpingPose;

    private PlayerPose currentPose = PlayerPose.IDLE;

    // -------------------------
    // Position & size
    // -------------------------
    private double x;
    private double y;
    private final double width = 64;   // much larger visual footprint
    private final double height = 72;  // taller silhouette for readability

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

        avatarContainer = new Group();
        node.getChildren().add(avatarContainer);

        buildAvatarPoses();
        showPose(PlayerPose.IDLE);

        syncRect();
    }

    private void buildAvatarPoses() {
        idlePose = buildAvatarPose(0, 0, 0, 0, 0);
        runningPose = buildAvatarPose(-6, 6, -6, 6, 2);
        jumpingPose = buildAvatarPose(-4, 8, 8, 12, 6);

        avatarContainer.getChildren().setAll(idlePose, runningPose, jumpingPose);
    }

    private Group buildAvatarPose(double torsoTiltDeg, double leftLegLift, double rightLegLift, double armLift, double liftAll) {
        Group pose = new Group();

        double headRadius = width * 0.22;
        double faceCenterX = width / 2.0;
        double faceCenterY = headRadius + 6 - liftAll;

        Circle face = new Circle(faceCenterX, faceCenterY, headRadius);
        face.setFill(Color.web("#F4CDA5"));
        face.setStroke(Color.web("#D09A6A"));

        Rectangle hair = new Rectangle(faceCenterX - headRadius - 3, 4 - liftAll, headRadius * 2 + 6, headRadius * 0.9);
        hair.setArcWidth(8);
        hair.setArcHeight(8);
        hair.setFill(Color.web("#5B3A2E"));

        Rectangle hatCrown = new Rectangle(faceCenterX - headRadius, 2 - liftAll, headRadius * 2, headRadius * 1.0);
        hatCrown.setArcWidth(8);
        hatCrown.setArcHeight(8);
        hatCrown.setFill(Color.web("#B03030"));
        hatCrown.setStroke(Color.web("#7D0E0E"));

        Rectangle hatBrim = new Rectangle(faceCenterX - headRadius - 6, headRadius * 0.8 - liftAll, headRadius * 2.4, headRadius * 0.35);
        hatBrim.setArcWidth(6);
        hatBrim.setArcHeight(6);
        hatBrim.setFill(Color.web("#8E1B1B"));

        double torsoY = headRadius * 2 + 8 - liftAll;
        double torsoHeight = height * 0.56;

        Rectangle robe = new Rectangle(6, torsoY, width - 12, torsoHeight);
        robe.setArcWidth(14);
        robe.setArcHeight(14);
        robe.setFill(Color.web("#2E7FA1"));
        robe.setStroke(Color.web("#1C4F69"));

        Rectangle robeTrim = new Rectangle(width / 2.0 - 4, torsoY + 2, 8, torsoHeight - 4);
        robeTrim.setFill(Color.web("#D8B464"));

        Rectangle sash = new Rectangle(8, torsoY + torsoHeight * 0.55, width - 16, 6);
        sash.setFill(Color.web("#C2873C"));

        Rectangle strap = new Rectangle(robe.getX() + robe.getWidth() - 12, torsoY + 6, 6, torsoHeight - 12);
        strap.setFill(Color.web("#8B5E3C"));

        Rectangle satchel = new Rectangle(robe.getX() + robe.getWidth() - 20, torsoY + torsoHeight - 24, 16, 18);
        satchel.setArcWidth(6);
        satchel.setArcHeight(6);
        satchel.setFill(Color.web("#B07A46"));
        satchel.setStroke(Color.web("#7A4F2B"));

        double pantsY = torsoY + torsoHeight - 6;
        double pantsHeight = height - pantsY - 10;
        Rectangle pants = new Rectangle(8, pantsY, width - 16, pantsHeight);
        pants.setArcWidth(8);
        pants.setArcHeight(8);
        pants.setFill(Color.web("#F0E5D8"));
        pants.setStroke(Color.web("#C4B8A6"));

        Rectangle leftBoot = new Rectangle(8, height - 12 - leftLegLift, 18, 10);
        leftBoot.setArcWidth(6);
        leftBoot.setArcHeight(6);
        leftBoot.setFill(Color.web("#7B4A24"));
        leftBoot.setStroke(Color.web("#5C3519"));

        Rectangle rightBoot = new Rectangle(width - 26, height - 12 - rightLegLift, 18, 10);
        rightBoot.setArcWidth(6);
        rightBoot.setArcHeight(6);
        rightBoot.setFill(Color.web("#7B4A24"));
        rightBoot.setStroke(Color.web("#5C3519"));

        Rectangle leftSleeve = new Rectangle(robe.getX() - 6, torsoY + 8 - armLift, 8, torsoHeight * 0.4);
        leftSleeve.setArcWidth(8);
        leftSleeve.setArcHeight(8);
        leftSleeve.setFill(Color.web("#2E7FA1"));

        Rectangle rightSleeve = new Rectangle(robe.getX() + robe.getWidth() - 2, torsoY + 10 - armLift, 8, torsoHeight * 0.36);
        rightSleeve.setArcWidth(8);
        rightSleeve.setArcHeight(8);
        rightSleeve.setFill(Color.web("#2E7FA1"));

        Rectangle collar = new Rectangle(robe.getX() + 8, torsoY - 4, robe.getWidth() - 16, 8);
        collar.setArcWidth(8);
        collar.setArcHeight(8);
        collar.setFill(Color.web("#2E7FA1"));
        collar.setStroke(Color.web("#D8B464"));

        if (torsoTiltDeg != 0) {
            pose.setRotate(torsoTiltDeg);
            pose.setTranslateX(width * 0.04 * Math.signum(torsoTiltDeg));
        }
        pose.setTranslateY(-liftAll * 0.15);

        pose.getChildren().addAll(
                hatBrim, hatCrown, hair, face,
                robe, robeTrim, collar, sash, strap, satchel,
                leftSleeve, rightSleeve,
                pants, leftBoot, rightBoot
        );

        return pose;
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

    public void updateAnimationState() {
        PlayerPose pose;
        if (!onGround) {
            pose = PlayerPose.JUMPING;
        } else if (Math.abs(velocityX) > 20) {
            pose = PlayerPose.RUNNING;
        } else {
            pose = PlayerPose.IDLE;
        }

        if (pose != currentPose) {
            currentPose = pose;
            showPose(pose);
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

    private void showPose(PlayerPose pose) {
        if (idlePose == null || runningPose == null || jumpingPose == null) return;

        idlePose.setVisible(pose == PlayerPose.IDLE);
        runningPose.setVisible(pose == PlayerPose.RUNNING);
        jumpingPose.setVisible(pose == PlayerPose.JUMPING);
    }

    private enum PlayerPose {
        IDLE,
        RUNNING,
        JUMPING
    }
}
