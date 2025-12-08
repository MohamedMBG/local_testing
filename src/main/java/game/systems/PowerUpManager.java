package game.systems;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpManager {
    List<PowerUp> powerUps = new ArrayList<PowerUp>();

    public void remove(){
        powerUps.clear();
    }

    public void spawnFrom(List<double[]> positions, double powerUpWidth, double powerUpHeight, PowerUpType type) {
        if(positions == null) return;
        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            PowerUp powerUp = new PowerUp(x, y, powerUpWidth, powerUpHeight, type);
            powerUps.add(powerUp);
        }
    }
    /**
     * Checks which power-ups the player collects this frame.
     * Returns a list of their types, so the game can apply effects.
     */
    public List<PowerUpType> updateAndGetCollected(
            double playerX, double playerY, double playerW, double playerH) {

        List<PowerUpType> collectedTypes = new ArrayList<>();
        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) {
            PowerUp p = iterator.next();

            if (!p.isCollected() && p.tryCollect(playerX, playerY, playerW, playerH)) {
                collectedTypes.add(p.getType());
                iterator.remove();  // remove once collected
            }
        }

        return collectedTypes;
    }

    // For rendering: get all active (non-collected) power-ups
    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
