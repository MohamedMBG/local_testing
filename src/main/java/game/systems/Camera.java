package game.systems; // Declares the package for this source file.

import game.utils.MathUtils;

public class Camera { // Defines a class.
    private double x, y; //(camera top-left in world coordinates) // Executes: private double x, y; //(camera top-left in world coordinates)
    private double viewWidth, viewHeight;

    public Camera(double viewHeight, double viewWidth) { // Begins a method or constructor with its signature.
        this.viewHeight = viewHeight; // Executes: this.viewHeight = viewHeight;
        this.viewWidth = viewWidth; // Executes: this.viewWidth = viewWidth;
        this.x = 0; // Executes: this.x = 0;
        this.y = 0; // Executes: this.y = 0;
    } // Closes a code block.

    // -------------------------------------------------
    // Basic getters
    // -------------------------------------------------

    public double getX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public double getY() { // Begins a method or constructor with its signature.
        return y;
    } // Closes a code block.

    public double getViewWidth() { // Begins a method or constructor with its signature.
        return viewWidth;
    } // Closes a code block.

    public double getViewHeight() { // Begins a method or constructor with its signature.
        return viewHeight;
    } // Closes a code block.

    /**
     * Often called "offset" in rendering code.
     * World position - offset = screen position.
     */
    public double getOffsetX() { // Begins a method or constructor with its signature.
        return x;
    } // Closes a code block.

    public double getOffsetY() { // Begins a method or constructor with its signature.
        return y;
    } // Closes a code block.

    // -------------------------------------------------
    // Position control
    // -------------------------------------------------

    /**
     * Instantly set the camera's top-left position in world coordinates.
     */
    public void setPosition(double x, double y) { // Begins a method or constructor with its signature.
        this.x = x; // Executes: this.x = x;
        this.y = y; // Executes: this.y = y;
    } // Closes a code block.

    /**
     * Instantly center the camera on a target point in world coordinates.
     * This does NOT clamp to the map; call clampToMap after if needed.
     */
    public void centerOn(double targetX, double targetY) { // Begins a method or constructor with its signature.
        this.x = targetX - viewWidth / 2.0; // Executes: this.x = targetX - viewWidth / 2.0;
        this.y = targetY - viewHeight / 2.0; // Executes: this.y = targetY - viewHeight / 2.0;
    } // Closes a code block.

    /**
     * Smoothly follow a target using linear interpolation (lerp).
     *
     * @param targetX     Target world X (usually player center X)
     * @param targetY     Target world Y (usually player center Y)
     * @param smoothFactor Between 0 and 1. Closer to 0 = very smooth/slow, closer to 1 = almost instant.
     */
    public void follow(double targetX, double targetY, double smoothFactor) { // Begins a method or constructor with its signature.
        // Desired camera position: center on target
        double targetCamX = targetX - viewWidth / 2.0;
        double targetCamY = targetY - viewHeight / 2.0;

        // Lerp current position towards target position
        this.x = MathUtils.lerp(this.x, targetCamX, smoothFactor); // Executes: this.x = MathUtils.lerp(this.x, targetCamX, smoothFactor);
        this.y = MathUtils.lerp(this.y, targetCamY, smoothFactor); // Executes: this.y = MathUtils.lerp(this.y, targetCamY, smoothFactor);
    } // Closes a code block.

    public void clampToMap(double mapWidth, double mapHeight) { // Begins a method or constructor with its signature.

        if (x < 0) x = 0; // Evaluates a conditional branch.
        if (y < 0) y = 0; // Evaluates a conditional branch.

        if (x + viewWidth > mapWidth) { // Begins a method or constructor with its signature.
            x = mapWidth - viewWidth; // Executes: x = mapWidth - viewWidth;
        } // Closes a code block.

        if (y + viewHeight > mapHeight) { // Begins a method or constructor with its signature.
            y = mapHeight - viewHeight; // Executes: y = mapHeight - viewHeight;
        } // Closes a code block.

        // Safety for small maps
        if (mapWidth < viewWidth) x = 0; // Evaluates a conditional branch.
        if (mapHeight < viewHeight) y = 0; // Evaluates a conditional branch.
    } // Closes a code block.
} // Closes a code block.
