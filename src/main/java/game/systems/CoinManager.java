package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import game.utils.Theme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CoinManager { // Defines a class.

    public static final double DEFAULT_SIZE = 20.0;

    private final List<Coin> coins = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) { // Begins a method or constructor with its signature.
        if (theme != null) { // Begins a method or constructor with its signature.
            this.theme = theme; // Executes: this.theme = theme;
        } // Closes a code block.
    } // Closes a code block.

    // Clears the coins
    public void remove() { // Begins a method or constructor with its signature.
        coins.clear(); // Executes: coins.clear();
    } // Closes a code block.

    // Overloaded method for easier use
    public void spawnFrom(List<double[]> positions) { // Begins a method or constructor with its signature.
        spawnFrom(positions, DEFAULT_SIZE, DEFAULT_SIZE);  // Default coin size // Executes: spawnFrom(positions, DEFAULT_SIZE, DEFAULT_SIZE);  // Default coin size
    } // Closes a code block.

    // Spawns coins with specific size
    public void spawnFrom(List<double[]> positions, double coinWidth, double coinHeight) { // Begins a method or constructor with its signature.
        if (positions == null) return; // Evaluates a conditional branch.

        for (double[] pos : positions) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.

            double x = pos[0];
            double y = pos[1];

            // Create a new coin and add it to the list
            coins.add(new Coin(x, y, coinWidth, coinHeight)); // Executes: coins.add(new Coin(x, y, coinWidth, coinHeight));
        } // Closes a code block.
    } // Closes a code block.

    // Updates and counts the collected coins, removes them from the world
    public int updateAndCountCollected(double playerX, double playerY, double playerW, double playerH) { // Begins a method or constructor with its signature.
        int collectedThisFrame = 0;

        Iterator<Coin> it = coins.iterator();
        while (it.hasNext()) { // Begins a method or constructor with its signature.
            Coin coin = it.next();

            if (!coin.isCollected()) { // Begins a method or constructor with its signature.
                boolean justCollected = coin.tryCollect(playerX, playerY, playerW, playerH);
                if (justCollected) { // Begins a method or constructor with its signature.
                    collectedThisFrame++; // Executes: collectedThisFrame++;
                    it.remove(); // Remove the collected coin from world // Executes: it.remove(); // Remove the collected coin from world
                } // Closes a code block.
            } else { // Executes: } else {
                it.remove(); // If coin is already collected, remove it // Executes: it.remove(); // If coin is already collected, remove it
            } // Closes a code block.
        } // Closes a code block.

        return collectedThisFrame;
    } // Closes a code block.

    // Renders the coins onto the canvas
    public void render(GraphicsContext gc, Camera camera) { // Begins a method or constructor with its signature.
        if (gc == null || camera == null) return; // Evaluates a conditional branch.

        // Shiny gold gradient used for all coins
        LinearGradient goldFill = new LinearGradient( // Executes: LinearGradient goldFill = new LinearGradient(
                0, 0, 0, 1, // Executes: 0, 0, 0, 1,
                true, // Executes: true,
                CycleMethod.NO_CYCLE, // Executes: CycleMethod.NO_CYCLE,
                new Stop(0.0, theme.getCoinLight()), // Executes: new Stop(0.0, theme.getCoinLight()),
                new Stop(0.45, theme.getCoinMid()), // Executes: new Stop(0.45, theme.getCoinMid()),
                new Stop(1.0, theme.getCoinShadow()) // Executes: new Stop(1.0, theme.getCoinShadow())
        ); // Executes: );

        Color goldOutline = theme.getCoinOutline();

        for (Coin coin : coins) { // Begins a method or constructor with its signature.
            // Get the camera offset (for scrolling)
            double ox = camera.getOffsetX();
            double oy = camera.getOffsetY();

            // Get the screen coordinates based on camera offsets
            double screenX = coin.getX() - ox;
            double screenY = coin.getY() - oy;

            double w = coin.getWidth();
            double h = coin.getHeight();

            // Main body
            gc.setFill(goldFill); // Executes: gc.setFill(goldFill);
            gc.fillOval(screenX, screenY, w, h); // Executes: gc.fillOval(screenX, screenY, w, h);

            // Outline
            gc.setStroke(goldOutline); // Executes: gc.setStroke(goldOutline);
            gc.setLineWidth(2); // Executes: gc.setLineWidth(2);
            gc.strokeOval(screenX, screenY, w, h); // Executes: gc.strokeOval(screenX, screenY, w, h);

            // Inner highlight to fake a bevelled edge
            gc.setStroke(Color.rgb(255, 255, 255, 0.65)); // Executes: gc.setStroke(Color.rgb(255, 255, 255, 0.65));
            gc.setLineWidth(1.2); // Executes: gc.setLineWidth(1.2);
            gc.strokeOval(screenX + 3, screenY + 3, w - 6, h - 6); // Executes: gc.strokeOval(screenX + 3, screenY + 3, w - 6, h - 6);
        } // Closes a code block.
    } // Closes a code block.

    // Getter for the list of coins
    public List<Coin> getCoins() { // Begins a method or constructor with its signature.
        return coins;
    } // Closes a code block.
} // Closes a code block.
