package game.systems;

import game.utils.MathUtils;

public class Camera {
    private double x, y; //(camera top-left in world coordinates)
    private double viewWidth, viewHeight;

    public Camera(double viewHeight, double viewWidth) {
        this.viewHeight = viewHeight;
        this.viewWidth = viewWidth;
        this.x = 0;
        this.y = 0;
    }

    // -------------------------------------------------
    // Basic getters
    // -------------------------------------------------

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getViewWidth() {
        return viewWidth;
    }

    public double getViewHeight() {
        return viewHeight;
    }

    /**
     * Often called "offset" in rendering code.
     * World position - offset = screen position.
     */
    public double getOffsetX() {
        return x;
    }

    public double getOffsetY() {
        return y;
    }

    // -------------------------------------------------
    // Position control
    // -------------------------------------------------

    /**
     * Instantly set the camera's top-left position in world coordinates.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Instantly center the camera on a target point in world coordinates.
     * This does NOT clamp to the map; call clampToMap after if needed.
     */
    public void centerOn(double targetX, double targetY) {
        this.x = targetX - viewWidth / 2.0;
        this.y = targetY - viewHeight / 2.0;
    }

    /**
     * Smoothly follow a target using linear interpolation (lerp).
     *
     * @param targetX     Target world X (usually player center X)
     * @param targetY     Target world Y (usually player center Y)
     * @param smoothFactor Between 0 and 1. Closer to 0 = very smooth/slow, closer to 1 = almost instant.
     */
    public void follow(double targetX, double targetY, double smoothFactor) {
        // Desired camera position: center on target
        double targetCamX = targetX - viewWidth / 2.0;
        double targetCamY = targetY - viewHeight / 2.0;

        // Lerp current position towards target position
        this.x = MathUtils.lerp(this.x, targetCamX, smoothFactor);
        this.y = MathUtils.lerp(this.y, targetCamY, smoothFactor);
    }

    public void clampToMap(double mapWidth, double mapHeight) {

        if (x < 0) x = 0;
        if (y < 0) y = 0;

        if (x + viewWidth > mapWidth) {
            x = mapWidth - viewWidth;
        }

        if (y + viewHeight > mapHeight) {
            y = mapHeight - viewHeight;
        }

        // Safety for small maps
        if (mapWidth < viewWidth) x = 0;
        if (mapHeight < viewHeight) y = 0;
    }
}
