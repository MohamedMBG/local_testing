package game.systems;

import javafx.scene.canvas.GraphicsContext;

import java.util.List;

public class GameWorld {

    private final TileMap tileMap;
    private final Camera camera;

    private final CoinManager coinManager;
    private final PowerUpManager powerUpManager;
    private final UIManager uiManager;

    // TODO: add Player reference when you have it accessible
    // private final Player player;

    public GameWorld(TileMap tileMap, Camera camera,
                     CoinManager coinManager,
                     PowerUpManager powerUpManager,
                     UIManager uiManager) {
        this.tileMap = tileMap;
        this.camera = camera;
        this.coinManager = coinManager;
        this.powerUpManager = powerUpManager;
        this.uiManager = uiManager;
    }

    public void update(double dt, double playerX, double playerY, double playerW, double playerH) {

        // Coins
        int coinsCollected = coinManager.updateAndCountCollected(playerX, playerY, playerW, playerH);
        if (coinsCollected > 0) {
            // score += coinsCollected;  // or coinsCollected * Coin.VALUE if you want
            // uiManager.setScore(score);
        }

        // PowerUps
        List<PowerUpType> types = powerUpManager.updateAndGetCollected(playerX, playerY, playerW, playerH);
        for (PowerUpType t : types) {
            // Apply effect later when Player exists
            // uiManager.showPopup("PowerUp: " + t);
        }
    }


    public void render(GraphicsContext gc) {
        // tileMap.render(gc, camera); // when ready

        coinManager.render(gc, camera);
        powerUpManager.render(gc, camera);

        // uiManager.render(gc);
    }

}
