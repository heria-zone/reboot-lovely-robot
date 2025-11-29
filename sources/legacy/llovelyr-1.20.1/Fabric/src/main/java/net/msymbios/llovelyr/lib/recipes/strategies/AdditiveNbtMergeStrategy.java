package net.msymbios.llovelyr.source.recipes.internal.strategies;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.msymbios.llovelyr.source.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.source.recipes.interfaces.INbtModifier;

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
    public ItemStack transferNbt(RecipeInputInventory inventory, ItemStack result) {
        ItemStack base = findItem(inventory, baseItem);
        
        NbtCompound resultNbt = base != null && base.hasNbt() 
            ? base.getNbt().copy() 
            : new NbtCompound();
        
        for (INbtModifier modifier : modifiers) {
            modifier.apply(inventory, resultNbt);
        }
        
        if (!resultNbt.isEmpty()) {
            result.setNbt(resultNbt);
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

} // Class: AdditiveNbtMergeStrategy
