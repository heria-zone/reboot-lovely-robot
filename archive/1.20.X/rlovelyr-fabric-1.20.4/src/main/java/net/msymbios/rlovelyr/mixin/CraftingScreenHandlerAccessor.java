package net.msymbios.rlovelyr.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CraftingScreenHandler.class)
public interface CraftingScreenHandlerAccessor {

    @Accessor("input")
    RecipeInputInventory getInput();

} // Interface CraftingScreenHandlerAccessor