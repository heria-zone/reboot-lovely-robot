package net.msymbios.llovelyr.source.recipes.internal.strategies;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    public ItemStack transferNbt(RecipeInputInventory inventory, ItemStack result) {
        ItemStack source = findItem(inventory, sourceItem);
        if (source != null && source.hasNbt()) {
            result.setNbt(source.getNbt().copy());
        }
        return result;
    } // transferNbt

    // -- Helper Methods --

    private ItemStack findItem(RecipeInputInventory inventory, Item item) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == item) return stack;
        }
        return null;
    } // findItem

} // Class: FullNbtCopyStrategy
