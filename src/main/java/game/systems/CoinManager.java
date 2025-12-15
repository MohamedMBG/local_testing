package game.systems;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoinManager {

    private final List<Coin> coins = new ArrayList<>();

    public void remove() {
        coins.clear();
    }

    public void spawnFrom(List<double[]> positions, double coinWidth, double coinHeight) {
        if (positions == null) return;

        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            coins.add(new Coin(x, y, coinWidth, coinHeight));
        }
    }

    // ✅ Counts collected coins AND removes them from the list
    public int updateAndCountCollected(double playerX, double playerY, double playerW, double playerH) {
        int collectedThisFrame = 0;

        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) {
            Coin coin = it.next();

            if (!coin.isCollected()) {
                boolean justCollected = coin.tryCollect(playerX, playerY, playerW, playerH);
                if (justCollected) {
                    collectedThisFrame++;
                    it.remove(); // ✅ important: remove from world
                }
            } else {
                // If somehow already collected, remove it anyway
                it.remove();
            }
        }

        return collectedThisFrame;
    }

    // ✅ Let manager render everything it owns
    public void render(GraphicsContext gc, Camera camera) {
        for (Coin coin : coins) {
            // If you have a sprite draw method in Coin, call it here.
            // Otherwise draw a simple placeholder.
            double screenX = coin.getX() - camera.getX();
            double screenY = coin.getY() - camera.getY();

            gc.fillOval(screenX, screenY, coin.getWidth(), coin.getHeight());
        }
    }

    public List<Coin> getCoins() {
        return coins;
    }
}
