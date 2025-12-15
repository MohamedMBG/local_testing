package game.systems;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpManager {

    private final List<PowerUp> powerUps = new ArrayList<>();

    public void remove() {
        powerUps.clear();
    }

    public void spawnFrom(List<double[]> positions, double powerUpWidth, double powerUpHeight, PowerUpType type) {
        if (positions == null) return;

        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            powerUps.add(new PowerUp(x, y, powerUpWidth, powerUpHeight, type));
        }
    }

    /**
     * Checks which power-ups the player collects this frame.
     * Returns a list of their types, so the game can apply effects.
     */
    public List<PowerUpType> updateAndGetCollected(double playerX, double playerY, double playerW, double playerH) {

        List<PowerUpType> collectedTypes = new ArrayList<>();
        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) {
            PowerUp p = iterator.next();

            if (!p.isCollected() && p.tryCollect(playerX, playerY, playerW, playerH)) {
                collectedTypes.add(p.getType());
                iterator.remove();
            } else if (p.isCollected()) {
                // safety cleanup
                iterator.remove();
            }
        }

        return collectedTypes;
    }

    // ✅ Manager renders what it owns
    public void render(GraphicsContext gc, Camera camera) {
        for (PowerUp p : powerUps) {
            double screenX = p.getX() - camera.getX();
            double screenY = p.getY() - camera.getY();

            // Placeholder draw (replace with sprite later)
            gc.fillRect(screenX, screenY, p.getWidth(), p.getHeight());
        }
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
