package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used when a text control's text changes.
 */
@Environment(EnvType.CLIENT)
public class TextChangedEvent implements IEvent {
    
    /**
     * The old text.
     */
    public final String oldText;

    /**
     * The new text.
     */
    public final String newText;

    /**
     * @param oldText the old text.
     * @param newText the new text.
     */
    public TextChangedEvent(String oldText, String newText) {
        this.oldText = oldText;
        this.newText = newText;
    }
}
