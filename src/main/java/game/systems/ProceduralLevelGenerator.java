package game.systems; // Declares the package for this source file.

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates random Mario-like ASCII levels that your existing LevelLoader can read.
 *
 * Legend (must match LevelLoader):
 *  '.' empty
 *  '#' solid tile (collision)
 *  'P' player spawn
 *  'C' coin
 *  'U' power-up
 *  'E' enemy
 *  'S' spike
 */
public class ProceduralLevelGenerator { // Defines a class.

    private ProceduralLevelGenerator() {} // Executes: private ProceduralLevelGenerator() {}

    /**
     * @param difficulty 1..N (higher = harder)
     * @param widthTiles map width in tiles (e.g. 90..140)
     * @param heightTiles map height in tiles (e.g. 8..14)
     * @param seed random seed (use System.currentTimeMillis() for new each run)
     */
    public static List<String> generate(int difficulty, int widthTiles, int heightTiles, long seed) { // Begins a method or constructor with its signature.
        if (widthTiles < 40) widthTiles = 40; // Evaluates a conditional branch.
        if (heightTiles < 6) heightTiles = 6; // Evaluates a conditional branch.
        if (difficulty < 1) difficulty = 1; // Evaluates a conditional branch.

        Random rng = new Random(seed);

        char[][] g = new char[heightTiles][widthTiles];

        // Fill empty
        for (int y = 0; y < heightTiles; y++) { // Begins a method or constructor with its signature.
            for (int x = 0; x < widthTiles; x++) { // Begins a method or constructor with its signature.
                g[y][x] = '.'; // Executes: g[y][x] = '.';
            } // Closes a code block.
        } // Closes a code block.

        int groundY = heightTiles - 1;

        // Always start with solid ground
        for (int x = 0; x < widthTiles; x++) { // Begins a method or constructor with its signature.
            g[groundY][x] = '#'; // Executes: g[groundY][x] = '#';
        } // Closes a code block.

        // Make some gaps in the ground (harder levels = more + wider gaps)
        double gapChance = clamp(0.02 + (difficulty - 1) * 0.012, 0.0, 0.18);
        int maxGap = Math.min(7, 2 + difficulty);          // max continuous empty tiles // Executes: int maxGap = Math.min(7, 2 + difficulty);          // max continuous empty tiles
        int safeStart = 0;
        int safeEnd = 10;
        int safeGoal = widthTiles - 12;

        for (int x = safeEnd; x < safeGoal; x++) { // Begins a method or constructor with its signature.
            if (rng.nextDouble() < gapChance) { // Begins a method or constructor with its signature.
                int gap = 1 + rng.nextInt(maxGap);
                for (int i = 0; i < gap && x + i < safeGoal; i++) { // Begins a method or constructor with its signature.
                    g[groundY][x + i] = '.'; // Executes: g[groundY][x + i] = '.';
                } // Closes a code block.
                x += gap; // skip ahead // Executes: x += gap; // skip ahead
            } // Closes a code block.
        } // Closes a code block.

        // Platforms (floating solid tiles)
        int platforms = 5 + difficulty * 2;
        int minLen = 4;
        int maxLen = Math.min(14, 7 + difficulty); // longer platforms in harder levels // Executes: int maxLen = Math.min(14, 7 + difficulty); // longer platforms in harder levels

        for (int i = 0; i < platforms; i++) { // Begins a method or constructor with its signature.
            int len = minLen + rng.nextInt(Math.max(1, maxLen - minLen + 1));
            int px = 8 + rng.nextInt(Math.max(1, widthTiles - 16 - len));
            // Choose a platform height: between (groundY-2) and (groundY-4-difficulty/2) but not above 1
            int maxUp = Math.min(4 + difficulty / 2, groundY - 2);
            int py = groundY - 2 - rng.nextInt(Math.max(1, maxUp));
            py = Math.max(1, py); // Executes: py = Math.max(1, py);

            for (int x = px; x < px + len && x < widthTiles; x++) { // Begins a method or constructor with its signature.
                // Don't overwrite gaps on ground row, but platforms are above, so ok
                g[py][x] = '#'; // Executes: g[py][x] = '#';
            } // Closes a code block.
        } // Closes a code block.

        // Player spawn
        int playerX = 2;
        int playerY = groundY - 1;
        g[playerY][playerX] = 'P'; // Executes: g[playerY][playerX] = 'P';

        // Spikes sit one tile above ground, only where ground exists under them
        double spikeChance = clamp(0.03 + difficulty * 0.02, 0.0, 0.28);
        for (int x = safeEnd; x < safeGoal; x++) { // Begins a method or constructor with its signature.
            if (g[groundY][x] == '#' // Evaluates a conditional branch.
                    && g[playerY][x] == '.' // Executes: && g[playerY][x] == '.'
                    && rng.nextDouble() < spikeChance) { // Begins a method or constructor with its signature.
                // keep area near player spawn clean
                if (x < 8) continue; // Evaluates a conditional branch.
                g[playerY][x] = 'S'; // Executes: g[playerY][x] = 'S';
            } // Closes a code block.
        } // Closes a code block.

        // Coins and enemies: place above solid tiles
        double coinChance = clamp(0.22 - (difficulty - 1) * 0.03, 0.06, 0.25);   // fewer coins as difficulty increases // Executes: double coinChance = clamp(0.22 - (difficulty - 1) * 0.03, 0.06, 0.25);   // fewer coins as difficulty increases
        double enemyChance = clamp(0.03 + (difficulty - 1) * 0.02, 0.03, 0.20);  // more enemies as difficulty increases // Executes: double enemyChance = clamp(0.03 + (difficulty - 1) * 0.02, 0.03, 0.20);  // more enemies as difficulty increases
        double powerUpChance = clamp(0.05 + (3 - difficulty) * 0.01, 0.03, 0.12); // common on easy levels, rarer on hard ones // Executes: double powerUpChance = clamp(0.05 + (3 - difficulty) * 0.01, 0.03, 0.12); // common on easy levels, rarer on hard ones

        boolean powerUpPlaced = false;
        List<int[]> powerUpCandidates = new ArrayList<>();

        for (int y = 1; y < heightTiles; y++) { // Begins a method or constructor with its signature.
            for (int x = 1; x < widthTiles - 1; x++) { // Begins a method or constructor with its signature.

                // keep start + goal areas simpler
                if (x < safeEnd || x > safeGoal) continue; // Evaluates a conditional branch.

                if (g[y][x] == '#') { // Begins a method or constructor with its signature.
                    int aboveY = y - 1;
                    if (aboveY < 0) continue; // Evaluates a conditional branch.

                    // only place if above is empty
                    if (g[aboveY][x] == '.') { // Begins a method or constructor with its signature.
                        powerUpCandidates.add(new int[]{x, aboveY}); // Executes: powerUpCandidates.add(new int[]{x, aboveY});
                        // Decide coin vs enemy
                        double r = rng.nextDouble();
                        if (r < enemyChance) { // Begins a method or constructor with its signature.
                            g[aboveY][x] = 'E'; // Executes: g[aboveY][x] = 'E';
                        } else if (r < enemyChance + powerUpChance) { // Begins a method or constructor with its signature.
                            g[aboveY][x] = 'U'; // Executes: g[aboveY][x] = 'U';
                            powerUpPlaced = true; // Executes: powerUpPlaced = true;
                        } else if (r < enemyChance + powerUpChance + coinChance) { // Begins a method or constructor with its signature.
                            g[aboveY][x] = 'C'; // Executes: g[aboveY][x] = 'C';
                        } // Closes a code block.
                    } // Closes a code block.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.

        // Guarantee at least one power-up spawn per level so the mechanic exists
        if (!powerUpPlaced && !powerUpCandidates.isEmpty()) { // Begins a method or constructor with its signature.
            int[] spot = powerUpCandidates.get(rng.nextInt(powerUpCandidates.size()));
            g[spot[1]][spot[0]] = 'U'; // Executes: g[spot[1]][spot[0]] = 'U';
        } // Closes a code block.

        // Make sure we didn't block the player tile
        g[playerY][playerX] = 'P'; // Executes: g[playerY][playerX] = 'P';

        // Convert to List<String>
        List<String> lines = new ArrayList<>();
        for (int y = 0; y < heightTiles; y++) { // Begins a method or constructor with its signature.
            lines.add(new String(g[y])); // Executes: lines.add(new String(g[y]));
        } // Closes a code block.
        return lines;
    } // Closes a code block.

    private static double clamp(double v, double min, double max) { // Begins a method or constructor with its signature.
        return Math.max(min, Math.min(max, v)); // Returns a value from the method.
    } // Closes a code block.
} // Closes a code block.
