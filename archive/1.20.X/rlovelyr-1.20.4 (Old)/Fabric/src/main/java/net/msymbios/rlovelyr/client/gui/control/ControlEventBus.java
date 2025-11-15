package net.msymbios.rlovelyr.client.gui.control;

import net.msymbios.rlovelyr.util.event.EventBus;

/**
 * An event bus for controls that will forward events to the appropriate subscribers.
 */
public class ControlEventBus extends EventBus<BaseControl> { } // Class ControlEventBus