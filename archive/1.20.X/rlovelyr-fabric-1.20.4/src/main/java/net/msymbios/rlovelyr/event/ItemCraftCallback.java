package net.msymbios.rlovelyr.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

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
    ActionResult onCraft(PlayerEntity player, ItemStack crafted, RecipeInputInventory matrix);

} // Interface ItemCraftCallback