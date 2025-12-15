package game.core;

import game.systems.TileMap;

public class Physics {

    private static final double COLLISION_EPSILON = 0.1; // small gap to avoid sticking

    public static void checkGroundCollision(Player player, Ground ground) {
        if (player.getRectangle().getBoundsInParent()
                .intersects(ground.getRectangle().getBoundsInParent())) {

            double top = ground.getY();
            // Place player exactly on top of the ground pane
            player.setPlayerY(top - player.getHeight());
            player.setVelocityY(0);
            player.setOnGround(true);
        }
    }

    // Main physics entry point
    public static void moveAndCollide(Player p, TileMap map, double dt) {
        // Apply gravity first
        p.applyGravity(dt);

        // --- Move X then collide on X ---
        double newX = p.getPlayerX() + p.getVelocityX() * dt;
        p.setPlayerX(newX);
        collideX(p, map);

        // --- Move Y then collide on Y ---
        double newY = p.getPlayerY() + p.getVelocityY() * dt;
        p.setPlayerY(newY);
        collideY(p, map);
    }

    private static void collideX(Player p, TileMap map) {
        double x = p.getPlayerX();
        double y = p.getPlayerY();
        double w = p.getWidth();
        double h = p.getHeight();

        int leftTile = (int) Math.floor(x / TileMap.TILE_SIZE);
        int rightTile = (int) Math.floor((x + w - 1) / TileMap.TILE_SIZE);
        int topTile = (int) Math.floor(y / TileMap.TILE_SIZE);
        int bottomTile = (int) Math.floor((y + h - 1) / TileMap.TILE_SIZE);

        // moving right
        if (p.getVelocityX() > 0) {
            for (int ty = topTile; ty <= bottomTile; ty++) {
                if (map.isSolidTile(rightTile, ty)) {
                    double tileLeft = rightTile * TileMap.TILE_SIZE;
                    // Place player with a small epsilon so they're not overlapping the tile and can continue to run
                    p.setPlayerX(tileLeft - w - COLLISION_EPSILON);
                    p.setVelocityX(0);
                    return;
                }
            }
        }
        // moving left
        else if (p.getVelocityX() < 0) {
            for (int ty = topTile; ty <= bottomTile; ty++) {
                if (map.isSolidTile(leftTile, ty)) {
                    double tileRight = (leftTile + 1) * TileMap.TILE_SIZE;
                    p.setPlayerX(tileRight + COLLISION_EPSILON);
                    p.setVelocityX(0);
                    return;
                }
            }
        }
    }

    private static void collideY(Player p, TileMap map) {
        double x = p.getPlayerX();
        double y = p.getPlayerY();
        double w = p.getWidth();
        double h = p.getHeight();

        int leftTile = (int) Math.floor(x / TileMap.TILE_SIZE);
        int rightTile = (int) Math.floor((x + w - 1) / TileMap.TILE_SIZE);
        int topTile = (int) Math.floor(y / TileMap.TILE_SIZE);
        int bottomTile = (int) Math.floor((y + h - 1) / TileMap.TILE_SIZE);

        p.setOnGround(false);

        // falling (moving down) or standing still: treat >= 0 so we also detect standing contact
        if (p.getVelocityY() >= 0) {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (map.isSolidTile(tx, bottomTile)) {
                    double tileTop = bottomTile * TileMap.TILE_SIZE;

                    // If player's feet are at or slightly above the tile top (within threshold), snap and set onGround
                    double feetY = p.getPlayerY() + h;
                    final double LAND_THRESHOLD = 6.0; // px within which we consider the player standing on the tile

                    if (feetY >= tileTop - LAND_THRESHOLD && feetY <= tileTop + LAND_THRESHOLD) {
                        // Place player slightly closer to the tile (3px) with epsilon to avoid sticking
                        p.setPlayerY(tileTop - h + 3 - COLLISION_EPSILON);
                        p.setVelocityY(0);
                        p.setOnGround(true);
                        return;
                    }
                }
            }
        }
        // jumping
        else if (p.getVelocityY() < 0) {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (map.isSolidTile(tx, topTile)) {
                    double tileBottom = (topTile + 1) * TileMap.TILE_SIZE;
                    p.setPlayerY(tileBottom);
                    p.setVelocityY(0);
                    return;
                }
            }
        }
    }
}
