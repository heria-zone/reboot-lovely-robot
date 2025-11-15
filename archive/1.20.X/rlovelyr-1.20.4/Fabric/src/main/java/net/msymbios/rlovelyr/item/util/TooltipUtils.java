package net.msymbios.rlovelyr.item.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import java.util.List;

public class TooltipUtils {

    // -- Variables --

    // Define default formatting for the tooltip
    private static final Formatting defaultFormatting = Formatting.GRAY;

    // -- Methods --

    // FORMATTING

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

    // TOOLTIPS

    /**
     * Adds a custom name tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the entity texture and custom name
     * */
    public static void addNameTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Retrieve the customName & texture information from the NBT compound
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        String customName = nbt.getString(LovelyRobotID.STAT_CUSTOM_NAME);

        // Define and text formatting for the tooltip
        var textFormatting = getFormattingColor(texture);

        // Add the customName tooltip if the customName is not empty
        if (!customName.isEmpty()) tooltip.add(Text.literal("Name: ").copy().formatted(defaultFormatting).append(Text.literal(customName).copy().formatted(textFormatting)));
    } // addNameTooltip ()

    /**
     * Adds a custom name tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the entity texture and custom name
     * */
    public static void addOwnerTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Define text formatting for the tooltip
        var textFormatting = Formatting.GREEN;

        // Retrieve the ownerName information from the NBT compound
        String ownerName = nbt.getString(LovelyRobotID.STAT_OWNER);

        // Add the ownerName tooltip if the owner is not empty
        if (!ownerName.isEmpty()) tooltip.add(Text.literal("Owner: ").copy().formatted(defaultFormatting).append(Text.literal(ownerName).copy().formatted(textFormatting)));
    } // addOwnerTooltip ()

    /**
     * Adds a type tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the type information
     * */
    public static void addTypeTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Define and text formatting for the tooltip
        var textFormatting = Formatting.WHITE;

        // Retrieve the type information from the NBT compound
        String type = nbt.getString(LovelyRobotID.STAT_TYPE);

        // Add the type tooltip if the type is not empty
        if (!type.isEmpty()) tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_TYPE).append(Text.literal(": ")).formatted(defaultFormatting).append(LovelyRobotID.getVariantTranslation(type).formatted(textFormatting)));
    } // addTypeTooltip ()

    /**
     * Adds a color tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the color information
     * */
    public static void addColorTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Retrieve the texture information from the NBT compound
        var texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));

        // Define and text formatting for the tooltip
        var textFormatting = getFormattingColor(texture);

        // Add the color tooltip to the list
        tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_DESIGN).append(Text.literal(": ")).formatted(defaultFormatting).append(LovelyRobotID.getTranslation(texture).formatted(textFormatting)));
    } // appendTooltip ()

    /**
     * Adds a level tooltip to the given list of texts based on the NBT compound.
     *
     * @param tooltip The list of texts to which the tooltip will be added
     * @param nbt The NBT compound containing the level information
     * */
    public static void addLevelTooltip(List<Text> tooltip, NbtCompound nbt) {
        // Define and text formatting for the tooltip
        var textFormatting = Formatting.GOLD;

        // Retrieve the level information from the NBT compound
        int level = nbt.getInt(LovelyRobotID.STAT_LEVEL);

        // Add the level tooltip to the list if the level is greater than 0
        if(level > 0) tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_LEVEL).append(Text.literal(": ")).formatted(defaultFormatting).append(Text.literal("" + level)).formatted(textFormatting));
    } // appendTooltip ()

} // Class TooltipUtils