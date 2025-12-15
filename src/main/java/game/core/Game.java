package game.core;

import game.systems.*;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {

    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    @Override
    public void start(Stage stage) {

        Pane root = new Pane();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: #5C94FC;");

        // ================= LEVELS =================
        List<List<String>> rawLevels = List.of(
                // Level 1 – simple intro
                List.of(
                        "................................................................................................................",
                        "............................................C......................C...........................................",
                        ".....................####..................E......................E.....................####.................",
                        "............C.................................................................................................",
                        "......####..............####..............####..............####..............####..............................",
                        "........C......................E......................C.....................E...............................",
                        "....P....S.......................................................................................................",
                        "###############################........#########...............###############################"
                ),
                // Level 2 – more platforms + coins + enemies


                List.of("................................................................................................................",
                        "................................................................................................................",
                        "................................................................................................................",
                        "........C......................E......................C.....................E...............................",
                        "......####..............####..............####..............####..............####..........................",
                        "........C......................E......................C.....................E...............................",
                        "....P....S.......................................................................................................",
                        "###############################...............................................###############################"
                        ));

        int currentLevelIndex = 0; // change this to try other levels
        List<String> rawLines = rawLevels.get(currentLevelIndex);
        List<String> lines = normalizeLevelLines(rawLines);

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

        Player player = new Player(level.getPlayerSpawnX(), level.getPlayerSpawnY());
        worldLayer.getChildren().add(player.getRectangle());

        // ================= UI =================
        UIManager uiManager = new UIManager(20, 30);
        root.getChildren().add(uiManager.getNode());

        // ================= GAME OVER UI =================
        GameOverScreen gameOverScreen = new GameOverScreen(WINDOW_WIDTH, WINDOW_HEIGHT);
        root.getChildren().add(gameOverScreen.getNode());

        // ================= INPUT =================
        InputManager input = new InputManager();
        input.setupInput(scene);

        // ================= CANVAS =================
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // ================= MANAGERS =================
        CoinManager coinManager = new CoinManager();
        EnemyManager enemyManager = new EnemyManager();
        SpikeManager spikeManager = new SpikeManager();

        // Light randomness: jitter coin and enemy positions a bit so each run feels different
        Random rng = new Random();
        List<double[]> jitteredCoins = jitterSpawns(level.getCoinSpawns(), tileMap, rng, 6, 4);
        List<double[]> jitteredEnemies = jitterSpawns(level.getEnemySpawns(), tileMap, rng, 4, 0);

        coinManager.spawnFrom(jitteredCoins);
        enemyManager.spawnFrom(jitteredEnemies);
        spikeManager.spawnFrom(level.getSpikeSpawns());

        // Create JavaFX nodes for spikes so they are visible in the world layer
        for (double[] sp : level.getSpikeSpawns()) {
            if (sp == null || sp.length < 2) continue;
            double sx = sp[0];
            double sy = sp[1] + TileMap.TILE_SIZE - Spike.SIZE;

            // base
            Rectangle base = new Rectangle(sx + 4, sy + Spike.SIZE - 6, Spike.SIZE - 8, 6);
            base.setFill(Color.web("#3E2723"));

            // triangle
            Polygon poly = new Polygon(
                    sx + Spike.SIZE / 2.0, sy + 4,
                    sx + 2, sy + Spike.SIZE - 6,
                    sx + Spike.SIZE - 2, sy + Spike.SIZE - 6
            );
            poly.setFill(Color.web("#D32F2F"));
            poly.setStroke(Color.web("#5D0E0E"));
            poly.setStrokeWidth(1.5);

            worldLayer.getChildren().addAll(base, poly);
        }

        // Bring the world layer to front (so any JavaFX nodes like spikes are visible above the canvas)
        worldLayer.toFront();

        // Debug: print spike positions so we can verify they were spawned
        System.out.println("Spawned spikes: " + level.getSpikeSpawns().size());

        // ================= WORLD =================
        GameWorld world = new GameWorld(
                tileMap,
                camera,
                coinManager,
                enemyManager,
                spikeManager,
                uiManager,
                player,
                level.getPlayerSpawnX(),
                level.getPlayerSpawnY(),
                gameOverScreen
        );

        // ================= LOOP =================
        GameLoop loop = new GameLoop(
                player,
                ground,
                input,
                WINDOW_WIDTH,
                WINDOW_HEIGHT,
                world,
                gc,
                tileMap,
                camera,
                worldLayer
        );

        loop.start();

        stage.setScene(scene);
        stage.setTitle("Super Mario – Real Game");
        stage.setResizable(false);
        stage.show();
    }

    private static List<double[]> jitterSpawns(List<double[]> original,
                                               TileMap tileMap,
                                               Random rng,
                                               double maxOffsetX,
                                               double maxOffsetY) {
        List<double[]> result = new ArrayList<>();
        if (original == null) return result;

        int worldWidth = tileMap.getWidthInPixels();

        for (double[] pos : original) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            double dx = (rng.nextDouble() * 2 - 1) * maxOffsetX; // [-max,max]
            double dy = (rng.nextDouble() * 2 - 1) * maxOffsetY;

            double nx = Math.max(0, Math.min(worldWidth - TileMap.TILE_SIZE, x + dx));
            double ny = Math.max(0, y + dy);

            result.add(new double[]{nx, ny});
        }
        return result;
    }

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
