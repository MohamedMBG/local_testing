package game.core;

import game.systems.*;
import game.utils.Theme;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {

    private static final int WINDOW_WIDTH = 1200;
    private static final int WINDOW_HEIGHT = 630;

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
    private DashboardScreen dashboardScreen;
    private Theme activeTheme = Theme.NEUTRAL;
    private int highestScore = 0;
    private Button dashboardButton;
    private boolean levelBootstrapped = false;
    private UIManager uiManager;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        root = new Pane();
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: " + activeTheme.toCss() + ";");

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

        // Dashboard lets players choose a theme and see their best score before the run starts
        dashboardScreen = new DashboardScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::onThemePicked, this::launchFromDashboard);
        root.getChildren().add(dashboardScreen.getNode());
        dashboardScreen.show();

        stage.setScene(scene);
        stage.setTitle("Super Mario – Real Game");
        stage.setResizable(false);
        stage.show();

        refocusScene();
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
        ground.applyTheme(activeTheme.getGround());
        worldLayer.getChildren().add(ground.getRectangle());

        double groundY = WINDOW_HEIGHT - 100 - 50;

// Use the level's X spawn, but force our calculated ground Y
        Player player = new Player(level.getPlayerSpawnX(), groundY);
        player.applyTheme(activeTheme);

        worldLayer.getChildren().add(player.getNode());

        // ================= UI =================
        uiManager = new UIManager(20, 30, activeTheme);
        uiManager.setBestScore(highestScore);
        uiManager.setThemeName(activeTheme.getDisplayName());
        root.getChildren().add(uiManager.getNode());

        // ================= GAME OVER SCREEN =================
        gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::restartCurrentLevel);
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
        PowerUpManager powerUpManager = new PowerUpManager();
        EnemyManager enemyManager = new EnemyManager();
        SpikeManager spikeManager = new SpikeManager();

        Random rng = new Random();
        List<double[]> coinSpawns = centerWithinTile(level.getCoinSpawns(), CoinManager.DEFAULT_SIZE, CoinManager.DEFAULT_SIZE);
        List<double[]> powerUpSpawns = centerWithinTile(level.getPowerUpSpawns(), PowerUpManager.DEFAULT_SIZE, PowerUpManager.DEFAULT_SIZE);
        List<double[]> enemySpawns = restOnTileTop(level.getEnemySpawns(), Enemy.SIZE, Enemy.SIZE);

        List<double[]> jitteredCoins = jitterSpawns(coinSpawns, tileMap, rng, 3, 2, CoinManager.DEFAULT_SIZE, CoinManager.DEFAULT_SIZE);
        List<double[]> jitteredPowerUps = jitterSpawns(powerUpSpawns, tileMap, rng, 2, 2, PowerUpManager.DEFAULT_SIZE, PowerUpManager.DEFAULT_SIZE);
        List<double[]> jitteredEnemies = jitterSpawns(enemySpawns, tileMap, rng, 2, 0, Enemy.SIZE, Enemy.SIZE);

        coinManager.spawnFrom(jitteredCoins);
        spawnPowerUps(powerUpManager, jitteredPowerUps, rng);
        enemyManager.spawnFrom(jitteredEnemies);
        spikeManager.spawnFrom(level.getSpikeSpawns());

        worldLayer.toFront();

        // ================= WORLD =================
        GameWorld world = new GameWorld(
                tileMap, camera, coinManager, powerUpManager, enemyManager, spikeManager,
                uiManager, player, level.getPlayerSpawnX(), level.getPlayerSpawnY(),
                gameOverScreen, activeTheme, this::onScoreChanged
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
        levelBootstrapped = true;
        attachDashboardButton();
        root.getChildren().add(dashboardScreen.getNode());
        dashboardScreen.hide();

        refocusScene();
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

    private void attachDashboardButton() {
        dashboardButton = new Button("Dashboard");
        dashboardButton.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 10; -fx-padding: 6 12; -fx-border-color: rgba(0,0,0,0.25); -fx-border-radius: 10;");
        dashboardButton.setLayoutX(WINDOW_WIDTH - 150);
        dashboardButton.setLayoutY(20);
        dashboardButton.setOnAction(e -> pauseIntoDashboard());
        root.getChildren().add(dashboardButton);
        dashboardButton.toFront();
    }

    private void pauseIntoDashboard() {
        if (activeLoop != null) {
            activeLoop.stop();
        }
        inputManager.setInputEnabled(false);
        if (dashboardScreen != null) {
            dashboardScreen.setHighScore(highestScore);
            dashboardScreen.show();
        }
    }

    private void launchFromDashboard() {
        if (!levelBootstrapped) {
            startLevel(currentLevelIndex);
        }
        if (dashboardScreen != null) {
            dashboardScreen.hide();
        }
        if (activeLoop != null) {
            activeLoop.start();
        }
        inputManager.resetAllInputs();
        inputManager.setInputEnabled(true);
        refocusScene();
    }

    private void onThemePicked(Theme theme) {
        this.activeTheme = theme;
        root.setStyle("-fx-background-color: " + theme.toCss() + ";");
        if (dashboardScreen != null) {
            dashboardScreen.setHighScore(highestScore);
        }
        // reapply to current level if it exists
        if (activeLoop != null) {
            startLevel(currentLevelIndex);
        }
    }

    private void onScoreChanged(int score) {
        if (score > highestScore) {
            highestScore = score;
            if (dashboardScreen != null) {
                dashboardScreen.setHighScore(highestScore);
            }
            if (uiManager != null) {
                uiManager.setBestScore(highestScore);
            }
        }
    }

    private static List<double[]> jitterSpawns(
            List<double[]> original,
            TileMap tileMap,
            Random rng,
            double maxOffsetX,
            double maxOffsetY,
            double itemWidth,
            double itemHeight) {

        List<double[]> result = new ArrayList<>();
        if (original == null) return result;

        int worldWidth = tileMap.getWidthInPixels();
        int worldHeight = tileMap.getHeightInPixels();

        for (double[] pos : original) {
            if (pos == null || pos.length < 2) continue;

            double dx = (rng.nextDouble() * 2 - 1) * maxOffsetX;
            double dy = (rng.nextDouble() * 2 - 1) * maxOffsetY;

            double nx = pos[0] + dx;
            double ny = pos[1] + dy;

            double margin = 4;
            nx = Math.max(margin, Math.min(worldWidth - itemWidth - margin, nx));
            ny = Math.max(margin, Math.min(worldHeight - itemHeight - margin, ny));

            result.add(new double[]{nx, ny});
        }
        return result;
    }

    private List<double[]> centerWithinTile(List<double[]> original, double itemWidth, double itemHeight) {
        List<double[]> result = new ArrayList<>();
        if (original == null) return result;

        double offsetX = (TileMap.TILE_SIZE - itemWidth) / 2.0;
        double offsetY = (TileMap.TILE_SIZE - itemHeight) / 2.0;

        for (double[] pos : original) {
            if (pos == null || pos.length < 2) continue;
            result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY});
        }
        return result;
    }

    private List<double[]> restOnTileTop(List<double[]> original, double itemWidth, double itemHeight) {
        List<double[]> result = new ArrayList<>();
        if (original == null) return result;

        double offsetX = (TileMap.TILE_SIZE - itemWidth) / 2.0;
        double offsetY = TileMap.TILE_SIZE - itemHeight;

        for (double[] pos : original) {
            if (pos == null || pos.length < 2) continue;
            result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY});
        }
        return result;
    }

    private void spawnPowerUps(PowerUpManager manager, List<double[]> positions, Random rng) {
        if (manager == null || positions == null) return;

        PowerUpType[] types = PowerUpType.values();
        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            PowerUpType type = types[rng.nextInt(types.length)];
            manager.spawn(pos[0], pos[1], PowerUpManager.DEFAULT_SIZE, PowerUpManager.DEFAULT_SIZE, type);
        }
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

    private void refocusScene() {
        if (scene != null && scene.getRoot() != null) {
            scene.getRoot().requestFocus();
        }
    }
}