package game.utils; // Declares the package for this source file.

public class MathUtils { // Defines a class.

    // ------------------------------
    // CLAMP
    // ------------------------------

    /**
     * Limits a value between min and max.
     */
    public static double clamp(double value, double min, double max) { // Begins a method or constructor with its signature.
        if (value < min) return min; // Evaluates a conditional branch.
        if (value > max) return max; // Evaluates a conditional branch.
        return value;
    } // Closes a code block.


    // ------------------------------
    // LINEAR INTERPOLATION
    // ------------------------------

    /**
     * Linear interpolation between a and b using t (0..1)
     */
    public static double lerp(double a, double b, double t) { // Begins a method or constructor with its signature.
        return a + (b - a) * t; // Returns a value from the method.
    } // Closes a code block.


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
    public static boolean rectIntersects( // Executes: public static boolean rectIntersects(
            double x1, double y1, double w1, double h1, // Executes: double x1, double y1, double w1, double h1,
            double x2, double y2, double w2, double h2 // Executes: double x2, double y2, double w2, double h2
    ) { // Executes: ) {
        return x1 < x2 + w2 && // Returns a value from the method.
                x1 + w1 > x2 && // Executes: x1 + w1 > x2 &&
                y1 < y2 + h2 && // Executes: y1 < y2 + h2 &&
                y1 + h1 > y2; // Executes: y1 + h1 > y2;
    } // Closes a code block.


    // ------------------------------
    // DISTANCE
    // ------------------------------

    /**
     * Returns squared distance (faster, no sqrt).
     */
    public static double distanceSquared(double x1, double y1, double x2, double y2) { // Begins a method or constructor with its signature.
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy; // Returns a value from the method.
    } // Closes a code block.

    /**
     * Returns Euclidean distance between two points.
     */
    public static double distance(double x1, double y1, double x2, double y2) { // Begins a method or constructor with its signature.
        return Math.sqrt(distanceSquared(x1, y1, x2, y2)); // Returns a value from the method.
    } // Closes a code block.


    // ------------------------------
    // APPROXIMATION
    // ------------------------------

    /**
     * Returns true if two doubles are approximately equal within a tolerance.
     */
    public static boolean approximately(double a, double b, double tolerance) { // Begins a method or constructor with its signature.
        return Math.abs(a - b) <= tolerance; // Returns a value from the method.
    } // Closes a code block.


    // ------------------------------
    // SIGN
    // ------------------------------

    /**
     * Returns -1 if negative, +1 if positive, 0 if zero.
     */
    public static int sign(double value) { // Begins a method or constructor with its signature.
        if (value > 0) return 1; // Evaluates a conditional branch.
        if (value < 0) return -1; // Evaluates a conditional branch.
        return 0; // Returns a value from the method.
    } // Closes a code block.


    // ------------------------------
    // WRAP (Useful for looping indices)
    // ------------------------------

    /**
     * Wrap a value into a 0..max-1 range.
     */
    public static int wrap(int value, int max) { // Begins a method or constructor with its signature.
        if (max <= 0) return 0; // Evaluates a conditional branch.
        value %= max; // Executes: value %= max;
        if (value < 0) value += max; // Evaluates a conditional branch.
        return value;
    } // Closes a code block.
} // Closes a code block.
