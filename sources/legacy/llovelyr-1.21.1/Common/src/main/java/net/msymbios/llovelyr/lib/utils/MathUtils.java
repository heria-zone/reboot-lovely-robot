package net.msymbios.llovelyr.lib.utils;

import java.util.Random;

/**
 * Provides mathematical utility functions for robot calculations.
 * <p>
 * <b>Architecture:</b> Centralizes mathematical operations that were duplicated
 * across loader implementations. Focuses on robot-specific calculations like
 * experience, level progression, and damage calculations.
 * <p>
 * <b>Design Decision:</b> Static utility methods with shared Random instance
 * for consistent randomization behavior across the mod.
 */
public class MathUtils {

    // -- Constants --

    /** Shared random instance for consistent behavior across calculations. */
    public static final Random RANDOM = new Random();

    /** Mathematical constant PI for angle calculations. */
    public static final double PI = 3.14159265358979323846;

    /** Degrees to radians conversion factor. */
    public static final double DEGREES_TO_RADIANS = PI / 180.0;

    /** Radians to degrees conversion factor. */
    public static final double RADIANS_TO_DEGREES = 180.0 / PI;

    // -- Range and Clamping --

    /**
     * Clamps value to specified range.
     * <p>
     * <b>Usage:</b> Ensures values stay within valid bounds for robot attributes
     * like health, level, and protection values.
     *
     * @param value value to clamp
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    } // clamp()

    /**
     * Clamps float value to specified range.
     *
     * @param value value to clamp
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    } // clamp()

    /**
     * Clamps double value to specified range.
     *
     * @param value value to clamp
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @return clamped value
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    } // clamp()

    // -- Random Generation --

    /**
     * Generates random integer in specified range (inclusive).
     * <p>
     * <b>Usage:</b> Used for robot spawning, random events, and procedural generation.
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (inclusive)
     * @return random integer in range
     */
    public static int randomInt(int min, int max) {
        if (min >= max) return min;
        return RANDOM.nextInt(max - min + 1) + min;
    } // randomInt()

    /**
     * Generates random float in specified range.
     *
     * @param min minimum value
     * @param max maximum value
     * @return random float in range
     */
    public static float randomFloat(float min, float max) {
        if (min >= max) return min;
        return RANDOM.nextFloat() * (max - min) + min;
    } // randomFloat()

    /**
     * Generates random double in specified range.
     *
     * @param min minimum value
     * @param max maximum value
     * @return random double in range
     */
    public static double randomDouble(double min, double max) {
        if (min >= max) return min;
        return RANDOM.nextDouble() * (max - min) + min;
    } // randomDouble()

    // -- Percentage and Probability --

    /**
     * Calculates percentage of value.
     * <p>
     * <b>Usage:</b> Used for damage calculations, experience bonuses, and stat modifications.
     *
     * @param value base value
     * @param percentage percentage (0-100)
     * @return percentage of value
     */
    public static float percentage(float value, float percentage) {
        return value * (percentage / 100.0f);
    } // percentage()

    /**
     * Tests random probability.
     * <p>
     * <b>Usage:</b> Used for random events, critical hits, and special abilities.
     *
     * @param chance probability (0.0 to 1.0)
     * @return true if random roll succeeds
     */
    public static boolean randomChance(double chance) {
        return RANDOM.nextDouble() < chance;
    } // randomChance()

    /**
     * Tests percentage probability.
     * <p>
     * <b>Usage:</b> More intuitive percentage-based probability testing.
     *
     * @param percentage probability (0-100)
     * @return true if random roll succeeds
     */
    public static boolean randomPercentage(float percentage) {
        return RANDOM.nextFloat() * 100.0f < percentage;
    } // randomPercentage()

    // -- Distance and Geometry --

    /**
     * Calculates 2D distance between two points.
     * <p>
     * <b>Usage:</b> Used for robot AI, interaction ranges, and proximity detection.
     *
     * @param x1 first point X coordinate
     * @param z1 first point Z coordinate
     * @param x2 second point X coordinate
     * @param z2 second point Z coordinate
     * @return distance between points
     */
    public static double distance2D(double x1, double z1, double x2, double z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dz * dz);
    } // distance2D()

    /**
     * Calculates 3D distance between two points.
     *
     * @param x1 first point X coordinate
     * @param y1 first point Y coordinate
     * @param z1 first point Z coordinate
     * @param x2 second point X coordinate
     * @param y2 second point Y coordinate
     * @param z2 second point Z coordinate
     * @return distance between points
     */
    public static double distance3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    } // distance3D()

    /**
     * Calculates squared distance (faster than distance for comparisons).
     * <p>
     * <b>Performance:</b> Avoids expensive square root calculation when only
     * comparing distances or checking if within range.
     *
     * @param x1 first point X coordinate
     * @param z1 first point Z coordinate
     * @param x2 second point X coordinate
     * @param z2 second point Z coordinate
     * @return squared distance between points
     */
    public static double distanceSquared2D(double x1, double z1, double x2, double z2) {
        double dx = x2 - x1;
        double dz = z2 - z1;
        return dx * dx + dz * dz;
    } // distanceSquared2D()

    // -- Angle Calculations --

    /**
     * Converts degrees to radians.
     *
     * @param degrees angle in degrees
     * @return angle in radians
     */
    public static double toRadians(double degrees) {
        return degrees * DEGREES_TO_RADIANS;
    } // toRadians()

    /**
     * Converts radians to degrees.
     *
     * @param radians angle in radians
     * @return angle in degrees
     */
    public static double toDegrees(double radians) {
        return radians * RADIANS_TO_DEGREES;
    } // toDegrees()

    /**
     * Normalizes angle to 0-360 degree range.
     * <p>
     * <b>Usage:</b> Ensures consistent angle representation for robot rotation and AI.
     *
     * @param degrees angle in degrees
     * @return normalized angle (0-360)
     */
    public static double normalizeAngle(double degrees) {
        degrees = degrees % 360.0;
        if (degrees < 0) degrees += 360.0;
        return degrees;
    } // normalizeAngle()

    // -- Interpolation --

    /**
     * Linear interpolation between two values.
     * <p>
     * <b>Usage:</b> Used for smooth animations, gradual stat changes, and transitions.
     *
     * @param start starting value
     * @param end ending value
     * @param t interpolation factor (0.0 to 1.0)
     * @return interpolated value
     */
    public static float lerp(float start, float end, float t) {
        return start + t * (end - start);
    } // lerp()

    /**
     * Double precision linear interpolation.
     *
     * @param start starting value
     * @param end ending value
     * @param t interpolation factor (0.0 to 1.0)
     * @return interpolated value
     */
    public static double lerp(double start, double end, double t) {
        return start + t * (end - start);
    } // lerp()

} // Class: MathUtils