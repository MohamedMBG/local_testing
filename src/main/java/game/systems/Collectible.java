package game.systems;

public class Collectible {

    protected double x,y;
    protected double height, width;
    protected boolean collected;

    public Collectible(double x, double y, double height, double width, boolean collected) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.collected = collected;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public boolean isCollected() {
        return collected;
    }

    public boolean tryCollect(double playerX, double playerY, double playerWidth, double playerHeight) {
        if (!collected &&
            playerX < x + width &&
            playerX + playerWidth > x &&
            playerY < y + height &&
            playerY + playerHeight > y) {
            collected = true;
            return true;
        }
        return false;
    }
}
