package net.msymbios.llovelyr.lib.recipes.modifiers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.msymbios.llovelyr.common.entity.enums.EntityTexture;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtModifier;

/**
 * Modifies color NBT key based on dye item in crafting grid.
 * <p>
 * <b>Generic Design:</b> Takes NBT key name as parameter, doesn't hardcode
 * "color". Maps dye items to EntityTexture enum values.
 * <p>
 * <b>Additive Behavior:</b> Only modifies the specified NBT key, preserving
 * all other NBT data in the compound.
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
    public void apply(CraftingContainer inventory, CompoundTag nbt) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof DyeItem) {
                int colorId = getColorIdFromDye(stack.getItem());
                nbt.putInt(nbtKey, colorId);
                break;
            }
        }
    } // apply ()

    // -- Helper Methods --

    private int getColorIdFromDye(Item dye) {
        if (dye == Items.WHITE_DYE) return EntityTexture.WHITE.getId();
        if (dye == Items.ORANGE_DYE) return EntityTexture.ORANGE.getId();
        if (dye == Items.MAGENTA_DYE) return EntityTexture.MAGENTA.getId();
        if (dye == Items.LIGHT_BLUE_DYE) return EntityTexture.LIGHT_BLUE.getId();
        if (dye == Items.YELLOW_DYE) return EntityTexture.YELLOW.getId();
        if (dye == Items.LIME_DYE) return EntityTexture.LIME.getId();
        if (dye == Items.PINK_DYE) return EntityTexture.PINK.getId();
        if (dye == Items.GRAY_DYE) return EntityTexture.GRAY.getId();
        if (dye == Items.LIGHT_GRAY_DYE) return EntityTexture.LIGHT_GRAY.getId();
        if (dye == Items.CYAN_DYE) return EntityTexture.CYAN.getId();
        if (dye == Items.PURPLE_DYE) return EntityTexture.PURPLE.getId();
        if (dye == Items.BLUE_DYE) return EntityTexture.BLUE.getId();
        if (dye == Items.BROWN_DYE) return EntityTexture.BROWN.getId();
        if (dye == Items.GREEN_DYE) return EntityTexture.GREEN.getId();
        if (dye == Items.RED_DYE) return EntityTexture.RED.getId();
        if (dye == Items.BLACK_DYE) return EntityTexture.BLACK.getId();
        return EntityTexture.WHITE.getId();
    } // getColorIdFromDye ()

} // Class: DyeColorModifier
