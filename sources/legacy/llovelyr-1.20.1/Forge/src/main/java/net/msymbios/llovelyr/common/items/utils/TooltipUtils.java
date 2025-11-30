package net.msymbios.llovelyr.common.items.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

import java.util.List;

/**
 * Provides formatted tooltip generation for robot items.
 * <p>
 * <b>Architecture:</b> Centralizes tooltip logic for consistent item hover
 * text across robot cores and spawn eggs. Reads NBT data to display robot
 * attributes with color-coded formatting.
 * <p>
 * <b>Formatting Strategy:</b> Uses EntityTexture-based color mapping for
 * visual consistency with in-game robot appearance.
 */
public class TooltipUtils {

    // -- Variables --

    private static final ChatFormatting defaultFormatting = ChatFormatting.GRAY;

    // -- Methods --

    /**
     * Maps EntityTexture to corresponding chat color for tooltip consistency.
     * <p>
     * <b>Design Decision:</b> Color mapping matches Minecraft dye colors to
     * provide intuitive visual feedback about robot appearance.
     *
     * @param texture the entity texture enum
     * @return the corresponding chat formatting color
     */
    public static ChatFormatting getFormattingColor(EntityTexture texture) {
        return switch (texture) {
            case ORANGE -> ChatFormatting.GOLD;
            case MAGENTA, PURPLE -> ChatFormatting.DARK_PURPLE;
            case LIGHT_BLUE, CYAN -> ChatFormatting.AQUA;
            case YELLOW -> ChatFormatting.YELLOW;
            case LIME -> ChatFormatting.GREEN;
            case PINK -> ChatFormatting.LIGHT_PURPLE;
            case GRAY, BLACK -> ChatFormatting.DARK_GRAY;
            case LIGHT_GRAY -> ChatFormatting.GRAY;
            case BLUE -> ChatFormatting.BLUE;
            case BROWN -> ChatFormatting.GOLD;
            case GREEN -> ChatFormatting.DARK_GREEN;
            case RED -> ChatFormatting.RED;
            default -> ChatFormatting.WHITE;
        };
    } // getFormattingColor()

    /**
     * Adds custom name tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addNameTooltip(List<Component> tooltip, CompoundTag nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyIdentifier.STAT_COLOR));
        String customName = nbt.getString(LovelyIdentifier.STAT_CUSTOM_NAME);
        ChatFormatting textFormatting = getFormattingColor(texture);

        if (!customName.isEmpty()) {
            tooltip.add(Component.literal("Name: ").withStyle(defaultFormatting)
                .append(Component.literal(customName).withStyle(textFormatting)));
        }
    } // addNameTooltip()

    /**
     * Adds owner name tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addOwnerTooltip(List<Component> tooltip, CompoundTag nbt) {
        String ownerName = nbt.getString(LovelyIdentifier.STAT_OWNER);

        if (!ownerName.isEmpty()) {
            tooltip.add(Component.literal("Owner: ").withStyle(defaultFormatting)
                .append(Component.literal(ownerName).withStyle(ChatFormatting.GREEN)));
        }
    } // addOwnerTooltip()

    /**
     * Adds robot type tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addTypeTooltip(List<Component> tooltip, CompoundTag nbt) {
        String type = nbt.getString(LovelyIdentifier.STAT_TYPE);

        if (!type.isEmpty()) {
            tooltip.add(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_TYPE)
                .append(": ").withStyle(defaultFormatting)
                .append(LovelyIdentifier.getVariantTranslation(type)).withStyle(ChatFormatting.WHITE));
        }
    } // addTypeTooltip()

    /**
     * Adds color/texture tooltip from NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addColorTooltip(List<Component> tooltip, CompoundTag nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyIdentifier.STAT_COLOR));
        ChatFormatting textFormatting = getFormattingColor(texture);

        tooltip.add(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_DESIGN)
            .append(": ").withStyle(defaultFormatting)
            .append(LovelyIdentifier.getTranslation(texture)).withStyle(textFormatting));
    } // addColorTooltip()

    /**
     * Adds level tooltip if level is greater than 0.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addLevelTooltip(List<Component> tooltip, CompoundTag nbt) {
        int level = nbt.getInt(LovelyIdentifier.STAT_LEVEL);

        if (level > 0) {
            tooltip.add(LovelyIdentifier.getMessageTranslation(LovelyIdentifier.MSG_LEVEL)
                .append(": ").withStyle(defaultFormatting)
                .append(Component.literal(String.valueOf(level)).withStyle(ChatFormatting.GOLD)));
        }
    } // addLevelTooltip()

} // Class: TooltipUtils