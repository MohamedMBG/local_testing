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

    @Override
    public void start(Stage stage) {

        Pane root = new Pane();
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        root.setStyle("-fx-background-color: #5C94FC;");

        // ================= LEVEL =================
        List<String> rawLines = List.of(
                "................................................................................................................",
                "................................................................................................................",
                "........C......................E......................C.....................E...............................",
                "......####..............####..............####..............####..............####..........................",
                "................................................................................................................",
                "....P...........................................................................................................",
                "###############################...............................................###############################"
        );

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

        // ================= INPUT =================
        InputManager input = new InputManager();
        input.setupInput(scene);

        // ================= CANVAS =================
        Canvas canvas = new Canvas(WINDOW_WIDTH, WINDOW_HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // ================= MANAGERS =================
        CoinManager coinManager = new CoinManager();
        coinManager.spawnFrom(level.getCoinSpawns());

        EnemyManager enemyManager = new EnemyManager();
        enemyManager.spawnFrom(level.getEnemySpawns());

        // ================= WORLD =================
        GameWorld world = new GameWorld(
                tileMap,
                camera,
                coinManager,
                enemyManager,
                uiManager,
                player,
                level.getPlayerSpawnX(),
                level.getPlayerSpawnY()
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
