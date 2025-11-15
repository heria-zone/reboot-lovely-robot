package net.msymbios.rlovelyr.client.gui.control.event.events.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * A mouse scrolled event.
 */
@Environment(EnvType.CLIENT)
public class MouseScrolledEvent extends HandleableEvent {
    /**
     * The pixel x position of the mouse.
     */
    public final double mouseX;

    /**
     * The pixel y position of the mouse.
     */
    public final double mouseY;

    /**
     * The amount scrolled horizontally.
     */
    public double horizontalAmount;

    /**
     * The amount scrolled vertically.
     */
    public double verticalAmount;

    /**
     * @param mouseX the pixel x position of the mouse.
     * @param mouseY the pixel y position of the mouse.
     * @param verticalAmount the amount scrolled.
     */
    public MouseScrolledEvent(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.horizontalAmount = horizontalAmount;
        this.verticalAmount = verticalAmount;
    }
}
