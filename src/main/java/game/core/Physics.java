package game.core;

import game.systems.TileMap;

/**
 * Tile-based physics with forgiving collisions.
 *
 * Key improvements vs. "sticky" collisions:
 * - Uses a tiny skin (epsilon) to avoid getting stuck inside tiles.
 * - Zeroes the collided axis velocity (X on wall hit, Y on ceiling/ground hit).
 * - Adds a small "step-up" when running into a 1-tile edge while grounded (smooth movement).
 */
public class Physics {

    // Small "skin" so we don't stay exactly inside a tile boundary
    private static final double COLLISION_EPSILON = 0.5;

    // How many pixels we can step up when hitting a wall while grounded
    private static final double STEP_HEIGHT = 6.0;

    public static void checkGroundCollision(Player player, Ground ground) {
        if (player.getRectangle().getBoundsInParent()
                .intersects(ground.getRectangle().getBoundsInParent())) {

            double top = ground.getY();
            player.setPlayerY(top - player.getHeight());
            player.setVelocityY(0);
            player.setOnGround(true);
        }
    }

    // Main physics entry point
    public static void moveAndCollide(Player p, TileMap map, double dt) {
        if (p == null || map == null) return;

        // Remember grounded state from previous frame (needed for step-up)
        boolean groundedBefore = p.isOnGround();

        // Apply gravity first
        p.applyGravity(dt);

        // --- Move X then collide on X ---
        double newX = p.getPlayerX() + p.getVelocityX() * dt;
        p.setPlayerX(newX);
        collideX(p, map, groundedBefore);

        // --- Move Y then collide on Y ---
        double newY = p.getPlayerY() + p.getVelocityY() * dt;
        p.setPlayerY(newY);
        collideY(p, map);
    }

    private static void collideX(Player p, TileMap map, boolean groundedBefore) {
        double x = p.getPlayerX();
        double y = p.getPlayerY();
        double w = p.getWidth();
        double h = p.getHeight();

        int leftTile = (int) Math.floor((x + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int rightTile = (int) Math.floor((x + w - COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int topTile = (int) Math.floor((y + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int bottomTile = (int) Math.floor((y + h - COLLISION_EPSILON) / TileMap.TILE_SIZE);

        // moving right
        if (p.getVelocityX() > 0) {
            for (int ty = topTile; ty <= bottomTile; ty++) {
                if (map.isSolidTile(rightTile, ty)) {
                    double tileLeft = rightTile * TileMap.TILE_SIZE;
                    double targetX = tileLeft - w - COLLISION_EPSILON;

                    // Try a small step-up to avoid getting stuck on tiny edges
                    if (groundedBefore && tryStepUp(p, map, targetX, y)) {
                        return;
                    }

                    p.setPlayerX(targetX);
                    p.setVelocityX(0); // stop pushing into the wall
                    return;
                }
            }
        }
        // moving left
        else if (p.getVelocityX() < 0) {
            for (int ty = topTile; ty <= bottomTile; ty++) {
                if (map.isSolidTile(leftTile, ty)) {
                    double tileRight = (leftTile + 1) * TileMap.TILE_SIZE;
                    double targetX = tileRight + COLLISION_EPSILON;

                    if (groundedBefore && tryStepUp(p, map, targetX, y)) {
                        return;
                    }

                    p.setPlayerX(targetX);
                    p.setVelocityX(0);
                    return;
                }
            }
        }

        // Extra: if player is slightly inside a wall (e.g. after spawn/jitter), push out
        // This helps prevent "frozen" movement when starting overlapped.
        x = p.getPlayerX();
        leftTile = (int) Math.floor((x + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        rightTile = (int) Math.floor((x + w - COLLISION_EPSILON) / TileMap.TILE_SIZE);

        for (int ty = topTile; ty <= bottomTile; ty++) {
            if (map.isSolidTile(leftTile, ty)) {
                double tileRight = (leftTile + 1) * TileMap.TILE_SIZE;
                if (x < tileRight) p.setPlayerX(tileRight + COLLISION_EPSILON);
            }
            if (map.isSolidTile(rightTile, ty)) {
                double tileLeft = rightTile * TileMap.TILE_SIZE;
                if (x + w > tileLeft) p.setPlayerX(tileLeft - w - COLLISION_EPSILON);
            }
        }
    }

    private static boolean tryStepUp(Player p, TileMap map, double targetX, double currentY) {
        // Save current position
        double oldX = p.getPlayerX();
        double oldY = p.getPlayerY();

        // Move to the wall-resolved X, then try stepping up a little
        p.setPlayerX(targetX);
        p.setPlayerY(currentY - STEP_HEIGHT);

        if (!isColliding(p, map)) {
            // success; keep stepped position
            return true;
        }

        // revert
        p.setPlayerX(oldX);
        p.setPlayerY(oldY);
        return false;
    }

    private static void collideY(Player p, TileMap map) {
        double x = p.getPlayerX();
        double y = p.getPlayerY();
        double w = p.getWidth();
        double h = p.getHeight();

        int leftTile = (int) Math.floor((x + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int rightTile = (int) Math.floor((x + w - COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int topTile = (int) Math.floor((y + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int bottomTile = (int) Math.floor((y + h - COLLISION_EPSILON) / TileMap.TILE_SIZE);

        p.setOnGround(false);

        // falling or standing
        if (p.getVelocityY() >= 0) {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (map.isSolidTile(tx, bottomTile)) {
                    double tileTop = bottomTile * TileMap.TILE_SIZE;

                    // Snap the player so feet sit exactly on tile top
                    p.setPlayerY(tileTop - h - COLLISION_EPSILON);
                    p.setVelocityY(0);
                    p.setOnGround(true);
                    return;
                }
            }
        }
        // jumping upward
        else {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (map.isSolidTile(tx, topTile)) {
                    double tileBottom = (topTile + 1) * TileMap.TILE_SIZE;
                    p.setPlayerY(tileBottom + COLLISION_EPSILON);
                    p.setVelocityY(0);
                    return;
                }
            }
        }
    }

    private static boolean isColliding(Player p, TileMap map) {
        double x = p.getPlayerX();
        double y = p.getPlayerY();
        double w = p.getWidth();
        double h = p.getHeight();

        int left = (int) Math.floor((x + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int right = (int) Math.floor((x + w - COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int top = (int) Math.floor((y + COLLISION_EPSILON) / TileMap.TILE_SIZE);
        int bottom = (int) Math.floor((y + h - COLLISION_EPSILON) / TileMap.TILE_SIZE);

        for (int ty = top; ty <= bottom; ty++) {
            for (int tx = left; tx <= right; tx++) {
                if (map.isSolidTile(tx, ty)) return true;
            }
        }
        return false;
    }
}
