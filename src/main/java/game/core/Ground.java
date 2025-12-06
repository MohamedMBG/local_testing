// Fichier : src/main/java/game/core/Ground.java
package game.core;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A2 (Partie Ground) - Classe représentant le sol du jeu
 */
public class Ground {

    private Rectangle rectangle;

    // Dimensions et position du sol
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * Constructeur du sol
     * @param x Position X du sol
     * @param y Position Y du sol
     * @param width Largeur du sol
     * @param height Hauteur du sol
     */
    public Ground(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Créer le rectangle qui représente le sol
        rectangle = new Rectangle(x, y, width, height);
        rectangle.setFill(Color.BROWN); // Couleur marron pour le sol
        rectangle.setStroke(Color.BLACK); // Bordure noire
        rectangle.setStrokeWidth(2);
    }

    // Getters
    public Rectangle getRectangle() {
        return rectangle;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Vérifie si le sol intersecte avec un rectangle donné
     * @param other Le rectangle à vérifier
     * @return true si intersection, false sinon
     */
    public boolean intersects(Rectangle other) {
        return rectangle.getBoundsInParent().intersects(other.getBoundsInParent());
    }
}