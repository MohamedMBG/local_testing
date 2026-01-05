package game.core; // Declares the package for this source file.

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

public class Game extends Application { // Defines a class.

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

    @Override // Applies an annotation to the following element.
    public void start(Stage stage) { // Begins a method or constructor with its signature.
        this.primaryStage = stage; // Executes: this.primaryStage = stage;
        root = new Pane(); // Executes: root = new Pane();
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT); // Executes: scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: " + activeTheme.toCss() + ";"); // Executes: root.setStyle("-fx-background-color: " + activeTheme.toCss() + ";");

        // ================= LEVELS GENERATION =================
        long seed = System.currentTimeMillis();
        int levelsCount = 40;
        int mapWidthTiles = 110;
        int mapHeightTiles = 8;

        rawLevels = new ArrayList<>(); // Executes: rawLevels = new ArrayList<>();
        for (int lvl = 1; lvl <= levelsCount; lvl++) { // Begins a method or constructor with its signature.
            rawLevels.add(ProceduralLevelGenerator.generate(lvl, mapWidthTiles, mapHeightTiles, seed + lvl * 999L)); // Executes: rawLevels.add(ProceduralLevelGenerator.generate(lvl, mapWidthTiles, mapHeightTiles, seed + lvl * 999L));
        } // Closes a code block.

        highScoreDatabase = new HighScoreDatabase(); // Executes: highScoreDatabase = new HighScoreDatabase();
        highestScore = highScoreDatabase.loadHighScore(); // Executes: highestScore = highScoreDatabase.loadHighScore();

        inputManager = new InputManager(); // Executes: inputManager = new InputManager();
        inputManager.setupInput(scene); // Executes: inputManager.setupInput(scene);

        // Dashboard lets players choose a theme and see their best score before the run starts
        dashboardScreen = new DashboardScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::onThemePicked, this::launchFromDashboard); // Executes: dashboardScreen = new DashboardScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::onThemePicked, this::launchFromDashboard);
        dashboardScreen.setHighScore(highestScore); // Executes: dashboardScreen.setHighScore(highestScore);
        root.getChildren().add(dashboardScreen.getNode()); // Executes: root.getChildren().add(dashboardScreen.getNode());
        dashboardScreen.show(); // Executes: dashboardScreen.show();

        stage.setScene(scene); // Executes: stage.setScene(scene);
        stage.setTitle("Super Mario – Real Game"); // Executes: stage.setTitle("Super Mario – Real Game");
        stage.setResizable(false); // Executes: stage.setResizable(false);
        stage.show(); // Executes: stage.show();

        refocusScene(); // Executes: refocusScene();
    } // Closes a code block.

    private void startLevel(int levelIndex) { // Begins a method or constructor with its signature.
        // 1. Cleanup previous level
        if (activeLoop != null) { // Begins a method or constructor with its signature.
            activeLoop.stop(); // Executes: activeLoop.stop();
        } // Closes a code block.
        root.getChildren().clear(); // Executes: root.getChildren().clear();

        // 2. Check if levels are finished
        if (levelIndex >= rawLevels.size()) { // Begins a method or constructor with its signature.
            System.out.println("ALL LEVELS COMPLETED!"); // Executes: System.out.println("ALL LEVELS COMPLETED!");
            return; // Returns a value from the method.
        } // Closes a code block.

        System.out.println("Starting Level: " + (levelIndex + 1)); // Executes: System.out.println("Starting Level: " + (levelIndex + 1));

        // 3. Load Level Data
        List<String> rawLines = rawLevels.get(levelIndex);
        List<String> normalizedLines = normalizeLevelLines(rawLines);
        List<String> lines = alignLevelToGround(normalizedLines);

        LevelLoader loader = new LevelLoader();
        LevelLoader.LevelData level = loader.loadFromLines(lines); // Executes: LevelLoader.LevelData level = loader.loadFromLines(lines);
        TileMap tileMap = level.getTileMap();

        // ================= CAMERA =================
        Camera camera = new Camera(WINDOW_HEIGHT, WINDOW_WIDTH);

        // ================= WORLD LAYER =================
        Group worldLayer = new Group();
        root.getChildren().add(worldLayer); // Executes: root.getChildren().add(worldLayer);

        // ================= GROUND =================
        final double groundHeight = 80;
        final double groundTopY = WINDOW_HEIGHT - groundHeight;

        Ground ground = new Ground(0, groundTopY, tileMap.getWidthInPixels(), groundHeight);
        ground.applyTheme(activeTheme.getGround()); // Executes: ground.applyTheme(activeTheme.getGround());
        worldLayer.getChildren().add(ground.getRectangle()); // Executes: worldLayer.getChildren().add(ground.getRectangle());

        // ================= PLAYER SPAWN (FALL FROM SKY) =================
        double spawnX = level.getPlayerSpawnX();
        double spawnY = 0; // Force start at Top of Screen // Executes: double spawnY = 0; // Force start at Top of Screen

        Player player = new Player(spawnX, spawnY);
        player.applyTheme(activeTheme); // Executes: player.applyTheme(activeTheme);
        worldLayer.getChildren().add(player.getNode()); // Executes: worldLayer.getChildren().add(player.getNode());

        // Ensure physics engine knows we are in the air
        setPlayerPositionBestEffort(player, spawnX, spawnY); // Executes: setPlayerPositionBestEffort(player, spawnX, spawnY);
        resetPlayerMotionBestEffort(player); // Executes: resetPlayerMotionBestEffort(player);
        invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false}); // Executes: invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false});

        // ================= SCREENS =================
        gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::restartCurrentLevel); // Executes: gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT, this::restartCurrentLevel);
        root.getChildren().add(gameOverScreen.getNode()); // Executes: root.getChildren().add(gameOverScreen.getNode());

        LevelCompleteScreen completeScreen = new LevelCompleteScreen(WINDOW_WIDTH, WINDOW_HEIGHT, () -> { // Begins a method or constructor with its signature.
            currentLevelIndex++; // Executes: currentLevelIndex++;
            startLevel(currentLevelIndex); // Executes: startLevel(currentLevelIndex);
        }); // Executes: });
        root.getChildren().add(completeScreen.getNode()); // Executes: root.getChildren().add(completeScreen.getNode());

        // ================= INPUT & CANVAS =================
        inputManager.resetAllInputs(); // Executes: inputManager.resetAllInputs();
        inputManager.setInputEnabled(true); // Executes: inputManager.setInputEnabled(true);

        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas); // Executes: root.getChildren().add(canvas);

        // ================= UI =================
        uiManager = new UIManager(20, 30, activeTheme); // Executes: uiManager = new UIManager(20, 30, activeTheme);
        uiManager.setBestScore(highestScore); // Executes: uiManager.setBestScore(highestScore);
        uiManager.setThemeName(activeTheme.getDisplayName()); // Executes: uiManager.setThemeName(activeTheme.getDisplayName());
        root.getChildren().add(uiManager.getNode()); // Executes: root.getChildren().add(uiManager.getNode());
        uiManager.getNode().toFront(); // Executes: uiManager.getNode().toFront();

        createFadeOverlay(); // Executes: createFadeOverlay();

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

        coinManager.spawnFrom(jitteredCoins); // Executes: coinManager.spawnFrom(jitteredCoins);
        spawnPowerUps(powerUpManager, jitteredPowerUps, rng); // Executes: spawnPowerUps(powerUpManager, jitteredPowerUps, rng);
        enemyManager.spawnFrom(jitteredEnemies); // Executes: enemyManager.spawnFrom(jitteredEnemies);
        spikeManager.spawnFrom(level.getSpikeSpawns()); // Executes: spikeManager.spawnFrom(level.getSpikeSpawns());

        worldLayer.toFront(); // Executes: worldLayer.toFront();

        // ================= WORLD OBJECT =================
        GameWorld world = new GameWorld( // Executes: GameWorld world = new GameWorld(
                tileMap, camera, coinManager, powerUpManager, enemyManager, spikeManager, // Executes: tileMap, camera, coinManager, powerUpManager, enemyManager, spikeManager,
                uiManager, player, // Executes: uiManager, player,
                level.getPlayerSpawnX(), // Executes: level.getPlayerSpawnX(),
                0, // <--- CRITICAL FIX: Pass 0 (Sky) instead of level.getPlayerSpawnY() // Executes: 0, // <--- CRITICAL FIX: Pass 0 (Sky) instead of level.getPlayerSpawnY()
                gameOverScreen, activeTheme, this::onScoreChanged // Executes: gameOverScreen, activeTheme, this::onScoreChanged
        ); // Executes: );

        // ================= GAME LOOP =================
        activeLoop = new GameLoop( // Executes: activeLoop = new GameLoop(
                player, ground, inputManager, WINDOW_WIDTH, WINDOW_HEIGHT, // Executes: player, ground, inputManager, WINDOW_WIDTH, WINDOW_HEIGHT,
                world, gc, tileMap, camera, worldLayer, // Executes: world, gc, tileMap, camera, worldLayer,
                this::restartCurrentLevel // Executes: this::restartCurrentLevel
        ) { // Executes: ) {
            @Override // Applies an annotation to the following element.
            public void handle(long now) { // Begins a method or constructor with its signature.
                super.handle(now); // Executes: super.handle(now);
                if (player.getPlayerX() > tileMap.getWidthInPixels() - 150) { // Begins a method or constructor with its signature.
                    this.stop(); // Executes: this.stop();
                    completeScreen.show(); // Executes: completeScreen.show();
                } // Closes a code block.
            } // Closes a code block.
        }; // Executes: };

        // Start Physics Immediately (Player falls while screen fades in)
        activeLoop.start(); // Executes: activeLoop.start();

        levelBootstrapped = true; // Executes: levelBootstrapped = true;
        attachDashboardButton(); // Executes: attachDashboardButton();
        root.getChildren().add(dashboardScreen.getNode()); // Executes: root.getChildren().add(dashboardScreen.getNode());
        dashboardScreen.hide(); // Executes: dashboardScreen.hide();

        refocusScene(); // Executes: refocusScene();
    } // Closes a code block.

    private void restartCurrentLevel() { // Begins a method or constructor with its signature.
        if (restarting) return; // Evaluates a conditional branch.
        restarting = true; // Executes: restarting = true;

        if (gameOverScreen != null) { // Begins a method or constructor with its signature.
            gameOverScreen.hide(); // Executes: gameOverScreen.hide();
        } // Closes a code block.

        // Disable input so user can't move while screen is black
        inputManager.resetAllInputs(); // Executes: inputManager.resetAllInputs();
        inputManager.setInputEnabled(false); // Executes: inputManager.setInputEnabled(false);

        Rectangle overlayBefore = fadeOverlay;
        if (overlayBefore != null) { // Begins a method or constructor with its signature.
            overlayBefore.setMouseTransparent(false); // Executes: overlayBefore.setMouseTransparent(false);

            // 1. Fade screen to BLACK
            FadeTransition fadeToBlack = new FadeTransition(Duration.millis(300), overlayBefore);
            fadeToBlack.setFromValue(0); // Executes: fadeToBlack.setFromValue(0);
            fadeToBlack.setToValue(1); // Executes: fadeToBlack.setToValue(1);

            fadeToBlack.setOnFinished(event -> { // Begins a method or constructor with its signature.
                // 2. Load the level
                // This spawns the player at Y=0 and STARTS the physics loop immediately.
                startLevel(currentLevelIndex); // Executes: startLevel(currentLevelIndex);

                // 3. Fade screen to TRANSPARENT (Reveal Game)
                Rectangle overlayAfter = fadeOverlay;
                if (overlayAfter != null) { // Begins a method or constructor with its signature.
                    overlayAfter.setOpacity(1); // Executes: overlayAfter.setOpacity(1);
                    overlayAfter.setMouseTransparent(false); // Executes: overlayAfter.setMouseTransparent(false);

                    // 600ms duration: Gives the player time to fall from Y=0 to the ground
                    // while the screen is clearing.
                    FadeTransition fadeToClear = new FadeTransition(Duration.millis(600), overlayAfter);
                    fadeToClear.setFromValue(1); // Executes: fadeToClear.setFromValue(1);
                    fadeToClear.setToValue(0); // Executes: fadeToClear.setToValue(0);

                    fadeToClear.setOnFinished(e -> { // Begins a method or constructor with its signature.
                        // 4. Re-enable controls only after landing/fade is done
                        inputManager.resetAllInputs(); // Executes: inputManager.resetAllInputs();
                        inputManager.setInputEnabled(true); // Executes: inputManager.setInputEnabled(true);
                        overlayAfter.setMouseTransparent(true); // Executes: overlayAfter.setMouseTransparent(true);
                        restarting = false; // Executes: restarting = false;
                        refocusScene(); // Executes: refocusScene();
                    }); // Executes: });
                    fadeToClear.play(); // Executes: fadeToClear.play();
                } else { // Executes: } else {
                    inputManager.setInputEnabled(true); // Executes: inputManager.setInputEnabled(true);
                    restarting = false; // Executes: restarting = false;
                } // Closes a code block.
            }); // Executes: });
            fadeToBlack.play(); // Executes: fadeToBlack.play();
        } else { // Executes: } else {
            // Fallback if no overlay
            startLevel(currentLevelIndex); // Executes: startLevel(currentLevelIndex);
            inputManager.setInputEnabled(true); // Executes: inputManager.setInputEnabled(true);
            restarting = false; // Executes: restarting = false;
        } // Closes a code block.
    } // Closes a code block.

    private void createFadeOverlay() { // Begins a method or constructor with its signature.
        fadeOverlay = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT, Color.BLACK); // Executes: fadeOverlay = new Rectangle(WINDOW_WIDTH, WINDOW_HEIGHT, Color.BLACK);
        fadeOverlay.setOpacity(0); // Executes: fadeOverlay.setOpacity(0);
        fadeOverlay.setMouseTransparent(true); // Executes: fadeOverlay.setMouseTransparent(true);
        root.getChildren().add(fadeOverlay); // Executes: root.getChildren().add(fadeOverlay);
        fadeOverlay.toFront(); // Executes: fadeOverlay.toFront();
    } // Closes a code block.

    private void attachDashboardButton() { // Begins a method or constructor with its signature.
        dashboardButton = new Button("Dashboard"); // Executes: dashboardButton = new Button("Dashboard");
        dashboardButton.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 10; -fx-padding: 6 12; -fx-border-color: rgba(0,0,0,0.25); -fx-border-radius: 10;"); // Executes: dashboardButton.setStyle("-fx-background-color: rgba(255,255,255,0.85); -fx-background-radius: 10; -fx-padding: 6 12; -fx-border-color: rgba(0,0,0,0.25); -fx-border-radius: 10;");
        dashboardButton.setLayoutX(WINDOW_WIDTH - 150); // Executes: dashboardButton.setLayoutX(WINDOW_WIDTH - 150);
        dashboardButton.setLayoutY(20); // Executes: dashboardButton.setLayoutY(20);
        dashboardButton.setOnAction(e -> pauseIntoDashboard()); // Executes: dashboardButton.setOnAction(e -> pauseIntoDashboard());
        root.getChildren().add(dashboardButton); // Executes: root.getChildren().add(dashboardButton);
        dashboardButton.toFront(); // Executes: dashboardButton.toFront();
    } // Closes a code block.

    private void pauseIntoDashboard() { // Begins a method or constructor with its signature.
        if (activeLoop != null) { // Begins a method or constructor with its signature.
            activeLoop.stop(); // Executes: activeLoop.stop();
        } // Closes a code block.
        inputManager.setInputEnabled(false); // Executes: inputManager.setInputEnabled(false);
        if (dashboardScreen != null) { // Begins a method or constructor with its signature.
            dashboardScreen.setHighScore(highestScore); // Executes: dashboardScreen.setHighScore(highestScore);
            dashboardScreen.show(); // Executes: dashboardScreen.show();
        } // Closes a code block.
    } // Closes a code block.

    private void launchFromDashboard() { // Begins a method or constructor with its signature.
        if (!levelBootstrapped) { // Begins a method or constructor with its signature.
            startLevel(currentLevelIndex); // Executes: startLevel(currentLevelIndex);
        } // Closes a code block.
        if (dashboardScreen != null) { // Begins a method or constructor with its signature.
            dashboardScreen.hide(); // Executes: dashboardScreen.hide();
        } // Closes a code block.
        if (activeLoop != null) { // Begins a method or constructor with its signature.
            activeLoop.start(); // Executes: activeLoop.start();
        } // Closes a code block.
        inputManager.resetAllInputs(); // Executes: inputManager.resetAllInputs();
        inputManager.setInputEnabled(true); // Executes: inputManager.setInputEnabled(true);
        refocusScene(); // Executes: refocusScene();
    } // Closes a code block.

    private void onThemePicked(Theme theme) { // Begins a method or constructor with its signature.
        this.activeTheme = theme; // Executes: this.activeTheme = theme;
        root.setStyle("-fx-background-color: " + theme.toCss() + ";"); // Executes: root.setStyle("-fx-background-color: " + theme.toCss() + ";");
        if (dashboardScreen != null) { // Begins a method or constructor with its signature.
            dashboardScreen.setHighScore(highestScore); // Executes: dashboardScreen.setHighScore(highestScore);
        } // Closes a code block.
        if (activeLoop != null) { // Begins a method or constructor with its signature.
            startLevel(currentLevelIndex); // Executes: startLevel(currentLevelIndex);
        } // Closes a code block.
    } // Closes a code block.

    private void onScoreChanged(int score) { // Begins a method or constructor with its signature.
        if (score > highestScore) { // Begins a method or constructor with its signature.
            highestScore = score; // Executes: highestScore = score;
            if (highScoreDatabase != null) { // Begins a method or constructor with its signature.
                highScoreDatabase.saveHighScore(highestScore); // Executes: highScoreDatabase.saveHighScore(highestScore);
            } // Closes a code block.
            if (dashboardScreen != null) { // Begins a method or constructor with its signature.
                dashboardScreen.setHighScore(highestScore); // Executes: dashboardScreen.setHighScore(highestScore);
            } // Closes a code block.
            if (uiManager != null) { // Begins a method or constructor with its signature.
                uiManager.setBestScore(highestScore); // Executes: uiManager.setBestScore(highestScore);
            } // Closes a code block.
        } // Closes a code block.
    } // Closes a code block.

    // ============================
    // Spawn helpers
    // ============================

    private static void forceLayout(Pane root, Node node) { // Begins a method or constructor with its signature.
        if (root == null || node == null) return; // Evaluates a conditional branch.
        root.applyCss(); // Executes: root.applyCss();
        root.layout(); // Executes: root.layout();
    } // Closes a code block.

    private static double safeNodeHeight(Node node) { // Begins a method or constructor with its signature.
        if (node == null) return 50; // Evaluates a conditional branch.
        Bounds b = node.getBoundsInParent();
        double h = (b != null) ? b.getHeight() : 0;
        return h > 1 ? h : 50; // Returns a value from the method.
    } // Closes a code block.

    /**
     * Drops the player down from startY to maxY until it collides with the tileMap.
     * Then returns a Y that places it ON TOP of that collision (not inside it).
     *
     * Works with your existing TileMap collision API via isCollidingBestEffort().
     */
    private double computeSpawnYOnFirstCollision( // Executes: private double computeSpawnYOnFirstCollision(
            TileMap tileMap, // Executes: TileMap tileMap,
            Player player, // Executes: Player player,
            double x, // Executes: double x,
            double startY, // Executes: double startY,
            double maxY, // Executes: double maxY,
            double stepY // Executes: double stepY
    ) { // Executes: ) {
        if (tileMap == null || player == null) return startY; // Evaluates a conditional branch.

        Node n = player.getNode();
        if (n == null) return startY; // Evaluates a conditional branch.

        // start from above
        setPlayerPositionBestEffort(player, x, startY); // Executes: setPlayerPositionBestEffort(player, x, startY);
        forceLayout(root, n); // Executes: forceLayout(root, n);

        boolean foundCollision = false;
        double y = startY;

        // Drop down until collision happens
        for (y = startY; y <= maxY; y += stepY) { // Begins a method or constructor with its signature.
            setPlayerPositionBestEffort(player, x, y); // Executes: setPlayerPositionBestEffort(player, x, y);
            forceLayout(root, n); // Executes: forceLayout(root, n);

            Bounds b = n.getBoundsInParent();
            if (isCollidingBestEffort(tileMap, b)) { // Begins a method or constructor with its signature.
                foundCollision = true; // Executes: foundCollision = true;
                break; // Breaks out of the current loop or switch.
            } // Closes a code block.
        } // Closes a code block.

        if (!foundCollision) { // Begins a method or constructor with its signature.
            // fallback: keep it near the bottom, still safe
            return maxY;
        } // Closes a code block.

        // We are colliding. Move up until we are NOT colliding anymore.
        // This puts the player "on top" of the solid tile instead of inside it.
        for (int i = 0; i < 200; i++) { // Begins a method or constructor with its signature.
            Bounds b = n.getBoundsInParent();
            if (!isCollidingBestEffort(tileMap, b)) { // Begins a method or constructor with its signature.
                break; // Breaks out of the current loop or switch.
            } // Closes a code block.
            setPlayerPositionBestEffort(player, x, getCurrentY(player, n) - 1); // Executes: setPlayerPositionBestEffort(player, x, getCurrentY(player, n) - 1);
            forceLayout(root, n); // Executes: forceLayout(root, n);
        } // Closes a code block.

        // final tiny safety margin
        return getCurrentY(player, n) - 1; // Returns a value from the method.
    } // Closes a code block.

    private double getCurrentY(Player player, Node n) { // Begins a method or constructor with its signature.
        Double currentY = getDoubleIfExists(player, "getY");
        if (currentY == null) currentY = getDoubleIfExists(player, "getPlayerY"); // Evaluates a conditional branch.
        if (currentY != null) return currentY; // Evaluates a conditional branch.
        return n.getLayoutY(); // Returns a value from the method.
    } // Closes a code block.

    private static void setPlayerPositionBestEffort(Player player, double x, double y) { // Begins a method or constructor with its signature.
        if (player == null) return; // Evaluates a conditional branch.

        if (invokeIfExists(player, "setPosition", new Class[]{double.class, double.class}, new Object[]{x, y})) return; // Evaluates a conditional branch.

        boolean xOk = invokeIfExists(player, "setX", new Class[]{double.class}, new Object[]{x}) // Executes: boolean xOk = invokeIfExists(player, "setX", new Class[]{double.class}, new Object[]{x})
                || invokeIfExists(player, "setPlayerX", new Class[]{double.class}, new Object[]{x}); // Executes: || invokeIfExists(player, "setPlayerX", new Class[]{double.class}, new Object[]{x});
        boolean yOk = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{y}) // Executes: boolean yOk = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{y})
                || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{y}); // Executes: || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{y});
        if (xOk || yOk) return; // Evaluates a conditional branch.

        Node n = player.getNode();
        if (n != null) { // Begins a method or constructor with its signature.
            n.setLayoutX(x); // Executes: n.setLayoutX(x);
            n.setLayoutY(y); // Executes: n.setLayoutY(y);
        } // Closes a code block.
    } // Closes a code block.

    private static void resetPlayerMotionBestEffort(Player player) { // Begins a method or constructor with its signature.
        if (player == null) return; // Evaluates a conditional branch.

        if (invokeIfExists(player, "setVelocity", new Class[]{double.class, double.class}, new Object[]{0.0, 0.0})) return; // Evaluates a conditional branch.

        invokeIfExists(player, "resetVelocity", new Class[]{}, new Object[]{}); // Executes: invokeIfExists(player, "resetVelocity", new Class[]{}, new Object[]{});
        invokeIfExists(player, "resetMovement", new Class[]{}, new Object[]{}); // Executes: invokeIfExists(player, "resetMovement", new Class[]{}, new Object[]{});
        invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false}); // Executes: invokeIfExists(player, "setOnGround", new Class[]{boolean.class}, new Object[]{false});
    } // Closes a code block.

    private static void nudgePlayerUpUntilFreeBestEffort(Player player, TileMap tileMap, int maxSteps, double stepY) { // Begins a method or constructor with its signature.
        if (player == null || tileMap == null) return; // Evaluates a conditional branch.

        Node n = player.getNode();
        if (n == null) return; // Evaluates a conditional branch.

        for (int i = 0; i < maxSteps; i++) { // Begins a method or constructor with its signature.
            if (!isCollidingBestEffort(tileMap, n.getBoundsInParent())) { // Begins a method or constructor with its signature.
                return; // Returns a value from the method.
            } // Closes a code block.

            boolean moved = false;
            Double currentY = getDoubleIfExists(player, "getY");
            if (currentY == null) currentY = getDoubleIfExists(player, "getPlayerY"); // Evaluates a conditional branch.
            if (currentY != null) { // Begins a method or constructor with its signature.
                moved = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{currentY - stepY}) // Executes: moved = invokeIfExists(player, "setY", new Class[]{double.class}, new Object[]{currentY - stepY})
                        || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{currentY - stepY}); // Executes: || invokeIfExists(player, "setPlayerY", new Class[]{double.class}, new Object[]{currentY - stepY});
            } // Closes a code block.

            if (!moved) { // Begins a method or constructor with its signature.
                n.setLayoutY(n.getLayoutY() - stepY); // Executes: n.setLayoutY(n.getLayoutY() - stepY);
            } // Closes a code block.
        } // Closes a code block.
    } // Closes a code block.

    private static boolean isCollidingBestEffort(TileMap tileMap, Bounds bounds) { // Begins a method or constructor with its signature.
        if (tileMap == null || bounds == null) return false; // Evaluates a conditional branch.

        Boolean b1 = (Boolean) invokeReturnIfExists(tileMap, "isColliding", // Executes: Boolean b1 = (Boolean) invokeReturnIfExists(tileMap, "isColliding",
                new Class[]{Bounds.class}, new Object[]{bounds}); // Executes: new Class[]{Bounds.class}, new Object[]{bounds});
        if (b1 != null) return b1; // Evaluates a conditional branch.

        Boolean b2 = (Boolean) invokeReturnIfExists(tileMap, "collides", // Executes: Boolean b2 = (Boolean) invokeReturnIfExists(tileMap, "collides",
                new Class[]{double.class, double.class, double.class, double.class}, // Executes: new Class[]{double.class, double.class, double.class, double.class},
                new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()}); // Executes: new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()});
        if (b2 != null) return b2; // Evaluates a conditional branch.

        Boolean b3 = (Boolean) invokeReturnIfExists(tileMap, "isSolidRect", // Executes: Boolean b3 = (Boolean) invokeReturnIfExists(tileMap, "isSolidRect",
                new Class[]{double.class, double.class, double.class, double.class}, // Executes: new Class[]{double.class, double.class, double.class, double.class},
                new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()}); // Executes: new Object[]{bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()});
        if (b3 != null) return b3; // Evaluates a conditional branch.

        return false;
    } // Closes a code block.

    private static boolean invokeIfExists(Object target, String methodName, Class<?>[] sig, Object[] args) { // Begins a method or constructor with its signature.
        try { // Attempts operations that might throw exceptions.
            Method m = target.getClass().getMethod(methodName, sig);
            m.setAccessible(true); // Executes: m.setAccessible(true);
            m.invoke(target, args); // Executes: m.invoke(target, args);
            return true;
        } catch (Exception ignored) { // Begins a method or constructor with its signature.
            return false;
        } // Closes a code block.
    } // Closes a code block.

    private static Object invokeReturnIfExists(Object target, String methodName, Class<?>[] sig, Object[] args) { // Begins a method or constructor with its signature.
        try { // Attempts operations that might throw exceptions.
            Method m = target.getClass().getMethod(methodName, sig);
            m.setAccessible(true); // Executes: m.setAccessible(true);
            return m.invoke(target, args); // Returns a value from the method.
        } catch (Exception ignored) { // Begins a method or constructor with its signature.
            return null;
        } // Closes a code block.
    } // Closes a code block.

    private static Double getDoubleIfExists(Object target, String methodName) { // Begins a method or constructor with its signature.
        try { // Attempts operations that might throw exceptions.
            Method m = target.getClass().getMethod(methodName);
            m.setAccessible(true); // Executes: m.setAccessible(true);
            Object r = m.invoke(target);
            if (r instanceof Number) return ((Number) r).doubleValue(); // Evaluates a conditional branch.
            return null;
        } catch (Exception ignored) { // Begins a method or constructor with its signature.
            return null;
        } // Closes a code block.
    } // Closes a code block.

    // ============================
    // Existing helper methods
    // ============================

    private static List<double[]> jitterSpawns( // Executes: private static List<double[]> jitterSpawns(
            List<double[]> original, // Executes: List<double[]> original,
            TileMap tileMap, // Executes: TileMap tileMap,
            Random rng, // Executes: Random rng,
            double maxOffsetX, // Executes: double maxOffsetX,
            double maxOffsetY, // Executes: double maxOffsetY,
            double itemWidth, // Executes: double itemWidth,
            double itemHeight) { // Executes: double itemHeight) {

        List<double[]> result = new ArrayList<>();
        if (original == null) return result; // Evaluates a conditional branch.

        int worldWidth = tileMap.getWidthInPixels();
        int worldHeight = tileMap.getHeightInPixels();

        for (double[] pos : original) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.

            double dx = (rng.nextDouble() * 2 - 1) * maxOffsetX;
            double dy = (rng.nextDouble() * 2 - 1) * maxOffsetY;

            double nx = pos[0] + dx;
            double ny = pos[1] + dy;

            double margin = 4;
            nx = Math.max(margin, Math.min(worldWidth - itemWidth - margin, nx)); // Executes: nx = Math.max(margin, Math.min(worldWidth - itemWidth - margin, nx));
            ny = Math.max(margin, Math.min(worldHeight - itemHeight - margin, ny)); // Executes: ny = Math.max(margin, Math.min(worldHeight - itemHeight - margin, ny));

            result.add(new double[]{nx, ny}); // Executes: result.add(new double[]{nx, ny});
        } // Closes a code block.
        return result;
    } // Closes a code block.

    private List<double[]> centerWithinTile(List<double[]> original, double itemWidth, double itemHeight) { // Begins a method or constructor with its signature.
        List<double[]> result = new ArrayList<>();
        if (original == null) return result; // Evaluates a conditional branch.

        double offsetX = (TileMap.TILE_SIZE - itemWidth) / 2.0;
        double offsetY = (TileMap.TILE_SIZE - itemHeight) / 2.0;

        for (double[] pos : original) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.
            result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY}); // Executes: result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY});
        } // Closes a code block.
        return result;
    } // Closes a code block.

    private List<double[]> restOnTileTop(List<double[]> original, double itemWidth, double itemHeight) { // Begins a method or constructor with its signature.
        List<double[]> result = new ArrayList<>();
        if (original == null) return result; // Evaluates a conditional branch.

        double offsetX = (TileMap.TILE_SIZE - itemWidth) / 2.0;
        double offsetY = TileMap.TILE_SIZE - itemHeight;

        for (double[] pos : original) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.
            result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY}); // Executes: result.add(new double[]{pos[0] + offsetX, pos[1] + offsetY});
        } // Closes a code block.
        return result;
    } // Closes a code block.

    private void spawnPowerUps(PowerUpManager manager, List<double[]> positions, Random rng) { // Begins a method or constructor with its signature.
        if (manager == null || positions == null) return; // Evaluates a conditional branch.

        PowerUpType[] types = PowerUpType.values();
        for (double[] pos : positions) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.

            PowerUpType type = types[rng.nextInt(types.length)];
            manager.spawn(pos[0], pos[1], PowerUpManager.DEFAULT_SIZE, PowerUpManager.DEFAULT_SIZE, type); // Executes: manager.spawn(pos[0], pos[1], PowerUpManager.DEFAULT_SIZE, PowerUpManager.DEFAULT_SIZE, type);
        } // Closes a code block.
    } // Closes a code block.

    private static List<String> normalizeLevelLines(List<String> raw) { // Begins a method or constructor with its signature.
        int width = raw.stream().mapToInt(String::length).max().orElse(0);
        return raw.stream().map(s -> String.format("%-" + width + "s", s).replace(' ', '.')).toList(); // Returns a value from the method.
    } // Closes a code block.

    private List<String> alignLevelToGround(List<String> rawLines) { // Begins a method or constructor with its signature.
        int tileSize = TileMap.TILE_SIZE;
        int mapHeight = rawLines.size() * tileSize;
        int floorY = WINDOW_HEIGHT - 80;
        int emptyPixels = floorY - mapHeight;
        int rowsToShift = emptyPixels / tileSize;

        if (rowsToShift <= 0) return rawLines; // Evaluates a conditional branch.

        int width = rawLines.get(0).length();
        String emptyRow = ".".repeat(width);

        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < rowsToShift; i++) { // Begins a method or constructor with its signature.
            newLevel.add(emptyRow); // Executes: newLevel.add(emptyRow);
        } // Closes a code block.
        newLevel.addAll(rawLines); // Executes: newLevel.addAll(rawLines);

        return newLevel;
    } // Closes a code block.

    private void refocusScene() { // Begins a method or constructor with its signature.
        if (scene != null && scene.getRoot() != null) { // Begins a method or constructor with its signature.
            scene.getRoot().requestFocus(); // Executes: scene.getRoot().requestFocus();
        } // Closes a code block.
    } // Closes a code block.

    public static void main(String[] args) { // Begins a method or constructor with its signature.
        launch(args); // Executes: launch(args);
    } // Closes a code block.
} // Closes a code block.
