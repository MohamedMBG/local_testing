package game.core;

import game.systems.*;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 650;

    // Class fields to manage state
    private Pane root;
    private Scene scene;
    private List<List<String>> rawLevels;
    private int currentLevelIndex = 0;
    private GameLoop activeLoop;
    private Stage primaryStage;
    private InputManager inputManager;
    private GameOverScreen gameOverScreen;
    private Rectangle fadeOverlay;
    private boolean restarting = false;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        root = new Pane();
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: #5C94FC;");

        // ================= LEVELS GENERATION =================
        long seed = System.currentTimeMillis();
        int levelsCount = 6;
        int mapWidthTiles = 110;
        int mapHeightTiles = 8;

        rawLevels = new ArrayList<>();
        for (int lvl = 1; lvl <= levelsCount; lvl++) {
            rawLevels.add(ProceduralLevelGenerator.generate(lvl, mapWidthTiles, mapHeightTiles, seed + lvl * 999L));
        }

        inputManager = new InputManager();
        inputManager.setupInput(scene);

        // Start the first level
        startLevel(currentLevelIndex);

        stage.setScene(scene);
        stage.setTitle("Super Mario – Real Game");
        stage.setResizable(false);
        stage.show();
    }

    private void startLevel(int levelIndex) {
        // 1. Cleanup previous level
        if (activeLoop != null) {
            activeLoop.stop(); // Stop the timer
        }
        root.getChildren().clear(); // Remove old objects

        // 2. Check if levels are finished
        if (levelIndex >= rawLevels.size()) {
            System.out.println("ALL LEVELS COMPLETED!");
            // You could show a "You Win the Game" screen here
            return;
        }

        System.out.println("Starting Level: " + (levelIndex + 1));

        // 3. Load Level Data
        List<String> rawLines = rawLevels.get(levelIndex);
        List<String> normalizedLines = normalizeLevelLines(rawLines);

// >>> NEW: Push everything down to the ground <<<
        List<String> lines = alignLevelToGround(normalizedLines);

        LevelLoader loader = new LevelLoader();
        LevelLoader.LevelData level = loader.loadFromLines(lines);
        TileMap tileMap = level.getTileMap();

        // ================= CAMERA =================
        Camera camera = new Camera(WINDOW_HEIGHT, WINDOW_WIDTH);

        // ================= WORLD LAYER =================
        Group worldLayer = new Group();
        root.getChildren().add(worldLayer);

        Ground ground = new Ground(0, WINDOW_HEIGHT - 80, tileMap.getWidthInPixels(), 80);
        worldLayer.getChildren().add(ground.getRectangle());

        double groundY = WINDOW_HEIGHT - 100 - 50;

// Use the level's X spawn, but force our calculated ground Y
        Player player = new Player(level.getPlayerSpawnX(), groundY);

        worldLayer.getChildren().add(player.getRectangle());

        // ================= UI =================
        UIManager uiManager = new UIManager(20, 30);
        root.getChildren().add(uiManager.getNode());

        // ================= GAME OVER SCREEN =================
        gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(gameOverScreen.getNode());

        // ================= LEVEL COMPLETE SCREEN =================
        // Pass a lambda to handle what happens when "Next Level" is clicked
        LevelCompleteScreen completeScreen = new LevelCompleteScreen(WINDOW_WIDTH, WINDOW_HEIGHT, () -> {
            currentLevelIndex++;
            startLevel(currentLevelIndex);
        });
        root.getChildren().add(completeScreen.getNode());

        // ================= INPUT =================
        inputManager.resetAllInputs();
        inputManager.setInputEnabled(true);

        // ================= CANVAS =================
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        createFadeOverlay();

        // ================= MANAGERS =================
        CoinManager coinManager = new CoinManager();
        EnemyManager enemyManager = new EnemyManager();
        SpikeManager spikeManager = new SpikeManager();

        Random rng = new Random();
        List<double[]> jitteredCoins = jitterSpawns(level.getCoinSpawns(), tileMap, rng, 6, 4);
        List<double[]> jitteredEnemies = jitterSpawns(level.getEnemySpawns(), tileMap, rng, 4, 0);

        coinManager.spawnFrom(jitteredCoins);
        enemyManager.spawnFrom(jitteredEnemies);
        spikeManager.spawnFrom(level.getSpikeSpawns());

        // Render Spikes
        for (double[] sp : level.getSpikeSpawns()) {
            if (sp == null || sp.length < 2) continue;
            double sx = sp[0];
            double sy = sp[1] + TileMap.TILE_SIZE - Spike.SIZE;

            Rectangle base = new Rectangle(sx + 4, sy + Spike.SIZE - 6, Spike.SIZE - 8, 6);
            base.setFill(Color.web("#3E2723"));
            Polygon poly = new Polygon(sx + Spike.SIZE / 2.0, sy + 4, sx + 2, sy + Spike.SIZE - 6, sx + Spike.SIZE - 2, sy + Spike.SIZE - 6);
            poly.setFill(Color.web("#D32F2F"));
            poly.setStroke(Color.web("#5D0E0E"));
            poly.setStrokeWidth(1.5);
            worldLayer.getChildren().addAll(base, poly);
        }
        worldLayer.toFront();

        // ================= WORLD =================
        GameWorld world = new GameWorld(
                tileMap, camera, coinManager, enemyManager, spikeManager,
                uiManager, player, level.getPlayerSpawnX(), level.getPlayerSpawnY(),
                gameOverScreen
        );

        // ================= LOOP =================
        activeLoop = new GameLoop(
                player, ground, inputManager, WINDOW_WIDTH, WINDOW_HEIGHT,
                world, gc, tileMap, camera, worldLayer,
                this::restartCurrentLevel
        ) {
            // Override update to check for win condition every frame
            @Override
            public void handle(long now) {
                super.handle(now);

                // WIN CONDITION CHECK:
                // If player is near the end of the map (Map Width - 100px)
                if (player.getPlayerX() > tileMap.getWidthInPixels() - 150) {
                    this.stop(); // Stop the game loop
                    completeScreen.show(); // Show the screen
                }
            }
        };

        activeLoop.start();
    }

    private void restartCurrentLevel() {
        if (restarting) return;
        restarting = true;

        if (gameOverScreen != null) {
            gameOverScreen.hide();
        }

        inputManager.resetAllInputs();
        inputManager.setInputEnabled(false);

        Rectangle overlayBefore = fadeOverlay;
        if (overlayBefore != null) {
            overlayBefore.setMouseTransparent(false);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), overlayBefore);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setOnFinished(event -> {
                startLevel(currentLevelIndex);

                Rectangle overlayAfter = fadeOverlay;
                if (overlayAfter != null) {
                    overlayAfter.setOpacity(1);
                    overlayAfter.setMouseTransparent(false);
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), overlayAfter);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(e -> {
                        inputManager.resetAllInputs();
                        inputManager.setInputEnabled(true);
                        overlayAfter.setMouseTransparent(true);
                        restarting = false;
                    });
                    fadeOut.play();
                } else {
                    inputManager.setInputEnabled(true);
                    restarting = false;
                }
            });
            fadeIn.play();
        } else {
            startLevel(currentLevelIndex);
            inputManager.setInputEnabled(true);
            restarting = false;
        }
    }

    // ... (Keep your existing helper methods: jitterSpawns, normalizeLevelLines, main) ...

    private void createFadeOverlay() {
        fadeOverlay = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT, Color.BLACK);
        fadeOverlay.setOpacity(0);
        fadeOverlay.setMouseTransparent(true);
        root.getChildren().add(fadeOverlay);
        fadeOverlay.toFront();
    }

    private static List<double[]> jitterSpawns(List<double[]> original, TileMap tileMap, Random rng, double maxOffsetX, double maxOffsetY) {
        // ... (Keep existing code) ...
        List<double[]> result = new ArrayList<>();
        if (original == null) return result;
        int worldWidth = tileMap.getWidthInPixels();
        for (double[] pos : original) {
            if (pos == null || pos.length < 2) continue;
            double x = pos[0]; double y = pos[1];
            double dx = (rng.nextDouble() * 2 - 1) * maxOffsetX;
            double dy = (rng.nextDouble() * 2 - 1) * maxOffsetY;
            double nx = Math.max(0, Math.min(worldWidth - TileMap.TILE_SIZE, x + dx));
            double ny = Math.max(0, y + dy);
            result.add(new double[]{nx, ny});
        }
        return result;
    }

    private static List<String> normalizeLevelLines(List<String> raw) {
        int width = raw.stream().mapToInt(String::length).max().orElse(0);
        return raw.stream().map(s -> String.format("%-" + width + "s", s).replace(' ', '.')).toList();
    }

    public static void main(String[] args) {
        launch(args);
    }
    // Add this method inside Game class
    private List<String> alignLevelToGround(List<String> rawLines) {
        // 1. Get the tile size (assuming it's available in TileMap)
        // If TileMap.TILE_SIZE is not public, check your TileMap.java and replace '32' with the actual number (usually 32, 40, or 64)
        int tileSize = TileMap.TILE_SIZE;

        // 2. Calculate map height in pixels
        int mapHeight = rawLines.size() * tileSize;

        // 3. Define where the "floor" is.
        // You set ground at (WINDOW_HEIGHT - 80).
        int floorY = WINDOW_HEIGHT - 80;

        // 4. Calculate how much empty space is currently below the map
        int emptyPixels = floorY - mapHeight;

        // 5. Convert pixels to rows (round down to be safe)
        int rowsToShift = emptyPixels / tileSize;

        if (rowsToShift <= 0) return rawLines; // Map is already tall enough

        // 6. Create a blank row string of the correct width
        int width = rawLines.get(0).length();
        String emptyRow = ".".repeat(width); // creates "......"

        // 7. Create a new list with padding at the top
        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < rowsToShift; i++) {
            newLevel.add(emptyRow); // Add air at the top
        }
        newLevel.addAll(rawLines); // Add the actual level below

        return newLevel;
    }
}