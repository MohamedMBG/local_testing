package game.systems;

import game.core.Player;
import game.utils.Theme;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.function.Consumer;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;
    private final CoinManager coinManager;
    private final PowerUpManager powerUpManager;
    private final EnemyManager enemyManager;
    private final SpikeManager spikeManager;
    private final UIManager uiManager;
    private final Player player;
    private final GameOverScreen gameOverScreen;
    private Theme theme;
    private Color goalFlagColor;

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
            Consumer<Integer> scoreListener
    ) {
        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.powerUpManager = powerUpManager;
        this.enemyManager = enemyManager;
        this.spikeManager = spikeManager;
        this.uiManager = uiManager;
        this.player = player;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.gameOverScreen = gameOverScreen;
        this.theme = theme;
        this.goalFlagColor = theme.getTileHighlight();
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
    public void update(double dt) {
        if (gameOver) {
            // Stop updating once game is over
            return;
        }

        if (invincibilityTimer > 0) {
            invincibilityTimer = Math.max(0, invincibilityTimer - dt);
        }

        // ===== Camera follow player (center) =====
        double cx = player.getPlayerX() + player.getWidth() / 2.0;
        double cy = player.getPlayerY() + player.getHeight() / 2.0;

        camera.follow(cx, cy, 0.12);
        camera.clampToMap(
                tileMap.getWidthInPixels(),
                tileMap.getHeightInPixels()
        );

        // ===== Coins =====
        int collected = coinManager.updateAndCountCollected(
                player.getPlayerX(),
                player.getPlayerY(),
                player.getWidth(),
                player.getHeight()
        );

        if (collected > 0) {
            coins += collected;
            score += collected * 10;
        }

        // ===== Enemies =====
        boolean playerHit = enemyManager.update(dt, player, tileMap);
        if (playerHit && !isInvincible()) {
            loseLife();
        }

        // ===== Spikes =====
        boolean playerTouchedSpike = spikeManager.checkPlayerCollision(
                player.getPlayerX(),
                player.getPlayerY(),
                player.getWidth(),
                player.getHeight()
        );
        if (playerTouchedSpike && !isInvincible()) {
            loseLife();
        }

        if (powerUpManager != null) {
            for (PowerUpType collected2 : powerUpManager.updateAndGetCollected(
                    player.getPlayerX(),
                    player.getPlayerY(),
                    player.getWidth(),
                    player.getHeight()
            )) {
                applyPowerUp(collected2);
            }
        }

        // ===== Level end (goal) =====
        if (player.getPlayerX() + player.getWidth() >= goalX) {
            // For now just log; here you would trigger loading the next level
            System.out.println("Level complete! (Hook this to load the next level.)");
        }

        // ===== UI =====
        uiManager.setAll(score, coins, lives);
        uiManager.setThemeName(theme.getDisplayName());
        if (scoreListener != null) {
            scoreListener.accept(score);
        }
    }

    // -------------------------------------------------
    // RENDER
    // -------------------------------------------------
    public void render(GraphicsContext gc) {
        if (gc == null) return;

        // Clear the whole camera view
        gc.clearRect(0, 0, camera.getViewWidth(), camera.getViewHeight());

        // ---- 1) Draw solid tiles so collisions / obstacles are visible ----
        renderTiles(gc);

        // ---- 2) Draw collectibles and enemies on top of tiles ----
        coinManager.render(gc, camera);
        if (powerUpManager != null) {
            powerUpManager.render(gc, camera);
        }
        enemyManager.render(gc, camera, theme);
        spikeManager.render(gc, camera);

        // ---- 3) Draw goal flag ----
        renderGoal(gc);

        // Debug overlay: show number of spikes
        gc.setFill(Color.WHITE);
        gc.fillText("Spikes: " + spikeManager.getCount(), 10, 20);

        // Extra debug markers for spikes (canvas overlay) - bright hazard color
        gc.setFill(theme.getSpikeFill());
        for (double[] sp : spikeManager.getPositions()) {
            double sx = sp[0] - camera.getOffsetX();
            double sy = sp[1] - camera.getOffsetY();
            gc.fillRect(sx + 4, sy + 4, 24, 12);
        }

        if (invincibilityTimer > 0) {
            gc.setFill(Color.YELLOW);
            gc.fillText("Star: " + String.format("%.1fs", invincibilityTimer), 10, 40);
        }
    }

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------
    private void loseLife() {
        lives--;
        if (lives <= 0) {
            lives = 0;
            gameOver = true;
            if (gameOverScreen != null) {
                gameOverScreen.show();
            }
        } else {
            respawnPlayer();
        }
    }

    private void respawnPlayer() {
        player.setPlayerX(spawnX);
        player.setPlayerY(spawnY);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.setOnGround(false);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    private void applyPowerUp(PowerUpType type) {
        switch (type) {
            case MUSHROOM -> score += 100;
            case FLOWER -> score += 150;
            case STAR -> invincibilityTimer = 6.0;
            case LIFE -> lives++;
        }
    }

    private boolean isInvincible() {
        return invincibilityTimer > 0;
    }

    // -------------------------------------------------
    // Getters (optional)
    // -------------------------------------------------
    public int getScore() {
        return score;
    }

    public int getCoins() {
        return coins;
    }

    public int getLives() {
        return lives;
    }

    // -------------------------------------------------
    // Internal: tile rendering (simple but clear)
    // -------------------------------------------------

    /**
     * Renders the solid tiles of the tile map using a pleasant Mario‑like palette
     * so that all collidable obstacles and platforms are clearly visible.
     */
    private void renderTiles(GraphicsContext gc) {
        final int tileSize = TileMap.TILE_SIZE;

        Color base = theme.getTileBase();
        Color highlight = theme.getTileHighlight();
        Color shadow = theme.getTileShadow();
        Color accent = theme.getTileAccent();

        int tilesX = tileMap.getWidthInTiles();
        int tilesY = tileMap.getHeightInTiles();

        // Only draw tiles that fall inside the camera view for efficiency
        int startTileX = Math.max(0, (int) (camera.getOffsetX() / tileSize) - 1);
        int endTileX = Math.min(tilesX, (int) ((camera.getOffsetX() + camera.getViewWidth()) / tileSize) + 2);
        int startTileY = Math.max(0, (int) (camera.getOffsetY() / tileSize) - 1);
        int endTileY = Math.min(tilesY, (int) ((camera.getOffsetY() + camera.getViewHeight()) / tileSize) + 2);

        for (int ty = startTileY; ty < endTileY; ty++) {
            for (int tx = startTileX; tx < endTileX; tx++) {
                if (!tileMap.isSolidTile(tx, ty)) continue;

                double worldX = tx * tileSize;
                double worldY = ty * tileSize;
                double screenX = worldX - camera.getOffsetX();
                double screenY = worldY - camera.getOffsetY();

                // Background bark block
                gc.setFill(base);
                gc.fillRoundRect(screenX, screenY, tileSize, tileSize, 10, 10);

                // Main branch running across the tile
                double branchHeight = tileSize * 0.35;
                double branchY = screenY + tileSize * 0.4;
                gc.setFill(highlight);
                gc.fillRoundRect(screenX - 2, branchY, tileSize + 4, branchHeight, 18, 18);

                // Deeper bark shadows to add depth
                gc.setFill(shadow);
                gc.fillRoundRect(screenX - 2, branchY + branchHeight * 0.45, tileSize + 4, branchHeight * 0.35, 18, 18);

                // Little twigs so collisions look like branches sticking out
                gc.setStroke(accent);
                gc.setLineWidth(4);
                gc.strokeLine(screenX + tileSize * 0.3, branchY + branchHeight * 0.25,
                        screenX + tileSize * 0.15, branchY - tileSize * 0.15);
                gc.strokeLine(screenX + tileSize * 0.65, branchY + branchHeight * 0.35,
                        screenX + tileSize * 0.9, branchY - tileSize * 0.1);

                // Knots to break up the surface
                gc.setFill(accent);
                gc.fillOval(screenX + tileSize * 0.55, branchY + branchHeight * 0.2, tileSize * 0.18, tileSize * 0.18);
                gc.fillOval(screenX + tileSize * 0.25, branchY + branchHeight * 0.5, tileSize * 0.14, tileSize * 0.14);
            }
        }
    }

    private void renderGoal(GraphicsContext gc) {
        double poleX = goalX - camera.getOffsetX();
        double baseY = tileMap.getHeightInPixels() - TileMap.TILE_SIZE - camera.getOffsetY();

        // Simple goal pole + flag
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(poleX, baseY - 6 * TileMap.TILE_SIZE, poleX, baseY);

        gc.setFill(Color.web("#FFEB3B"));
        gc.fillPolygon(
                new double[]{poleX, poleX + 24, poleX},
                new double[]{baseY - 6 * TileMap.TILE_SIZE, baseY - 6 * TileMap.TILE_SIZE + 12, baseY - 6 * TileMap.TILE_SIZE + 24},
                3
        );
        gc.setFill(goalFlagColor);
        gc.fillOval(poleX - 4, baseY - 6 * TileMap.TILE_SIZE - 6, 12, 12);
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        this.goalFlagColor = theme.getTileHighlight();
    }
}
