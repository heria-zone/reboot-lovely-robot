package net.msymbios.rlovelyr.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.common.MinecraftForge;
import net.msymbios.rlovelyr.event.CraftingTableChangeEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorkbenchContainer.class)
public abstract class WorkbenchContainerMixin {

    // -- Variables --
    @Shadow
    private CraftingInventory craftSlots;
    @Shadow
    private PlayerEntity player;

    // -- Methods --
    @Inject(method = "recipeMatches", at = @At("RETURN"), cancellable = true)
    public void recipeMatches(IRecipe<? super CraftingInventory> inventory, CallbackInfoReturnable<Boolean> ci) {
        boolean matches = inventory.matches(craftSlots, player.level);
        if (matches) MinecraftForge.EVENT_BUS.post(new CraftingTableChangeEvent(player.level, craftSlots));
        ci.setReturnValue(matches);
    } // recipeMatches ()

} // Class WorkbenchContainerMixin
