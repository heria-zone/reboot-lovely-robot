package net.msymbios.rlovelyr.client.gui.control.event.events;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.msymbios.rlovelyr.client.gui.control.BaseControl;
import net.msymbios.rlovelyr.client.gui.control.controls.panels.TabbedPanel;
import net.msymbios.rlovelyr.util.interfaces.IEvent;

/**
 * An event fired when a tab is changed.
 */
@Environment(EnvType.CLIENT)
public class TabChangedEvent implements IEvent {
    /**
     * The tab control that was switched to.
     */
    public final TabbedPanel.TabControl tabControl;

    /**
     * The tab container that was switched to.
     */
    public final BaseControl containerControl;

    /**
     * @param tabControl       the tab control that was switched to.
     * @param containerControl the tab container that was switched to.
     */
    public TabChangedEvent(TabbedPanel.TabControl tabControl, BaseControl containerControl) {
        this.tabControl = tabControl;
        this.containerControl = containerControl;
    }
}
