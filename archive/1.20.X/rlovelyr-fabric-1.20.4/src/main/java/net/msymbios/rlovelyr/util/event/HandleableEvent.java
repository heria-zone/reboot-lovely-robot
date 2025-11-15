package net.msymbios.rlovelyr.util.event;

import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * A class that represents an event.
 */
public class HandleableEvent implements IEvent {

    // -- Variables --

    /**
     * Whether the event is handled.
     */
    private boolean isHandled = false;

    // -- Methods --

    /**
     * @return whether the event is handled.
     */
    public boolean isHandled()
    {
        return isHandled;
    }

    /**
     * Sets whether the event is handled.
     *
     * @param isHandled whether the event is handled.
     */
    public void setIsHandled(boolean isHandled)
    {
        this.isHandled = isHandled;
    }

} // Class HandleableEvent