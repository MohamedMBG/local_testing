package game.systems;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoinManager {

    private final List<Coin> coins = new ArrayList<>();

    // Clears the coins
    public void remove() {
        coins.clear();
    }

    // Overloaded method for easier use
    public void spawnFrom(List<double[]> positions) {
        spawnFrom(positions, 16, 16);  // Default coin size
    }

    // Spawns coins with specific size
    public void spawnFrom(List<double[]> positions, double coinWidth, double coinHeight) {
        if (positions == null) return;

        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            // Create a new coin and add it to the list
            coins.add(new Coin(x, y, coinWidth, coinHeight));
        }
    }

    // Updates and counts the collected coins, removes them from the world
    public int updateAndCountCollected(double playerX, double playerY, double playerW, double playerH) {
        int collectedThisFrame = 0;

        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin coin = it.next();

            if (!coin.isCollected()) {
                boolean justCollected = coin.tryCollect(playerX, playerY, playerW, playerH);
                if (justCollected) {
                    collectedThisFrame++;
                    it.remove(); // Remove the collected coin from world
                }
            } else {
                it.remove(); // If coin is already collected, remove it
            }
        }

        return collectedThisFrame;
    }

    // Renders the coins onto the canvas
    public void render(GraphicsContext gc, Camera camera) {
        for (Coin coin : coins) {
            // Get the camera offset (for scrolling)
            double ox = camera.getOffsetX();
            double oy = camera.getOffsetY();

            // Get the screen coordinates based on camera offsets
            double screenX = coin.getX() - ox;
            double screenY = coin.getY() - oy;

            // Draw the coin (or you can use sprites here)
            gc.fillOval(screenX, screenY, coin.getWidth(), coin.getHeight());
        }
    }

    // Getter for the list of coins
    public List<Coin> getCoins() {
        return coins;
    }
}
