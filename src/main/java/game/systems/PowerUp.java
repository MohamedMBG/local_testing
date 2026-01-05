package game.systems; // Declares the package for this source file.

public class PowerUp extends Collectible{ // Defines a class.

    private final PowerUpType type;

    public PowerUp(double x, double y, double width, double height, PowerUpType type) { // Begins a method or constructor with its signature.
        super(x, y, width, height); // Executes: super(x, y, width, height);
        this.type = type; // Executes: this.type = type;
    } // Closes a code block.

    public PowerUpType getType() { // Begins a method or constructor with its signature.
        return type;
    } // Closes a code block.

} // Closes a code block.
