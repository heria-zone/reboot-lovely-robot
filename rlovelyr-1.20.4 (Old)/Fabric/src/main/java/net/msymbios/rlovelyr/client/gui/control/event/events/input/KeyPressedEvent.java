package net.msymbios.rlovelyr.client.gui.control.event.events.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * A key pressed event.
 */
@Environment(EnvType.CLIENT)
public class KeyPressedEvent extends HandleableEvent {
    /**
     * The key code.
     */
    public final int keyCode;

    /**
     * The scan code.
     */
    public final int scanCode;

    /**
     * The modifiers.
     */
    public final int modifiers;

    /**
     * @param keyCode   the key code.
     * @param scanCode  the scan code.
     * @param modifiers the modifiers.
     */
    public KeyPressedEvent(int keyCode, int scanCode, int modifiers) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }
}
