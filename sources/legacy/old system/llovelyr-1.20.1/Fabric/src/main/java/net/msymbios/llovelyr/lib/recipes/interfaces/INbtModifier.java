package net.msymbios.llovelyr.lib.recipes.interfaces;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.nbt.NbtCompound;

/**
 * Defines how to modify NBT based on crafting ingredients.
 * <p>
 * <b>Architecture:</b> Completely generic modifier interface that doesn't
 * assume specific NBT keys or values. Implementations determine what to
 * modify and how to determine new values.
 * <p>
 * <b>Composability:</b> Multiple modifiers can be chained together, each
 * adding or overwriting specific NBT keys without affecting others.
 * <p>
 * <b>Additive Behavior:</b> Modifiers add or overwrite specific keys but
 * never erase unrelated NBT data, ensuring data preservation.
 */
@FunctionalInterface
public interface INbtModifier {

    // -- Methods --

    /**
     * Applies NBT modifications based on crafting ingredients.
     * <p>
     * <b>Behavior:</b> Searches crafting grid for relevant items and modifies
     * NBT accordingly. Should be additive - only modify specific keys without
     * erasing other data.
     *
     * @param inventory crafting grid to search for modifier items
     * @param nbt NBT compound to modify
     */
    void apply(RecipeInputInventory inventory, NbtCompound nbt);

} // Interface: INbtModifier
