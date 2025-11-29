package net.msymbios.llovelyr.source.recipes.interfaces;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;

/**
 * Strategy for transferring and modifying NBT during crafting.
 * <p>
 * <b>Architecture:</b> Defines contract for NBT transfer behavior, enabling
 * different strategies (full copy, additive merge, hybrid) to be swapped
 * without changing recipe implementation.
 * <p>
 * <b>Design Pattern:</b> Strategy pattern allows recipe classes to delegate
 * NBT transfer logic, promoting reusability and testability.
 */
@FunctionalInterface
public interface INbtTransferStrategy {

    // -- Methods --

    /**
     * Transfers and/or modifies NBT from crafting ingredients to result.
     * <p>
     * <b>Behavior:</b> Implementation determines whether to copy all NBT,
     * merge additively, or apply hybrid approach. Called for both preview
     * and final craft.
     *
     * @param inventory crafting grid inventory
     * @param result base result item stack
     * @return result with NBT applied
     */
    ItemStack transferNbt(CraftingContainer inventory, ItemStack result);

} // Interface: INbtTransferStrategy
