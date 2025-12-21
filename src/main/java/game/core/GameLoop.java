package game.core;

import game.systems.Camera;
import game.systems.GameWorld;
import game.systems.TileMap;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {

    private final Player player;
    private final Ground ground;
    private final InputManager inputManager;

    private final double windowWidth;
    private final double windowHeight;

    private final GameWorld world;
    private final GraphicsContext gc;
    private final TileMap tileMap;
    private final Runnable restartCallback;

    // NEW: for camera syncing with JavaFX nodes
    private final Camera camera;
    private final Group worldLayer;

    private long lastTime = 0;

    public GameLoop(Player player,
                    Ground ground,
                    InputManager inputManager,
                    double windowWidth,
                    double windowHeight,
                    GameWorld world,
                    GraphicsContext gc,
                    TileMap tileMap,
                    Camera camera,
                    Group worldLayer,
                    Runnable restartCallback) {

        this.player = player;
        this.ground = ground;
        this.inputManager = inputManager;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;

        this.world = world;
        this.gc = gc;
        this.tileMap = tileMap;

        this.camera = camera;
        this.worldLayer = worldLayer;
        this.restartCallback = restartCallback;
    }

    @Override
    public void handle(long now) {

        if (lastTime == 0) {
            lastTime = now;
            return;
        }

        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now;

        // avoid huge dt if debugger/lag
        if (dt > 0.05) dt = 0.05;

        // Stop the game loop updates if game is over, but listen for restart
        if (world != null && world.isGameOver()) {
            if (inputManager.isRestartPressed() && restartCallback != null) {
                restartCallback.run();
                inputManager.resetRestart();
            }
            return;
        }

        // ========= INPUT =========
        if (inputManager.isLeftPressed()) player.moveLeft();
        else if (inputManager.isRightPressed()) player.moveRight();
        else player.stopX();

        if (inputManager.isJumpPressed()) {
            player.jump();
            inputManager.resetJump();
        }

        // ========= PHYSICS =========
        // IMPORTANT: do axis-separated collision (move X then resolve, move Y then resolve)
        Physics.moveAndCollide(player, tileMap, dt);

        // Safety: ground pane collision (still ok)
        Physics.checkGroundCollision(player, ground);

        // DO NOT limit to window width; limit to map width
        if (tileMap != null) {
            player.constrainToBounds(tileMap.getWidthInPixels());
        } else {
            player.constrainToBounds(windowWidth);
        }

        player.updateAnimationState();

        // ========= CAMERA FOLLOW =========
        if (camera != null && tileMap != null) {
            double px = player.getPlayerX() + player.getWidth() / 2.0;
            double py = player.getPlayerY() + player.getHeight() / 2.0;

            camera.follow(px, py, 0.12);

            // clamp camera to map
            camera.setPosition(
                    clamp(camera.getX(), 0, tileMap.getWidthInPixels() - camera.getViewWidth()),
                    clamp(camera.getY(), 0, tileMap.getHeightInPixels() - camera.getViewHeight())
            );
        }

        // ========= UPDATE WORLD (coins/powerups/score) =========
        if (world != null) world.update(dt);

        // ========= SYNC JAVA FX NODES WITH CAMERA =========
        // THIS is what makes collisions match what you see.
        if (worldLayer != null && camera != null) {
            worldLayer.setTranslateX(-camera.getOffsetX());
            worldLayer.setTranslateY(-camera.getOffsetY());
        }

        // ========= RENDER CANVAS OVERLAY =========
        if (gc != null && world != null) {
            gc.clearRect(0, 0, windowWidth, windowHeight);
            world.render(gc);
        }
    }

    private static double clamp(double v, double min, double max) {
        if (max < min) return min;
        return Math.max(min, Math.min(max, v));
    }
}
