package net.msymbios.llovelyr.lib.recipes.strategies;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtModifier;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtTransferStrategy;

import java.util.List;

/**
 * Merges NBT from base item and applies modifiers additively.
 * <p>
 * <b>Behavior:</b>
 * - Starts with base item's existing NBT (if any)
 * - Applies modifiers that add/overwrite specific keys
 * - Preserves all unmodified NBT keys
 * - Never loses data unless explicitly overwritten
 * <p>
 * <b>Use Case:</b> Spawn egg + dye (keep all data, just change color)
 */
public class AdditiveNbtMergeStrategy implements INbtTransferStrategy {

    // -- Fields --

    private final Item baseItem;
    private final List<INbtModifier> modifiers;

    // -- Constructor --

    public AdditiveNbtMergeStrategy(Item baseItem, List<INbtModifier> modifiers) {
        this.baseItem = baseItem;
        this.modifiers = modifiers;
    } // AdditiveNbtMergeStrategy

    // -- Inherited Methods --

    @Override
    public ItemStack transferNbt(CraftingContainer inventory, ItemStack result) {
        ItemStack base = findItem(inventory, baseItem);
        
        CompoundTag resultNbt = base != null && base.hasTag() 
            ? base.getTag().copy() 
            : new CompoundTag();
        
        for (INbtModifier modifier : modifiers) {
            modifier.apply(inventory, resultNbt);
        }
        
        if (!resultNbt.isEmpty()) {
            result.setTag(resultNbt);
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

} // Class: AdditiveNbtMergeStrategy
