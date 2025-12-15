package game.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelLoader {

    public static class LevelData {
        private final TileMap tileMap;

        private final double playerSpawnX;
        private final double playerSpawnY;

        private final List<double[]> coinSpawns;
        private final List<double[]> powerUpSpawns;
        private final List<double[]> enemySpawns;

        public LevelData(TileMap tileMap,
                         double playerSpawnX,
                         double playerSpawnY,
                         List<double[]> coinSpawns,
                         List<double[]> powerUpSpawns,
                         List<double[]> enemySpawns) {

            this.tileMap = tileMap;
            this.playerSpawnX = playerSpawnX;
            this.playerSpawnY = playerSpawnY;
            this.coinSpawns = coinSpawns;
            this.powerUpSpawns = powerUpSpawns;
            this.enemySpawns = enemySpawns;
        }

        public TileMap getTileMap() {
            return tileMap;
        }

        public double getPlayerSpawnX() {
            return playerSpawnX;
        }

        public double getPlayerSpawnY() {
            return playerSpawnY;
        }

        /** Each entry is a double[2] = { x, y } in world coordinates (pixels). */
        public List<double[]> getCoinSpawns() {
            return Collections.unmodifiableList(coinSpawns);
        }

        public List<double[]> getPowerUpSpawns() {
            return Collections.unmodifiableList(powerUpSpawns);
        }

        public List<double[]> getEnemySpawns() {
            return Collections.unmodifiableList(enemySpawns);
        }
    }

    /**
     * Conventions:
     *  '#' -> solid tile
     *  '.' or ' ' -> empty
     *  'P' -> player spawn
     *  'C' -> coin spawn
     *  'U' -> power-up spawn
     *  'E' -> enemy spawn
     *
     * All lines must have the same length.
     */
    public LevelData loadFromLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Level lines must not be null or empty.");
        }

        int height = lines.size();
        int width = lines.get(0).length();

        for (String line : lines) {
            if (line.length() != width) {
                throw new IllegalArgumentException("All level lines must have the same length.");
            }
        }

        // Build collision map (# => solid)
        TileMap tileMap = TileMap.fromCharLines(lines);

        double playerSpawnX = 0;
        double playerSpawnY = 0;
        boolean playerSpawnFound = false;

        List<double[]> coinSpawns = new ArrayList<>();
        List<double[]> powerUpSpawns = new ArrayList<>();
        List<double[]> enemySpawns = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);

            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);

                double worldX = x * TileMap.TILE_SIZE;
                double worldY = y * TileMap.TILE_SIZE;

                switch (c) {
                    case 'P' -> {
                        playerSpawnX = worldX;
                        playerSpawnY = worldY;
                        playerSpawnFound = true;
                    }
                    case 'C' -> coinSpawns.add(new double[]{worldX, worldY});
                    case 'U' -> powerUpSpawns.add(new double[]{worldX, worldY});
                    case 'E' -> enemySpawns.add(new double[]{worldX, worldY});
                    default -> {
                        // handled by TileMap (e.g. '#', '.', ' ')
                    }
                }
            }
        }

        if (!playerSpawnFound) {
            throw new IllegalStateException("No player spawn 'P' found in level lines.");
        }

        return new LevelData(tileMap, playerSpawnX, playerSpawnY, coinSpawns, powerUpSpawns, enemySpawns);
    }
}
