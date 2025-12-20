package game.systems;

public class PowerUp extends Collectible{

    private final PowerUpType type;

    public PowerUp(double x, double y, double width, double height, PowerUpType type) {
        super(x, y, width, height);
        this.type = type;
    }

    public PowerUpType getType() {
        return type;
    }

}
