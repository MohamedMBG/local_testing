// Fichier : src/main/java/game/core/Game.java
package game.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A1 + A2 (Ground) + A8 (HUD)
 * Classe principale du jeu Mario
 */
public class Game extends Application {

    // Dimensions de la fenêtre
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Le Pane principal qui contiendra tous les éléments du jeu
    private Pane root;

    // Objet Ground (sol)
    private Ground ground;

    // A8 - HUD (Score)
    private HUD hud;

    @Override
    public void start(Stage primaryStage) {
        // Créer le Pane principal (root)
        root = new Pane();
        root.setStyle("-fx-background-color: #5C94FC;"); // Bleu ciel comme Mario

        // Créer la Scene avec le root et les dimensions
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // ========== A2 : Initialize Game Objects (Ground) ==========
        // Créer le sol en bas de l'écran
        ground = new Ground(0, WINDOW_HEIGHT - 100, WINDOW_WIDTH, 100);

        // Ajouter le sol au root
        root.getChildren().add(ground.getRectangle());
        // ============================================================

        // ========== A8 : Basic HUD (Score) ==========
        // Créer le HUD
        hud = new HUD();

        // Ajouter le texte du score au root
        root.getChildren().add(hud.getScoreText());
        // ============================================

        // Configurer le Stage (la fenêtre)
        primaryStage.setTitle("Super Mario Game - By Monssef");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("✅ A1 - JavaFX Project Setup : TERMINÉ !");
        System.out.println("✅ A2 - Ground créé : TERMINÉ !");
        System.out.println("✅ A8 - HUD (Score) créé : TERMINÉ !");
    }

    public static void main(String[] args) {
        launch(args);
    }
}