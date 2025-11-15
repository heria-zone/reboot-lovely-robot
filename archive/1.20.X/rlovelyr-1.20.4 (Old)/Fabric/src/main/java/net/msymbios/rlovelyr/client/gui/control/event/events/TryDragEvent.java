package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * An event used when trying to drag a control.
 */
@Environment(EnvType.CLIENT)
public class TryDragEvent extends HandleableEvent {
    /**
     * The pixel x position of the mouse.
     */
    public final double mouseX;

    /**
     * The pixel y position of the mouse.
     */
    public final double mouseY;

    /**
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     */
    public TryDragEvent(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
