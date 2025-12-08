package game.systems;

import java.util.ArrayList;
import java.util.List;

public class CoinManager {
    private List<Coin> coins = new ArrayList<Coin>();

    public void remove(){
        coins.clear();
    }

    public void spawnFrom(List<double[]> positions, double coinWidth, double coinHeight) {
        if(positions == null) return;
        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            Coin coin = new Coin(x, y, coinWidth, coinHeight);
            coins.add(coin);
        }
    }

    public int updateAndCountCollected(double playerX, double playerY, double playerW, double playerH) {
        int collectedThisFrame = 0;

        for (Coin coin : coins) {
            if (!coin.isCollected()) {
                boolean justCollected = coin.tryCollect(playerX, playerY, playerW, playerH);
                if (justCollected) {
                    collectedThisFrame++;
                }
            }
        }

        return collectedThisFrame;
    }

    public List<Coin> getCoins() {
        return coins;
    }


}
