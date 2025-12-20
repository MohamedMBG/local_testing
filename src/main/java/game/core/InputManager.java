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
    private boolean restartPressed = false;

    private boolean inputEnabled = true;

    /**
     * Initialise les événements clavier sur la scène
     * @param scene La scène du jeu
     */
    public void setupInput(Scene scene) {

        // A4 - Quand une touche est pressée
        scene.setOnKeyPressed(event -> {
            if (!inputEnabled) return;

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
            if (code == KeyCode.R) {
                restartPressed = true;
            }
        });

        // A4 - Quand une touche est relâchée
        scene.setOnKeyReleased(event -> {
            if (!inputEnabled) return;

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
            if (code == KeyCode.R) {
                restartPressed = false;
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

    public boolean isRestartPressed() {
        return restartPressed;
    }

    // Reset jump (utilisé après avoir sauté)
    public void resetJump() {
        jumpPressed = false;
    }

    public void resetRestart() {
        restartPressed = false;
    }

    public void resetAllInputs() {
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        restartPressed = false;
    }

    public void setInputEnabled(boolean enabled) {
        this.inputEnabled = enabled;
        if (!enabled) {
            resetAllInputs();
        }
    }
}