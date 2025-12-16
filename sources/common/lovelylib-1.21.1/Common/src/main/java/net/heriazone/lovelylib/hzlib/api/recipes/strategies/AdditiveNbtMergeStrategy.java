package net.heriazone.lovelylib.hzlib.api.recipes.strategies;

import net.heriazone.lovelylib.hzlib.api.recipes.interfaces.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;

import java.util.List;

/**
 * Merges data from base item and applies modifiers additively.
 * <p>
 * <b>Behavior:</b>
 * - Starts with base item's existing custom data (if any)
 * - Applies modifiers that add/overwrite specific keys
 * - Preserves all unmodified data keys
 * - Never loses data unless explicitly overwritten
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses Data Components and CraftingInput.
 * This strategy extracts custom data to CompoundTag, applies modifiers, then stores
 * back as CustomData component.
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
    } // Constructor: AdditiveNbtMergeStrategy ()

    // -- Inherited Methods --

    @Override
    public ItemStack transferNbt(CraftingInput input, ItemStack result) {
        ItemStack base = findItem(input, baseItem);

        // Extract existing custom data from base item
        CompoundTag resultNbt = new CompoundTag();

        if (!base.isEmpty() && base.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = base.get(DataComponents.CUSTOM_DATA);
            if (customData != null && !customData.isEmpty()) {
                resultNbt = customData.copyTag();
            }
        }

        // Apply all modifiers to the tag
        for (INbtModifier modifier : modifiers) {
            modifier.apply(input, resultNbt);
        }

        // Store modified data back as CustomData component
        if (!resultNbt.isEmpty()) {
            result.set(DataComponents.CUSTOM_DATA, CustomData.of(resultNbt));
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

} // Class: AdditiveNbtMergeStrategy