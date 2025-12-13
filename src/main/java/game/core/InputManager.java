// Fichier : src/main/java/game/core/InputManager.java
package game.core;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

/**
 * A4 - Input System
 * Classe pour gérer les entrées clavier
 */
public class InputManager {

    // Booleans pour savoir quelles touches sont pressées
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;

    /**
     * Initialise les événements clavier sur la scène
     * @param scene La scène du jeu
     */
    public void setupInput(Scene scene) {

        // A4 - Quand une touche est pressée
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.LEFT || code == KeyCode.Q) {
                leftPressed = true;
            }
            if (code == KeyCode.RIGHT || code == KeyCode.D) {
                rightPressed = true;
            }
            if (code == KeyCode.SPACE || code == KeyCode.UP || code == KeyCode.Z) {
                jumpPressed = true;
            }
        });

        // A4 - Quand une touche est relâchée
        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();

            if (code == KeyCode.LEFT || code == KeyCode.Q) {
                leftPressed = false;
            }
            if (code == KeyCode.RIGHT || code == KeyCode.D) {
                rightPressed = false;
            }
            if (code == KeyCode.SPACE || code == KeyCode.UP || code == KeyCode.Z) {
                jumpPressed = false;
            }
        });
    }

    // Getters
    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    // Reset jump (utilisé après avoir sauté)
    public void resetJump() {
        jumpPressed = false;
    }
}