package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when an item is moved.
 */
@Environment(EnvType.CLIENT)
public class ItemMovedEvent implements IEvent {
    /**
     * The item that was moved.
     */
    public final Item movedItem;

    /**
     * The closest item that the moved item was move to.
     */
    public final Item closestItem;

    /**
     * Whether to insert the moved item before or after the closest item.
     */
    public final boolean insertBefore;

    /**
     * @param movedItem    the item that was moved.
     * @param closestItem  the closest item that the moved item was move to.
     * @param insertBefore whether to insert the moved item before or after the closest item.
     */
    public ItemMovedEvent(Item movedItem, Item closestItem, boolean insertBefore) {
        this.movedItem = movedItem;
        this.closestItem = closestItem;
        this.insertBefore = insertBefore;
    }
}
