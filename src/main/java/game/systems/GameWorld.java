package game.systems;

import game.core.Player;
import javafx.scene.canvas.GraphicsContext;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;
    private final CoinManager coinManager;
    private final EnemyManager enemyManager;
    private final UIManager ui;
    private final Player player;

    private final double spawnX;
    private final double spawnY;

    private int score = 0;
    private int lives = 3;

    public GameWorld(TileMap tileMap,
                     Camera camera,
                     CoinManager coinManager,
                     EnemyManager enemyManager,
                     UIManager ui,
                     Player player,
                     double spawnX,
                     double spawnY) {

        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.enemyManager = enemyManager;
        this.ui = ui;
        this.player = player;
        this.spawnX = spawnX;
        this.spawnY = spawnY;
    }

    public void update(double dt) {

        // ===== CAMERA FOLLOW =====
        double px = player.getPlayerX() + player.getWidth() / 2;
        double py = player.getPlayerY() + player.getHeight() / 2;

        camera.follow(px, py, 0.12);
        camera.clampToMap(
                tileMap.getWidthInPixels(),
                tileMap.getHeightInPixels()
        );

        // ===== COINS =====
        score += coinManager.updateAndCountCollected(
                player.getPlayerX(),
                player.getPlayerY(),
                player.getWidth(),
                player.getHeight()
        );

        // ===== ENEMIES =====
        boolean hit = enemyManager.update(dt, player, tileMap);
        if (hit) {
            lives--;
            player.setPlayerX(spawnX);
            player.setPlayerY(spawnY);
        }

        // ===== UI =====
        ui.set(score, lives);
    }

    public void render(GraphicsContext gc) {

        // ===== TILES =====
        double ox = camera.getOffsetX();
        double oy = camera.getOffsetY();

        for (int y = 0; y < tileMap.getHeightInTiles(); y++) {
            for (int x = 0; x < tileMap.getWidthInTiles(); x++) {
                if (tileMap.getTile(x, y) == 1) {
                    gc.fillRect(
                            x * TileMap.TILE_SIZE - ox,
                            y * TileMap.TILE_SIZE - oy,
                            TileMap.TILE_SIZE,
                            TileMap.TILE_SIZE
                    );
                }
            }
        }

        coinManager.render(gc, camera);
        enemyManager.render(gc, camera);
    }
}
