package game.systems;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import game.utils.Theme;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpManager {

    public static final double DEFAULT_SIZE = 24.0;

    private final List<PowerUp> powerUps = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    public void remove() {
        powerUps.clear();
    }

    public void spawnFrom(List<double[]> positions, double powerUpWidth, double powerUpHeight, PowerUpType type) {
        if (positions == null) return;

        for (double[] pos : positions) {
            if (pos == null || pos.length < 2) continue;

            double x = pos[0];
            double y = pos[1];

            spawn(x, y, powerUpWidth, powerUpHeight, type);
        }
    }

    public void spawn(double x, double y, double powerUpWidth, double powerUpHeight, PowerUpType type) {
        powerUps.add(new PowerUp(x, y, powerUpWidth, powerUpHeight, type));
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
            double screenX = p.getX() - camera.getOffsetX();
            double screenY = p.getY() - camera.getOffsetY();

            Color base = colorFor(p.getType());
            Color glowBlend = theme.getPowerUpGlow();
            Color rim = base.interpolate(glowBlend, 0.3).deriveColor(0, 1, 0.92, 1);
            Color glow = glowBlend.interpolate(base, 0.4).deriveColor(0, 1, 1.15, 0.65);

            gc.setFill(glow);
            gc.fillOval(screenX - 4, screenY - 4, p.getWidth() + 8, p.getHeight() + 8);

            gc.setFill(base);
            gc.fillOval(screenX, screenY, p.getWidth(), p.getHeight());
            gc.setStroke(rim);
            gc.setLineWidth(2);
            gc.strokeOval(screenX + 2, screenY + 2, p.getWidth() - 4, p.getHeight() - 4);

            gc.setFill(Color.WHITE);
            gc.fillText(symbolFor(p.getType()), screenX + p.getWidth() / 2.7, screenY + p.getHeight() / 1.8);
        }
    }

    private Color colorFor(PowerUpType type) {
        return switch (type) {
            case MUSHROOM -> Color.web("#FF7043");
            case FLOWER -> Color.web("#FFEB3B");
            case STAR -> Color.web("#81D4FA");
            case LIFE -> Color.web("#C62828");
        };
    }

    private String symbolFor(PowerUpType type) {
        return switch (type) {
            case MUSHROOM -> "M";
            case FLOWER -> "F";
            case STAR -> "✦";
            case LIFE -> "+";
        };
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    //detecting repo
}
