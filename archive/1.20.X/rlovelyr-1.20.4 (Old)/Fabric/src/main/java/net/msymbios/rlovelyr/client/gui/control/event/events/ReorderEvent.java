package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event used items are reordered within a panel.
 */
public class ReorderEvent implements IEvent {
    /**
     * The dragged control.
     */
    public final BaseControl draggedControl;

    /**
     * The closest control.
     */
    public final BaseControl closestControl;

    /**
     * Whether to insert the dragged control before the closest control.
     */
    public final boolean insertBefore;

    /**
     * @param draggedControl the dragged control.
     * @param closestControl the closest control.
     * @param insertBefore   whether to insert the dragged control before or after the closest control.
     */
    public ReorderEvent(BaseControl draggedControl, BaseControl closestControl, boolean insertBefore) {
        this.draggedControl = draggedControl;
        this.closestControl = closestControl;
        this.insertBefore = insertBefore;
    }

} // Class ReorderEvent