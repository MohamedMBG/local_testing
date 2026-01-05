package game.systems; // Declares the package for this source file.

import game.core.Player;
import game.utils.Theme;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import java.util.function.Consumer;

public class GameWorld { // Defines a class.

    private final TileMap tileMap; // The tile map (solid tiles, map size, collision grid). // Executes: private final TileMap tileMap; // The tile map (solid tiles, map size, collision grid).
    private final Camera camera; // The camera that follows the player and defines the visible view. // Executes: private final Camera camera; // The camera that follows the player and defines the visible view.
    private final CoinManager coinManager; // Manager responsible for coins (spawning, update, rendering, collecting). // Executes: private final CoinManager coinManager; // Manager responsible for coins (spawning, update, rendering, collecting).
    private final PowerUpManager powerUpManager; // Manager responsible for power-ups (like STAR). // Executes: private final PowerUpManager powerUpManager; // Manager responsible for power-ups (like STAR).
    private final EnemyManager enemyManager; // Manager responsible for enemies (movement, collisions, rendering). // Executes: private final EnemyManager enemyManager; // Manager responsible for enemies (movement, collisions, rendering).
    private final SpikeManager spikeManager; // Manager responsible for spike hazards. // Executes: private final SpikeManager spikeManager; // Manager responsible for spike hazards.
    private final UIManager uiManager; // UI manager for score/coins/lives display. // Executes: private final UIManager uiManager; // UI manager for score/coins/lives display.
    private final Player player; // The main player object. // Executes: private final Player player; // The main player object.
    private final GameOverScreen gameOverScreen; // Screen shown when lives reach 0. // Executes: private final GameOverScreen gameOverScreen; // Screen shown when lives reach 0.
    private Theme theme; // Current visual theme (colors for background, tiles, power-up glow...). // Executes: private Theme theme; // Current visual theme (colors for background, tiles, power-up glow...).
    private Color goalFlagColor; // Cached color used to draw the goal flag (depends on theme). // Executes: private Color goalFlagColor; // Cached color used to draw the goal flag (depends on theme).

    // ---- Game state ----
    private int score = 0;
    private int coins = 0;
    private int lives = 3;
    private boolean gameOver = false;

    // Respawn point
    private final double spawnX;
    private final double spawnY;

    // Simple level end goal position (near the right side of the map)
    private final double goalX;

    // Temporary invincibility timer from star power-ups
    // This is the "protection" mechanic: while > 0, player cannot lose lives from enemies/spikes.
    private double invincibilityTimer = 0;
    private final Consumer<Integer> scoreListener;

    public GameWorld( // Executes: public GameWorld(
                      TileMap tileMap, // Executes: TileMap tileMap,
                      Camera camera, // Executes: Camera camera,
                      CoinManager coinManager, // Executes: CoinManager coinManager,
                      PowerUpManager powerUpManager, // Executes: PowerUpManager powerUpManager,
                      EnemyManager enemyManager, // Executes: EnemyManager enemyManager,
                      SpikeManager spikeManager, // Executes: SpikeManager spikeManager,
                      UIManager uiManager, // Executes: UIManager uiManager,
                      Player player, // Executes: Player player,
                      double spawnX, // Executes: double spawnX,
                      double spawnY, // Executes: double spawnY,
                      GameOverScreen gameOverScreen, // Executes: GameOverScreen gameOverScreen,
                      Theme theme, // Executes: Theme theme,
                      Consumer<Integer> scoreListener // Score callback reference. // Executes: Consumer<Integer> scoreListener // Score callback reference.
    ) { // Executes: ) {
        this.tileMap = tileMap; // Store tile map in this world. // Executes: this.tileMap = tileMap; // Store tile map in this world.
        this.camera = camera; // Store camera in this world. // Executes: this.camera = camera; // Store camera in this world.
        this.coinManager = coinManager; // Store coin manager. // Executes: this.coinManager = coinManager; // Store coin manager.
        this.powerUpManager = powerUpManager; // Store power-up manager (might be null). // Executes: this.powerUpManager = powerUpManager; // Store power-up manager (might be null).
        this.enemyManager = enemyManager; // Store enemy manager. // Executes: this.enemyManager = enemyManager; // Store enemy manager.
        this.spikeManager = spikeManager; // Store spike manager. // Executes: this.spikeManager = spikeManager; // Store spike manager.
        this.uiManager = uiManager; // Store UI manager. // Executes: this.uiManager = uiManager; // Store UI manager.
        this.player = player; // Store player. // Executes: this.player = player; // Store player.
        this.spawnX = spawnX; // Store respawn X. // Executes: this.spawnX = spawnX; // Store respawn X.
        this.spawnY = spawnY; // Store respawn Y. // Executes: this.spawnY = spawnY; // Store respawn Y.
        this.gameOverScreen = gameOverScreen; // Store game over screen. // Executes: this.gameOverScreen = gameOverScreen; // Store game over screen.
        this.theme = theme; // Store theme. // Executes: this.theme = theme; // Store theme.
        this.goalFlagColor = theme.getTileHighlight(); // Choose a nice color for the goal flag based on theme. // Executes: this.goalFlagColor = theme.getTileHighlight(); // Choose a nice color for the goal flag based on theme.
        this.scoreListener = scoreListener; // Executes: this.scoreListener = scoreListener;

        coinManager.setTheme(theme); // Executes: coinManager.setTheme(theme);
        if (powerUpManager != null) { // Begins a method or constructor with its signature.
            powerUpManager.setTheme(theme); // Executes: powerUpManager.setTheme(theme);
        } // Closes a code block.
        enemyManager.setTheme(theme); // Executes: enemyManager.setTheme(theme);
        spikeManager.setTheme(theme); // Executes: spikeManager.setTheme(theme);

        // Place goal a bit before the very end of the map
        this.goalX = tileMap.getWidthInPixels() - 2 * TileMap.TILE_SIZE; // Executes: this.goalX = tileMap.getWidthInPixels() - 2 * TileMap.TILE_SIZE;
    } // Closes a code block.

    // -------------------------------------------------
    // UPDATE
    // -------------------------------------------------
    public void update(double dt) { // Update world logic. dt = delta time (seconds since last frame). // Executes: public void update(double dt) { // Update world logic. dt = delta time (seconds since last frame).
        if (gameOver) { // If game is already over... // Evaluates a conditional branch.
            // Stop updating once game is over
            return; // ...exit early so nothing moves/changes anymore. // Returns a value from the method.
        } // Closes a code block.

        // count down invincibility time every frame.
        if (invincibilityTimer > 0) { // If player currently has invincibility active... // Evaluates a conditional branch.
            invincibilityTimer = Math.max(0, invincibilityTimer - dt); // Decrease timer and never go below 0. // Executes: invincibilityTimer = Math.max(0, invincibilityTimer - dt); // Decrease timer and never go below 0.
        } // Closes a code block.

        // ===== Camera follow player (center) =====
        double cx = player.getPlayerX() + player.getWidth() / 2.0; // Player center X (for smooth following). // Executes: double cx = player.getPlayerX() + player.getWidth() / 2.0; // Player center X (for smooth following).
        double cy = player.getPlayerY() + player.getHeight() / 2.0; // Player center Y. // Executes: double cy = player.getPlayerY() + player.getHeight() / 2.0; // Player center Y.

        camera.follow(cx, cy, 0.12); // Move camera toward player center (0.12 = smoothing factor). // Executes: camera.follow(cx, cy, 0.12); // Move camera toward player center (0.12 = smoothing factor).
        camera.clampToMap( // Prevent camera from leaving map bounds. // Executes: camera.clampToMap( // Prevent camera from leaving map bounds.
                tileMap.getWidthInPixels(), // Map width in pixels. // Executes: tileMap.getWidthInPixels(), // Map width in pixels.
                tileMap.getHeightInPixels() // Map height in pixels. // Executes: tileMap.getHeightInPixels() // Map height in pixels.
        ); // Executes: );

        // ===== Coins =====
        int collected = coinManager.updateAndCountCollected( // Update coins and detect collisions with player. // Executes: int collected = coinManager.updateAndCountCollected( // Update coins and detect collisions with player.
                player.getPlayerX(), // Player X. // Executes: player.getPlayerX(), // Player X.
                player.getPlayerY(), // Player Y. // Executes: player.getPlayerY(), // Player Y.
                player.getWidth(), // Player width. // Executes: player.getWidth(), // Player width.
                player.getHeight() // Player height. // Executes: player.getHeight() // Player height.
        ); // Executes: );

        if (collected > 0) { // If any coin(s) were collected this frame... // Evaluates a conditional branch.
            coins += collected; // Increase coin count. // Executes: coins += collected; // Increase coin count.
            score += collected * 10; // Increase score (10 points per coin). // Executes: score += collected * 10; // Increase score (10 points per coin).
        } // Closes a code block.

        // ===== Enemies =====
        boolean playerHit = enemyManager.update(dt, player, tileMap); // Update enemies and check if player got hit. // Executes: boolean playerHit = enemyManager.update(dt, player, tileMap); // Update enemies and check if player got hit.
        //  STAR PROTECTION: only lose life if hit AND not invincible.
        if (playerHit && !isInvincible()) { // If enemy hit player AND STAR protection is NOT active... // Evaluates a conditional branch.
            loseLife(); // Decrease life and respawn or end game. // Executes: loseLife(); // Decrease life and respawn or end game.
        } // Closes a code block.

        // ===== Spikes =====
        boolean playerTouchedSpike = spikeManager.checkPlayerCollision( // Check collision between player and spikes. // Executes: boolean playerTouchedSpike = spikeManager.checkPlayerCollision( // Check collision between player and spikes.
                player.getPlayerX(), // Player X. // Executes: player.getPlayerX(), // Player X.
                player.getPlayerY(), // Player Y. // Executes: player.getPlayerY(), // Player Y.
                player.getWidth(), // Player width. // Executes: player.getWidth(), // Player width.
                player.getHeight() // Player height. // Executes: player.getHeight() // Player height.
        ); // Executes: );
        //  STAR PROTECTION: spikes also cannot hurt you if invincible.
        if (playerTouchedSpike && !isInvincible()) { // If spike touched player AND STAR protection is NOT active... // Evaluates a conditional branch.
            loseLife(); // Decrease life and respawn or end game. // Executes: loseLife(); // Decrease life and respawn or end game.
        } // Closes a code block.

        if (powerUpManager != null) { // If we have power-ups enabled in this world... // Evaluates a conditional branch.
            for (PowerUpType collected2 : powerUpManager.updateAndGetCollected( // Update power-ups and get what was collected. // Begins a loop over a range or collection.
                    player.getPlayerX(), // Player X. // Executes: player.getPlayerX(), // Player X.
                    player.getPlayerY(), // Player Y. // Executes: player.getPlayerY(), // Player Y.
                    player.getWidth(), // Player width. // Executes: player.getWidth(), // Player width.
                    player.getHeight() // Player height. // Executes: player.getHeight() // Player height.
            )) { // Executes: )) {
                applyPowerUp(collected2); // Apply each collected power-up effect (STAR sets invincibilityTimer). // Executes: applyPowerUp(collected2); // Apply each collected power-up effect (STAR sets invincibilityTimer).
            } // Closes a code block.
        } // Closes a code block.

        // ===== Level end (goal) =====
        if (player.getPlayerX() + player.getWidth() >= goalX) { // If player's right side reaches goal line... // Evaluates a conditional branch.
            // For now just log; here you would trigger loading the next level
            System.out.println("Level complete! (Hook this to load the next level.)"); // Debug message. // Executes: System.out.println("Level complete! (Hook this to load the next level.)"); // Debug message.
        } // Closes a code block.

        // ===== UI =====
        uiManager.setAll(score, coins, lives); // Update UI values. // Executes: uiManager.setAll(score, coins, lives); // Update UI values.
        uiManager.setThemeName(theme.getDisplayName()); // Show the theme name (optional). // Executes: uiManager.setThemeName(theme.getDisplayName()); // Show the theme name (optional).
        uiManager.setTheme(theme); // Give UI the theme so it matches the world colors. // Executes: uiManager.setTheme(theme); // Give UI the theme so it matches the world colors.
        if (scoreListener != null) { // If someone is listening for score updates... // Evaluates a conditional branch.
            scoreListener.accept(score); // Notify them with the new score. // Executes: scoreListener.accept(score); // Notify them with the new score.
        } // Closes a code block.
    } // Closes a code block.

    // -------------------------------------------------
    // RENDER
    // -------------------------------------------------
    public void render(GraphicsContext gc) { // Draw the world to the canvas. // Executes: public void render(GraphicsContext gc) { // Draw the world to the canvas.
        if (gc == null) return; // Safety: if GraphicsContext is missing, do nothing. // Evaluates a conditional branch.

        renderBackdrop(gc); // Draw background first (sky gradient, sparkles, etc.). // Executes: renderBackdrop(gc); // Draw background first (sky gradient, sparkles, etc.).

        // ---- 1) Draw solid tiles so collisions / obstacles are visible ----
        renderTiles(gc); // Draw the collidable map tiles. // Executes: renderTiles(gc); // Draw the collidable map tiles.

        // ---- 2) Draw collectibles and enemies on top of tiles ----
        coinManager.render(gc, camera); // Draw coins using camera offset. // Executes: coinManager.render(gc, camera); // Draw coins using camera offset.
        if (powerUpManager != null) { // If power-ups exist... // Evaluates a conditional branch.
            powerUpManager.render(gc, camera); // Draw power-ups (including STAR). // Executes: powerUpManager.render(gc, camera); // Draw power-ups (including STAR).
        } // Closes a code block.
        enemyManager.render(gc, camera, theme); // Draw enemies with theme-based colors. // Executes: enemyManager.render(gc, camera, theme); // Draw enemies with theme-based colors.
        spikeManager.render(gc, camera); // Draw spikes. // Executes: spikeManager.render(gc, camera); // Draw spikes.

        // ---- 3) Draw goal flag ----
        renderGoal(gc); // Draw end-of-level goal. // Executes: renderGoal(gc); // Draw end-of-level goal.
    } // Closes a code block.

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------
    private void loseLife() { // Called when player takes damage. // Executes: private void loseLife() { // Called when player takes damage.
        lives--; // Reduce lives by 1. // Executes: lives--; // Reduce lives by 1.
        if (lives <= 0) { // If no lives left... // Evaluates a conditional branch.
            lives = 0; // Clamp to 0 (no negative lives). // Executes: lives = 0; // Clamp to 0 (no negative lives).
            gameOver = true; // Mark game as ended. // Executes: gameOver = true; // Mark game as ended.
            if (gameOverScreen != null) { // If gameOverScreen exists... // Evaluates a conditional branch.
                gameOverScreen.show(); // Show game over screen. // Executes: gameOverScreen.show(); // Show game over screen.
            } // Closes a code block.
        } else { // Otherwise, player still has lives remaining... // Executes: } else { // Otherwise, player still has lives remaining...
            respawnPlayer(); // Respawn the player at spawnX/spawnY. // Executes: respawnPlayer(); // Respawn the player at spawnX/spawnY.
        } // Closes a code block.
    } // Closes a code block.

    private void respawnPlayer() { // Reset player to spawn point. // Executes: private void respawnPlayer() { // Reset player to spawn point.
        player.setPlayerX(spawnX); // Move player X to spawn. // Executes: player.setPlayerX(spawnX); // Move player X to spawn.
        player.setPlayerY(spawnY); // Move player Y to spawn. // Executes: player.setPlayerY(spawnY); // Move player Y to spawn.
        player.setVelocityX(0); // Reset horizontal speed so player doesn't keep sliding. // Executes: player.setVelocityX(0); // Reset horizontal speed so player doesn't keep sliding.
        player.setVelocityY(0); // Reset vertical speed so player doesn't keep falling/jumping. // Executes: player.setVelocityY(0); // Reset vertical speed so player doesn't keep falling/jumping.
        player.setOnGround(false); // Player is not considered grounded immediately after respawn. // Executes: player.setOnGround(false); // Player is not considered grounded immediately after respawn.
    } // Closes a code block.

    public boolean isGameOver() { // Public getter: is game over? // Executes: public boolean isGameOver() { // Public getter: is game over?
        return gameOver; // Return the current gameOver state. // Returns a value from the method.
    } // Closes a code block.

    private void applyPowerUp(PowerUpType type) { // Apply effects depending on power-up type. // Executes: private void applyPowerUp(PowerUpType type) { // Apply effects depending on power-up type.
        switch (type) { // Switch on the collected power-up. // Starts a switch statement.
            case MUSHROOM -> score += 100; // Mushroom gives points (placeholder effect). // Defines a switch case branch.
            case FLOWER -> score += 150; // Flower gives points (placeholder effect). // Defines a switch case branch.
            //  STAR PROTECTION: STAR sets invincibility time (seconds).
            case STAR -> invincibilityTimer = 6.0; // Make player invincible for 6 seconds. // Defines a switch case branch.
            case LIFE -> lives++; // Extra life. // Defines a switch case branch.
        } // Closes a code block.
    } // Closes a code block.

    private boolean isInvincible() { // Helper: checks if STAR protection is active. // Executes: private boolean isInvincible() { // Helper: checks if STAR protection is active.
        return invincibilityTimer > 0; // Returns a value from the method.
    } // Closes a code block.

    // -------------------------------------------------
    // Getters (optional)
    // -------------------------------------------------
    public int getScore() { // Public getter for score. // Executes: public int getScore() { // Public getter for score.
        return score; // Return score. // Returns a value from the method.
    } // Closes a code block.

    public int getCoins() { // Public getter for coins. // Executes: public int getCoins() { // Public getter for coins.
        return coins; // Return coins. // Returns a value from the method.
    } // Closes a code block.

    public int getLives() { // Public getter for lives. // Executes: public int getLives() { // Public getter for lives.
        return lives; // Return lives. // Returns a value from the method.
    } // Closes a code block.

    // -------------------------------------------------
    // Internal: tile rendering (simple but clear)
    // -------------------------------------------------

    /**
     * Renders the solid tiles of the tile map using a pleasant Mario-like palette
     * so that all collidable obstacles and platforms are clearly visible.
     */
    private void renderTiles(GraphicsContext gc) { // Draw only solid tiles visible in the camera view. // Executes: private void renderTiles(GraphicsContext gc) { // Draw only solid tiles visible in the camera view.
        final int tileSize = TileMap.TILE_SIZE; // Pixel size of one tile. // Executes: final int tileSize = TileMap.TILE_SIZE; // Pixel size of one tile.

        Color base = theme.getTileBase(); // Base tile color from theme. // Executes: Color base = theme.getTileBase(); // Base tile color from theme.
        Color highlight = theme.getTileHighlight(); // Highlight color from theme. // Executes: Color highlight = theme.getTileHighlight(); // Highlight color from theme.
        Color shadow = theme.getTileShadow(); // Shadow color from theme. // Executes: Color shadow = theme.getTileShadow(); // Shadow color from theme.
        Color accent = theme.getTileAccent(); // Accent color from theme. // Executes: Color accent = theme.getTileAccent(); // Accent color from theme.

        int tilesX = tileMap.getWidthInTiles(); // Number of tiles horizontally. // Executes: int tilesX = tileMap.getWidthInTiles(); // Number of tiles horizontally.
        int tilesY = tileMap.getHeightInTiles(); // Number of tiles vertically. // Executes: int tilesY = tileMap.getHeightInTiles(); // Number of tiles vertically.

        // Only draw tiles that fall inside the camera view for efficiency
        int startTileX = Math.max(0, (int) (camera.getOffsetX() / tileSize) - 1); // First visible tile X. // Executes: int startTileX = Math.max(0, (int) (camera.getOffsetX() / tileSize) - 1); // First visible tile X.
        int endTileX = Math.min(tilesX, (int) ((camera.getOffsetX() + camera.getViewWidth()) / tileSize) + 2); // Last visible tile X. // Executes: int endTileX = Math.min(tilesX, (int) ((camera.getOffsetX() + camera.getViewWidth()) / tileSize) + 2); // Last visible tile X.
        int startTileY = Math.max(0, (int) (camera.getOffsetY() / tileSize) - 1); // First visible tile Y. // Executes: int startTileY = Math.max(0, (int) (camera.getOffsetY() / tileSize) - 1); // First visible tile Y.
        int endTileY = Math.min(tilesY, (int) ((camera.getOffsetY() + camera.getViewHeight()) / tileSize) + 2); // Last visible tile Y. // Executes: int endTileY = Math.min(tilesY, (int) ((camera.getOffsetY() + camera.getViewHeight()) / tileSize) + 2); // Last visible tile Y.

        for (int ty = startTileY; ty < endTileY; ty++) { // Loop through visible tile rows. // Begins a loop over a range or collection.
            for (int tx = startTileX; tx < endTileX; tx++) { // Loop through visible tile columns. // Begins a loop over a range or collection.
                if (!tileMap.isSolidTile(tx, ty)) continue; // Skip non-solid tiles. // Evaluates a conditional branch.

                double worldX = tx * tileSize; // Tile world X in pixels. // Executes: double worldX = tx * tileSize; // Tile world X in pixels.
                double worldY = ty * tileSize; // Tile world Y in pixels. // Executes: double worldY = ty * tileSize; // Tile world Y in pixels.
                double screenX = worldX - camera.getOffsetX(); // Convert world X to screen X. // Executes: double screenX = worldX - camera.getOffsetX(); // Convert world X to screen X.
                double screenY = worldY - camera.getOffsetY(); // Convert world Y to screen Y. // Executes: double screenY = worldY - camera.getOffsetY(); // Convert world Y to screen Y.

                // Background bark block
                gc.setFill(base); // Set fill color to base. // Executes: gc.setFill(base); // Set fill color to base.
                gc.fillRoundRect(screenX, screenY, tileSize, tileSize, 10, 10); // Draw tile base shape. // Executes: gc.fillRoundRect(screenX, screenY, tileSize, tileSize, 10, 10); // Draw tile base shape.

                // Main branch running across the tile
                double branchHeight = tileSize * 0.35; // Branch height proportion. // Executes: double branchHeight = tileSize * 0.35; // Branch height proportion.
                double branchY = screenY + tileSize * 0.4; // Branch Y position. // Executes: double branchY = screenY + tileSize * 0.4; // Branch Y position.
                gc.setFill(highlight); // Set highlight fill. // Executes: gc.setFill(highlight); // Set highlight fill.
                gc.fillRoundRect(screenX - 2, branchY, tileSize + 4, branchHeight, 18, 18); // Draw branch highlight. // Executes: gc.fillRoundRect(screenX - 2, branchY, tileSize + 4, branchHeight, 18, 18); // Draw branch highlight.

                // Deeper bark shadows to add depth
                gc.setFill(shadow); // Set shadow fill. // Executes: gc.setFill(shadow); // Set shadow fill.
                gc.fillRoundRect(screenX - 2, branchY + branchHeight * 0.45, tileSize + 4, branchHeight * 0.35, 18, 18); // Draw shadow. // Executes: gc.fillRoundRect(screenX - 2, branchY + branchHeight * 0.45, tileSize + 4, branchHeight * 0.35, 18, 18); // Draw shadow.

                // Little twigs so collisions look like branches sticking out
                gc.setStroke(accent); // Set stroke color to accent. // Executes: gc.setStroke(accent); // Set stroke color to accent.
                gc.setLineWidth(4); // Set twig thickness. // Executes: gc.setLineWidth(4); // Set twig thickness.
                gc.strokeLine(screenX + tileSize * 0.3, branchY + branchHeight * 0.25, // Twig 1 start. // Executes: gc.strokeLine(screenX + tileSize * 0.3, branchY + branchHeight * 0.25, // Twig 1 start.
                        screenX + tileSize * 0.15, branchY - tileSize * 0.15); // Twig 1 end. // Executes: screenX + tileSize * 0.15, branchY - tileSize * 0.15); // Twig 1 end.
                gc.strokeLine(screenX + tileSize * 0.65, branchY + branchHeight * 0.35, // Twig 2 start. // Executes: gc.strokeLine(screenX + tileSize * 0.65, branchY + branchHeight * 0.35, // Twig 2 start.
                        screenX + tileSize * 0.9, branchY - tileSize * 0.1); // Twig 2 end. // Executes: screenX + tileSize * 0.9, branchY - tileSize * 0.1); // Twig 2 end.

                // Knots to break up the surface
                gc.setFill(accent); // Set fill to accent for knots. // Executes: gc.setFill(accent); // Set fill to accent for knots.
                gc.fillOval(screenX + tileSize * 0.55, branchY + branchHeight * 0.2, tileSize * 0.18, tileSize * 0.18); // Knot 1. // Executes: gc.fillOval(screenX + tileSize * 0.55, branchY + branchHeight * 0.2, tileSize * 0.18, tileSize * 0.18); // Knot 1.
                gc.fillOval(screenX + tileSize * 0.25, branchY + branchHeight * 0.5, tileSize * 0.14, tileSize * 0.14); // Knot 2. // Executes: gc.fillOval(screenX + tileSize * 0.25, branchY + branchHeight * 0.5, tileSize * 0.14, tileSize * 0.14); // Knot 2.
            } // Closes a code block.
        } // Closes a code block.
    } // Closes a code block.

    private void renderGoal(GraphicsContext gc) { // Draw the goal pole and flag. // Executes: private void renderGoal(GraphicsContext gc) { // Draw the goal pole and flag.
        double poleX = goalX - camera.getOffsetX(); // Convert goal world X to screen X. // Executes: double poleX = goalX - camera.getOffsetX(); // Convert goal world X to screen X.
        double baseY = tileMap.getHeightInPixels() - TileMap.TILE_SIZE - camera.getOffsetY(); // Base Y near ground. // Executes: double baseY = tileMap.getHeightInPixels() - TileMap.TILE_SIZE - camera.getOffsetY(); // Base Y near ground.

        // Simple goal pole + flag
        gc.setStroke(Color.WHITE); // Pole color. // Executes: gc.setStroke(Color.WHITE); // Pole color.
        gc.setLineWidth(3); // Pole thickness. // Executes: gc.setLineWidth(3); // Pole thickness.
        gc.strokeLine(poleX, baseY - 6 * TileMap.TILE_SIZE, poleX, baseY); // Draw pole line. // Executes: gc.strokeLine(poleX, baseY - 6 * TileMap.TILE_SIZE, poleX, baseY); // Draw pole line.

        gc.setFill(Color.web("#FFEB3B")); // Yellow flag triangle. // Executes: gc.setFill(Color.web("#FFEB3B")); // Yellow flag triangle.
        gc.fillPolygon( // Draw triangle flag. // Executes: gc.fillPolygon( // Draw triangle flag.
                new double[]{poleX, poleX + 24, poleX}, // X points. // Executes: new double[]{poleX, poleX + 24, poleX}, // X points.
                new double[]{baseY - 6 * TileMap.TILE_SIZE, baseY - 6 * TileMap.TILE_SIZE + 12, baseY - 6 * TileMap.TILE_SIZE + 24}, // Y points. // Executes: new double[]{baseY - 6 * TileMap.TILE_SIZE, baseY - 6 * TileMap.TILE_SIZE + 12, baseY - 6 * TileMap.TILE_SIZE + 24}, // Y points.
                3 // Number of points. // Executes: 3 // Number of points.
        ); // Executes: );
        gc.setFill(goalFlagColor); // The little top cap uses theme highlight color. // Executes: gc.setFill(goalFlagColor); // The little top cap uses theme highlight color.
        gc.fillOval(poleX - 4, baseY - 6 * TileMap.TILE_SIZE - 6, 12, 12); // Draw cap circle. // Executes: gc.fillOval(poleX - 4, baseY - 6 * TileMap.TILE_SIZE - 6, 12, 12); // Draw cap circle.
    } // Closes a code block.

    public void setTheme(Theme theme) { // Change the theme at runtime. // Executes: public void setTheme(Theme theme) { // Change the theme at runtime.
        this.theme = theme; // Save new theme. // Executes: this.theme = theme; // Save new theme.
        this.goalFlagColor = theme.getTileHighlight(); // Update goal flag color to match. // Executes: this.goalFlagColor = theme.getTileHighlight(); // Update goal flag color to match.
    } // Closes a code block.

    private void renderBackdrop(GraphicsContext gc) { // Draw background gradient + parallax effects. // Executes: private void renderBackdrop(GraphicsContext gc) { // Draw background gradient + parallax effects.
        double viewWidth = camera.getViewWidth(); // Visible width. // Executes: double viewWidth = camera.getViewWidth(); // Visible width.
        double viewHeight = camera.getViewHeight(); // Visible height. // Executes: double viewHeight = camera.getViewHeight(); // Visible height.

        LinearGradient gradient = new LinearGradient( // Create a vertical gradient for the sky. // Executes: LinearGradient gradient = new LinearGradient( // Create a vertical gradient for the sky.
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE, // From top to bottom, proportional coords, no repeat. // Executes: 0, 0, 0, 1, true, CycleMethod.NO_CYCLE, // From top to bottom, proportional coords, no repeat.
                new Stop(0, theme.getBackground()), // Top color. // Executes: new Stop(0, theme.getBackground()), // Top color.
                new Stop(1, theme.getBackgroundBottom()) // Bottom color. // Executes: new Stop(1, theme.getBackgroundBottom()) // Bottom color.
        ); // Executes: );
        gc.setFill(gradient); // Set fill to gradient. // Executes: gc.setFill(gradient); // Set fill to gradient.
        gc.fillRect(0, 0, viewWidth, viewHeight); // Paint the whole screen. // Executes: gc.fillRect(0, 0, viewWidth, viewHeight); // Paint the whole screen.

        // Distant glow behind the level to make silhouettes pop
        gc.setFill(theme.getBackgroundBottom().interpolate(theme.getGround(), 0.35)); // Mixed color. // Executes: gc.setFill(theme.getBackgroundBottom().interpolate(theme.getGround(), 0.35)); // Mixed color.
        gc.fillRect(0, viewHeight * 0.72, viewWidth, viewHeight * 0.3); // Draw glow band. // Executes: gc.fillRect(0, viewHeight * 0.72, viewWidth, viewHeight * 0.3); // Draw glow band.

        // Soft parallax streaks to convey motion
        double parallax = (camera.getOffsetX() / 3.5) % viewWidth; // Create slow-moving offset for parallax. // Executes: double parallax = (camera.getOffsetX() / 3.5) % viewWidth; // Create slow-moving offset for parallax.
        gc.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.22)); // Semi-transparent streak color. // Executes: gc.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.22)); // Semi-transparent streak color.
        gc.setLineWidth(2.2); // Streak thickness. // Executes: gc.setLineWidth(2.2); // Streak thickness.
        for (int i = 0; i < 7; i++) { // Draw a few streak lines. // Begins a loop over a range or collection.
            double x = (i * 210) - parallax; // Move streaks based on parallax. // Executes: double x = (i * 210) - parallax; // Move streaks based on parallax.
            double y = viewHeight * 0.32 + Math.sin((x + camera.getOffsetX()) * 0.004) * 12; // Wavy y. // Executes: double y = viewHeight * 0.32 + Math.sin((x + camera.getOffsetX()) * 0.004) * 12; // Wavy y.
            gc.strokeLine(x, y, x + 150, y + 18); // Draw streak. // Executes: gc.strokeLine(x, y, x + 150, y + 18); // Draw streak.
        } // Closes a code block.

        // Ambient sparkles that match the active theme
        gc.setFill(theme.getPowerUpGlow().deriveColor(0, 1, 1, 0.16)); // Soft sparkle color. // Executes: gc.setFill(theme.getPowerUpGlow().deriveColor(0, 1, 1, 0.16)); // Soft sparkle color.
        double spread = viewWidth / 6.0; // Spread sparkles across width. // Executes: double spread = viewWidth / 6.0; // Spread sparkles across width.
        for (int i = 0; i < 6; i++) { // Draw 6 sparkles. // Begins a loop over a range or collection.
            double x = (i * spread * 0.95) - (camera.getOffsetX() * 0.12 % spread) + 40; // Slight motion. // Executes: double x = (i * spread * 0.95) - (camera.getOffsetX() * 0.12 % spread) + 40; // Slight motion.
            double y = (viewHeight * 0.18) + (i % 2 == 0 ? 10 : -10); // Alternate y up/down. // Executes: double y = (viewHeight * 0.18) + (i % 2 == 0 ? 10 : -10); // Alternate y up/down.
            gc.fillOval(x, y, 18, 18); // Draw sparkle circle. // Executes: gc.fillOval(x, y, 18, 18); // Draw sparkle circle.
        } // Closes a code block.
    } // Closes a code block.
} // Closes a code block.
