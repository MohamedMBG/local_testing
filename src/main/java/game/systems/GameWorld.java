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
        if (player == null) return; // without player, nothing to update safely

        // 1) Update player
        player.update(dt);

        // 2) Camera follows player (only if your Camera has follow())
        if (camera != null) {
            // camera.follow(player.getX(), player.getY(), player.getWidth(), player.getHeight());
        }

        // 3) Coin collection
        if (coinManager != null) {
            int collectedCoins = coinManager.updateAndCountCollected(
                    player.getX(), player.getY(), player.getWidth(), player.getHeight()
            );
            if (collectedCoins > 0) {
                score += collectedCoins; // or collectedCoins * Coin.VALUE
                // if you have hud/uiManager score setter:
                // uiManager.setScore(score);
            }
        }

        // 4) PowerUp collection + apply
        if (powerUpManager != null) {
            List<PowerUpType> collectedTypes = powerUpManager.updateAndGetCollected(
                    player.getX(), player.getY(), player.getWidth(), player.getHeight()
            );

            // Apply effects (only if Player supports it)
            for (PowerUpType t : collectedTypes) {
                player.applyPowerUp(t);
            }
        }

        // 5) UI update (optional)
        // if (uiManager != null) uiManager.update(dt);
    }

    public void render(GraphicsContext gc) {
        if (gc == null) return;

        // Collectibles
        if (coinManager != null) coinManager.render(gc, camera);
        if (powerUpManager != null) powerUpManager.render(gc, camera);

        // Player
        if (player != null) player.render(gc, camera);

        // UI (coins)
        if (uiManager != null) uiManager.render(gc, score);
    }

    public int getScore() {
        return score;
    }
}
