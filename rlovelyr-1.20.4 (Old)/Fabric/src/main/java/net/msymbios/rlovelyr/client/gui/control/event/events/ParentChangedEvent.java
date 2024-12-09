package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event fired when a control's parent changes.
 */
@Environment(EnvType.CLIENT)
public class ParentChangedEvent implements IEvent {
    /**
     * The old parent.
     */
    public final BaseControl oldParent;

    /**
     * The new parent.
     */
    public final BaseControl newParent;

    /**
     * @param oldParent the old parent.
     * @param newParent the new parent.
     */
    public ParentChangedEvent(BaseControl oldParent, BaseControl newParent) {
        this.oldParent = oldParent;
        this.newParent = newParent;
    }
}
