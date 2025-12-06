// Fichier : src/main/java/game/core/GameLoop.java
package game.core;

import javafx.animation.AnimationTimer;

/**
 * A3 - Game Loop
 * Classe qui gère la boucle de jeu (rafraîchissement continu)
 */
public class GameLoop extends AnimationTimer {

    // Référence vers Game pour accéder aux objets du jeu
    private Game game;

    /**
     * Constructeur
     */
    public GameLoop(Game game) {
        this.game = game;
    }

    /**
     * Méthode appelée automatiquement à chaque frame (~60 fois par seconde)
     * @param now : le temps actuel en nanosecondes
     */
    @Override
    public void handle(long now) {
        // Appeler la méthode update() du jeu
        update();
    }

    /**
     * Méthode update() - appelée chaque frame
     * C'est ici qu'on mettra :
     * - La physique (gravité)
     * - Les mouvements
     * - Les collisions
     * - Etc.
     */
    private void update() {
        // Pour l'instant, juste un message pour tester
        // On ajoutera le code de physique et mouvement dans les prochaines tâches

        // TODO A5 : Ajouter la logique de mouvement
        // TODO A6 : Ajouter la gravité et le saut
        // TODO A7 : Ajouter les collisions
    }
}