package net.msymbios.rlovelyr.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

public interface ItemCraftCallback {

    // -- Variable --
    Event<ItemCraftCallback> EVENT = EventFactory.createArrayBacked(ItemCraftCallback.class,
            (listeners) -> (player, crafted, matrix) -> {
                for (ItemCraftCallback listener : listeners) {
                    ActionResult result = listener.onCraft(player, crafted, matrix);
                    if(result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    // -- Method --
    ActionResult onCraft(PlayerEntity player, ItemStack crafted, CraftingInventory matrix);

} // Interface ItemCraftCallback