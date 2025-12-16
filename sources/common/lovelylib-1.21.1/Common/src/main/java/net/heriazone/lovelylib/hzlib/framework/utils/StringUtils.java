package net.heriazone.lovelylib.hzlib.framework.utils;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Provides string processing and formatting utilities for robot systems.
 * <p>
 * <b>Architecture:</b> Centralizes string operations that were duplicated
 * across loader implementations. Focuses on robot naming, message formatting,
 * and data validation.
 * <p>
 * <b>Design Decision:</b> Static utility methods for performance and simplicity.
 * Includes validation patterns for consistent data handling.
 */
public class StringUtils {

    // -- Validation Patterns --

    /** Pattern for valid robot names (alphanumeric, spaces, basic punctuation). */
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9 ._-]{1,32}$");

    /** Pattern for valid owner names (Minecraft username format). */
    private static final Pattern VALID_OWNER_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    // -- String Validation --

    /**
     * Validates robot name format and length.
     * <p>
     * <b>Rules:</b> 1-32 characters, alphanumeric plus spaces and basic punctuation.
     * Prevents injection attacks and ensures display compatibility.
     *
     * @param name robot name to validate
     * @return true if name is valid
     */
    public static boolean isValidRobotName(String name) {
        return name != null && VALID_NAME_PATTERN.matcher(name).matches();
    } // isValidRobotName()

    /**
     * Validates owner name format (Minecraft username rules).
     * <p>
     * <b>Rules:</b> 3-16 characters, alphanumeric and underscores only.
     *
     * @param ownerName owner name to validate
     * @return true if owner name is valid
     */
    public static boolean isValidOwnerName(String ownerName) {
        return ownerName != null && VALID_OWNER_PATTERN.matcher(ownerName).matches();
    } // isValidOwnerName()

    // -- String Cleaning and Formatting --

    /**
     * Sanitizes string for safe display and storage.
     * <p>
     * <b>Safety:</b> Removes control characters and trims whitespace.
     * Prevents display issues and potential security problems.
     *
     * @param input string to sanitize
     * @return sanitized string, or empty string if input is null
     */
    public static String sanitize(String input) {
        if (input == null) return "";

        // Remove control characters and trim
        String sanitized = input.replaceAll("[\\p{Cntrl}]", "").trim();

        // Collapse multiple spaces to single space
        sanitized = sanitized.replaceAll("\\s+", " ");

        return sanitized;
    } // sanitize()

    /**
     * Truncates string to maximum length with ellipsis.
     * <p>
     * <b>Usage:</b> Ensures strings fit within display constraints while
     * indicating truncation to user.
     *
     * @param text string to truncate
     * @param maxLength maximum allowed length (including ellipsis)
     * @return truncated string with ellipsis if needed
     */
    public static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        if (maxLength <= 3) return "...".substring(0, maxLength);

        return text.substring(0, maxLength - 3) + "...";
    } // truncate()

    // -- Case Conversion --

    /**
     * Converts string to title case (first letter of each word capitalized).
     * <p>
     * <b>Usage:</b> Used for robot names and display text formatting.
     *
     * @param text string to convert
     * @return title case string
     */
    public static String toTitleCase(String text) {
        if (text == null || text.isEmpty()) return text;

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;

        for (char c : text.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(Character.toLowerCase(c));
            }
        }

        return result.toString();
    } // toTitleCase()

    // -- String Joining and Splitting --

    /**
     * Joins list of strings with specified delimiter.
     * <p>
     * <b>Usage:</b> Used for creating comma-separated lists in tooltips and messages.
     *
     * @param strings list of strings to join
     * @param delimiter delimiter to use between strings
     * @return joined string
     */
    public static String join(List<String> strings, String delimiter) {
        if (strings == null || strings.isEmpty()) return "";
        if (strings.size() == 1) return strings.get(0);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            if (i > 0) result.append(delimiter);
            result.append(strings.get(i));
        }

        return result.toString();
    } // join()

    /**
     * Joins array of strings with specified delimiter.
     *
     * @param strings array of strings to join
     * @param delimiter delimiter to use between strings
     * @return joined string
     */
    public static String join(String[] strings, String delimiter) {
        if (strings == null || strings.length == 0) return "";
        if (strings.length == 1) return strings[0];

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i > 0) result.append(delimiter);
            result.append(strings[i]);
        }

        return result.toString();
    } // join()

    // -- Null Safety --

    /**
     * Returns empty string if input is null, otherwise returns input.
     * <p>
     * <b>Safety:</b> Prevents null pointer exceptions in string operations.
     *
     * @param input string to check
     * @return input string or empty string if null
     */
    public static String nullToEmpty(String input) {
        return input == null ? "" : input;
    } // nullToEmpty()

    /**
     * Returns null if input is empty or whitespace-only, otherwise returns trimmed input.
     * <p>
     * <b>Usage:</b> Normalizes empty strings to null for consistent data handling.
     *
     * @param input string to check
     * @return trimmed string or null if empty/whitespace
     */
    public static String emptyToNull(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        return trimmed.isEmpty() ? null : trimmed;
    } // emptyToNull()

    // -- Comparison --

    /**
     * Null-safe string equality comparison.
     * <p>
     * <b>Safety:</b> Handles null values gracefully without throwing exceptions.
     *
     * @param a first string
     * @param b second string
     * @return true if strings are equal (both null counts as equal)
     */
    public static boolean equals(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equals(b);
    } // equals()

    /**
     * Null-safe case-insensitive string equality comparison.
     *
     * @param a first string
     * @param b second string
     * @return true if strings are equal ignoring case
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        return a.equalsIgnoreCase(b);
    } // equalsIgnoreCase()

    // -- Formatting Helpers --

    /**
     * Formats number with appropriate suffix (K, M, B).
     * <p>
     * <b>Usage:</b> Used for displaying large numbers like experience points
     * in a compact, readable format.
     *
     * @param number number to format
     * @return formatted string with suffix
     */
    public static String formatLargeNumber(long number) {
        if (number < 1000) return String.valueOf(number);
        if (number < 1000000) return String.format("%.1fK", number / 1000.0);
        if (number < 1000000000) return String.format("%.1fM", number / 1000000.0);
        return String.format("%.1fB", number / 1000000000.0);
    } // formatLargeNumber()

    /**
     * Formats percentage with specified decimal places.
     * <p>
     * <b>Usage:</b> Used for displaying protection percentages and stat bonuses.
     *
     * @param value percentage value (0.0 to 1.0)
     * @param decimalPlaces number of decimal places
     * @return formatted percentage string
     */
    public static String formatPercentage(double value, int decimalPlaces) {
        String format = "%." + decimalPlaces + "f%%";
        return String.format(format, value * 100.0);
    } // formatPercentage()

} // Class: StringUtils