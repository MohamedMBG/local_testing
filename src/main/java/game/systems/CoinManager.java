package game.systems;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

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
        if (gc == null || camera == null) return;

        // Shiny gold gradient used for all coins
        LinearGradient goldFill = new LinearGradient(
                0, 0, 0, 1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#FFF9C4")),
                new Stop(0.4, Color.web("#FFE082")),
                new Stop(1.0, Color.web("#FFB300"))
        );

        Color goldOutline = Color.web("#D68600");

        for (Coin coin : coins) {
            // Get the camera offset (for scrolling)
            double ox = camera.getOffsetX();
            double oy = camera.getOffsetY();

            // Get the screen coordinates based on camera offsets
            double screenX = coin.getX() - ox;
            double screenY = coin.getY() - oy;

            double w = coin.getWidth();
            double h = coin.getHeight();

            // Main body
            gc.setFill(goldFill);
            gc.fillOval(screenX, screenY, w, h);

            // Outline
            gc.setStroke(goldOutline);
            gc.setLineWidth(2);
            gc.strokeOval(screenX, screenY, w, h);

            // Inner highlight to fake a bevelled edge
            gc.setStroke(Color.rgb(255, 255, 255, 0.65));
            gc.setLineWidth(1.2);
            gc.strokeOval(screenX + 3, screenY + 3, w - 6, h - 6);
        }
    }

    // Getter for the list of coins
    public List<Coin> getCoins() {
        return coins;
    }
}
