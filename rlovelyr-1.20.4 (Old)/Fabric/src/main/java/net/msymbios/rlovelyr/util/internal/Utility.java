package net.msymbios.rlovelyr.util.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
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
    public static Formatting getFormattingColor(EntityTexture texture) {
        Formatting value = Formatting.WHITE;

        // Determine the formatting color based on the EntityTexture
        switch (texture) {
            case RANDOM -> value = Formatting.WHITE;
            case WHITE -> value = Formatting.WHITE;
            case ORANGE -> value = Formatting.GOLD;
            case MAGENTA -> value = Formatting.DARK_PURPLE;
            case LIGHT_BLUE -> value = Formatting.AQUA;
            case YELLOW -> value = Formatting.YELLOW;
            case LIME -> value = Formatting.GREEN;
            case PINK -> value = Formatting.LIGHT_PURPLE;
            case GRAY -> value = Formatting.DARK_GRAY;
            case LIGHT_GRAY -> value = Formatting.GRAY;
            case CYAN -> value = Formatting.AQUA;
            case PURPLE -> value = Formatting.DARK_PURPLE;
            case BLUE -> value = Formatting.BLUE;
            case BROWN -> value = Formatting.GOLD;
            case GREEN -> value = Formatting.DARK_GREEN;
            case RED -> value = Formatting.RED;
            case BLACK -> value = Formatting.GRAY;
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
    public static void addNameTooltip(List<Text> tooltip, NbtCompound nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        String customName = nbt.getString(LovelyRobotID.STAT_CUSTOM_NAME);
        if (!customName.isEmpty()) tooltip.add(Text.literal(customName).formatted(Utility.getFormattingColor(texture)));
    } // appendTooltip ()

    /**
     * Adds a type tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the type information
     * */
    public static void addTypeTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Define default and text formatting for the tooltip
        var defaultFormatting = Formatting.GRAY;
        var textFormatting = Formatting.WHITE;

        // Retrieve the type information from the NBT compound
        String type = nbt.getString(LovelyRobotID.STAT_TYPE);

        // Add the type tooltip if the type is not empty
        if (!type.isEmpty()) tooltip.add(Text.translatable(LovelyRobotID.TRANS_MSG_TYPE).append(Text.literal(": ")).formatted(defaultFormatting).append(Text.translatable(type).formatted(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a color tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the color information
     * */
    public static void addColorTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Retrieve the texture information from the NBT compound
        var texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));

        // Define default and text formatting for the tooltip
        var defaultFormatting = Formatting.GRAY;
        var textFormatting = Utility.getFormattingColor(texture);

        // Add the color tooltip to the list
        tooltip.add(Text.translatable(LovelyRobotID.TRANS_MSG_COLOR).append(Text.literal(": ")).formatted(defaultFormatting).append(Text.translatable(Utility.getTranslation(texture)).formatted(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a level tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the level information
     * */
    public static void addLevelTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Define default and text formatting for the tooltip
        var defaultFormatting = Formatting.GRAY;
        var textFormatting = Formatting.GOLD;

        // Retrieve the level information from the NBT compound
        int level = nbt.getInt(LovelyRobotID.STAT_LEVEL);

        // Add the level tooltip to the list if the level is greater than 0
        if(level > 0) tooltip.add(Text.translatable(LovelyRobotID.TRANS_MSG_LEVEL).append(Text.literal(": ")).formatted(defaultFormatting).append(Text.literal("" + level).formatted(textFormatting)));
    } // appendTooltip ()

} // Class Utility