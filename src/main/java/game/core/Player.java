// Fichier : src/main/java/game/core/Player.java
package game.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A2 - Player (Joueur)
 * Classe représentant le joueur (Mario)
 */
public class Player {

    private Rectangle rectangle;

    // Position et dimensions
    private double playerX;
    private double playerY;
    private double width = 40;
    private double height = 40;

    // Physique (A6)
    private double velocityY = 0;
    private boolean isOnGround = false;

    // Constantes
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12;
    private static final double MOVE_SPEED = 5;

    /**
     * Constructeur du joueur
     * @param startX Position X initiale
     * @param startY Position Y initiale
     */
    public Player(double startX, double startY) {
        this.playerX = startX;
        this.playerY = startY;

        // Créer le rectangle du joueur (rouge comme Mario)
        rectangle = new Rectangle(playerX, playerY, width, height);
        rectangle.setFill(Color.RED);
        rectangle.setStroke(Color.DARKRED);
        rectangle.setStrokeWidth(2);
    }

    // Getters
    public Rectangle getRectangle() {
        return rectangle;
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    // Setters
    public void setPlayerX(double x) {
        this.playerX = x;
        rectangle.setX(x);
    }

    public void setPlayerY(double y) {
        this.playerY = y;
        rectangle.setY(y);
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public void setOnGround(boolean onGround) {
        this.isOnGround = onGround;
    }

    /**
     * A5 - Déplacer le joueur à gauche
     */
    public void moveLeft() {
        playerX -= MOVE_SPEED;
        rectangle.setX(playerX);
    }

    /**
     * A5 - Déplacer le joueur à droite
     */
    public void moveRight() {
        playerX += MOVE_SPEED;
        rectangle.setX(playerX);
    }

    /**
     * A6 - Faire sauter le joueur
     */
    public void jump() {
        if (isOnGround) {
            velocityY = JUMP_STRENGTH;
            isOnGround = false;
        }
    }

    /**
     * A6 - Appliquer la gravité
     */
    public void applyGravity() {
        velocityY += GRAVITY;
        playerY += velocityY;
        rectangle.setY(playerY);
    }

    /**
     * A5 - Empêcher le joueur de sortir de l'écran
     */
    public void constrainToBounds(double windowWidth) {
        if (playerX < 0) {
            playerX = 0;
        }
        if (playerX + width > windowWidth) {
            playerX = windowWidth - width;
        }
        rectangle.setX(playerX);
    }
}