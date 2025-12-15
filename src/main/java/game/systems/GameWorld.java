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

    // ✅ now GameWorld owns the player
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

        // 1) update player (method name may differ in your Player)
        player.update(dt);

        // 2) camera follows player (adapt to your camera API)
        // camera.follow(player.getX(), player.getY(), player.getWidth(), player.getHeight());

        // 3) coin collection
        int collectedCoins = coinManager.updateAndCountCollected(
                player.getX(), player.getY(), player.getWidth(), player.getHeight()
        );
        if (collectedCoins > 0) {
            score += collectedCoins; // or * Coin.VALUE
            // uiManager.setScore(score); // if you have it
        }

        // 4) powerup collection + apply
        List<PowerUpType> types = powerUpManager.updateAndGetCollected(
                player.getX(), player.getY(), player.getWidth(), player.getHeight()
        );

        for (PowerUpType t : types) {
            // Best practice: Player handles effects
            player.applyPowerUp(t); // if Player doesn’t have this, add it in core (Person A can)
        }
    }

    public void render(GraphicsContext gc) {
        // tileMap.render(gc, camera); // if implemented

        coinManager.render(gc, camera);
        powerUpManager.render(gc, camera);

        // Player rendering (adapt to your Player render method)
        player.render(gc, camera);

        // uiManager.render(gc); // if implemented
    }
}