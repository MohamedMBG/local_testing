package game.systems;

import game.core.Player;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;
    private final CoinManager coinManager;
    private final EnemyManager enemyManager;
    private final UIManager uiManager;
    private final Player player;
    private final GameOverScreen gameOverScreen;

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

    public GameWorld(
            TileMap tileMap,
            Camera camera,
            CoinManager coinManager,
            EnemyManager enemyManager,
            UIManager uiManager,
            Player player,
            double spawnX,
            double spawnY,
            GameOverScreen gameOverScreen
    ) {
        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.enemyManager = enemyManager;
        this.uiManager = uiManager;
        this.player = player;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.gameOverScreen = gameOverScreen;

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
        if (playerHit) {
            loseLife();
        }

        // ===== Level end (goal) =====
        if (player.getPlayerX() + player.getWidth() >= goalX) {
            // For now just log; here you would trigger loading the next level
            System.out.println("Level complete! (Hook this to load the next level.)");
        }

        // ===== UI =====
        uiManager.setAll(score, coins, lives);
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
        enemyManager.render(gc, camera);

        // ---- 3) Draw goal flag ----
        renderGoal(gc);
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

        // Simple colors with a bit of depth
        Color base = Color.web("#D97B32");     // warm orange/brown
        Color topHighlight = Color.web("#F4B15A");
        Color outline = Color.web("#6B3B16");

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

                // Base block
                gc.setFill(base);
                gc.fillRoundRect(screenX, screenY, tileSize, tileSize, 6, 6);

                // Top highlight strip
                gc.setFill(topHighlight);
                gc.fillRoundRect(screenX + 2, screenY + 2, tileSize - 4, tileSize / 3.0, 6, 6);

                // Outline
                gc.setStroke(outline);
                gc.setLineWidth(1.5);
                gc.strokeRoundRect(screenX, screenY, tileSize, tileSize, 6, 6);
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
    }
}
