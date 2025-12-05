package game.systems;

import java.util.ArrayList;
import java.util.List;

public class TileMap {
    /** size of one tile in pixel **/
    public static final int TILE_SIZE = 32;

    private final int[][] tiles;
    private final int widthInTiles;
    private final int heightInTiles;

    public TileMap(int[][] tiles) {
        if (tiles == null || tiles.length == 0 || tiles[0].length == 0) {
            throw new IllegalArgumentException("Tiles array must not be null or empty.");
        }

        this.heightInTiles = tiles.length;
        this.widthInTiles = tiles[0].length;

        // Copy to protect internal data from outside modification
        this.tiles = new int[heightInTiles][widthInTiles];
        for (int y = 0; y < heightInTiles; y++) {
            if (tiles[y].length != widthInTiles) {
                throw new IllegalArgumentException("All rows in tiles array must have the same length.");
            }
            System.arraycopy(tiles[y], 0, this.tiles[y], 0, widthInTiles);
        }
    }

    /**
     * Creates a TileMap from a list of character strings (like a text-based level format).
     * This is useful for designing levels in text files. Each character represents a tile type.
     * Logic: Parses each line character by character, converting symbols to tile IDs using charToTileId().
     * This allows for human-readable level design with symbols like '#' for walls.
     *
     * @param lines List of strings where each string is a row of the tilemap
     * @return A new TileMap instance
     */
    public static TileMap fromCharLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Lines must not be null or empty.");
        }

        int height = lines.size();
        int width = lines.get(0).length();

        int[][] tiles = new int[height][width];

        for (int y = 0; y < height; y++) {
            String line = lines.get(y);
            if (line.length() != width) {
                throw new IllegalArgumentException("All lines must have the same length.");
            }

            for (int x = 0; x < width; x++) {
                char c = line.charAt(x);
                tiles[y][x] = charToTileId(c);
            }
        }

        return new TileMap(tiles);
    }

    /**
     * Converts a character symbol to a tile ID for the internal tile representation.
     * Logic: Uses a switch statement to map level design symbols to game tile types.
     * '#' represents solid blocks (like walls or platforms in Mario).
     * Other characters (spaces, dots, spawn markers) are treated as empty space (0) for collision,
     * but can be used by LevelLoader to place entities like players, coins, or power-ups.
     *
     * @param c Character from the level design file
     * @return Integer tile ID (0 for empty, 1 for solid)
     */
    private static int charToTileId(char c) {
        switch (c) {
            case '#':
                return 1; // solid block
            // These characters are treated as "empty" for the tilemap
            // but LevelLoader can still use them for spawns.
            case ' ':
            case '.':
            case 'P': // player spawn
            case 'C': // coin spawn
            case 'U': // power-up spawn
            default:
                return 0; // empty space
        }
    }

    // -------------------------------------------------
    // Basic getters
    // -------------------------------------------------

    /**
     * Returns the width of the tilemap in tiles (not pixels).
     * Logic: Simple getter for the pre-calculated width in tile units.
     *
     * @return Width in tile units
     */
    public int getWidthInTiles() {
        return widthInTiles;
    }

    /**
     * Returns the height of the tilemap in tiles (not pixels).
     * Logic: Simple getter for the pre-calculated height in tile units.
     *
     * @return Height in tile units
     */
    public int getHeightInTiles() {
        return heightInTiles;
    }

    /**
     * Returns the width of the tilemap in pixels.
     * Logic: Converts tile dimensions to pixel dimensions by multiplying with TILE_SIZE.
     * This is useful for rendering and positioning entities in the game world.
     *
     * @return Width in pixels
     */
    public int getWidthInPixels() {
        return widthInTiles * TILE_SIZE;
    }

    /**
     * Returns the height of the tilemap in pixels.
     * Logic: Converts tile dimensions to pixel dimensions by multiplying with TILE_SIZE.
     * This is useful for rendering and positioning entities in the game world.
     *
     * @return Height in pixels
     */
    public int getHeightInPixels() {
        return heightInTiles * TILE_SIZE;
    }

    /**
     * Returns the raw tile ID at (x, y) in tile coordinates.
     * Logic: Checks bounds first - if out of bounds, returns 0 (empty space).
     * This prevents array index errors and treats out-of-bounds as passable terrain.
     * If in bounds, returns the tile ID from the internal 2D array.
     *
     * @param tileX X coordinate in tile units
     * @param tileY Y coordinate in tile units
     * @return Tile ID (0 for empty, 1 for solid, etc.)
     */
    public int getTile(int tileX, int tileY) {
        if (!isInBounds(tileX, tileY)) {
            return 0;
        }
        return tiles[tileY][tileX];
    }

    /**
     * Checks if tile (x, y) in tile coordinates is solid.
     * Logic: Uses getTile() to retrieve the tile ID and checks if it equals 1 (solid).
     * This is the core collision detection logic for tile-based platforms in Mario.
     * Out of bounds tiles return false (treating outside world as non-solid/passable).
     *
     * @param tileX X coordinate in tile units
     * @param tileY Y coordinate in tile units
     * @return true if tile is solid (ID == 1), false otherwise
     */
    public boolean isSolidTile(int tileX, int tileY) {
        return getTile(tileX, tileY) == 1;
    }

    /**
     * Converts world coordinates (in pixels) to tile coordinates, and checks if that tile is solid.
     * Logic: First converts pixel coordinates to tile coordinates by dividing by TILE_SIZE
     * and flooring the result. Then calls isSolidTile() with the converted coordinates.
     * This is the primary method used for collision detection with moving entities.
     *
     * @param worldX X coordinate in pixels (world space)
     * @param worldY Y coordinate in pixels (world space)
     * @return true if the corresponding tile is solid, false otherwise
     */
    public boolean isSolidAt(double worldX, double worldY) {
        int tileX = (int) Math.floor(worldX / TILE_SIZE);
        int tileY = (int) Math.floor(worldY / TILE_SIZE);
        return isSolidTile(tileX, tileY);
    }

    /**
     * Helper to know if a tile index is inside the map.
     * Logic: Simple bounds checking against the tilemap dimensions.
     * Used internally to prevent array index errors and for edge-case handling.
     *
     * @param tileX X coordinate in tile units
     * @param tileY Y coordinate in tile units
     * @return true if coordinates are within the tilemap boundaries
     */
    public boolean isInBounds(int tileX, int tileY) {
        return tileX >= 0 && tileX < widthInTiles &&
                tileY >= 0 && tileY < heightInTiles;
    }

    /**
     * Optional: returns a copy of the internal tiles array if needed.
     * Logic: Creates a new 2D array and copies all values from the internal array.
     * This protects the internal data from modification while allowing external access.
     * Useful for debugging or level editing tools.
     *
     * @return A deep copy of the tiles array
     */
    public int[][] copyTiles() {
        int[][] copy = new int[heightInTiles][widthInTiles];
        for (int y = 0; y < heightInTiles; y++) {
            System.arraycopy(tiles[y], 0, copy[y], 0, widthInTiles);
        }
        return copy;
    }

    // -------------------------------------------------
    // Simple debug helper
    // -------------------------------------------------

    /**
     * Returns a human-readable version of the map using '#' for solid and '.' for empty.
     * Logic: Converts the internal numeric tile IDs back to character symbols.
     * This essentially reverses the fromCharLines() process for debugging purposes.
     * Useful for console output to verify the tilemap was loaded correctly.
     *
     * @return List of strings representing the tilemap visually
     */
    public List<String> toDebugStringLines() {
        List<String> result = new ArrayList<>();
        for (int y = 0; y < heightInTiles; y++) {
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < widthInTiles; x++) {
                sb.append(tiles[y][x] == 1 ? '#' : '.');
            }
            result.add(sb.toString());
        }
        return result;
    }
}