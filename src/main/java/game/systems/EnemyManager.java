package game.systems;

import game.core.Player;
import game.utils.Theme;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    private final List<Enemy> enemies = new ArrayList<>();
    private Theme theme = Theme.SUMMER;

    public void setTheme(Theme theme) {
        if (theme != null) {
            this.theme = theme;
        }
    }

    public void spawnFrom(List<double[]> positions) {
        for (double[] p : positions) {
            enemies.add(new Enemy(p[0], p[1]));
        }
    }

    public boolean update(double dt, Player player, TileMap map) {
        for (Enemy e : enemies) {
            // Make enemy move towards the player on X
            double dir = Math.signum(player.getPlayerX() - e.getX());
            if (dir != 0) {
                e.setVelocityX(dir * 70); // slightly faster than player
            }

            e.update(dt, map);

            if (e.checkPlayerCollision(
                    player.getPlayerX(),
                    player.getPlayerY(),
                    player.getWidth(),
                    player.getHeight())) {
                // Player dies when touching enemies
                return true;
            }
        }
        return false;
    }

    public void render(GraphicsContext gc, Camera camera, Theme theme) {
        for (Enemy e : enemies) {
            e.render(gc, camera, theme != null ? theme : this.theme);
        }
    }
}
