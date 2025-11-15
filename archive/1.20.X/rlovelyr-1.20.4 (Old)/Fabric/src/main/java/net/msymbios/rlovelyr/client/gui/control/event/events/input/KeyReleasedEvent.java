package net.msymbios.rlovelyr.client.gui.control.event.events.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * A key released event.
 */
@Environment(EnvType.CLIENT)
public class KeyReleasedEvent extends HandleableEvent {
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
    public KeyReleasedEvent(int keyCode, int scanCode, int modifiers) {
        this.keyCode = keyCode;
        this.scanCode = scanCode;
        this.modifiers = modifiers;
    }
}
