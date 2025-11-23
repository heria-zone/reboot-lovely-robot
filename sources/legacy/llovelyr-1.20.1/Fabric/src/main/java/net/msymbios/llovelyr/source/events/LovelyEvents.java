package net.msymbios.llovelyr.source.events;

import net.msymbios.llovelyr.source.events.interfaces.IItemCraftCallback;

/**
 * Centralizes Fabric event handler registration.
 * <p>
 * <b>Architecture:</b> Provides single registration point for all custom event
 * listeners, maintaining clean separation between event definition (interfaces)
 * and event handling (implementations).
 * <p>
 * <b>Design Pattern:</b> Uses Fabric's event system with explicit registration
 * rather than annotation-based auto-registration, providing fine-grained control
 * over listener lifecycle.
 */
public class LovelyEvents {

    // -- Methods --

    /**
     * Registers all custom event handlers with Fabric event system.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization (onInitialize) to
     * ensure handlers are registered before events fire.
     * <p>
     * <b>Registered Events:</b>
     * - ItemCraftCallback: Handles spawn egg NBT transfer and color customization
     * <p>
     * <i>Note:</i> Additional event registrations should be added here to maintain
     * centralized event management.
     */
    public static void register() {
        IItemCraftCallback.EVENT.register(new ItemCraftHandler());
    } // register()

} // Class: LovelyEvents