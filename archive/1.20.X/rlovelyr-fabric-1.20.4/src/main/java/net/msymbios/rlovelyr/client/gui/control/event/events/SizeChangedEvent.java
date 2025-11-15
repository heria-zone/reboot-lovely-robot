package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event that is fired when a control's size is changed.
 */
@Environment(EnvType.CLIENT)
public class SizeChangedEvent implements IEvent {
    /**
     * The previous width of the control.
     */
    public final double oldWidth;

    /**
     * The previous height of the control.
     */
    public final double oldHeight;

    /**
     * @param oldWidth  the previous width of the control.
     * @param oldHeight the previous height of the control.
     */
    public SizeChangedEvent(double oldWidth, double oldHeight) {
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
    }
}
