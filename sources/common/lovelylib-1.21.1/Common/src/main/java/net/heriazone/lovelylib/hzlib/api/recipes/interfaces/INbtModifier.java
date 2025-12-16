package net.heriazone.lovelylib.hzlib.api.recipes.interfaces;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.crafting.CraftingInput;

/**
 * Modifier for applying specific data changes during crafting.
 * <p>
 * <b>Architecture:</b> Composable modifiers allow flexible data manipulation
 * without hardcoding logic in recipes. Multiple modifiers can be chained.
 * <p>
 * <b>Migration Note:</b> While Minecraft 1.21.1 uses Data Components for ItemStack
 * storage, this interface still uses CompoundTag as an intermediate format for
 * recipe processing. The tag is converted to/from components by the strategy.
 * Also uses CraftingInput instead of CraftingContainer.
 * <p>
 * <b>Design Pattern:</b> Command pattern for data modifications, enabling
 * reusable and testable modification logic.
 */
@FunctionalInterface
public interface INbtModifier {

    // -- Methods --

    /**
     * Applies data modifications based on crafting ingredients.
     * <p>
     * <b>Behavior:</b> Searches input for modifier items (dyes, name tags,
     * etc.) and applies corresponding changes to the tag. Additive by default.
     * <p>
     * <b>Implementation Note:</b> Tag is used as intermediate format and will be
     * converted to Data Components by the transfer strategy.
     *
     * @param input crafting input to search for modifier items
     * @param nbt compound tag to modify (intermediate format)
     */
    void apply(CraftingInput input, CompoundTag nbt);

} // Interface: INbtModifier