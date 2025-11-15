package net.msymbios.rlovelyr.item.util;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import java.util.List;

public class TooltipUtils {

    // -- Variables --

    // Define default formatting for the tooltip
    private static final ChatFormatting defaultFormatting = ChatFormatting.GRAY;

    // -- Methods --

    // FORMATTING

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

    // TOOLTIPS

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
        var textFormatting = getFormattingColor(texture);

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
        if (!type.isEmpty()) tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_TYPE).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(LovelyRobotID.getVariantTranslation(type).withStyle(textFormatting)));
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
        var textFormatting = getFormattingColor(texture);

        // Add the color tooltip to the list
        tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_DESIGN).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(LovelyRobotID.getTranslation(texture).withStyle(textFormatting)));
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
        if(level > 0) tooltip.add(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_LEVEL).append(Component.nullToEmpty(": ")).withStyle(defaultFormatting).append(Component.nullToEmpty("" + level)).withStyle(textFormatting));
    } // appendTooltip ()

} // Class TooltipUtils