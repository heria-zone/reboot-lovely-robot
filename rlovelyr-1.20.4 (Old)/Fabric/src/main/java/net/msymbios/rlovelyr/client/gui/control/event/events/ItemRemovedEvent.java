package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when an item is remove from a control.
 */
@Environment(EnvType.CLIENT)
public class ItemRemovedEvent implements IEvent {
    /**
     * The item that was removed.
     */
    public final Item item;

    /**
     * @param item the item that was removed.
     */
    public ItemRemovedEvent(Item item) {
        this.item = item;
    }
}
