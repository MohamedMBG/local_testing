package game.systems; // Declares the package for this source file.

import java.util.ArrayList;
import java.util.List;

public class TileMap { // Defines a class.
    /** size of one tile in pixel **/
    public static final int TILE_SIZE = 32;

    private final int[][] tiles;
    private final int widthInTiles;
    private final int heightInTiles;

    public TileMap(int[][] tiles) { // Begins a method or constructor with its signature.
        if (tiles == null || tiles.length == 0 || tiles[0].length == 0) { // Begins a method or constructor with its signature.
            throw new IllegalArgumentException("Tiles array must not be null or empty."); // Throws an exception.
        } // Closes a code block.

        this.heightInTiles = tiles.length; // Executes: this.heightInTiles = tiles.length;
        this.widthInTiles = tiles[0].length; // Executes: this.widthInTiles = tiles[0].length;

        // Copy to protect internal data from outside modification
        this.tiles = new int[heightInTiles][widthInTiles]; // Executes: this.tiles = new int[heightInTiles][widthInTiles];
        for (int y = 0; y < heightInTiles; y++) { // Begins a method or constructor with its signature.
            if (tiles[y].length != widthInTiles) { // Begins a method or constructor with its signature.
                throw new IllegalArgumentException("All rows in tiles array must have the same length."); // Throws an exception.
            } // Closes a code block.
            System.arraycopy(tiles[y], 0, this.tiles[y], 0, widthInTiles); // Executes: System.arraycopy(tiles[y], 0, this.tiles[y], 0, widthInTiles);
        } // Closes a code block.
    } // Closes a code block.

    /**
     * Creates a TileMap from a list of character strings (like a text-based level format).
     * This is useful for designing levels in text files. Each character represents a tile type.
     * Logic: Parses each line character by character, converting symbols to tile IDs using charToTileId().
     * This allows for human-readable level design with symbols like '#' for walls.
     *
     * @param lines List of strings where each string is a row of the tilemap
     * @return A new TileMap instance
     */
    public static TileMap fromCharLines(List<String> lines) { // Begins a method or constructor with its signature.
        if (lines == null || lines.isEmpty()) { // Begins a method or constructor with its signature.
            throw new IllegalArgumentException("Lines must not be null or empty."); // Throws an exception.
        } // Closes a code block.

        int height = lines.size();
        int width = lines.get(0).length();

        int[][] tiles = new int[height][width];

        for (int y = 0; y < height; y++) { // Begins a method or constructor with its signature.
            String line = lines.get(y);
            if (line.length() != width) { // Begins a method or constructor with its signature.
                throw new IllegalArgumentException("All lines must have the same length."); // Throws an exception.
            } // Closes a code block.

            for (int x = 0; x < width; x++) { // Begins a method or constructor with its signature.
                char c = line.charAt(x);
                tiles[y][x] = charToTileId(c); // Executes: tiles[y][x] = charToTileId(c);
            } // Closes a code block.
        } // Closes a code block.

        return new TileMap(tiles); // Returns a value from the method.
    } // Closes a code block.

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
    private static int charToTileId(char c) { // Begins a method or constructor with its signature.
        switch (c) { // Begins a method or constructor with its signature.
            case '#': // Defines a switch case branch.
                return 1; // solid block // Returns a value from the method.
            // These characters are treated as "empty" for the tilemap
            // but LevelLoader can still use them for spawns.
            case ' ': // Defines a switch case branch.
            case '.': // Defines a switch case branch.
            case 'P': // player spawn // Defines a switch case branch.
            case 'C': // coin spawn // Defines a switch case branch.
            case 'U': // power-up spawn // Defines a switch case branch.
            default: // Defines the default switch branch.
                return 0; // empty space // Returns a value from the method.
        } // Closes a code block.
    } // Closes a code block.

    // -------------------------------------------------
    // Basic getters
    // -------------------------------------------------

    /**
     * Returns the width of the tilemap in tiles (not pixels).
     * Logic: Simple getter for the pre-calculated width in tile units.
     *
     * @return Width in tile units
     */
    public int getWidthInTiles() { // Begins a method or constructor with its signature.
        return widthInTiles;
    } // Closes a code block.

    /**
     * Returns the height of the tilemap in tiles (not pixels).
     * Logic: Simple getter for the pre-calculated height in tile units.
     *
     * @return Height in tile units
     */
    public int getHeightInTiles() { // Begins a method or constructor with its signature.
        return heightInTiles;
    } // Closes a code block.

    /**
     * Returns the width of the tilemap in pixels.
     * Logic: Converts tile dimensions to pixel dimensions by multiplying with TILE_SIZE.
     * This is useful for rendering and positioning entities in the game world.
     *
     * @return Width in pixels
     */
    public int getWidthInPixels() { // Begins a method or constructor with its signature.
        return widthInTiles * TILE_SIZE; // Returns a value from the method.
    } // Closes a code block.

    /**
     * Returns the height of the tilemap in pixels.
     * Logic: Converts tile dimensions to pixel dimensions by multiplying with TILE_SIZE.
     * This is useful for rendering and positioning entities in the game world.
     *
     * @return Height in pixels
     */
    public int getHeightInPixels() { // Begins a method or constructor with its signature.
        return heightInTiles * TILE_SIZE; // Returns a value from the method.
    } // Closes a code block.

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
    public int getTile(int tileX, int tileY) { // Begins a method or constructor with its signature.
        if (!isInBounds(tileX, tileY)) { // Begins a method or constructor with its signature.
            return 0; // Returns a value from the method.
        } // Closes a code block.
        return tiles[tileY][tileX]; // Returns a value from the method.
    } // Closes a code block.

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
    public boolean isSolidTile(int tileX, int tileY) { // Begins a method or constructor with its signature.
        return getTile(tileX, tileY) == 1; // Returns a value from the method.
    } // Closes a code block.

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
    public boolean isSolidAt(double worldX, double worldY) { // Begins a method or constructor with its signature.
        int tileX = (int) Math.floor(worldX / TILE_SIZE);
        int tileY = (int) Math.floor(worldY / TILE_SIZE);
        return isSolidTile(tileX, tileY); // Returns a value from the method.
    } // Closes a code block.

    /**
     * Helper to know if a tile index is inside the map.
     * Logic: Simple bounds checking against the tilemap dimensions.
     * Used internally to prevent array index errors and for edge-case handling.
     *
     * @param tileX X coordinate in tile units
     * @param tileY Y coordinate in tile units
     * @return true if coordinates are within the tilemap boundaries
     */
    public boolean isInBounds(int tileX, int tileY) { // Begins a method or constructor with its signature.
        return tileX >= 0 && tileX < widthInTiles && // Returns a value from the method.
                tileY >= 0 && tileY < heightInTiles; // Executes: tileY >= 0 && tileY < heightInTiles;
    } // Closes a code block.

    /**
     * Optional: returns a copy of the internal tiles array if needed.
     * Logic: Creates a new 2D array and copies all values from the internal array.
     * This protects the internal data from modification while allowing external access.
     * Useful for debugging or level editing tools.
     *
     * @return A deep copy of the tiles array
     */
    public int[][] copyTiles() { // Begins a method or constructor with its signature.
        int[][] copy = new int[heightInTiles][widthInTiles];
        for (int y = 0; y < heightInTiles; y++) { // Begins a method or constructor with its signature.
            System.arraycopy(tiles[y], 0, copy[y], 0, widthInTiles); // Executes: System.arraycopy(tiles[y], 0, copy[y], 0, widthInTiles);
        } // Closes a code block.
        return copy;
    } // Closes a code block.

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
    public List<String> toDebugStringLines() { // Begins a method or constructor with its signature.
        List<String> result = new ArrayList<>();
        for (int y = 0; y < heightInTiles; y++) { // Begins a method or constructor with its signature.
            StringBuilder sb = new StringBuilder();
            for (int x = 0; x < widthInTiles; x++) { // Begins a method or constructor with its signature.
                sb.append(tiles[y][x] == 1 ? '#' : '.'); // Executes: sb.append(tiles[y][x] == 1 ? '#' : '.');
            } // Closes a code block.
            result.add(sb.toString()); // Executes: result.add(sb.toString());
        } // Closes a code block.
        return result;
    } // Closes a code block.
} // Closes a code block.
