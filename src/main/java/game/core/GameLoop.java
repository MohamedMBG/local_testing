package game.core; // Declares the package for this source file.

import game.systems.Camera;
import game.systems.GameWorld;
import game.systems.TileMap;
import javafx.animation.AnimationTimer;
import javafx.scene.Group;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer { // Defines a class.

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

    public GameLoop(Player player, // Executes: public GameLoop(Player player,
                    Ground ground, // Executes: Ground ground,
                    InputManager inputManager, // Executes: InputManager inputManager,
                    double windowWidth, // Executes: double windowWidth,
                    double windowHeight, // Executes: double windowHeight,
                    GameWorld world, // Executes: GameWorld world,
                    GraphicsContext gc, // Executes: GraphicsContext gc,
                    TileMap tileMap, // Executes: TileMap tileMap,
                    Camera camera, // Executes: Camera camera,
                    Group worldLayer, // Executes: Group worldLayer,
                    Runnable restartCallback) { // Executes: Runnable restartCallback) {

        this.player = player; // Executes: this.player = player;
        this.ground = ground; // Executes: this.ground = ground;
        this.inputManager = inputManager; // Executes: this.inputManager = inputManager;
        this.windowWidth = windowWidth; // Executes: this.windowWidth = windowWidth;
        this.windowHeight = windowHeight; // Executes: this.windowHeight = windowHeight;

        this.world = world; // Executes: this.world = world;
        this.gc = gc; // Executes: this.gc = gc;
        this.tileMap = tileMap; // Executes: this.tileMap = tileMap;

        this.camera = camera; // Executes: this.camera = camera;
        this.worldLayer = worldLayer; // Executes: this.worldLayer = worldLayer;
        this.restartCallback = restartCallback; // Executes: this.restartCallback = restartCallback;
    } // Closes a code block.

    @Override // Applies an annotation to the following element.
    public void handle(long now) { // Begins a method or constructor with its signature.

        if (lastTime == 0) { // Begins a method or constructor with its signature.
            lastTime = now; // Executes: lastTime = now;
            return; // Returns a value from the method.
        } // Closes a code block.

        double dt = (now - lastTime) / 1_000_000_000.0;
        lastTime = now; // Executes: lastTime = now;

        // avoid huge dt if debugger/lag
        if (dt > 0.05) dt = 0.05; // Evaluates a conditional branch.

        // Stop the game loop updates if game is over, but listen for restart
        if (world != null && world.isGameOver()) { // Begins a method or constructor with its signature.
            if (inputManager.isRestartPressed() && restartCallback != null) { // Begins a method or constructor with its signature.
                restartCallback.run(); // Executes: restartCallback.run();
                inputManager.resetRestart(); // Executes: inputManager.resetRestart();
            } // Closes a code block.
            return; // Returns a value from the method.
        } // Closes a code block.

        // ========= INPUT =========
        if (inputManager.isLeftPressed()) player.moveLeft(); // Evaluates a conditional branch.
        else if (inputManager.isRightPressed()) player.moveRight(); // Evaluates an alternative conditional branch.
        else player.stopX(); // Handles the fallback branch.

        if (inputManager.isJumpPressed()) { // Begins a method or constructor with its signature.
            player.jump(); // Executes: player.jump();
            inputManager.resetJump(); // Executes: inputManager.resetJump();
        } // Closes a code block.

        // ========= PHYSICS =========
        // IMPORTANT: do axis-separated collision (move X then resolve, move Y then resolve)
        Physics.moveAndCollide(player, tileMap, dt); // Executes: Physics.moveAndCollide(player, tileMap, dt);

        // Safety: ground pane collision (still ok)
        Physics.checkGroundCollision(player, ground); // Executes: Physics.checkGroundCollision(player, ground);

        // DO NOT limit to window width; limit to map width
        if (tileMap != null) { // Begins a method or constructor with its signature.
            player.constrainToBounds(tileMap.getWidthInPixels()); // Executes: player.constrainToBounds(tileMap.getWidthInPixels());
        } else { // Executes: } else {
            player.constrainToBounds(windowWidth); // Executes: player.constrainToBounds(windowWidth);
        } // Closes a code block.

        // ========= CAMERA FOLLOW =========
        if (camera != null && tileMap != null) { // Begins a method or constructor with its signature.
            double px = player.getPlayerX() + player.getWidth() / 2.0;
            double py = player.getPlayerY() + player.getHeight() / 2.0;

            camera.follow(px, py, 0.12); // Executes: camera.follow(px, py, 0.12);

            // clamp camera to map
            camera.setPosition( // Executes: camera.setPosition(
                    clamp(camera.getX(), 0, tileMap.getWidthInPixels() - camera.getViewWidth()), // Executes: clamp(camera.getX(), 0, tileMap.getWidthInPixels() - camera.getViewWidth()),
                    clamp(camera.getY(), 0, tileMap.getHeightInPixels() - camera.getViewHeight()) // Executes: clamp(camera.getY(), 0, tileMap.getHeightInPixels() - camera.getViewHeight())
            ); // Executes: );
        } // Closes a code block.

        player.tick(dt); // Executes: player.tick(dt);

        // ========= UPDATE WORLD (coins/powerups/score) =========
        if (world != null) world.update(dt); // Evaluates a conditional branch.

        // ========= SYNC JAVA FX NODES WITH CAMERA =========
        // THIS is what makes collisions match what you see.
        if (worldLayer != null && camera != null) { // Begins a method or constructor with its signature.
            worldLayer.setTranslateX(-camera.getOffsetX()); // Executes: worldLayer.setTranslateX(-camera.getOffsetX());
            worldLayer.setTranslateY(-camera.getOffsetY()); // Executes: worldLayer.setTranslateY(-camera.getOffsetY());
        } // Closes a code block.

        // ========= RENDER CANVAS OVERLAY =========
        if (gc != null && world != null) { // Begins a method or constructor with its signature.
            gc.clearRect(0, 0, windowWidth, windowHeight); // Executes: gc.clearRect(0, 0, windowWidth, windowHeight);
            world.render(gc); // Executes: world.render(gc);
        } // Closes a code block.
    } // Closes a code block.

    private static double clamp(double v, double min, double max) { // Begins a method or constructor with its signature.
        if (max < min) return min; // Evaluates a conditional branch.
        return Math.max(min, Math.min(max, v)); // Returns a value from the method.
    } // Closes a code block.
} // Closes a code block.
