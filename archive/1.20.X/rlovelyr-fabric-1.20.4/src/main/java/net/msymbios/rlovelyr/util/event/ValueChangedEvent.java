package net.msymbios.rlovelyr.util.event;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when a value is changed.
 */
@Environment(EnvType.CLIENT)
public class ValueChangedEvent<T> implements IEvent {

    // -- Variables --

    /**
     * @param oldValue the old value.
     */
    public final T oldValue;

    /**
     * @param newValue the new value.
     */
    public final T newValue;

    // -- Methods --

    /**
     * @param oldValue the old value.
     * @param newValue the new value.
     */
    public ValueChangedEvent(T oldValue, T newValue) {
        this.oldValue = oldValue;
        this.newValue = newValue;
    } // Constructor ValueChangedEvent ()

} // Class ValueChangedEvent