package net.heriazone.hzlib.api.recipes.strategies;

import net.heriazone.hzlib.api.recipes.interfaces.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * Copies ALL custom data from source item to result.
 * <p>
 * <b>Behavior:</b> Complete replacement - result gets exact copy of source custom data.
 * No modifications applied, no data preserved from result's original custom data.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses Data Components and CraftingInput.
 * This strategy copies the CUSTOM_DATA component from source to result.
 * <p>
 * <b>Use Case:</b> Robot core → spawn egg (transfer everything)
 */
public class FullNbtCopyStrategy implements INbtTransferStrategy {

    // -- Variables --

    private final Item sourceItem;

    // -- Constructor --

    public FullNbtCopyStrategy(Item sourceItem) {
        this.sourceItem = sourceItem;
    } // Constructor: FullNbtCopyStrategy ()

    // -- Inherited Methods --

    @Override
    public ItemStack transferNbt(CraftingInput input, ItemStack result) {
        ItemStack source = findItem(input, sourceItem);

        // Check if source exists and is not empty
        if (!source.isEmpty() && source.has(DataComponents.CUSTOM_DATA)) {
            CustomData sourceData = source.get(DataComponents.CUSTOM_DATA);

            if (sourceData != null && !sourceData.isEmpty()) {
                // Copy the entire NBT tag from source
                CompoundTag copiedTag = sourceData.copyTag();

                // Apply to result using CustomData
                //result.set(DataComponents.CUSTOM_DATA, CustomData.of(copiedTag));
                result.set(DataComponents.CUSTOM_DATA, CustomData.EMPTY.update(tag -> tag.merge(copiedTag)));
            }
        }

        return result;
    } // transferNbt ()

    // -- Helper Methods --

    private ItemStack findItem(CraftingInput input, Item item) {
        for (ItemStack stack : input.items()) {
            if (!stack.isEmpty() && stack.getItem() == item) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    } // findItem ()

} // Class: FullNbtCopyStrategy