// Fichier : src/main/java/game/core/InputManager.java
package game.core; // Declares the package for this source file.

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * A4 - Input System
 * Classe pour gérer les entrées clavier
 */
public class InputManager { // Defines a class.

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
    public void setupInput(Scene scene) { // Begins a method or constructor with its signature.

        // Capture keyboard input even when UI controls have focus by using filters
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> { // Begins a method or constructor with its signature.
            if (!inputEnabled) return; // Evaluates a conditional branch.

            KeyCode code = event.getCode();

            if (code == KeyCode.LEFT || code == KeyCode.Q) { // Begins a method or constructor with its signature.
                leftPressed = true; // Executes: leftPressed = true;
            } // Closes a code block.
            if (code == KeyCode.RIGHT || code == KeyCode.D) { // Begins a method or constructor with its signature.
                rightPressed = true; // Executes: rightPressed = true;
            } // Closes a code block.
            if (code == KeyCode.SPACE || code == KeyCode.UP || code == KeyCode.Z) { // Begins a method or constructor with its signature.
                jumpPressed = true; // Executes: jumpPressed = true;
            } // Closes a code block.
            if (code == KeyCode.R) { // Begins a method or constructor with its signature.
                restartPressed = true; // Executes: restartPressed = true;
            } // Closes a code block.
        }); // Executes: });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> { // Begins a method or constructor with its signature.
            if (!inputEnabled) return; // Evaluates a conditional branch.

            KeyCode code = event.getCode();

            if (code == KeyCode.LEFT || code == KeyCode.Q) { // Begins a method or constructor with its signature.
                leftPressed = false; // Executes: leftPressed = false;
            } // Closes a code block.
            if (code == KeyCode.RIGHT || code == KeyCode.D) { // Begins a method or constructor with its signature.
                rightPressed = false; // Executes: rightPressed = false;
            } // Closes a code block.
            if (code == KeyCode.SPACE || code == KeyCode.UP || code == KeyCode.Z) { // Begins a method or constructor with its signature.
                jumpPressed = false; // Executes: jumpPressed = false;
            } // Closes a code block.
            if (code == KeyCode.R) { // Begins a method or constructor with its signature.
                restartPressed = false; // Executes: restartPressed = false;
            } // Closes a code block.
        }); // Executes: });
    } // Closes a code block.

    // Getters
    public boolean isLeftPressed() { // Begins a method or constructor with its signature.
        return leftPressed;
    } // Closes a code block.

    public boolean isRightPressed() { // Begins a method or constructor with its signature.
        return rightPressed;
    } // Closes a code block.

    public boolean isJumpPressed() { // Begins a method or constructor with its signature.
        return jumpPressed;
    } // Closes a code block.

    public boolean isRestartPressed() { // Begins a method or constructor with its signature.
        return restartPressed;
    } // Closes a code block.

    // Reset jump (utilisé après avoir sauté)
    public void resetJump() { // Begins a method or constructor with its signature.
        jumpPressed = false; // Executes: jumpPressed = false;
    } // Closes a code block.

    public void resetRestart() { // Begins a method or constructor with its signature.
        restartPressed = false; // Executes: restartPressed = false;
    } // Closes a code block.

    public void resetAllInputs() { // Begins a method or constructor with its signature.
        leftPressed = false; // Executes: leftPressed = false;
        rightPressed = false; // Executes: rightPressed = false;
        jumpPressed = false; // Executes: jumpPressed = false;
        restartPressed = false; // Executes: restartPressed = false;
    } // Closes a code block.

    public void setInputEnabled(boolean enabled) { // Begins a method or constructor with its signature.
        this.inputEnabled = enabled; // Executes: this.inputEnabled = enabled;
        if (!enabled) { // Begins a method or constructor with its signature.
            resetAllInputs(); // Executes: resetAllInputs();
        } // Closes a code block.
    } // Closes a code block.
} // Closes a code block.
