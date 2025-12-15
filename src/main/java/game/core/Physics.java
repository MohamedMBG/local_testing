package game.core;

import game.systems.TileMap;

public class Physics {

    public static void checkGroundCollision(Player player, Ground ground) {
        if (player.getRectangle().getBoundsInParent()
                .intersects(ground.getRectangle().getBoundsInParent())) {

            double top = ground.getY();
            player.setPlayerY(top - player.getHeight());
            player.setVelocityY(0);
            player.setOnGround(true);
        }
    }

    // Call this instead of your old one
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
                    p.setPlayerX(tileLeft - w);
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
                    p.setPlayerX(tileRight);
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

        // falling
        if (p.getVelocityY() > 0) {
            for (int tx = leftTile; tx <= rightTile; tx++) {
                if (map.isSolidTile(tx, bottomTile)) {
                    double tileTop = bottomTile * TileMap.TILE_SIZE;
                    p.setPlayerY(tileTop - h);
                    p.setVelocityY(0);
                    p.setOnGround(true);
                    return;
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
