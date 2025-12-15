package game.systems;

import game.core.Player;
import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;
    private final CoinManager coinManager;
    private final PowerUpManager powerUpManager;
    private final UIManager uiManager;
    private final Player player;

    private int score = 0;

    public GameWorld(TileMap tileMap,
                     Camera camera,
                     CoinManager coinManager,
                     PowerUpManager powerUpManager,
                     UIManager uiManager,
                     Player player) {
        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.powerUpManager = powerUpManager;
        this.uiManager = uiManager;
        this.player = player;
    }

    public void update(double dt) {
        if (player == null) return;

        // camera follow
        if (camera != null) {
            double px = player.getPlayerX() + player.getWidth() / 2.0;
            double py = player.getPlayerY() + player.getHeight() / 2.0;
            camera.follow(px, py, 0.15);
        }

        // coins
        if (coinManager != null) {
            int collected = coinManager.updateAndCountCollected(
                    player.getPlayerX(), player.getPlayerY(), player.getWidth(), player.getHeight()
            );
            if (collected > 0) score += collected;
        }

        // powerups (optional)
        if (powerUpManager != null) {
            List<PowerUpType> collectedTypes = powerUpManager.updateAndGetCollected(
                    player.getPlayerX(), player.getPlayerY(), player.getWidth(), player.getHeight()
            );
            // If your Player doesn't support it yet, ignore for now.
        }
    }

    public void render(GraphicsContext gc) {
        if (gc == null) return;

        // 1) render tiles as obstacles
        renderTiles(gc);

        // 2) collectibles
        if (coinManager != null) coinManager.render(gc, camera);
        if (powerUpManager != null) powerUpManager.render(gc, camera);

        // 3) UI
        if (uiManager != null) uiManager.render(gc, score);
    }

    private void renderTiles(GraphicsContext gc) {
        if (tileMap == null) return;

        double ox = camera != null ? camera.getOffsetX() : 0;
        double oy = camera != null ? camera.getOffsetY() : 0;

        for (int ty = 0; ty < tileMap.getHeightInTiles(); ty++) {
            for (int tx = 0; tx < tileMap.getWidthInTiles(); tx++) {
                if (tileMap.getTile(tx, ty) == 1) {
                    double x = tx * TileMap.TILE_SIZE - ox;
                    double y = ty * TileMap.TILE_SIZE - oy;
                    gc.fillRect(x, y, TileMap.TILE_SIZE, TileMap.TILE_SIZE);
                }
            }
        }
    }

    public int getScore() {
        return score;
    }
}
