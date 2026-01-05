package game.systems; // Declares the package for this source file.

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelLoader { // Defines a class.

    public static class LevelData { // Defines a class.
        private final TileMap tileMap;

        private final double playerSpawnX;
        private final double playerSpawnY;

        private final List<double[]> coinSpawns;
        private final List<double[]> powerUpSpawns;
        private final List<double[]> enemySpawns;
        private final List<double[]> spikeSpawns;

        public LevelData(TileMap tileMap, // Executes: public LevelData(TileMap tileMap,
                         double playerSpawnX, // Executes: double playerSpawnX,
                         double playerSpawnY, // Executes: double playerSpawnY,
                         List<double[]> coinSpawns, // Executes: List<double[]> coinSpawns,
                         List<double[]> powerUpSpawns, // Executes: List<double[]> powerUpSpawns,
                         List<double[]> enemySpawns, // Executes: List<double[]> enemySpawns,
                         List<double[]> spikeSpawns) { // Executes: List<double[]> spikeSpawns) {

            this.tileMap = tileMap; // Executes: this.tileMap = tileMap;
            this.playerSpawnX = playerSpawnX; // Executes: this.playerSpawnX = playerSpawnX;
            this.playerSpawnY = playerSpawnY; // Executes: this.playerSpawnY = playerSpawnY;
            this.coinSpawns = coinSpawns; // Executes: this.coinSpawns = coinSpawns;
            this.powerUpSpawns = powerUpSpawns; // Executes: this.powerUpSpawns = powerUpSpawns;
            this.enemySpawns = enemySpawns; // Executes: this.enemySpawns = enemySpawns;
            this.spikeSpawns = spikeSpawns; // Executes: this.spikeSpawns = spikeSpawns;
        } // Closes a code block.

        public TileMap getTileMap() { // Begins a method or constructor with its signature.
            return tileMap;
        } // Closes a code block.

        public double getPlayerSpawnX() { // Begins a method or constructor with its signature.
            return playerSpawnX;
        } // Closes a code block.

        public double getPlayerSpawnY() { // Begins a method or constructor with its signature.
            return playerSpawnY;
        } // Closes a code block.

        /** Each entry is a double[2] = { x, y } in world coordinates (pixels). */
        public List<double[]> getCoinSpawns() { // Begins a method or constructor with its signature.
            return Collections.unmodifiableList(coinSpawns); // Returns a value from the method.
        } // Closes a code block.

        public List<double[]> getPowerUpSpawns() { // Begins a method or constructor with its signature.
            return Collections.unmodifiableList(powerUpSpawns); // Returns a value from the method.
        } // Closes a code block.

        public List<double[]> getEnemySpawns() { // Begins a method or constructor with its signature.
            return Collections.unmodifiableList(enemySpawns); // Returns a value from the method.
        } // Closes a code block.

        public List<double[]> getSpikeSpawns() { // Begins a method or constructor with its signature.
            return Collections.unmodifiableList(spikeSpawns); // Returns a value from the method.
        } // Closes a code block.
    } // Closes a code block.

    /**
     * Conventions:
     *  '#' -> solid tile
     *  '.' or ' ' -> empty
     *  'P' -> player spawn
     *  'C' -> coin spawn
     *  'U' -> power-up spawn
     *  'E' -> enemy spawn
     *  'S' -> spike spawn
     *
     * All lines must have the same length.
     */
    public LevelData loadFromLines(List<String> lines) { // Begins a method or constructor with its signature.
        if (lines == null || lines.isEmpty()) { // Begins a method or constructor with its signature.
            throw new IllegalArgumentException("Level lines must not be null or empty."); // Throws an exception.
        } // Closes a code block.

        int height = lines.size();
        int width = lines.get(0).length();

        for (String line : lines) { // Begins a method or constructor with its signature.
            if (line.length() != width) { // Begins a method or constructor with its signature.
                throw new IllegalArgumentException("All level lines must have the same length."); // Throws an exception.
            } // Closes a code block.
        } // Closes a code block.

        // Build collision map (# => solid)
        TileMap tileMap = TileMap.fromCharLines(lines);

        double playerSpawnX = 0;
        double playerSpawnY = 0;
        boolean playerSpawnFound = false;

        List<double[]> coinSpawns = new ArrayList<>();
        List<double[]> powerUpSpawns = new ArrayList<>();
        List<double[]> enemySpawns = new ArrayList<>();
        List<double[]> spikeSpawns = new ArrayList<>();

        for (int y = 0; y < height; y++) { // Begins a method or constructor with its signature.
            String line = lines.get(y);

            for (int x = 0; x < width; x++) { // Begins a method or constructor with its signature.
                char c = line.charAt(x);

                double worldX = x * TileMap.TILE_SIZE;
                double worldY = y * TileMap.TILE_SIZE;

                switch (c) { // Begins a method or constructor with its signature.
                    case 'P' -> { // Defines a switch case branch.
                        playerSpawnX = worldX; // Executes: playerSpawnX = worldX;
                        playerSpawnY = worldY; // Executes: playerSpawnY = worldY;
                        playerSpawnFound = true; // Executes: playerSpawnFound = true;
                    } // Closes a code block.
                    case 'C' -> coinSpawns.add(new double[]{worldX, worldY}); // Defines a switch case branch.
                    case 'U' -> powerUpSpawns.add(new double[]{worldX, worldY}); // Defines a switch case branch.
                    case 'E' -> enemySpawns.add(new double[]{worldX, worldY}); // Defines a switch case branch.
                    case 'S' -> spikeSpawns.add(new double[]{worldX, worldY}); // Defines a switch case branch.
                    default -> { // Defines the default switch branch.
                        // handled by TileMap (e.g. '#', '.', ' ')
                    } // Closes a code block.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.

        if (!playerSpawnFound) { // Begins a method or constructor with its signature.
            throw new IllegalStateException("No player spawn 'P' found in level lines."); // Throws an exception.
        } // Closes a code block.

        return new LevelData(tileMap, playerSpawnX, playerSpawnY, coinSpawns, powerUpSpawns, enemySpawns, spikeSpawns); // Returns a value from the method.
    } // Closes a code block.
} // Closes a code block.
