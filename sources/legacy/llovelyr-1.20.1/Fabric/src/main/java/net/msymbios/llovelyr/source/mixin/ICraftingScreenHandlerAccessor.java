package net.msymbios.llovelyr.source.mixin;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.screen.CraftingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Mixin accessor exposing private crafting input inventory.
 * <p>
 * <b>Architecture:</b> Provides read access to CraftingScreenHandler's private
 * input field, enabling inspection of crafting ingredients during result slot
 * interaction without reflection overhead.
 * <p>
 * <b>Usage Pattern:</b> Cast CraftingScreenHandler to this accessor interface
 * to retrieve crafting matrix for custom crafting logic (e.g., NBT transfer,
 * color customization).
 * <p>
 * <i>Note:</i> Mixin accessors are compile-time safe alternatives to reflection.
 */
@Mixin(CraftingScreenHandler.class)
public interface ICraftingScreenHandlerAccessor {

    // -- Methods --

    /**
     * Accesses private input field containing crafting matrix.
     * <p>
     * <b>Design Decision:</b> Accessor mixin preferred over @Shadow to maintain
     * interface contract and avoid field shadowing complexity.
     *
     * @return the crafting input inventory (3x3 grid)
     */
    @Accessor("input")
    RecipeInputInventory getInput();

} // Interface: ICraftingScreenHandlerAccessor