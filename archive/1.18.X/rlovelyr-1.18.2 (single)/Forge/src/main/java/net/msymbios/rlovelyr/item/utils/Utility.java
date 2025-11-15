package net.msymbios.rlovelyr.item.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import java.util.List;
import java.util.Random;

public class Utility {

    // -- Variables --

    public static final double PI = 3.14159265358979323846;

    private static final Random random = new Random();

    // Define default formatting for the tooltip
    private static final ChatFormatting defaultFormatting = ChatFormatting.AQUA;

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
        catch (Exception ignored) {} // Custom name not found, return empty string
        return customName;
    } // getEntityCustomName ()


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

    // TOOLTIP

    /**
     * Adds a custom name tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the entity texture and custom name
     * */
    public static void addNameTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Retrieve the customName & texture information from the NBT compound
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        String customName = nbt.getString(LovelyRobotID.STAT_CUSTOM_NAME);

        // Define and text formatting for the tooltip
        var textFormatting = Utility.getFormattingColor(texture);

        // Add the customName tooltip if the customName is not empty
        if (!customName.isEmpty()) tooltip.add(Component.nullToEmpty("Name: ").copy().withStyle(defaultFormatting).append(Component.nullToEmpty(customName).copy().withStyle(textFormatting)));
    } // addNameTooltip ()

    /**
     * Adds a custom name tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the entity texture and custom name
     * */
    public static void addOwnerTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Define text formatting for the tooltip
        var textFormatting = ChatFormatting.GREEN;

        // Retrieve the ownerName information from the NBT compound
        String ownerName = nbt.getString(LovelyRobotID.STAT_OWNER);

        // Add the ownerName tooltip if the owner is not empty
        if (!ownerName.isEmpty()) tooltip.add(Component.nullToEmpty("Owner: ").copy().withStyle(defaultFormatting).append(Component.nullToEmpty(ownerName).copy().withStyle(textFormatting)));
    } // addOwnerTooltip ()

    /**
     * Adds a type tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the type information
     * */
    public static void addTypeTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Define and text formatting for the tooltip
        var textFormatting = ChatFormatting.WHITE;

        // Retrieve the type information from the NBT compound
        String type = nbt.getString(LovelyRobotID.STAT_TYPE);

        // Add the type tooltip if the type is not empty
        if (!type.isEmpty()) tooltip.add(new TranslatableComponent(LovelyRobotID.TRANS_MSG_TYPE).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(new TranslatableComponent(type).withStyle(textFormatting)));
    } // addTypeTooltip ()

    /**
     * Adds a color tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the color information
     * */
    public static void addColorTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Retrieve the texture information from the NBT compound
        var texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));

        // Define and text formatting for the tooltip
        var textFormatting = Utility.getFormattingColor(texture);

        // Add the color tooltip to the list
        tooltip.add(new TranslatableComponent(LovelyRobotID.TRANS_MSG_COLOR).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(new TranslatableComponent(LovelyRobotID.getTranslation(texture)).withStyle(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a level tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the level information
     * */
    public static void addLevelTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Define and text formatting for the tooltip
        var textFormatting = ChatFormatting.GOLD;

        // Retrieve the level information from the NBT compound
        int level = nbt.getInt(LovelyRobotID.STAT_LEVEL);

        // Add the level tooltip to the list if the level is greater than 0
        if(level > 0) tooltip.add(new TranslatableComponent(LovelyRobotID.TRANS_MSG_LEVEL).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(Component.nullToEmpty("" + level)).withStyle(textFormatting));
    } // appendTooltip ()

    // -- FORMATTING --

    /**
     * Returns the formatting color based on the provided EntityTexture.
     * @param texture The EntityTexture to determine the formatting color for.
     * @return The corresponding Formatting color for the EntityTexture.
     */
    public static ChatFormatting getFormattingColor(EntityTexture texture) {
        ChatFormatting value = ChatFormatting.WHITE;

        // Determine the formatting color based on the EntityTexture
        switch (texture) {
            case RANDOM -> value = ChatFormatting.WHITE;
            case WHITE -> value = ChatFormatting.WHITE;
            case ORANGE -> value = ChatFormatting.GOLD;
            case MAGENTA -> value = ChatFormatting.DARK_PURPLE;
            case LIGHT_BLUE -> value = ChatFormatting.AQUA;
            case YELLOW -> value = ChatFormatting.YELLOW;
            case LIME -> value = ChatFormatting.GREEN;
            case PINK -> value = ChatFormatting.LIGHT_PURPLE;
            case GRAY -> value = ChatFormatting.DARK_GRAY;
            case LIGHT_GRAY -> value = ChatFormatting.GRAY;
            case CYAN -> value = ChatFormatting.AQUA;
            case PURPLE -> value = ChatFormatting.DARK_PURPLE;
            case BLUE -> value = ChatFormatting.BLUE;
            case BROWN -> value = ChatFormatting.GOLD;
            case GREEN -> value = ChatFormatting.DARK_GREEN;
            case RED -> value = ChatFormatting.RED;
            case BLACK -> value = ChatFormatting.GRAY;
        }

        return value;
    } // getFormattingColor ()

} // Class Utility