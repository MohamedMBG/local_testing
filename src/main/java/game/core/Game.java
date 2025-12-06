// Fichier : src/main/java/game/core/Game.java
package game.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A1 - JavaFX Project Setup
 * Classe principale du jeu Mario
 */
public class Game extends Application {

    // Dimensions de la fenêtre
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Le Pane principal qui contiendra tous les éléments du jeu
    private Pane root;

    @Override
    public void start(Stage primaryStage) {
        // Créer le Pane principal (root)
        root = new Pane();
        root.setStyle("-fx-background-color: #5C94FC;"); // Bleu ciel comme Mario

        // Créer la Scene avec le root et les dimensions
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Configurer le Stage (la fenêtre)
        primaryStage.setTitle("Super Mario Game - By Monssef");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Empêcher le redimensionnement
        primaryStage.show();

        System.out.println("✅ A1 - JavaFX Project Setup : TERMINÉ !");
    }

    public static void main(String[] args) {
        launch(args);
    }
}