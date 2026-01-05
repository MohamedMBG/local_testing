package game.systems; // Declares the package for this source file.

import game.core.Player;
import game.utils.Theme;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager { // Defines a class.

    private final List<Enemy> enemies = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) { // Begins a method or constructor with its signature.
        if (theme != null) { // Begins a method or constructor with its signature.
            this.theme = theme; // Executes: this.theme = theme;
        } // Closes a code block.
    } // Closes a code block.

    public void spawnFrom(List<double[]> positions) { // Begins a method or constructor with its signature.
        for (double[] p : positions) { // Begins a method or constructor with its signature.
            enemies.add(new Enemy(p[0], p[1])); // Executes: enemies.add(new Enemy(p[0], p[1]));
        } // Closes a code block.
    } // Closes a code block.

    public boolean update(double dt, Player player, TileMap map) { // Begins a method or constructor with its signature.
        for (Enemy e : enemies) { // Begins a method or constructor with its signature.
            // Make enemy move towards the player on X
            double dir = Math.signum(player.getPlayerX() - e.getX());
            if (dir != 0) { // Begins a method or constructor with its signature.
                e.setVelocityX(dir * 70); // slightly faster than player // Executes: e.setVelocityX(dir * 70); // slightly faster than player
            } // Closes a code block.

            e.update(dt, map); // Executes: e.update(dt, map);

            if (e.checkPlayerCollision( // Evaluates a conditional branch.
                    player.getPlayerX(), // Executes: player.getPlayerX(),
                    player.getPlayerY(), // Executes: player.getPlayerY(),
                    player.getWidth(), // Executes: player.getWidth(),
                    player.getHeight())) { // Begins a method or constructor with its signature.
                // Player dies when touching enemies
                return true;
            } // Closes a code block.
        } // Closes a code block.
        return false;
    } // Closes a code block.

    public void render(GraphicsContext gc, Camera camera, Theme theme) { // Begins a method or constructor with its signature.
        for (Enemy e : enemies) { // Begins a method or constructor with its signature.
            e.render(gc, camera, theme != null ? theme : this.theme); // Executes: e.render(gc, camera, theme != null ? theme : this.theme);
        } // Closes a code block.
    } // Closes a code block.
} // Closes a code block.
