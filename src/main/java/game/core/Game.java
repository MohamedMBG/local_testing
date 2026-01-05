package game.core;

import game.systems.*;
import game.utils.HighScoreDatabase;
import game.utils.Theme;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {

    private static final int WINDOW_WIDTH = 1250;
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
    private HighScoreDatabase highScoreDatabase;
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
        int levelsCount = 40;
        int mapWidthTiles = 110;
        int mapHeightTiles = 8;

        rawLevels = new ArrayList<>();
        for (int lvl = 1; lvl <= levelsCount; lvl++) {
            rawLevels.add(ProceduralLevelGenerator.generate(lvl, mapWidthTiles, mapHeightTiles, seed + lvl * 999L));
        }

        highScoreDatabase = new HighScoreDatabase();
        highestScore = highScoreDatabase.loadHighScore();

        inputManager = new InputManager();
        inputManager.setupInput(scene);

        // Dashboard lets players choose a theme and see their best score before the run starts
        dashboardScreen = new DashboardScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::onThemePicked, this::launchFromDashboard);
        dashboardScreen.setHighScore(highestScore);
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
            activeLoop.stop();
        }
        root.getChildren().clear();

        // 2. Check if levels are finished
        if (levelIndex >= rawLevels.size()) {
            System.out.println("ALL LEVELS COMPLETED!");
            return;
        }

        System.out.println("Starting Level: " + (levelIndex + 1));

        // 3. Load Level Data
        List<String> rawLines = rawLevels.get(levelIndex);
        List<String> normalizedLines = normalizeLevelLines(rawLines);
        List<String> lines = alignLevelToGround(normalizedLines);

        LevelLoader loader = new LevelLoader();
        LevelLoader.LevelData level = loader.loadFromLines(lines);
        TileMap tileMap = level.getTileMap();

        // ================= CAMERA =================
        Camera camera = new Camera(WINDOW_HEIGHT, WINDOW_WIDTH);

        // ================= WORLD LAYER =================
        Group worldLayer = new Group();
        root.getChildren().add(worldLayer);

        // ================= GROUND =================
        final double groundHeight = 80;
        final double groundTopY = WINDOW_HEIGHT - groundHeight;

        Ground ground = new Ground(0, groundTopY, tileMap.getWidthInPixels(), groundHeight);
        ground.applyTheme(activeTheme.getGround());
        worldLayer.getChildren().add(ground.getRectangle());

        // ================= PLAYER SPAWN (FALL FROM SKY) =================
        double spawnX = level.getPlayerSpawnX();
        double spawnY = 0; // Force start at Top of Screen

        Player player = new Player(spawnX, spawnY);
        player.applyTheme(activeTheme);
        worldLayer.getChildren().add(player.getNode());

        // Ensure physics engine knows we are in the air
        setPlayerPositionBestEffort(player, spawnX, spawnY);
        resetPlayerMotionBestEffort(player);
        invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false});

        // ================= SCREENS =================
        gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::restartCurrentLevel);
        root.getChildren().add(gameOverScreen.getNode());

        LevelCompleteScreen completeScreen = new LevelCompleteScreen(WINDOW_WIDTH, WINDOW_HEIGHT, () -> {
            currentLevelIndex++;
            startLevel(currentLevelIndex);
        });
        root.getChildren().add(completeScreen.getNode());

        // ================= INPUT & CANVAS =================
        inputManager.resetAllInputs();
        inputManager.setInputEnabled(true);

        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // ================= UI =================
        uiManager = new UIManager(20, 30, activeTheme);
        uiManager.setBestScore(highestScore);
        uiManager.setThemeName(activeTheme.getDisplayName());
        root.getChildren().add(uiManager.getNode());
        uiManager.getNode().toFront();

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

        // ================= WORLD OBJECT =================
        GameWorld world = new GameWorld(
                tileMap, camera, coinManager, powerUpManager, enemyManager, spikeManager,
                uiManager, player,
                level.getPlayerSpawnX(),
                0, // <--- CRITICAL FIX: Pass 0 (Sky) instead of level.getPlayerSpawnY()
                gameOverScreen, activeTheme, this::onScoreChanged
        );

        // ================= GAME LOOP =================
        activeLoop = new GameLoop(
                player, ground, inputManager, WINDOW_WIDTH, WINDOW_HEIGHT,
                world, gc, tileMap, camera, worldLayer,
                this::restartCurrentLevel
        ) {
            @Override
            public void handle(long now) {
                super.handle(now);
                if (player.getPlayerX() > tileMap.getWidthInPixels() - 150) {
                    this.stop();
                    completeScreen.show();
                }
            }
        };

        // Start Physics Immediately (Player falls while screen fades in)
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

        // Disable input so user can't move while screen is black
        inputManager.resetAllInputs();
        inputManager.setInputEnabled(false);

        Rectangle overlayBefore = fadeOverlay;
        if (overlayBefore != null) {
            overlayBefore.setMouseTransparent(false);

            // 1. Fade screen to BLACK
            FadeTransition fadeToBlack = new FadeTransition(Duration.millis(300), overlayBefore);
            fadeToBlack.setFromValue(0);
            fadeToBlack.setToValue(1);

            fadeToBlack.setOnFinished(event -> {
                // 2. Load the level
                // This spawns the player at Y=0 and STARTS the physics loop immediately.
                startLevel(currentLevelIndex);

                // 3. Fade screen to TRANSPARENT (Reveal Game)
                Rectangle overlayAfter = fadeOverlay;
                if (overlayAfter != null) {
                    overlayAfter.setOpacity(1);
                    overlayAfter.setMouseTransparent(false);

                    // 600ms duration: Gives the player time to fall from Y=0 to the ground
                    // while the screen is clearing.
                    FadeTransition fadeToClear = new FadeTransition(Duration.millis(600), overlayAfter);
                    fadeToClear.setFromValue(1);
                    fadeToClear.setToValue(0);

                    fadeToClear.setOnFinished(e -> {
                        // 4. Re-enable controls only after landing/fade is done
                        inputManager.resetAllInputs();
                        inputManager.setInputEnabled(true);
                        overlayAfter.setMouseTransparent(true);
                        restarting = false;
                        refocusScene();
                    });
                    fadeToClear.play();
                } else {
                    inputManager.setInputEnabled(true);
                    restarting = false;
                }
            });
            fadeToBlack.play();
        } else {
            // Fallback if no overlay
            startLevel(currentLevelIndex);
            inputManager.setInputEnabled(true);
            restarting = false;
        }
    }

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
        if (activeLoop != null) {
            startLevel(currentLevelIndex);
        }
    }

    private void onScoreChanged(int score) {
        if (score > highestScore) {
            highestScore = score;
            if (highScoreDatabase != null) {
                highScoreDatabase.saveHighScore(highestScore);
            }
            if (dashboardScreen != null) {
                dashboardScreen.setHighScore(highestScore);
            }
            if (uiManager != null) {
                uiManager.setBestScore(highestScore);
            }
        }
    }

    // ============================
    // Spawn helpers
    // ============================

    private static void forceLayout(Pane root, Node node) {
        if (root == null || node == null) return;
        root.applyCss();
        root.layout();
    }

    private static double safeNodeHeight(Node node) {
        if (node == null) return 50;
        Bounds b = node.getBoundsInParent();
        double h = (b != null) ? b.getHeight() : 0;
        return h > 1 ? h : 50;
    }

    /**
     * Drops the player down from startY to maxY until it collides with the tileMap.
     * Then returns a Y that places it ON TOP of that collision (not inside it).
     *
     * Works with your existing TileMap collision API via isCollidingBestEffort().
     */
    private double computeSpawnYOnFirstCollision(
            TileMap tileMap,
            Player player,
            double x,
            double startY,
            double maxY,
            double stepY
    ) {
        if (tileMap == null || player == null) return startY;

        Node n = player.getNode();
        if (n == null) return startY;

        // start from above
        setPlayerPositionBestEffort(player, x, startY);
        forceLayout(root, n);

        boolean foundCollision = false;
        double y = startY;

        // Drop down until collision happens
        for (y = startY; y <= maxY; y += stepY) {
            setPlayerPositionBestEffort(player, x, y);
            forceLayout(root, n);

            Bounds b = n.getBoundsInParent();
            if (isCollidingBestEffort(tileMap, b)) {
                foundCollision = true;
                break;
            }
        }

        if (!foundCollision) {
            // fallback: keep it near the bottom, still safe
            return maxY;
        }

        // We are colliding. Move up until we are NOT colliding anymore.
        // This puts the player "on top" of the solid tile instead of inside it.
        for (int i = 0; i < 200; i++) {
            Bounds b = n.getBoundsInParent();
            if (!isCollidingBestEffort(tileMap, b)) {
                break;
            }
            setPlayerPositionBestEffort(player, x, getCurrentY(player, n) - 1);
            forceLayout(root, n);
        }

        // final tiny safety margin
        return getCurrentY(player, n) - 1;
    }

    private double getCurrentY(Player player, Node n) {
        Double currentY = getDoubleIfExists(player, "getY");
        if (currentY == null) currentY = getDoubleIfExists(player, "getPlayerY");
        if (currentY != null) return currentY;
        return n.getLayoutY();
    }

    private static void setPlayerPositionBestEffort(Player player, double x, double y) {
        if (player == null) return;

        if (invokeIfExists(player, "setPosition", new Class[]{double.class, double.class}, new Object[]{x, y})) return;

        boolean xOk = invokeIfExists(player, "setX", new Class[]{double.class}, new Object[]{x})
                || invokeIfExists(player, "setPlayerX", new Class[]{double.class}, new Object[]{x});
        boolean yOk = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{y})
                || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{y});
        if (xOk || yOk) return;

        Node n = player.getNode();
        if (n != null) {
            n.setLayoutX(x);
            n.setLayoutY(y);
        }
    }

    private static void resetPlayerMotionBestEffort(Player player) {
        if (player == null) return;

        if (invokeIfExists(player, "setVelocity", new Class[]{double.class, double.class}, new Object[]{0.0, 0.0})) return;

        invokeIfExists(player, "resetVelocity", new Class[]{}, new Object[]{});
        invokeIfExists(player, "resetMovement", new Class[]{}, new Object[]{});
        invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false});
    }

    private static void nudgePlayerUpUntilFreeBestEffort(Player player, TileMap tileMap, int maxSteps, double stepY) {
        if (player == null || tileMap == null) return;

        Node n = player.getNode();
        if (n == null) return;

        for (int i = 0; i < maxSteps; i++) {
            if (!isCollidingBestEffort(tileMap, n.getBoundsInParent())) {
                return;
            }

            boolean moved = false;
            Double currentY = getDoubleIfExists(player, "getY");
            if (currentY == null) currentY = getDoubleIfExists(player, "getPlayerY");
            if (currentY != null) {
                moved = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{currentY - stepY})
                        || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{currentY - stepY});
            }

            if (!moved) {
                n.setLayoutY(n.getLayoutY() - stepY);
            }
        }
    }

    private static boolean isCollidingBestEffort(TileMap tileMap, Bounds bounds) {
        if (tileMap == null || bounds == null) return false;

        Boolean b1 = (Boolean) invokeReturnIfExists(tileMap, "isColliding",
                new Class[]{Bounds.class}, new Object[]{bounds});
        if (b1 != null) return b1;

        Boolean b2 = (Boolean) invokeReturnIfExists(tileMap, "collides",
                new Class[]{double.class, double.class, double.class, double.class},
                new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()});
        if (b2 != null) return b2;

        Boolean b3 = (Boolean) invokeReturnIfExists(tileMap, "isSolidRect",
                new Class[]{double.class, double.class, double.class, double.class},
                new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()});
        if (b3 != null) return b3;

        return false;
    }

    private static boolean invokeIfExists(Object target, String methodName, Class<?>[] sig, Object[] args) {
        try {
            Method m = target.getClass().getMethod(methodName, sig);
            m.setAccessible(true);
            m.invoke(target, args);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    private static Object invokeReturnIfExists(Object target, String methodName, Class<?>[] sig, Object[] args) {
        try {
            Method m = target.getClass().getMethod(methodName, sig);
            m.setAccessible(true);
            return m.invoke(target, args);
        } catch (Exception ignored) {
            return null;
        }
    }

    private static Double getDoubleIfExists(Object target, String methodName) {
        try {
            Method m = target.getClass().getMethod(methodName);
            m.setAccessible(true);
            Object r = m.invoke(target);
            if (r instanceof Number) return ((Number) r).doubleValue();
            return null;
        } catch (Exception ignored) {
            return null;
        }
    }

    // ============================
    // Existing helper methods
    // ============================

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

    private List<String> alignLevelToGround(List<String> rawLines) {
        int tileSize = TileMap.TILE_SIZE;
        int mapHeight = rawLines.size() * tileSize;
        int floorY = WINDOW_HEIGHT - 80;
        int emptyPixels = floorY - mapHeight;
        int rowsToShift = emptyPixels / tileSize;

        if (rowsToShift <= 0) return rawLines;

        int width = rawLines.get(0).length();
        String emptyRow = ".".repeat(width);

        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < rowsToShift; i++) {
            newLevel.add(emptyRow);
        }
        newLevel.addAll(rawLines);

        return newLevel;
    }

    private void refocusScene() {
        if (scene != null && scene.getRoot() != null) {
            scene.getRoot().requestFocus();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
