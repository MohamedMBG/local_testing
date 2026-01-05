package game.systems; // Declares the package for this source file.

import javafx.scene.canvas.GraphicsContext;
import game.utils.Theme;

import java.util.ArrayList;
import java.util.List;

public class SpikeManager { // Defines a class.

    private final List<Spike> spikes = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) { // Begins a method or constructor with its signature.
        if (theme != null) { // Begins a method or constructor with its signature.
            this.theme = theme; // Executes: this.theme = theme;
        } // Closes a code block.
    } // Closes a code block.

    public void spawnFrom(List<double[]> positions) { // Begins a method or constructor with its signature.
        if (positions == null || positions.isEmpty()) { // Begins a method or constructor with its signature.
            System.out.println("SpikeManager: no spike spawns provided."); // Executes: System.out.println("SpikeManager: no spike spawns provided.");
            return; // Returns a value from the method.
        } // Closes a code block.

        System.out.println("SpikeManager: spawning " + positions.size() + " spikes..."); // Executes: System.out.println("SpikeManager: spawning " + positions.size() + " spikes...");
        for (double[] p : positions) { // Begins a method or constructor with its signature.
            if (p == null || p.length < 2) continue; // Evaluates a conditional branch.
            // Positions come in as top-left of tile. Place spike so it sits on top of the tile.
            double x = p[0];
            double y = p[1] + TileMap.TILE_SIZE - Spike.SIZE; // align bottom of spike with top of tile // Executes: double y = p[1] + TileMap.TILE_SIZE - Spike.SIZE; // align bottom of spike with top of tile
            spikes.add(new Spike(x, y)); // Executes: spikes.add(new Spike(x, y));
            System.out.println(String.format("  spike at world(%.1f, %.1f) -> place(%.1f, %.1f)", p[0], p[1], x, y)); // Executes: System.out.println(String.format("  spike at world(%.1f, %.1f) -> place(%.1f, %.1f)", p[0], p[1], x, y));
        } // Closes a code block.
    } // Closes a code block.

    public boolean checkPlayerCollision(double px, double py, double pw, double ph) { // Begins a method or constructor with its signature.
        for (Spike spike : spikes) { // Begins a method or constructor with its signature.
            if (spike.checkPlayerCollision(px, py, pw, ph)) { // Begins a method or constructor with its signature.
                spike.deactivate(); // Executes: spike.deactivate();
                return true; // player touched spike // Returns a value from the method.
            } // Closes a code block.
        } // Closes a code block.
        return false;
    } // Closes a code block.

    public void render(GraphicsContext gc, Camera camera) { // Begins a method or constructor with its signature.
        for (Spike spike : spikes) { // Begins a method or constructor with its signature.
            spike.render(gc, camera, theme); // Executes: spike.render(gc, camera, theme);
        } // Closes a code block.
    } // Closes a code block.

    public int getCount() { // Begins a method or constructor with its signature.
        return spikes.size(); // Returns a value from the method.
    } // Closes a code block.

    /** Returns world coordinates of active spikes (x,y) for debug rendering. */
    public List<double[]> getPositions() { // Begins a method or constructor with its signature.
        List<double[]> out = new ArrayList<>();
        for (Spike s : spikes) { // Begins a method or constructor with its signature.
            if (s == null) continue; // Evaluates a conditional branch.
            out.add(new double[]{s.getX(), s.getY()}); // Executes: out.add(new double[]{s.getX(), s.getY()});
        } // Closes a code block.
        return out;
    } // Closes a code block.
} // Closes a code block.
