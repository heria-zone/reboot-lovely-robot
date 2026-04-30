package net.heriazone.lovelylib.api.recipes.modifiers;

import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.recipes.interfaces.INbtModifier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * Modifies color data key based on dye item in crafting input.
 * <p>
 * <b>Generic Design:</b> Takes data key name as parameter, doesn't hardcode
 * "color". Maps dye items to EntityTexture enum values.
 * <p>
 * <b>Dual write:</b> Writes both {@code STAT_COLOR} (int, for item model predicates)
 * and {@code STAT_COLOR_VARIANT} (entity-specific string, for entity restoration).
 * The entity type key is read from {@code STAT_TYPE} already present in the NBT.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses CraftingInput instead of CraftingContainer.
 */
public class DyeColorModifier implements INbtModifier {

    // -- Fields --

    private final String nbtKey;

    // -- Constructor --

    public DyeColorModifier(String nbtKey) {
        this.nbtKey = nbtKey;
    } // DyeColorModifier

    // -- Inherited Methods --

    @Override
    public void apply(CraftingInput input, CompoundTag nbt) {
        for (ItemStack stack : input.items()) {
            if (stack.getItem() instanceof DyeItem) {
                EntityTexture color = getTextureFromDye(stack.getItem());
                int colorId = color.getId();

                // Write int for item model predicate
                nbt.putInt(nbtKey, colorId);

                // Write entity-specific string for entity restoration (ADR_012)
                // Read entity type key from existing NBT (set when item was created)
                String entityKey = nbt.getString(LovelyConstant.STAT_TYPE);
                String colorVariant = entityKey.isEmpty()
                        ? color.Name()
                        : entityKey + "_" + color.Name();
                nbt.putString(LovelyConstant.STAT_COLOR_VARIANT, colorVariant);

                break;
            }
        }
    } // apply ()

    // -- Helper Methods --

    private EntityTexture getTextureFromDye(Item dye) {
        if (dye == Items.WHITE_DYE)      return EntityTexture.WHITE;
        if (dye == Items.ORANGE_DYE)     return EntityTexture.ORANGE;
        if (dye == Items.MAGENTA_DYE)    return EntityTexture.MAGENTA;
        if (dye == Items.LIGHT_BLUE_DYE) return EntityTexture.LIGHT_BLUE;
        if (dye == Items.YELLOW_DYE)     return EntityTexture.YELLOW;
        if (dye == Items.LIME_DYE)       return EntityTexture.LIME;
        if (dye == Items.PINK_DYE)       return EntityTexture.PINK;
        if (dye == Items.GRAY_DYE)       return EntityTexture.GRAY;
        if (dye == Items.LIGHT_GRAY_DYE) return EntityTexture.LIGHT_GRAY;
        if (dye == Items.CYAN_DYE)       return EntityTexture.CYAN;
        if (dye == Items.PURPLE_DYE)     return EntityTexture.PURPLE;
        if (dye == Items.BLUE_DYE)       return EntityTexture.BLUE;
        if (dye == Items.BROWN_DYE)      return EntityTexture.BROWN;
        if (dye == Items.GREEN_DYE)      return EntityTexture.GREEN;
        if (dye == Items.RED_DYE)        return EntityTexture.RED;
        if (dye == Items.BLACK_DYE)      return EntityTexture.BLACK;
        return EntityTexture.WHITE;
    } // getTextureFromDye ()

} // Class: DyeColorModifier