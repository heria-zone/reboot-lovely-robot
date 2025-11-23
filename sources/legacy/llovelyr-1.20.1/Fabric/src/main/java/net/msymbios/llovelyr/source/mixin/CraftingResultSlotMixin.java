package net.msymbios.llovelyr.source.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.CraftingResultSlot;
import net.msymbios.llovelyr.source.events.interfaces.IItemCraftCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin injecting custom crafting event into result slot interaction.
 * <p>
 * <b>Architecture:</b> Intercepts crafting result retrieval to fire custom
 * event with access to both crafted item and ingredient matrix. Enables
 * NBT transfer and color customization during crafting.
 * <p>
 * <b>Injection Point:</b> Targets moment after vanilla crafting logic but
 * before item consumption, allowing modification of crafted item NBT based
 * on ingredients.
 * <p>
 * <b>Server-Side Only:</b> Event fires only on logical server to prevent
 * desync and duplicate processing.
 */
@Mixin(CraftingResultSlot.class)
public class CraftingResultSlotMixin {

    // -- Methods --

    /**
     * Injects custom crafting event after vanilla crafting completes.
     * <p>
     * <b>Injection Strategy:</b> Targets INVOKE of onCrafted to ensure vanilla
     * logic completes before custom processing. Uses accessor pattern to retrieve
     * crafting matrix without reflection.
     * <p>
     * <b>Event Flow:</b> Vanilla crafting → onCrafted → [INJECTION] → custom event
     * → ingredient consumption.
     *
     * @param player the player taking crafted item
     * @param stack the crafted item stack
     * @param ci callback info for mixin injection
     */
    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/slot/CraftingResultSlot;onCrafted(Lnet/minecraft/item/ItemStack;)V"))
    private void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (!player.getWorld().isClient) {
            ScreenHandler handler = player.currentScreenHandler;
            if (handler instanceof CraftingScreenHandler craftingHandler) {
                ICraftingScreenHandlerAccessor accessor = (ICraftingScreenHandlerAccessor) craftingHandler;
                RecipeInputInventory input = accessor.getInput();
                IItemCraftCallback.EVENT.invoker().onCraft(player, stack, input);
            }
        }
    } // onTakeItem()

} // Class: CraftingResultSlotMixin