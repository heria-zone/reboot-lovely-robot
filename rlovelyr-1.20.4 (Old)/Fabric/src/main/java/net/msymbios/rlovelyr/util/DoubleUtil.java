package net.msymbios.rlovelyr.util;

/**
 * A utility class for double values.
 */
public class DoubleUtil {

    // -- Variables --

    /**
     * The default epsilon value.
     */
    public static final double EPSILON = 0.000000001;

    // -- Methods --

    /**
     * Compares two double values to see if they are equal to within the given epsilon.
     *
     * @param a       the first value.
     * @param b       the second value.
     * @param epsilon the epsilon value.
     */
    public static boolean isSame(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    /**
     * Compares two double values to see if they are equal to within {@link #EPSILON}.
     *
     * @param a the first value.
     * @param b the second value.
     * @return true if the values are equal, false otherwise.
     */
    public static boolean isSame(double a, double b) {
        return isSame(a, b, EPSILON);
    }

    /**
     * Checks if a double value is positive and finite.
     *
     * @param value the value to check.
     * @return true if the value is positive and finite, false otherwise.
     */
    public static boolean isPositiveAndFinite(double value) {
        return value >= 0.0 && !Double.isInfinite(value);
    }

    /**
     * Checks if a double value is between or equal to the given min and max.
     *
     * @param value the value to check.
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return true if the value is between or equal to the given min and max, false otherwise.
     */
    public static boolean isBetween(double value, double min, double max) {
        return value >= min && value <= max;
    }

    /**
     * Clamps a double value between the given min and max.
     *
     * @param value the value to clamp.
     * @param min   the minimum value.
     * @param max   the maximum value.
     * @return the clamped value.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

} // Class DoubleUtil