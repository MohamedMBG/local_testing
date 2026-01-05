package game.systems; // Declares the package for this source file.

public class Collectible { // Defines a class.

    protected double x,y; // Executes: protected double x,y;
    protected double height, width;
    protected boolean collected;

    public Collectible(double x, double y, double height, double width) { // Begins a method or constructor with its signature.
        this.x = x; // Executes: this.x = x;
        this.y = y; // Executes: this.y = y;
        this.height = height; // Executes: this.height = height;
        this.width = width; // Executes: this.width = width;
        this.collected = false; // Executes: this.collected = false;
    } // Closes a code block.

    public double getX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public double getY() { // Begins a method or constructor with its signature.
        return y;
    } // Closes a code block.

    public double getHeight() { // Begins a method or constructor with its signature.
        return height;
    } // Closes a code block.

    public double getWidth() { // Begins a method or constructor with its signature.
        return width;
    } // Closes a code block.

    public boolean isCollected() { // Begins a method or constructor with its signature.
        return collected;
    } // Closes a code block.

    public boolean tryCollect(double playerX, double playerY, double playerWidth, double playerHeight) { // Begins a method or constructor with its signature.
        if (!collected && // Evaluates a conditional branch.
            playerX < x + width && // Executes: playerX < x + width &&
            playerX + playerWidth > x && // Executes: playerX + playerWidth > x &&
            playerY < y + height && // Executes: playerY < y + height &&
            playerY + playerHeight > y) { // Executes: playerY + playerHeight > y) {
            collected = true; // Executes: collected = true;
            return true;
        } // Closes a code block.
        return false;
    } // Closes a code block.
} // Closes a code block.
