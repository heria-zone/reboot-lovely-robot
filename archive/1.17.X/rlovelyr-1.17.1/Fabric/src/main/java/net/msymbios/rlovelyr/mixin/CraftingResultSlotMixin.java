package net.msymbios.rlovelyr.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.msymbios.rlovelyr.event.ItemCraftCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/CraftingResultSlot;onCrafted(Lnet/minecraft/item/ItemStack;)V"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if(!player.world.isClient) {
            ScreenHandler handler = player.currentScreenHandler;
            if(handler instanceof CraftingScreenHandler) {
                CraftingScreenHandler craftingHandler = (CraftingScreenHandler) handler;
                CraftingScreenHandlerAccessor accessor = (CraftingScreenHandlerAccessor) craftingHandler;
                CraftingInventory input = accessor.getInput();
                ItemCraftCallback.EVENT.invoker().onCraft(player, stack, input);
            }
        }
    } // onTakeItem ()

} // Class CraftingResultSlotMixin