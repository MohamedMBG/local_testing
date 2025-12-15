package game.core;

import game.systems.*;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;

public class Game extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    private Pane root;

    private Ground ground;
    private Player player;

    private HUD hud;
    private GameLoop gameLoop;
    private InputManager inputManager;

    private GameWorld world;
    private Canvas overlayCanvas;
    private GraphicsContext gc;

    private Group worldLayer;

    @Override
    public void start(Stage primaryStage) {

        root = new Pane();
        root.setStyle("-fx-background-color: #5C94FC;");

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // BIGGER LEVEL
        List<String> rawLines = List.of(
                "................................................................................",
                "................................................................................",
                "................................................................................",
                "...........C.....................C.....................C.......................",
                "......####..................####...............####............................",
                "................................................................................",
                "....C....................C..................C.................................",
                "....................####.........................####.........................",
                "................................................................................",
                ".....####............................................................####......",
                "................................................................................",
                "................................................................................",
                "....P..........................................................................",
                "#########################................................................#######"
        );

        // ✅ Normalize all lines to same length
        List<String> lines = normalizeLevelLines(rawLines);

        // ✅ Create loader (you forgot this)
        LevelLoader loader = new LevelLoader();
        LevelLoader.LevelData level = loader.loadFromLines(lines);

        TileMap tileMap = level.getTileMap();

        // Camera (height, width) - as in your class
        Camera camera = new Camera(WINDOW_HEIGHT, WINDOW_WIDTH);

        // World layer: put player + ground rectangles here
        worldLayer = new Group();
        root.getChildren().add(worldLayer);

        // Ground should be world width
        ground = new Ground(0, WINDOW_HEIGHT - 100, tileMap.getWidthInPixels(), 100);
        worldLayer.getChildren().add(ground.getRectangle());

        // Player spawn from level
        player = new Player(level.getPlayerSpawnX(), level.getPlayerSpawnY());
        worldLayer.getChildren().add(player.getRectangle());

        // HUD stays fixed on screen
        hud = new HUD();
        root.getChildren().add(hud.getScoreText());

        // Input
        inputManager = new InputManager();
        inputManager.setupInput(scene);

        // Canvas overlay (tiles / coins / UI)
        overlayCanvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        gc = overlayCanvas.getGraphicsContext2D();
        root.getChildren().add(overlayCanvas);

        // Managers
        CoinManager coinManager = new CoinManager();
        coinManager.spawnFrom(level.getCoinSpawns(), 16, 16);

        PowerUpManager powerUpManager = new PowerUpManager();
        powerUpManager.spawnFrom(level.getPowerUpSpawns(), 20, 20, PowerUpType.MUSHROOM);

        UIManager uiManager = new UIManager(20, 40);

        world = new GameWorld(tileMap, camera, coinManager, powerUpManager, uiManager, player);

        // Game loop: pass camera + worldLayer (must match your GameLoop constructor)
        gameLoop = new GameLoop(
                player,
                ground,
                inputManager,
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                world,
                gc,
                tileMap,
                camera,
                worldLayer
        );
        gameLoop.start();

        primaryStage.setTitle("Super Mario Game - Merged");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        System.out.println("Coins spawns: " + level.getCoinSpawns().size());
        System.out.println("Map pixels: " + tileMap.getWidthInPixels() + " x " + tileMap.getHeightInPixels());
    }

    // ✅ MUST be inside the Game class
    private static List<String> normalizeLevelLines(List<String> raw) {
        int width = raw.stream().mapToInt(String::length).max().orElse(0);
        return raw.stream()
                .map(s -> String.format("%-" + width + "s", s).replace(' ', '.'))
                .toList();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
