package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.utils.Theme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpManager { // Defines a class.

    public static final double DEFAULT_SIZE = 24.0;

    private final List<PowerUp> powerUps = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) { // Begins a method or constructor with its signature.
        if (theme != null) { // Begins a method or constructor with its signature.
            this.theme = theme; // Executes: this.theme = theme;
        } // Closes a code block.
    } // Closes a code block.

    public void remove() { // Begins a method or constructor with its signature.
        powerUps.clear(); // Executes: powerUps.clear();
    } // Closes a code block.

    public void spawnFrom(List<double[]> positions, double powerUpWidth, double powerUpHeight, PowerUpType type) { // Begins a method or constructor with its signature.
        if (positions == null) return; // Evaluates a conditional branch.

        for (double[] pos : positions) { // Begins a method or constructor with its signature.
            if (pos == null || pos.length < 2) continue; // Evaluates a conditional branch.

            double x = pos[0];
            double y = pos[1];

            spawn(x, y, powerUpWidth, powerUpHeight, type); // Executes: spawn(x, y, powerUpWidth, powerUpHeight, type);
        } // Closes a code block.
    } // Closes a code block.

    public void spawn(double x, double y, double powerUpWidth, double powerUpHeight, PowerUpType type) { // Begins a method or constructor with its signature.
        powerUps.add(new PowerUp(x, y, powerUpWidth, powerUpHeight, type)); // Executes: powerUps.add(new PowerUp(x, y, powerUpWidth, powerUpHeight, type));
    } // Closes a code block.

    /**
     * Checks which power-ups the player collects this frame.
     * Returns a list of their types, so the game can apply effects.
     */
    public List<PowerUpType> updateAndGetCollected(double playerX, double playerY, double playerW, double playerH) { // Begins a method or constructor with its signature.

        List<PowerUpType> collectedTypes = new ArrayList<>();
        Iterator<PowerUp> iterator = powerUps.iterator();

        while (iterator.hasNext()) { // Begins a method or constructor with its signature.
            PowerUp p = iterator.next();

            if (!p.isCollected() && p.tryCollect(playerX, playerY, playerW, playerH)) { // Begins a method or constructor with its signature.
                collectedTypes.add(p.getType()); // Executes: collectedTypes.add(p.getType());
                iterator.remove(); // Executes: iterator.remove();
            } else if (p.isCollected()) { // Begins a method or constructor with its signature.
                // safety cleanup
                iterator.remove(); // Executes: iterator.remove();
            } // Closes a code block.
        } // Closes a code block.

        return collectedTypes;
    } // Closes a code block.

    // ✅ Manager renders what it owns
    public void render(GraphicsContext gc, Camera camera) { // Begins a method or constructor with its signature.
        for (PowerUp p : powerUps) { // Begins a method or constructor with its signature.
            double screenX = p.getX() - camera.getOffsetX();
            double screenY = p.getY() - camera.getOffsetY();

            Color base = colorFor(p.getType());
            Color glowBlend = theme.getPowerUpGlow();
            Color rim = base.interpolate(glowBlend, 0.3).deriveColor(0, 1, 0.92, 1);
            Color glow = glowBlend.interpolate(base, 0.4).deriveColor(0, 1, 1.15, 0.65);

            gc.setFill(glow); // Executes: gc.setFill(glow);
            gc.fillOval(screenX - 4, screenY - 4, p.getWidth() + 8, p.getHeight() + 8); // Executes: gc.fillOval(screenX - 4, screenY - 4, p.getWidth() + 8, p.getHeight() + 8);

            gc.setFill(base); // Executes: gc.setFill(base);
            gc.fillOval(screenX, screenY, p.getWidth(), p.getHeight()); // Executes: gc.fillOval(screenX, screenY, p.getWidth(), p.getHeight());
            gc.setStroke(rim); // Executes: gc.setStroke(rim);
            gc.setLineWidth(2); // Executes: gc.setLineWidth(2);
            gc.strokeOval(screenX + 2, screenY + 2, p.getWidth() - 4, p.getHeight() - 4); // Executes: gc.strokeOval(screenX + 2, screenY + 2, p.getWidth() - 4, p.getHeight() - 4);

            gc.setFill(Color.WHITE); // Executes: gc.setFill(Color.WHITE);
            gc.fillText(symbolFor(p.getType()), screenX + p.getWidth() / 2.7, screenY + p.getHeight() / 1.8); // Executes: gc.fillText(symbolFor(p.getType()), screenX + p.getWidth() / 2.7, screenY + p.getHeight() / 1.8);
        } // Closes a code block.
    } // Closes a code block.

    private Color colorFor(PowerUpType type) { // Begins a method or constructor with its signature.
        return switch (type) { // Begins a method or constructor with its signature.
            case MUSHROOM -> Color.web("#FF7043"); // Defines a switch case branch.
            case FLOWER -> Color.web("#FFEB3B"); // Defines a switch case branch.
            case STAR -> Color.web("#81D4FA"); // Defines a switch case branch.
            case LIFE -> Color.web("#C62828"); // Defines a switch case branch.
        }; // Executes: };
    } // Closes a code block.

    private String symbolFor(PowerUpType type) { // Begins a method or constructor with its signature.
        return switch (type) { // Begins a method or constructor with its signature.
            case MUSHROOM -> "M"; // Defines a switch case branch.
            case FLOWER -> "F"; // Defines a switch case branch.
            case STAR -> "✦"; // Defines a switch case branch.
            case LIFE -> "+"; // Defines a switch case branch.
        }; // Executes: };
    } // Closes a code block.

    public List<PowerUp> getPowerUps() { // Begins a method or constructor with its signature.
        return powerUps;
    } // Closes a code block.

    //detecting repo
} // Closes a code block.
