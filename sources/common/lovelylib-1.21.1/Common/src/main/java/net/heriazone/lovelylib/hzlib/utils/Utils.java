package net.heriazone.lovelylib.hzlib.utils;

import net.heriazone.lovelylib.hzlib.framework.utils.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;
import java.util.Objects;

/**
 * Common utility methods and constants used throughout the mod.
 * <p>
 * <b>Architecture:</b> Centralizes frequently used operations to avoid code duplication
 * and provide consistent behavior across all robot systems.
 * <p>
 * <b>Design Decision:</b> Static utility class rather than instance methods keeps usage
 * simple and avoids unnecessary object creation. Random instance is shared to maintain
 * consistent randomization behavior.
 */
public class Utils {

    // -- Constants --

    /** Mathematical constant PI for angle calculations. */
    public static final double PI = MathUtils.PI;

    // -- Entity Name Methods --

    /**
     * Retrieves custom name from entity with safe null handling.
     * <p>
     * <b>Usage:</b> Used for displaying robot names in messages and UI. Returns empty
     * string rather than null to simplify string concatenation.
     *
     * @param entity entity to get custom name from
     * @return custom name or empty string if not set
     */
    public static String getEntityCustomName(LivingEntity entity) {
        String customName = "";
        try {
            customName = entity.getCustomName().getString();
        } catch (Exception ignored) {
            // Custom name not found, return empty string
        }
        return customName;
    } // getEntityCustomName ()

    /**
     * Retrieves owner's name from tameable entity with safe null handling.
     * <p>
     * <b>Usage:</b> Used for displaying owner information in robot stats and messages.
     *
     * @param entity tameable entity to get owner name from
     * @return owner's scoreboard name or empty string if no owner
     */
    public static String getEntityOwnerName(TamableAnimal entity) {
        String ownerName = "";
        try {
            ownerName = Objects.requireNonNull(entity.getOwner()).getScoreboardName();
        } catch (Exception ignored) {
            // Owner not found, return empty string
        }
        return ownerName;
    } // getEntityOwnerName ()`n    // -- Random Generation --

    /**
     * Generates random title for robot naming.
     * <p>
     * <b>Design Decision:</b> Provides flavorful default names when robots spawn without
     * custom names. Combines adjectives with locations for variety.
     * <p>
     * <b>Usage:</b> Called during robot spawning or when player requests random name.
     *
     * @return randomly generated title string (e.g., "Brave the Mountains")
     */
    public static String getRandomTitle() {
        List<String> titles = List.of(
                "Wishful", "Cute", "Clever", "Adventurer", "Lazy", "Silly", "Stupid",
                "Smart", "Fancy", "Lucky", "Stinky", "Brave", "Swift", "Wise", "Bold",
                "Fearless", "Mighty", "Silent", "Cunning", "Valiant", "Merciless",
                "Gentle", "Fierce", "Noble", "Reckless", "Mysterious"
        );

        List<String> connectors = List.of(
                " the ", " of the ", " from the ", " with the ",
                " among the ", " beneath the ", " above the "
        );

        List<String> placeTitles = List.of(
                "Mountains", "Forest", "Desert", "Sea", "Sky",
                "Valley", "Caves", "Plains"
        );

        String connector = connectors.get(MathUtils.RANDOM.nextInt(connectors.size()));
        String title;

        // Use place titles for location-based connectors
        if (connector.equals(" of the ") || connector.equals(" from the ") ||
                connector.equals(" among the ") || connector.equals(" beneath the ") ||
                connector.equals(" above the ")) {
            title = placeTitles.get(MathUtils.RANDOM.nextInt(placeTitles.size()));
        } else {
            title = titles.get(MathUtils.RANDOM.nextInt(titles.size()));
        }

        return connector + title;
    } // getRandomTitle ()

    // -- Boolean Operations --

    /**
     * Inverts boolean value.
     * <p>
     * <b>Usage:</b> Provides semantic clarity when toggling states (e.g., sitting mode).
     * More readable than inline negation in complex conditions.
     *
     * @param value boolean to invert
     * @return inverted boolean value
     */
    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

} // Class: Utils