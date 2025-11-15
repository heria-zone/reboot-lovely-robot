package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when a control loses or gains focus.
 */
@Environment(EnvType.CLIENT)
public class FocusChangedEvent implements IEvent {
    /**
     * The old focus state of the control.
     */
    public final boolean oldFocus;

    /**
     * The new focus state of the control.
     */
    public final boolean newFocus;

    /**
     * @param oldFocus the old focus state of the control.
     * @param newFocus the new focus state of the control.
     */
    public FocusChangedEvent(boolean oldFocus, boolean newFocus) {
        this.oldFocus = oldFocus;
        this.newFocus = newFocus;
    }
}
