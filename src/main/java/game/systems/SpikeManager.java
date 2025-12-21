package game.systems;

import javafx.scene.canvas.GraphicsContext;
import game.utils.Theme;

import java.util.ArrayList;
import java.util.List;

public class SpikeManager {

    private final List<Spike> spikes = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    public void spawnFrom(List<double[]> positions) {
        if (positions == null || positions.isEmpty()) {
            System.out.println("SpikeManager: no spike spawns provided.");
            return;
        }

        System.out.println("SpikeManager: spawning " + positions.size() + " spikes...");
        for (double[] p : positions) {
            if (p == null || p.length < 2) continue;
            // Positions come in as top-left of tile. Place spike so it sits on top of the tile.
            double x = p[0];
            double y = p[1] + TileMap.TILE_SIZE - Spike.SIZE; // align bottom of spike with top of tile
            spikes.add(new Spike(x, y));
            System.out.println(String.format("  spike at world(%.1f, %.1f) -> place(%.1f, %.1f)", p[0], p[1], x, y));
        }
    }

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) {
        for (Spike spike : spikes) {
            if (spike.checkPlayerCollision(px, py, pw, ph)) {
                spike.deactivate();
                return true; // player touched spike
            }
        }
        return false;
    }

    public void render(GraphicsContext gc, Camera camera) {
        for (Spike spike : spikes) {
            spike.render(gc, camera, theme);
        }
    }

    public int getCount() {
        return spikes.size();
    }

    /** Returns world coordinates of active spikes (x,y) for debug rendering. */
    public List<double[]> getPositions() {
        List<double[]> out = new ArrayList<>();
        for (Spike s : spikes) {
            if (s == null) continue;
            out.add(new double[]{s.getX(), s.getY()});
        }
        return out;
    }
}
