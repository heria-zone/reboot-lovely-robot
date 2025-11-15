package net.msymbios.rlovelyr.util.internal;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.enums.EntityState;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;

import java.util.List;

public class Utility {

    // -- Variables --
    public static final double PI = 3.14159265358979323846;

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

    /**
     * Inverts a boolean value.
     *
     * @param  value  the boolean value to be inverted
     * @return       the inverted boolean value
     */
    public static boolean invertBoolean(boolean value) {
        return !value;
    } // invertBoolean ()

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

    // TRANSLATABLE

    /**
     * Retrieves the translatable string based on the given entity variant.
     *
     * @param  variant  the entity variant for which the translatable string is to be retrieved
     * @return          the translatable message key corresponding to the entity variant
     */
    public static String getTranslation(EntityVariant variant) {
        String value = LovelyRobotID.TRANS_MSG_BUNNY;
        switch (variant) {
            case Bunny -> value = LovelyRobotID.TRANS_MSG_BUNNY;
            case Bunny2 -> value = LovelyRobotID.TRANS_MSG_BUNNY2;
            case Dragon -> value = LovelyRobotID.TRANS_MSG_DRAGON;
            case Honey -> value = LovelyRobotID.TRANS_MSG_HONEY;
            case Kitsune -> value = LovelyRobotID.TRANS_MSG_KITSUNE;
            case Neko -> value = LovelyRobotID.TRANS_MSG_NEKO;
            case Vanilla -> value = LovelyRobotID.TRANS_MSG_VANILLA;
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the translatable message key based on the given entity State.
     *
     * @param  state  the entity state for which the translatable message key is needed
     * @return        the translatable message key corresponding to the entity state
     */
    public static String getTranslation(EntityState state){
        String value = LovelyRobotID.TRANS_MSG_FOLLOW;
        switch (state) {
            case Follow -> value = LovelyRobotID.TRANS_MSG_FOLLOW;
            case Defense -> value = LovelyRobotID.TRANS_MSG_DEFENCE;
            case Standby -> value = LovelyRobotID.TRANS_MSG_STANDBY;
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the corresponding translatable message key based a given Texture.
     *
     * @param  texture the entity texture for which the translatable message key is needed
     * @return         the message key corresponding to the EntityTexture
     */
    public static String getTranslation(EntityTexture texture) {
        String value = LovelyRobotID.TRANS_MSG_PINK;
        switch (texture) {
            case RANDOM -> value = LovelyRobotID.TRANS_MSG_RANDOM;
            case WHITE -> value = LovelyRobotID.TRANS_MSG_WHITE;
            case ORANGE -> value = LovelyRobotID.TRANS_MSG_ORANGE;
            case MAGENTA -> value = LovelyRobotID.TRANS_MSG_MAGENTA;
            case LIGHT_BLUE -> value = LovelyRobotID.TRANS_MSG_LIGHT_BLUE;
            case YELLOW -> value = LovelyRobotID.TRANS_MSG_YELLOW;
            case LIME -> value = LovelyRobotID.TRANS_MSG_LIME;
            case PINK -> value = LovelyRobotID.TRANS_MSG_PINK;
            case GRAY -> value = LovelyRobotID.TRANS_MSG_GRAY;
            case LIGHT_GRAY -> value = LovelyRobotID.TRANS_MSG_LIGHT_GRAY;
            case CYAN -> value = LovelyRobotID.TRANS_MSG_CYAN;
            case PURPLE -> value = LovelyRobotID.TRANS_MSG_PURPLE;
            case BLUE -> value = LovelyRobotID.TRANS_MSG_BLUE;
            case BROWN -> value = LovelyRobotID.TRANS_MSG_BROWN;
            case GREEN -> value = LovelyRobotID.TRANS_MSG_GREEN;
            case RED -> value = LovelyRobotID.TRANS_MSG_RED;
            case BLACK -> value = LovelyRobotID.TRANS_MSG_BLACK;
        }
        return value;
    } // getTranslation ()

    // TOOLTIP

    /**
     * Adds a custom name tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the entity texture and custom name
     * */
    public static void addNameTooltip(List<Component> tooltip, CompoundTag nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        String customName = nbt.getString(LovelyRobotID.STAT_CUSTOM_NAME);
        if (!customName.isEmpty()) tooltip.add(Component.literal(customName).withStyle(Utility.getFormattingColor(texture)));
    } // appendTooltip ()

    /**
     * Adds a type tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the type information
     * */
    public static void addTypeTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Define default and text formatting for the tooltip
        var defaultFormatting = ChatFormatting.GRAY;
        var textFormatting = ChatFormatting.WHITE;

        // Retrieve the type information from the NBT compound
        String type = nbt.getString(LovelyRobotID.STAT_TYPE);

        // Add the type tooltip if the type is not empty
        if (!type.isEmpty()) tooltip.add(Component.translatable(LovelyRobotID.TRANS_MSG_TYPE).append(Component.literal(": ")).withStyle(defaultFormatting).append(Component.translatable(type).withStyle(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a color tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the color information
     * */
    public static void addColorTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Retrieve the texture information from the NBT compound
        var texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));

        // Define default and text formatting for the tooltip
        var defaultFormatting = ChatFormatting.GRAY;
        var textFormatting = Utility.getFormattingColor(texture);

        // Add the color tooltip to the list
        tooltip.add(Component.translatable(LovelyRobotID.TRANS_MSG_COLOR).append(Component.literal(": ")).withStyle(defaultFormatting).append(Component.translatable(Utility.getTranslation(texture)).withStyle(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a level tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the level information
     * */
    public static void addLevelTooltip(List<Component> tooltip, CompoundTag nbt) {
        // Define default and text formatting for the tooltip
        var defaultFormatting = ChatFormatting.GRAY;
        var textFormatting = ChatFormatting.GOLD;

        // Retrieve the level information from the NBT compound
        int level = nbt.getInt(LovelyRobotID.STAT_LEVEL);

        // Add the level tooltip to the list if the level is greater than 0
        if(level > 0) tooltip.add(Component.translatable(LovelyRobotID.TRANS_MSG_LEVEL).append(Component.literal(": ")).withStyle(defaultFormatting).append(Component.literal("" + level).withStyle(textFormatting)));
    } // appendTooltip ()

} // Class Utility