package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * An event used when trying to hover over a control.
 */
@Environment(EnvType.CLIENT)
public class TryHoverEvent extends HandleableEvent {
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
    public TryHoverEvent(double mouseX, double mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
