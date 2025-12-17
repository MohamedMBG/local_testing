// Fichier : src/main/java/game/core/Game.java
package game.core;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * VERSION FINALE COMPLÈTE
 * A1 à A8 - Jeu Mario complet
 */
public class Game extends Application {


    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;


    private Pane root;


    private Ground ground;
    private Player player;


    private HUD hud;


    private GameLoop gameLoop;


    private InputManager inputManager;

    @Override
    public void start(Stage primaryStage) {

        root = new Pane();
        root.setStyle("-fx-background-color: #5C94FC;"); // Bleu ciel comme Mario


        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);




        ground = new Ground(0, WINDOW_HEIGHT - 100, WINDOW_WIDTH, 100);
        root.getChildren().add(ground.getRectangle());

        // Créer le joueur (position initiale au centre, sur le sol)
        player = new Player(100, WINDOW_HEIGHT - 140);
        root.getChildren().add(player.getRectangle());

        // ===================================================


        hud = new HUD();
        root.getChildren().add(hud.getScoreText());
        // ============================================


        inputManager = new InputManager();
        inputManager.setupInput(scene);
        // =======================================

        // ========== A3 : Game Loop ==========
        gameLoop = new GameLoop(player, ground, inputManager, WINDOW_WIDTH);
        gameLoop.start(); // Démarrer la boucle de jeu
        // ====================================


        primaryStage.setTitle("Super Mario Game - By Monssef");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("✅ A1 - JavaFX Project Setup : TERMINÉ !");
        System.out.println("✅ A2 - Game Objects (Player + Ground) : TERMINÉ !");
        System.out.println("✅ A3 - Game Loop : TERMINÉ !");
        System.out.println("✅ A4 - Input System : TERMINÉ !");
        System.out.println("✅ A5 - Movement Logic : TERMINÉ !");
        System.out.println("✅ A6 - Gravity & Jump : TERMINÉ !");
        System.out.println("✅ A7 - Ground Collision : TERMINÉ !");
        System.out.println("✅ A8 - HUD (Score) : TERMINÉ !");
        System.out.println("🎮 JEU PRÊT ! Utilisez les flèches ou Q/D pour bouger, ESPACE pour sauter !");
    }

    public static void main(String[] args) {
        launch(args);
    }
}