package net.msymbios.rlovelyr.client.gui.control.event.events.input;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.event.HandleableEvent;

/**
 * A char typed event.
 */
@Environment(EnvType.CLIENT)
public class CharTypedEvent extends HandleableEvent {
    /**
     * The char typed.
     */
    public final char character;

    /**
     * The key code.
     */
    public final int keyCode;

    /**
     * @param charTyped the char typed.
     * @param keyCode   the key code.
     */
    public CharTypedEvent(char charTyped, int keyCode) {
        this.character = charTyped;
        this.keyCode = keyCode;
    }
}
