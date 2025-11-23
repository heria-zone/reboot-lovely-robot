package net.msymbios.llovelyr.item.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.msymbios.llovelyr.config.LovelyRobotID;
import net.msymbios.llovelyr.entity.internal.enums.EntityTexture;

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

    private static final Formatting defaultFormatting = Formatting.GRAY;

    // -- Methods --

    /**
     * Maps EntityTexture to corresponding chat color for tooltip consistency.
     * <p>
     * <b>Design Decision:</b> Color mapping matches Minecraft dye colors to
     * provide intuitive visual feedback about robot appearance.
     *
     * @param texture the entity texture enum
     * @return the corresponding formatting color
     */
    public static Formatting getFormattingColor(EntityTexture texture) {
        return switch (texture) {
            case ORANGE -> Formatting.GOLD;
            case MAGENTA, PURPLE -> Formatting.DARK_PURPLE;
            case LIGHT_BLUE, CYAN -> Formatting.AQUA;
            case YELLOW -> Formatting.YELLOW;
            case LIME -> Formatting.GREEN;
            case PINK -> Formatting.LIGHT_PURPLE;
            case GRAY, BLACK -> Formatting.DARK_GRAY;
            case LIGHT_GRAY -> Formatting.GRAY;
            case BLUE -> Formatting.BLUE;
            case BROWN -> Formatting.GOLD;
            case GREEN -> Formatting.DARK_GREEN;
            case RED -> Formatting.RED;
            default -> Formatting.WHITE;
        };
    } // getFormattingColor()

    /**
     * Adds custom name tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addNameTooltip(List<Text> tooltip, NbtCompound nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        String customName = nbt.getString(LovelyRobotID.STAT_CUSTOM_NAME);
        Formatting textFormatting = getFormattingColor(texture);

        if (!customName.isEmpty()) {
            tooltip.add(Text.literal("Name: ").copy().formatted(defaultFormatting)
                .append(Text.literal(customName).copy().formatted(textFormatting)));
        }
    } // addNameTooltip()

    /**
     * Adds owner name tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addOwnerTooltip(List<Text> tooltip, NbtCompound nbt) {
        String ownerName = nbt.getString(LovelyRobotID.STAT_OWNER);

        if (!ownerName.isEmpty()) {
            tooltip.add(Text.literal("Owner: ").copy().formatted(defaultFormatting)
                .append(Text.literal(ownerName).copy().formatted(Formatting.GREEN)));
        }
    } // addOwnerTooltip()

    /**
     * Adds robot type tooltip if present in NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addTypeTooltip(List<Text> tooltip, NbtCompound nbt) {
        String type = nbt.getString(LovelyRobotID.STAT_TYPE);

        if (!type.isEmpty()) {
            tooltip.add(Text.translatable(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_TYPE))
                .append(": ").formatted(defaultFormatting)
                .append(Text.translatable(LovelyRobotID.getVariantTranslation(type)).formatted(Formatting.WHITE)));
        }
    } // addTypeTooltip()

    /**
     * Adds color/texture tooltip from NBT.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addColorTooltip(List<Text> tooltip, NbtCompound nbt) {
        EntityTexture texture = EntityTexture.byId(nbt.getInt(LovelyRobotID.STAT_COLOR));
        Formatting textFormatting = getFormattingColor(texture);

        tooltip.add(Text.translatable(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_DESIGN))
            .append(": ").formatted(defaultFormatting)
            .append(Text.translatable(LovelyRobotID.getTranslation(texture)).formatted(textFormatting)));
    } // addColorTooltip()

    /**
     * Adds level tooltip if level is greater than 0.
     *
     * @param tooltip the tooltip list to append to
     * @param nbt the NBT compound containing robot data
     */
    public static void addLevelTooltip(List<Text> tooltip, NbtCompound nbt) {
        int level = nbt.getInt(LovelyRobotID.STAT_LEVEL);

        if (level > 0) {
            tooltip.add(Text.translatable(LovelyRobotID.getMessageTranslation(LovelyRobotID.MSG_LEVEL))
                .append(": ").formatted(defaultFormatting)
                .append(Text.literal(String.valueOf(level)).formatted(Formatting.GOLD)));
        }
    } // addLevelTooltip()

} // Class: TooltipUtils
