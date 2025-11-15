package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when a selection changes.
 */
@Environment(EnvType.CLIENT)
public class SelectionChangedEvent<T> implements IEvent {

    /**
     * The previously selected item.
     */
    public final T previousItem;

    /**
     * The newly selected item.
     */
    public final T newItem;

    /**
     * @param previousItem the previously selected item.
     * @param newItem      the newly selected item.
     */
    public SelectionChangedEvent(T previousItem, T newItem) {
        this.previousItem = previousItem;
        this.newItem = newItem;
    }
}
