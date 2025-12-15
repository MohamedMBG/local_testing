// Fichier : src/main/java/game/core/GameLoop.java
package game.core;

import javafx.animation.AnimationTimer;

/**
 * A3 - Game Loop
 * Classe pour la boucle de jeu (mise à jour continue)
 */
public class GameLoop extends AnimationTimer {

    private Player player;
    private Ground ground;
    private InputManager inputManager;
    private double windowWidth;

    /**
     * Constructeur de la boucle de jeu
     */
    public GameLoop(Player player, Ground ground, InputManager inputManager, double windowWidth) {
        this.player = player;
        this.ground = ground;
        this.inputManager = inputManager;
        this.windowWidth = windowWidth;
    }

    @Override
    public void handle(long now) {
        // A3 - Appeler la méthode update à chaque frame
        update();
    }

    /**
     * A3 + A5 + A6 + A7 - Méthode update appelée à chaque frame
     */
    private void update() {

        // A5 - Movement Logic (déplacement gauche/droite)
        if (inputManager.isLeftPressed()) {
            player.moveLeft();
        }
        if (inputManager.isRightPressed()) {
            player.moveRight();
        }

        // A6 - Jump (saut)
        if (inputManager.isJumpPressed()) {
            player.jump();
        }

        // A6 - Appliquer la gravité
        player.applyGravity();

        // A7 - Vérifier la collision avec le sol
        Physics.checkGroundCollision(player, ground);

        // A5 - Empêcher le joueur de sortir de l'écran
        player.constrainToBounds(windowWidth);
    }
}