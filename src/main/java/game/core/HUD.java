// Fichier : src/main/java/game/core/HUD.java
package game.core;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * A8 - Basic HUD (Score Text Only)
 * Classe pour afficher le score en haut à gauche
 */
public class HUD {

    private Text scoreText;
    private int score;

    /**
     * Constructeur du HUD
     */
    public HUD() {
        this.score = 0;

        // Créer le texte du score
        scoreText = new Text();
        scoreText.setX(20); // Position X (20 pixels du bord gauche)
        scoreText.setY(30); // Position Y (30 pixels du haut)
        scoreText.setFont(Font.font("Arial", FontWeight.BOLD, 24)); // Police
        scoreText.setFill(Color.WHITE); // Couleur blanche
        scoreText.setStroke(Color.BLACK); // Bordure noire pour la lisibilité
        scoreText.setStrokeWidth(1);

        // Initialiser le texte
        updateScoreHUD();
    }

    /**
     * Met à jour l'affichage du score
     */
    public void updateScoreHUD() {
        scoreText.setText("Score: " + score);
    }

    /**
     * Ajoute des points au score
     * @param points Nombre de points à ajouter
     */
    public void addScore(int points) {
        score += points;
        updateScoreHUD();
    }

    /**
     * Réinitialise le score à 0
     */
    public void resetScore() {
        score = 0;
        updateScoreHUD();
    }

    // Getters
    public Text getScoreText() {
        return scoreText;
    }

    public int getScore() {
        return score;
    }

    /**
     * Définit un score spécifique
     * @param score Le nouveau score
     */
    public void setScore(int score) {
        this.score = score;
        updateScoreHUD();
    }
}