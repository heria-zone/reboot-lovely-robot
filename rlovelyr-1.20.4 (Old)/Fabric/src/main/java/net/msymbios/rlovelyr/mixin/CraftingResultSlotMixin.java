package net.msymbios.rlovelyr.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.msymbios.rlovelyr.event.ItemCraftCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    // -- Methods --

    /**
     * This method is called when a player takes an item from the crafting result slot.
     * It passes the item to the ItemCraftCallback event.
     *
     * @param player the player who took the item
     * @param stack the item that was taken
     * @param ci the callback info
     */
    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/CraftingResultSlot;onCrafted(Lnet/minecraft/item/ItemStack;)V"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if(!player.getWorld().isClient) {
            ScreenHandler handler = player.currentScreenHandler;
            if(handler instanceof CraftingScreenHandler craftingHandler) {
                CraftingScreenHandlerAccessor accessor = (CraftingScreenHandlerAccessor) craftingHandler;
                RecipeInputInventory input = accessor.getInput();
                ItemCraftCallback.EVENT.invoker().onCraft(player, stack, input);
            }
        }
    } // onTakeItem ()

} // Class CraftingResultSlotMixin