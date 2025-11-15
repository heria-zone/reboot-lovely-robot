package net.msymbios.rlovelyr.common.util.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Utility {

    // -- Variables --

    public static final double PI = 3.14159265358979323846;

    public static final Random random = new Random();

    // -- Methods --

    /**
     * Gets the custom name of a LivingEntity.
     *
     * @param entity the LivingEntity to get the custom name from
     * @return the custom name of the LivingEntity, or an empty string if not present
     */
    public static String getEntityCustomName(LivingEntity entity) {
        String customName = "";
        try {customName = entity.getCustomName().getString();}
        catch (Exception ignored) {} // custom name not be found, return empty string
        return customName;
    } // getEntityCustomName ()

    /**
     * Retrieves the name of the owner of a given TameableEntity.
     *
     * @param entity the TameableEntity to retrieve the owner's name for
     * @return the owner's name, or an empty string if no custom name is found
     */
    public static String getEntityOwnerName(TameableEntity entity) {
        String ownerName = "";
        try {ownerName = Objects.requireNonNull(entity.getOwner()).getNameForScoreboard();}
        catch (Exception ignored) {} // owner name not be found, return empty string
        return ownerName;
    } // getEntityOwnerName ()

    public static String getRandomTitle() {
        List<String> titles = List.of("Wishful", "Cute", "Clever", "Adventurer", "Lazy", "Silly", "Stupid", "Smart", "Fancy", "Lucky", "Stinky", "Brave", "Swift", "Wise", "Bold", "Fearless", "Mighty", "Silent", "Cunning", "Valiant", "Merciless", "Gentle", "Fierce", "Noble", "Reckless", "Mysterious");
        List<String> connectors = List.of(" the ", " of the ", " from the ", " with the ", " among the ", " beneath the ", " above the ");
        List<String> placeTitles = List.of("Mountains", "Forest", "Desert", "Sea", "Sky", "Valley", "Caves", "Plains");

        String connector = connectors.get(random.nextInt(connectors.size()));
        String title;

        if (connector.equals(" of the ") || connector.equals(" from the ") || connector.equals(" among the ") || connector.equals(" beneath the ") || connector.equals(" above the ")) {
            title = placeTitles.get(random.nextInt(placeTitles.size()));
        } else {
            title = titles.get(random.nextInt(titles.size()));
        }

        return connector + title;
    } // getRandomTitle ()

    /**
     * Inverts a boolean value.
     *
     * @param  value  the boolean value to be inverted
     * @return       the inverted boolean value
     */
    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

} // Class Utility