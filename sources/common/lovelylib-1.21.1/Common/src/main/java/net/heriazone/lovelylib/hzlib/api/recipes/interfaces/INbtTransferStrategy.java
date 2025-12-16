package net.heriazone.lovelylib.hzlib.api.recipes.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * Strategy for transferring and modifying item data during crafting.
 * <p>
 * <b>Architecture:</b> Defines contract for data transfer behavior, enabling
 * different strategies (full copy, additive merge, hybrid) to be swapped
 * without changing recipe implementation.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses Data Components instead of NBT
 * for ItemStack storage. Implementations handle conversion between CompoundTag
 * (intermediate format) and Data Components (storage format). Also uses
 * CraftingInput instead of CraftingContainer.
 * <p>
 * <b>Design Pattern:</b> Strategy pattern allows recipe classes to delegate
 * data transfer logic, promoting reusability and testability.
 */
@FunctionalInterface
public interface INbtTransferStrategy {

    // -- Methods --

    /**
     * Transfers and/or modifies data from crafting ingredients to result.
     * <p>
     * <b>Behavior:</b> Implementation determines whether to copy all data,
     * merge additively, or apply hybrid approach. Handles conversion between
     * Data Components and intermediate CompoundTag format.
     * <p>
     * <b>Called for:</b> Both preview (client) and final craft (server).
     *
     * @param input crafting input containing ingredients
     * @param result base result item stack
     * @return result with data applied via Data Components
     */
    ItemStack transferNbt(CraftingInput input, ItemStack result);

} // Interface: INbtTransferStrategy