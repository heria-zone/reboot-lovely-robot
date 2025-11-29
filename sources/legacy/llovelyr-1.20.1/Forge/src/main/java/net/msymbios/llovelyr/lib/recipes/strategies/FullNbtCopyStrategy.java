package net.msymbios.llovelyr.source.recipes.internal.strategies;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.msymbios.llovelyr.source.recipes.interfaces.INbtTransferStrategy;

/**
 * Copies ALL NBT from source item to result.
 * <p>
 * <b>Behavior:</b> Complete replacement - result gets exact copy of source NBT.
 * No modifications applied, no data preserved from result's original NBT.
 * <p>
 * <b>Use Case:</b> Robot core → spawn egg (transfer everything)
 */
public class FullNbtCopyStrategy implements INbtTransferStrategy {

    // -- Fields --

    private final Item sourceItem;

    // -- Constructor --

    public FullNbtCopyStrategy(Item sourceItem) {
        this.sourceItem = sourceItem;
    } // FullNbtCopyStrategy

    // -- Inherited Methods --

    @Override
    public ItemStack transferNbt(CraftingContainer inventory, ItemStack result) {
        ItemStack source = findItem(inventory, sourceItem);
        if (source != null && source.hasTag()) {
            result.setTag(source.getTag().copy());
        }
        return result;
    } // transferNbt

    // -- Helper Methods --

    private ItemStack findItem(CraftingContainer inventory, Item item) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() == item) return stack;
        }
        return null;
    } // findItem

} // Class: FullNbtCopyStrategy
