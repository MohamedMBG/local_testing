package game.core; // Declares the package for this source file.

import game.systems.TileMap;

public class Physics { // Defines a class.

    // A tiny gap to keep floating point errors from gluing us to the wall
    private static final double SKIN = 0.01;

    // Max fall speed (Terminal Velocity) to prevent falling through floors
    private static final double MAX_FALL_SPEED = 1500.0; // High value for pixels // Executes: private static final double MAX_FALL_SPEED = 1500.0; // High value for pixels

    // Physics simulation step size (prevents tunneling through walls)
    private static final double MAX_STEP_SIZE = TileMap.TILE_SIZE / 2.0;

    /**
     * Main physics entry point.
     * Handles Gravity, Velocity, and Map Collisions.
     */
    public static void moveAndCollide(Player p, TileMap map, double dt) { // Begins a method or constructor with its signature.
        if (p == null || map == null) return; // Evaluates a conditional branch.

        // 1. Apply Gravity
        // NOTE: Ensure p.applyGravity() uses a value like 1200.0, NOT 9.8!
        p.applyGravity(dt); // Executes: p.applyGravity(dt);

        // Clamp falling speed so we don't fall through the world
        if (p.getVelocityY() > MAX_FALL_SPEED) { // Begins a method or constructor with its signature.
            p.setVelocityY(MAX_FALL_SPEED); // Executes: p.setVelocityY(MAX_FALL_SPEED);
        } // Closes a code block.

        // 2. Determine sub-steps for safety
        double totalDx = p.getVelocityX() * dt;
        double totalDy = p.getVelocityY() * dt;

        // Calculate how many steps we need to be safe
        double distance = Math.sqrt(totalDx * totalDx + totalDy * totalDy);
        int steps = (int) Math.ceil(distance / MAX_STEP_SIZE);
        if (steps <= 0) steps = 1; // Evaluates a conditional branch.

        double stepDx = totalDx / steps;
        double stepDy = totalDy / steps;

        // Reset ground state at the start of the frame
        p.setOnGround(false); // Executes: p.setOnGround(false);

        // 3. Execute sub-steps
        for (int i = 0; i < steps; i++) { // Begins a method or constructor with its signature.
            resolveX(p, map, stepDx); // Executes: resolveX(p, map, stepDx);
            resolveY(p, map, stepDy); // Executes: resolveY(p, map, stepDy);

            // If we hit a wall/floor, stop adding velocity for remaining steps
            if (p.getVelocityX() == 0) stepDx = 0; // Evaluates a conditional branch.
            if (p.getVelocityY() == 0) stepDy = 0; // Evaluates a conditional branch.
        } // Closes a code block.
    } // Closes a code block.

    private static void resolveX(Player p, TileMap map, double dx) { // Begins a method or constructor with its signature.
        p.setPlayerX(p.getPlayerX() + dx); // Executes: p.setPlayerX(p.getPlayerX() + dx);

        int leftTile = getTileIdx(p.getPlayerX() + SKIN);
        int rightTile = getTileIdx(p.getPlayerX() + p.getWidth() - SKIN);
        int topTile = getTileIdx(p.getPlayerY() + SKIN);
        int bottomTile = getTileIdx(p.getPlayerY() + p.getHeight() - SKIN);

        // Moving Right
        if (dx > 0) { // Begins a method or constructor with its signature.
            for (int ty = topTile; ty <= bottomTile; ty++) { // Begins a method or constructor with its signature.
                if (map.isSolidTile(rightTile, ty)) { // Begins a method or constructor with its signature.
                    p.setPlayerX((rightTile * TileMap.TILE_SIZE) - p.getWidth() - SKIN); // Executes: p.setPlayerX((rightTile * TileMap.TILE_SIZE) - p.getWidth() - SKIN);
                    p.setVelocityX(0); // Executes: p.setVelocityX(0);
                    return; // Returns a value from the method.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
        // Moving Left
        else if (dx < 0) { // Begins a method or constructor with its signature.
            for (int ty = topTile; ty <= bottomTile; ty++) { // Begins a method or constructor with its signature.
                if (map.isSolidTile(leftTile, ty)) { // Begins a method or constructor with its signature.
                    p.setPlayerX((leftTile + 1) * TileMap.TILE_SIZE + SKIN); // Executes: p.setPlayerX((leftTile + 1) * TileMap.TILE_SIZE + SKIN);
                    p.setVelocityX(0); // Executes: p.setVelocityX(0);
                    return; // Returns a value from the method.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
    } // Closes a code block.

    private static void resolveY(Player p, TileMap map, double dy) { // Begins a method or constructor with its signature.
        p.setPlayerY(p.getPlayerY() + dy); // Executes: p.setPlayerY(p.getPlayerY() + dy);

        int leftTile = getTileIdx(p.getPlayerX() + SKIN);
        int rightTile = getTileIdx(p.getPlayerX() + p.getWidth() - SKIN);
        int topTile = getTileIdx(p.getPlayerY() + SKIN);
        int bottomTile = getTileIdx(p.getPlayerY() + p.getHeight() - SKIN);

        // Falling / Moving Down / Standing
        if (dy >= 0) { // Begins a method or constructor with its signature.
            for (int tx = leftTile; tx <= rightTile; tx++) { // Begins a method or constructor with its signature.
                if (map.isSolidTile(tx, bottomTile)) { // Begins a method or constructor with its signature.
                    // Snap to top of the block
                    p.setPlayerY((bottomTile * TileMap.TILE_SIZE) - p.getHeight() - SKIN); // Executes: p.setPlayerY((bottomTile * TileMap.TILE_SIZE) - p.getHeight() - SKIN);
                    p.setVelocityY(0); // Executes: p.setVelocityY(0);
                    p.setOnGround(true); // Critical: We found the floor // Executes: p.setOnGround(true); // Critical: We found the floor
                    return; // Returns a value from the method.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
        // Jumping / Moving Up
        else if (dy < 0) { // Begins a method or constructor with its signature.
            for (int tx = leftTile; tx <= rightTile; tx++) { // Begins a method or constructor with its signature.
                if (map.isSolidTile(tx, topTile)) { // Begins a method or constructor with its signature.
                    // Try to slide around the corner first
                    if (applyCornerCorrection(p, map, tx, topTile, leftTile, rightTile)) { // Begins a method or constructor with its signature.
                        return; // Returns a value from the method.
                    } // Closes a code block.
                    // Hit ceiling
                    p.setPlayerY((topTile + 1) * TileMap.TILE_SIZE + SKIN); // Executes: p.setPlayerY((topTile + 1) * TileMap.TILE_SIZE + SKIN);
                    p.setVelocityY(0); // Executes: p.setVelocityY(0);
                    return; // Returns a value from the method.
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
    } // Closes a code block.

    /**
     * Compatibility method for your GameLoop.
     * Keeps player from falling if they walk off the TileMap onto the "Ground" object.
     */
    public static void checkGroundCollision(Player player, Ground ground) { // Begins a method or constructor with its signature.
        if (ground == null) return; // Evaluates a conditional branch.

        double pX = player.getPlayerX();
        double pY = player.getPlayerY();
        double pW = player.getWidth();
        double pH = player.getHeight();

        double gX = ground.getRectangle().getX();
        double gY = ground.getRectangle().getY();
        double gW = ground.getRectangle().getWidth();
        double gH = ground.getRectangle().getHeight();

        // Simple AABB overlap check
        boolean overlap = pX < gX + gW && pX + pW > gX && // Executes: boolean overlap = pX < gX + gW && pX + pW > gX &&
                pY < gY + gH && pY + pH > gY; // Executes: pY < gY + gH && pY + pH > gY;

        // If overlapping and falling down
        if (overlap && player.getVelocityY() >= 0) { // Begins a method or constructor with its signature.
            player.setPlayerY(gY - pH); // Executes: player.setPlayerY(gY - pH);
            player.setVelocityY(0); // Executes: player.setVelocityY(0);
            player.setOnGround(true); // Executes: player.setOnGround(true);
        } // Closes a code block.
    } // Closes a code block.

    /**
     * Nudges the player sideways if they hit the corner of a block with their head,
     * allowing them to slide up instead of stopping dead.
     */
    private static boolean applyCornerCorrection(Player p, TileMap map, int hitTileX, int hitTileY, int leftTile, int rightTile) { // Begins a method or constructor with its signature.
        double overlapAmount = 10.0; // Pixels of leniency // Executes: double overlapAmount = 10.0; // Pixels of leniency
        double playerCenterX = p.getPlayerX() + (p.getWidth() / 2.0);
        double tileCenterX = (hitTileX * TileMap.TILE_SIZE) + (TileMap.TILE_SIZE / 2.0);

        // Hit left corner?
        if (playerCenterX < tileCenterX) { // Begins a method or constructor with its signature.
            if (!map.isSolidTile(hitTileX - 1, hitTileY)) { // Check if space to left is open // Evaluates a conditional branch.
                double penetration = (p.getPlayerX() + p.getWidth()) - (hitTileX * TileMap.TILE_SIZE);
                if (penetration < overlapAmount) { // Begins a method or constructor with its signature.
                    p.setPlayerX((hitTileX * TileMap.TILE_SIZE) - p.getWidth() - SKIN); // Executes: p.setPlayerX((hitTileX * TileMap.TILE_SIZE) - p.getWidth() - SKIN);
                    return true;
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
        // Hit right corner?
        else { // Handles the fallback branch.
            if (!map.isSolidTile(hitTileX + 1, hitTileY)) { // Check if space to right is open // Evaluates a conditional branch.
                double penetration = ((hitTileX + 1) * TileMap.TILE_SIZE) - p.getPlayerX();
                if (penetration < overlapAmount) { // Begins a method or constructor with its signature.
                    p.setPlayerX(((hitTileX + 1) * TileMap.TILE_SIZE) + SKIN); // Executes: p.setPlayerX(((hitTileX + 1) * TileMap.TILE_SIZE) + SKIN);
                    return true;
                } // Closes a code block.
            } // Closes a code block.
        } // Closes a code block.
        return false;
    } // Closes a code block.

    private static int getTileIdx(double pos) { // Begins a method or constructor with its signature.
        return (int) Math.floor(pos / TileMap.TILE_SIZE); // Returns a value from the method.
    } // Closes a code block.
} // Closes a code block.
