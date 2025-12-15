package game.systems;

import game.core.Player;
import javafx.scene.canvas.GraphicsContext;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;
    private final CoinManager coinManager;
    private final EnemyManager enemyManager;
    private final UIManager uiManager;
    private final Player player;

    // ---- Game state ----
    private int score = 0;
    private int coins = 0;
    private int lives = 3;

    // Respawn point
    private final double spawnX;
    private final double spawnY;

    public GameWorld(
            TileMap tileMap,
            Camera camera,
            CoinManager coinManager,
            EnemyManager enemyManager,
            UIManager uiManager,
            Player player,
            double spawnX,
            double spawnY
    ) {
        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.enemyManager = enemyManager;
        this.uiManager = uiManager;
        this.player = player;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    // -------------------------------------------------
    // UPDATE
    // -------------------------------------------------
    public void update(double dt) {

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

        // ===== (Future) Enemies =====
        // if (enemyManager.playerHit(player)) {
        //     loseLife();
        // }

        // ===== UI =====
        uiManager.setAll(score, coins, lives);
    }

    // -------------------------------------------------
    // RENDER
    // -------------------------------------------------
    public void render(GraphicsContext gc) {
        if (gc == null) return;

        gc.clearRect(
                0,
                0,
                camera.getViewWidth(),
                camera.getViewHeight()
        );

        coinManager.render(gc, camera);
        // enemyManager.render(gc, camera);
    }

    // -------------------------------------------------
    // Helpers
    // -------------------------------------------------
    private void loseLife() {
        lives--;
        respawnPlayer();
    }

    private void respawnPlayer() {
        player.setPlayerX(spawnX);
        player.setPlayerY(spawnY);
        player.setVelocityX(0);
        player.setVelocityY(0);
        player.setOnGround(false);
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
}
