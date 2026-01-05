package game.systems;

import game.core.Player;
import game.utils.Theme;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import java.util.function.Consumer;

public class GameWorld {

    private final TileMap tileMap; // The tile map (solid tiles, map size, collision grid).
    private final Camera camera; // The camera that follows the player and defines the visible view.
    private final CoinManager coinManager; // Manager responsible for coins (spawning, update, rendering, collecting).
    private final PowerUpManager powerUpManager; // Manager responsible for power-ups (like STAR).
    private final EnemyManager enemyManager; // Manager responsible for enemies (movement, collisions, rendering).
    private final SpikeManager spikeManager; // Manager responsible for spike hazards.
    private final UIManager uiManager; // UI manager for score/coins/lives display.
    private final Player player; // The main player object.
    private final GameOverScreen gameOverScreen; // Screen shown when lives reach 0.
    private Theme theme; // Current visual theme (colors for background, tiles, power-up glow...).
    private Color goalFlagColor; // Cached color used to draw the goal flag (depends on theme).

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

    public GameWorld(
                      TileMap tileMap,
                      Camera camera,
                      CoinManager coinManager,
                      PowerUpManager powerUpManager,
                      EnemyManager enemyManager,
                      SpikeManager spikeManager,
                      UIManager uiManager,
                      Player player,
                      double spawnX,
                      double spawnY,
                      GameOverScreen gameOverScreen,
                      Theme theme,
                      Consumer<Integer> scoreListener // Score callback reference.
    ) {
        this.tileMap = tileMap; // Store tile map in this world.
        this.camera = camera; // Store camera in this world.
        this.coinManager = coinManager; // Store coin manager.
        this.powerUpManager = powerUpManager; // Store power-up manager (might be null).
        this.enemyManager = enemyManager; // Store enemy manager.
        this.spikeManager = spikeManager; // Store spike manager.
        this.uiManager = uiManager; // Store UI manager.
        this.player = player; // Store player.
        this.spawnX = spawnX; // Store respawn X.
        this.spawnY = spawnY; // Store respawn Y.
        this.gameOverScreen = gameOverScreen; // Store game over screen.
        this.theme = theme; // Store theme.
        this.goalFlagColor = theme.getTileHighlight(); // Choose a nice color for the goal flag based on theme.
        this.scoreListener = scoreListener;

        coinManager.setTheme(theme);
        if (powerUpManager != null) {
            powerUpManager.setTheme(theme);
        }
        enemyManager.setTheme(theme);
        spikeManager.setTheme(theme);

        // Place goal a bit before the very end of the map
        this.goalX = tileMap.getWidthInPixels() - 2 * TileMap.TILE_SIZE;
    }

    // -------------------------------------------------
    // UPDATE
    // -------------------------------------------------
    public void update(double dt) { // Update world logic. dt = delta time (seconds since last frame).
        if (gameOver) { // If game is already over...
            // Stop updating once game is over
            return; // ...exit early so nothing moves/changes anymore.
        }

        // count down invincibility time every frame.
        if (invincibilityTimer > 0) { // If player currently has invincibility active...
            invincibilityTimer = Math.max(0, invincibilityTimer - dt); // Decrease timer and never go below 0.
        }

        // ===== Camera follow player (center) =====
        double cx = player.getPlayerX() + player.getWidth() / 2.0; // Player center X (for smooth following).
        double cy = player.getPlayerY() + player.getHeight() / 2.0; // Player center Y.

        camera.follow(cx, cy, 0.12); // Move camera toward player center (0.12 = smoothing factor).
        camera.clampToMap( // Prevent camera from leaving map bounds.
                tileMap.getWidthInPixels(), // Map width in pixels.
                tileMap.getHeightInPixels() // Map height in pixels.
        );

        // ===== Coins =====
        int collected = coinManager.updateAndCountCollected( // Update coins and detect collisions with player.
                player.getPlayerX(), // Player X.
                player.getPlayerY(), // Player Y.
                player.getWidth(), // Player width.
                player.getHeight() // Player height.
        );

        if (collected > 0) { // If any coin(s) were collected this frame...
            coins += collected; // Increase coin count.
            score += collected * 10; // Increase score (10 points per coin).
        }

        // ===== Enemies =====
        boolean playerHit = enemyManager.update(dt, player, tileMap); // Update enemies and check if player got hit.
        //  STAR PROTECTION: only lose life if hit AND not invincible.
        if (playerHit && !isInvincible()) { // If enemy hit player AND STAR protection is NOT active...
            loseLife(); // Decrease life and respawn or end game.
        }

        // ===== Spikes =====
        boolean playerTouchedSpike = spikeManager.checkPlayerCollision( // Check collision between player and spikes.
                player.getPlayerX(), // Player X.
                player.getPlayerY(), // Player Y.
                player.getWidth(), // Player width.
                player.getHeight() // Player height.
        );
        //  STAR PROTECTION: spikes also cannot hurt you if invincible.
        if (playerTouchedSpike && !isInvincible()) { // If spike touched player AND STAR protection is NOT active...
            loseLife(); // Decrease life and respawn or end game.
        }

        if (powerUpManager != null) { // If we have power-ups enabled in this world...
            for (PowerUpType collected2 : powerUpManager.updateAndGetCollected( // Update power-ups and get what was collected.
                    player.getPlayerX(), // Player X.
                    player.getPlayerY(), // Player Y.
                    player.getWidth(), // Player width.
                    player.getHeight() // Player height.
            )) {
                applyPowerUp(collected2); // Apply each collected power-up effect (STAR sets invincibilityTimer).
            }
        }

        // ===== Level end (goal) =====
        if (player.getPlayerX() + player.getWidth() >= goalX) { // If player's right side reaches goal line...
            // For now just log; here you would trigger loading the next level
            System.out.println("Level complete! (Hook this to load the next level.)"); // Debug message.
        }

        // ===== UI =====
        uiManager.setAll(score, coins, lives); // Update UI values.
        uiManager.setThemeName(theme.getDisplayName()); // Show the theme name (optional).
        uiManager.setTheme(theme); // Give UI the theme so it matches the world colors.
        if (scoreListener != null) { // If someone is listening for score updates...
            scoreListener.accept(score); // Notify them with the new score.
        }
    }

    // -------------------------------------------------
    // RENDER
    // -------------------------------------------------
    public void render(GraphicsContext gc) { // Draw the world to the canvas.
        if (gc == null) return; // Safety: if GraphicsContext is missing, do nothing.

        renderBackdrop(gc); // Draw background first (sky gradient, sparkles, etc.).

        // ---- 1) Draw solid tiles so collisions / obstacles are visible ----
        renderTiles(gc); // Draw the collidable map tiles.

        // ---- 2) Draw collectibles and enemies on top of tiles ----
        coinManager.render(gc, camera); // Draw coins using camera offset.
        if (powerUpManager != null) { // If power-ups exist...
            powerUpManager.render(gc, camera); // Draw power-ups (including STAR).
        }
        enemyManager.render(gc, camera, theme); // Draw enemies with theme-based colors.
        spikeManager.render(gc, camera); // Draw spikes.

        // ---- 3) Draw goal flag ----
        renderGoal(gc); // Draw end-of-level goal.
    }

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------
    private void loseLife() { // Called when player takes damage.
        lives--; // Reduce lives by 1.
        if (lives <= 0) { // If no lives left...
            lives = 0; // Clamp to 0 (no negative lives).
            gameOver = true; // Mark game as ended.
            if (gameOverScreen != null) { // If gameOverScreen exists...
                gameOverScreen.show(); // Show game over screen.
            }
        } else { // Otherwise, player still has lives remaining...
            respawnPlayer(); // Respawn the player at spawnX/spawnY.
        }
    }

    private void respawnPlayer() { // Reset player to spawn point.
        player.setPlayerX(spawnX); // Move player X to spawn.
        player.setPlayerY(spawnY); // Move player Y to spawn.
        player.setVelocityX(0); // Reset horizontal speed so player doesn't keep sliding.
        player.setVelocityY(0); // Reset vertical speed so player doesn't keep falling/jumping.
        player.setOnGround(false); // Player is not considered grounded immediately after respawn.
    }

    public boolean isGameOver() { // Public getter: is game over?
        return gameOver; // Return the current gameOver state.
    }

    private void applyPowerUp(PowerUpType type) { // Apply effects depending on power-up type.
        switch (type) { // Switch on the collected power-up.
            case MUSHROOM -> score += 100; // Mushroom gives points (placeholder effect).
            case FLOWER -> score += 150; // Flower gives points (placeholder effect).
            //  STAR PROTECTION: STAR sets invincibility time (seconds).
            case STAR -> invincibilityTimer = 6.0; // Make player invincible for 6 seconds.
            case LIFE -> lives++; // Extra life.
        }
    }

    private boolean isInvincible() { // Helper: checks if STAR protection is active.
        return invincibilityTimer > 0;
    }

    // -------------------------------------------------
    // Getters (optional)
    // -------------------------------------------------
    public int getScore() { // Public getter for score.
        return score; // Return score.
    }

    public int getCoins() { // Public getter for coins.
        return coins; // Return coins.
    }

    public int getLives() { // Public getter for lives.
        return lives; // Return lives.
    }

    // -------------------------------------------------
    // Internal: tile rendering (simple but clear)
    // -------------------------------------------------

    /**
     * Renders the solid tiles of the tile map using a pleasant Mario-like palette
     * so that all collidable obstacles and platforms are clearly visible.
     */
    private void renderTiles(GraphicsContext gc) { // Draw only solid tiles visible in the camera view.
        final int tileSize = TileMap.TILE_SIZE; // Pixel size of one tile.

        Color base = theme.getTileBase(); // Base tile color from theme.
        Color highlight = theme.getTileHighlight(); // Highlight color from theme.
        Color shadow = theme.getTileShadow(); // Shadow color from theme.
        Color accent = theme.getTileAccent(); // Accent color from theme.

        int tilesX = tileMap.getWidthInTiles(); // Number of tiles horizontally.
        int tilesY = tileMap.getHeightInTiles(); // Number of tiles vertically.

        // Only draw tiles that fall inside the camera view for efficiency
        int startTileX = Math.max(0, (int) (camera.getOffsetX() / tileSize) - 1); // First visible tile X.
        int endTileX = Math.min(tilesX, (int) ((camera.getOffsetX() + camera.getViewWidth()) / tileSize) + 2); // Last visible tile X.
        int startTileY = Math.max(0, (int) (camera.getOffsetY() / tileSize) - 1); // First visible tile Y.
        int endTileY = Math.min(tilesY, (int) ((camera.getOffsetY() + camera.getViewHeight()) / tileSize) + 2); // Last visible tile Y.

        for (int ty = startTileY; ty < endTileY; ty++) { // Loop through visible tile rows.
            for (int tx = startTileX; tx < endTileX; tx++) { // Loop through visible tile columns.
                if (!tileMap.isSolidTile(tx, ty)) continue; // Skip non-solid tiles.

                double worldX = tx * tileSize; // Tile world X in pixels.
                double worldY = ty * tileSize; // Tile world Y in pixels.
                double screenX = worldX - camera.getOffsetX(); // Convert world X to screen X.
                double screenY = worldY - camera.getOffsetY(); // Convert world Y to screen Y.

                // Background bark block
                gc.setFill(base); // Set fill color to base.
                gc.fillRoundRect(screenX, screenY, tileSize, tileSize, 10, 10); // Draw tile base shape.

                // Main branch running across the tile
                double branchHeight = tileSize * 0.35; // Branch height proportion.
                double branchY = screenY + tileSize * 0.4; // Branch Y position.
                gc.setFill(highlight); // Set highlight fill.
                gc.fillRoundRect(screenX - 2, branchY, tileSize + 4, branchHeight, 18, 18); // Draw branch highlight.

                // Deeper bark shadows to add depth
                gc.setFill(shadow); // Set shadow fill.
                gc.fillRoundRect(screenX - 2, branchY + branchHeight * 0.45, tileSize + 4, branchHeight * 0.35, 18, 18); // Draw shadow.

                // Little twigs so collisions look like branches sticking out
                gc.setStroke(accent); // Set stroke color to accent.
                gc.setLineWidth(4); // Set twig thickness.
                gc.strokeLine(screenX + tileSize * 0.3, branchY + branchHeight * 0.25, // Twig 1 start.
                        screenX + tileSize * 0.15, branchY - tileSize * 0.15); // Twig 1 end.
                gc.strokeLine(screenX + tileSize * 0.65, branchY + branchHeight * 0.35, // Twig 2 start.
                        screenX + tileSize * 0.9, branchY - tileSize * 0.1); // Twig 2 end.

                // Knots to break up the surface
                gc.setFill(accent); // Set fill to accent for knots.
                gc.fillOval(screenX + tileSize * 0.55, branchY + branchHeight * 0.2, tileSize * 0.18, tileSize * 0.18); // Knot 1.
                gc.fillOval(screenX + tileSize * 0.25, branchY + branchHeight * 0.5, tileSize * 0.14, tileSize * 0.14); // Knot 2.
            }
        }
    }

    private void renderGoal(GraphicsContext gc) { // Draw the goal pole and flag.
        double poleX = goalX - camera.getOffsetX(); // Convert goal world X to screen X.
        double baseY = tileMap.getHeightInPixels() - TileMap.TILE_SIZE - camera.getOffsetY(); // Base Y near ground.

        // Simple goal pole + flag
        gc.setStroke(Color.WHITE); // Pole color.
        gc.setLineWidth(3); // Pole thickness.
        gc.strokeLine(poleX, baseY - 6 * TileMap.TILE_SIZE, poleX, baseY); // Draw pole line.

        gc.setFill(Color.web("#FFEB3B")); // Yellow flag triangle.
        gc.fillPolygon( // Draw triangle flag.
                new double[]{poleX, poleX + 24, poleX}, // X points.
                new double[]{baseY - 6 * TileMap.TILE_SIZE, baseY - 6 * TileMap.TILE_SIZE + 12, baseY - 6 * TileMap.TILE_SIZE + 24}, // Y points.
                3 // Number of points.
        );
        gc.setFill(goalFlagColor); // The little top cap uses theme highlight color.
        gc.fillOval(poleX - 4, baseY - 6 * TileMap.TILE_SIZE - 6, 12, 12); // Draw cap circle.
    }

    public void setTheme(Theme theme) { // Change the theme at runtime.
        this.theme = theme; // Save new theme.
        this.goalFlagColor = theme.getTileHighlight(); // Update goal flag color to match.
    }

    private void renderBackdrop(GraphicsContext gc) { // Draw background gradient + parallax effects.
        double viewWidth = camera.getViewWidth(); // Visible width.
        double viewHeight = camera.getViewHeight(); // Visible height.

        LinearGradient gradient = new LinearGradient( // Create a vertical gradient for the sky.
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE, // From top to bottom, proportional coords, no repeat.
                new Stop(0, theme.getBackground()), // Top color.
                new Stop(1, theme.getBackgroundBottom()) // Bottom color.
        );
        gc.setFill(gradient); // Set fill to gradient.
        gc.fillRect(0, 0, viewWidth, viewHeight); // Paint the whole screen.

        // Distant glow behind the level to make silhouettes pop
        gc.setFill(theme.getBackgroundBottom().interpolate(theme.getGround(), 0.35)); // Mixed color.
        gc.fillRect(0, viewHeight * 0.72, viewWidth, viewHeight * 0.3); // Draw glow band.

        // Soft parallax streaks to convey motion
        double parallax = (camera.getOffsetX() / 3.5) % viewWidth; // Create slow-moving offset for parallax.
        gc.setStroke(theme.getTileAccent().deriveColor(0, 1, 1, 0.22)); // Semi-transparent streak color.
        gc.setLineWidth(2.2); // Streak thickness.
        for (int i = 0; i < 7; i++) { // Draw a few streak lines.
            double x = (i * 210) - parallax; // Move streaks based on parallax.
            double y = viewHeight * 0.32 + Math.sin((x + camera.getOffsetX()) * 0.004) * 12; // Wavy y.
            gc.strokeLine(x, y, x + 150, y + 18); // Draw streak.
        }

        // Ambient sparkles that match the active theme
        gc.setFill(theme.getPowerUpGlow().deriveColor(0, 1, 1, 0.16)); // Soft sparkle color.
        double spread = viewWidth / 6.0; // Spread sparkles across width.
        for (int i = 0; i < 6; i++) { // Draw 6 sparkles.
            double x = (i * spread * 0.95) - (camera.getOffsetX() * 0.12 % spread) + 40; // Slight motion.
            double y = (viewHeight * 0.18) + (i % 2 == 0 ? 10 : -10); // Alternate y up/down.
            gc.fillOval(x, y, 18, 18); // Draw sparkle circle.
        }
    }
}
