package game.utils;

public class MathUtils {

    // ------------------------------
    // CLAMP
    // ------------------------------

    /**
     * Limits a value between min and max.
     */
    public static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }


    // ------------------------------
    // LINEAR INTERPOLATION
    // ------------------------------

    /**
     * Linear interpolation between a and b using t (0..1)
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }


    // ------------------------------
    // RECTANGLE INTERSECTION
    // ------------------------------

    /**
     * Checks if two rectangles intersect.
     *
     * @param x1 First rectangle X
     * @param y1 First rectangle Y
     * @param w1 First rectangle width
     * @param h1 First rectangle height
     * @param x2 Second rectangle X
     * @param y2 Second rectangle Y
     * @param w2 Second rectangle width
     * @param h2 Second rectangle height
     */
    public static boolean rectIntersects(
            double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2
    ) {
        return x1 < x2 + w2 &&
                x1 + w1 > x2 &&
                y1 < y2 + h2 &&
                y1 + h1 > y2;
    }


    // ------------------------------
    // DISTANCE
    // ------------------------------

    /**
     * Returns squared distance (faster, no sqrt).
     */
    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    /**
     * Returns Euclidean distance between two points.
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }


    // ------------------------------
    // APPROXIMATION
    // ------------------------------

    /**
     * Returns true if two doubles are approximately equal within a tolerance.
     */
    public static boolean approximately(double a, double b, double tolerance) {
        return Math.abs(a - b) <= tolerance;
    }


    // ------------------------------
    // SIGN
    // ------------------------------

    /**
     * Returns -1 if negative, +1 if positive, 0 if zero.
     */
    public static int sign(double value) {
        if (value > 0) return 1;
        if (value < 0) return -1;
        return 0;
    }


    // ------------------------------
    // WRAP (Useful for looping indices)
    // ------------------------------

    /**
     * Wrap a value into a 0..max-1 range.
     */
    public static int wrap(int value, int max) {
        if (max <= 0) return 0;
        value %= max;
        if (value < 0) value += max;
        return value;
    }
}
