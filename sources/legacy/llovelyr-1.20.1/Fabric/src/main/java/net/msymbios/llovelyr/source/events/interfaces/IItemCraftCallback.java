package net.msymbios.llovelyr.source.events.interfaces;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;

/**
 * Fabric event callback for custom crafting logic with ingredient access.
 * <p>
 * <b>Architecture:</b> Provides event-driven hook into crafting system, allowing
 * mods to inspect ingredients and modify crafted items without replacing vanilla
 * recipes. Supports multiple listeners with short-circuit behavior.
 * <p>
 * <b>Event Pattern:</b> Array-backed event with priority-based execution. First
 * listener returning non-PASS result terminates event chain.
 * <p>
 * <b>Use Cases:</b> NBT transfer from ingredients, color customization via dyes,
 * conditional crafting modifications based on ingredient properties.
 */
public interface IItemCraftCallback {

    // -- Variable --

    /**
     * Global event instance for crafting callbacks.
     * <p>
     * <b>Listener Registration:</b> Register via EVENT.register(callback) during
     * mod initialization. Listeners execute in registration order until one
     * returns non-PASS result.
     * <p>
     * <b>Thread Safety:</b> Event fires on server thread during crafting result
     * retrieval. No synchronization needed for listener registration.
     */
    Event<IItemCraftCallback> EVENT = EventFactory.createArrayBacked(IItemCraftCallback.class,
            (listeners) -> (player, crafted, matrix) -> {
                for (IItemCraftCallback listener : listeners) {
                    ActionResult result = listener.onCraft(player, crafted, matrix);
                    if (result != ActionResult.PASS) {
                        return result;
                    }
                }
                return ActionResult.PASS;
            }
    );

    // -- Methods --

    /**
     * Invoked when player takes crafted item from result slot.
     * <p>
     * <b>Timing:</b> Called after vanilla crafting logic but before ingredient
     * consumption, allowing modification of crafted item based on ingredients.
     * <p>
     * <b>Return Behavior:</b>
     * - PASS: Continue to next listener or vanilla behavior
     * - SUCCESS/CONSUME: Terminate event chain, prevent further listeners
     * - FAIL: Cancel crafting (use cautiously)
     *
     * @param player the player crafting the item
     * @param crafted the crafted item (mutable - can modify NBT)
     * @param matrix the crafting input inventory (read-only access)
     * @return action result controlling event propagation
     */
    ActionResult onCraft(PlayerEntity player, ItemStack crafted, RecipeInputInventory matrix);

} // Interface: IItemCraftCallback