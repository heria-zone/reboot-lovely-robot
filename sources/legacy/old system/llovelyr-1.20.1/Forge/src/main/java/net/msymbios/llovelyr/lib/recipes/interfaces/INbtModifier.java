package net.msymbios.llovelyr.lib.recipes.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.CraftingContainer;

/**
 * Modifier for applying specific NBT changes during crafting.
 * <p>
 * <b>Architecture:</b> Composable modifiers allow flexible NBT manipulation
 * without hardcoding logic in recipes. Multiple modifiers can be chained.
 * <p>
 * <b>Design Pattern:</b> Command pattern for NBT modifications, enabling
 * reusable and testable modification logic.
 */
@FunctionalInterface
public interface INbtModifier {

    // -- Methods --

    /**
     * Applies NBT modifications based on crafting ingredients.
     * <p>
     * <b>Behavior:</b> Searches inventory for modifier items (dyes, name tags,
     * etc.) and applies corresponding NBT changes. Additive by default.
     *
     * @param inventory crafting grid to search for modifier items
     * @param nbt NBT compound to modify
     */
    void apply(CraftingContainer inventory, CompoundTag nbt);

} // Interface: INbtModifier
