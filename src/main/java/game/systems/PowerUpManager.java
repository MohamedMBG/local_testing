package game.systems;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PowerUpManager {

    public static final double DEFAULT_SIZE = 24.0;

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

            drawBadge(gc, screenX, screenY, p.getWidth(), p.getHeight(), p.getType());
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

    private void drawBadge(GraphicsContext gc, double x, double y, double width, double height, PowerUpType type) {
        double inset = Math.min(width, height) * 0.12;
        double badgeX = x + inset;
        double badgeY = y + inset;
        double badgeW = width - inset * 2;
        double badgeH = height - inset * 2;

        Color baseColor = colorFor(type);
        Color glow = lighten(baseColor, 0.4);
        Color shade = darken(baseColor, 0.25);

        // Soft drop shadow
        gc.setFill(new Color(0, 0, 0, 0.25));
        gc.fillOval(badgeX + 2, badgeY + 3, badgeW, badgeH);

        // Gradient badge body
        LinearGradient gradient = new LinearGradient(
                0, badgeY, 0, badgeY + badgeH,
                false, CycleMethod.NO_CYCLE,
                new Stop(0, glow),
                new Stop(1, shade)
        );
        gc.setFill(gradient);
        gc.fillOval(badgeX, badgeY, badgeW, badgeH);

        // Glossy highlight
        gc.setFill(new Color(1, 1, 1, 0.18));
        gc.fillArc(badgeX + badgeW * 0.1, badgeY + badgeH * 0.08, badgeW * 0.8, badgeH * 0.6, 0, 180);

        // Ring outline
        gc.setStroke(lighten(baseColor, 0.55));
        gc.setLineWidth(Math.max(1.5, badgeW * 0.08));
        gc.strokeOval(badgeX, badgeY, badgeW, badgeH);
        gc.setStroke(darken(baseColor, 0.45));
        gc.setLineWidth(Math.max(1.2, badgeW * 0.04));
        gc.strokeOval(badgeX + badgeW * 0.02, badgeY + badgeH * 0.02, badgeW * 0.96, badgeH * 0.96);

        drawSymbol(gc, badgeX, badgeY, badgeW, badgeH, type);
    }

    private void drawSymbol(GraphicsContext gc, double x, double y, double width, double height, PowerUpType type) {
        double cx = x + width / 2.0;
        double cy = y + height / 2.0;
        double size = Math.min(width, height) * 0.65;

        switch (type) {
            case MUSHROOM -> drawMushroom(gc, cx, cy, size);
            case FLOWER -> drawFlower(gc, cx, cy, size);
            case STAR -> drawStar(gc, cx, cy, size * 0.9);
            case LIFE -> drawHeart(gc, cx, cy, size * 0.85);
        }
    }

    private void drawMushroom(GraphicsContext gc, double cx, double cy, double size) {
        double capHeight = size * 0.45;
        double stemHeight = size * 0.22;
        double stemWidth = size * 0.35;

        double capX = cx - size * 0.35;
        double capY = cy - capHeight * 0.8;

        // Cap
        Color capColor = Color.web("#FF7043");
        gc.setFill(capColor);
        gc.fillArc(capX, capY, size * 0.7, capHeight * 1.2, 0, 180);
        gc.setFill(lighten(capColor, 0.45));
        gc.fillArc(capX + size * 0.08, capY + capHeight * 0.1, size * 0.54, capHeight * 0.9, 10, 160);

        // Spots
        gc.setFill(Color.WHITE);
        gc.fillOval(cx - size * 0.24, capY + capHeight * 0.25, size * 0.16, size * 0.16);
        gc.fillOval(cx + size * 0.02, capY + capHeight * 0.35, size * 0.12, size * 0.12);
        gc.fillOval(cx - size * 0.07, capY + capHeight * 0.1, size * 0.14, size * 0.14);

        // Stem
        gc.setFill(Color.web("#F0E7D8"));
        double stemX = cx - stemWidth / 2;
        double stemY = cy - stemHeight * 0.1;
        gc.fillRoundRect(stemX, stemY, stemWidth, stemHeight, size * 0.2, size * 0.2);
        gc.setStroke(new Color(0, 0, 0, 0.35));
        gc.setLineWidth(Math.max(1, size * 0.025));
        gc.strokeRoundRect(stemX, stemY, stemWidth, stemHeight, size * 0.2, size * 0.2);
    }

    private void drawFlower(GraphicsContext gc, double cx, double cy, double size) {
        Color petal = Color.web("#FFEB3B");
        Color center = Color.web("#FF6F00");

        double petalRadius = size * 0.18;
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            double px = cx + Math.cos(angle) * size * 0.28 - petalRadius;
            double py = cy + Math.sin(angle) * size * 0.28 - petalRadius;
            gc.setFill(lighten(petal, 0.12));
            gc.fillOval(px, py, petalRadius * 2, petalRadius * 2);
            gc.setStroke(darken(petal, 0.25));
            gc.setLineWidth(Math.max(1, size * 0.02));
            gc.strokeOval(px, py, petalRadius * 2, petalRadius * 2);
        }

        gc.setFill(center);
        double coreRadius = size * 0.2;
        gc.fillOval(cx - coreRadius, cy - coreRadius, coreRadius * 2, coreRadius * 2);
        gc.setStroke(darken(center, 0.3));
        gc.setLineWidth(Math.max(1, size * 0.025));
        gc.strokeOval(cx - coreRadius, cy - coreRadius, coreRadius * 2, coreRadius * 2);
    }

    private void drawStar(GraphicsContext gc, double cx, double cy, double size) {
        Color star = Color.web("#FFF59D");
        double outer = size / 2;
        double inner = outer * 0.45;
        double[] xs = new double[10];
        double[] ys = new double[10];

        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            double radius = (i % 2 == 0) ? outer : inner;
            xs[i] = cx + Math.cos(angle) * radius;
            ys[i] = cy + Math.sin(angle) * radius;
        }

        gc.setFill(star);
        gc.fillPolygon(xs, ys, 10);
        gc.setStroke(darken(star, 0.35));
        gc.setLineWidth(Math.max(1, size * 0.04));
        gc.strokePolygon(xs, ys, 10);
    }

    private void drawHeart(GraphicsContext gc, double cx, double cy, double size) {
        Color heart = Color.web("#E53935");
        double radius = size * 0.24;

        gc.setFill(heart);
        gc.fillOval(cx - radius - size * 0.08, cy - radius * 1.1, radius * 2, radius * 2);
        gc.fillOval(cx - radius + size * 0.08, cy - radius * 1.1, radius * 2, radius * 2);

        double[] xs = {cx - size * 0.42, cx, cx + size * 0.42};
        double[] ys = {cy - radius * 0.2, cy + size * 0.48, cy - radius * 0.2};
        gc.fillPolygon(xs, ys, 3);

        gc.setStroke(darken(heart, 0.3));
        gc.setLineWidth(Math.max(1, size * 0.035));
        gc.strokeOval(cx - radius - size * 0.08, cy - radius * 1.1, radius * 2, radius * 2);
        gc.strokeOval(cx - radius + size * 0.08, cy - radius * 1.1, radius * 2, radius * 2);
        gc.strokePolygon(xs, ys, 3);
    }

    private Color lighten(Color color, double amount) {
        return color.interpolate(Color.WHITE, amount);
    }

    private Color darken(Color color, double amount) {
        return color.interpolate(Color.BLACK, amount);
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
