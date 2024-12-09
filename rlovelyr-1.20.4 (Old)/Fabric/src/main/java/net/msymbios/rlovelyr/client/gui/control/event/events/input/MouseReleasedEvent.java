package net.msymbios.rlovelyr.client.gui.control.event.events.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * A mouse released event.
 */
@Environment(EnvType.CLIENT)
public class MouseReleasedEvent extends HandleableEvent {
    /**
     * The pixel x position of the mouse.
     */
    public final double mouseX;

    /**
     * The pixel y position of the mouse.
     */
    public final double mouseY;

    /**
     * The mouse button.
     */
    public final int button;

    /**
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     * @param button the mouse button.
     */
    public MouseReleasedEvent(double mouseX, double mouseY, int button) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.button = button;
    }
}
