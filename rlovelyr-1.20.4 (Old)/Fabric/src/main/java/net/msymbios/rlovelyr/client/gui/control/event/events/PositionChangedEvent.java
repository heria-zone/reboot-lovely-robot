package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event that is fired when a control's position is changed.
 */
@Environment(EnvType.CLIENT)
public class PositionChangedEvent implements IEvent {
    /**
     * The previous x position of the control.
     */
    public final double oldX;

    /**
     * The previous y position of the control.
     */
    public final double oldY;

    /**
     * @param oldX the previous x position of the control.
     * @param oldY the previous y position of the control.
     */
    public PositionChangedEvent(double oldX, double oldY) {
        this.oldX = oldX;
        this.oldY = oldY;
    }
}
