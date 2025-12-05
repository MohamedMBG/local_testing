package game.systems;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelLoader {

    /**
     * Holds all data needed to start a level.
     * This is an inner static class so it stays grouped with the loader,
     * and you don't need to touch other packages like core.
     */

    public static class LevelData  {
        private final TileMap tileMap;

        private double playerSpawnX, playerSpawnY;

        private List<double[]> coinSpawns;

        private List<double[]> powerUpSpawns;

        public LevelData(TileMap tileMap, double playerSpawnX, double playerSpawnY, List<double[]> coinSpawns, List<double[]> powerUpSpawns) {
            this.tileMap = tileMap;
            this.playerSpawnX = playerSpawnX;
            this.playerSpawnY = playerSpawnY;
            this.coinSpawns = coinSpawns;
            this.powerUpSpawns = powerUpSpawns;
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
    }

    /**
     * Parses a level from a list of strings.
     *
     * Conventions:
     *  '#' -> solid tile
     *  '.' or ' ' -> empty
     *  'P' -> player spawn
     *  'C' -> coin spawn
     *  'U' -> power-up spawn
     *
     * All lines must have the same length.
     *
     * IMPORTANT:
     *  - TileMap uses the same char lines to build the collision map.
     *  - Positions are converted to world coordinates in pixels, where
     *    (tileX * TILE_SIZE, tileY * TILE_SIZE) is the top-left of the tile.
     */

    public LevelData loadFromLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Level lines must not be null or empty.");
        }

        int height = lines.size();
        int width = lines.get(0).length();

        // Validate line lengths
        for (String line : lines) {
            if (line.length() != width) {
                throw new IllegalArgumentException("All level lines must have the same length.");
            }
        }

        // First, create the TileMap using the same lines.
        TileMap tileMap = TileMap.fromCharLines(lines);

        // These will store world positions (in pixels)
        double playerSpawnX = 0;
        double playerSpawnY = 0;
        boolean playerSpawnFound = false;

        List<double[]> coinSpawns = new ArrayList<>();
        List<double[]> powerUpSpawns = new ArrayList<>();

        // Scan the lines for special characters: P, C, U
        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);

                // Convert tile coordinates to world coordinates (top-left of the tile)
                double worldX = x * TileMap.TILE_SIZE;
                double worldY = y * TileMap.TILE_SIZE;

                switch (c) {
                    case 'P' -> {
                        // Player spawn (if multiple P exist, we use the last one found)
                        playerSpawnX = worldX;
                        playerSpawnY = worldY;
                        playerSpawnFound = true;
                    }
                    case 'C' -> {
                        // Coin spawn
                        coinSpawns.add(new double[]{worldX, worldY});
                    }
                    case 'U' -> {
                        // Power-up spawn
                        powerUpSpawns.add(new double[]{worldX, worldY});
                    }
                    default -> {
                        // Other chars handled by TileMap (e.g. '#', '.', ' ')
                    }
                }
            }
        }

        if (!playerSpawnFound) {
            // You can decide whether to treat this as an error or allow it.
            // Here we choose to throw, because usually every level needs a player spawn.
            throw new IllegalStateException("No player spawn 'P' found in level lines.");
        }

        return new LevelData(tileMap, playerSpawnX, playerSpawnY, coinSpawns, powerUpSpawns);
    }

}
