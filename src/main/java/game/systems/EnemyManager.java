package game.systems;

import game.core.Player;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.List;

public class EnemyManager {

    private final List<Enemy> enemies = new ArrayList<>();

    public void spawnFrom(List<double[]> positions) {
        for (double[] p : positions) {
            enemies.add(new Enemy(p[0], p[1]));
        }
    }

    public boolean update(double dt, Player player, TileMap map) {
        for (Enemy e : enemies) {
            e.update(dt, map);

            if (e.checkPlayerCollision(
                    player.getPlayerX(),
                    player.getPlayerY(),
                    player.getWidth(),
                    player.getHeight())) {

                // Jump kill logic
                if (player.getVy() > 0) {
                    e.kill();
                    player.setVy(-400);
                } else {
                    return true; // player hit
                }
            }
        }
        return false;
    }

    public void render(GraphicsContext gc, Camera camera) {
        for (Enemy e : enemies) {
            e.render(gc, camera);
        }
    }
}
