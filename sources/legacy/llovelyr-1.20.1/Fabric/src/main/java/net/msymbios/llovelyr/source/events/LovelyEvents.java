package net.msymbios.llovelyr.source.events;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.msymbios.llovelyr.source.commands.LovelyCommand;

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
 * <p>
 * <i>Note:</i> NBT transfer during crafting is now handled by custom recipe
 * system instead of event handlers.
 */
public class LovelyEvents {

    // -- Methods --

    /**
     * Registers all custom event handlers with Fabric event system.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization (onInitialize) to
     * ensure handlers are registered before events fire.
     * <p>
     * <b>Command Registration:</b> Uses CommandRegistrationCallback to register
     * robot management commands with Brigadier command system.
     */
    public static void register() {
        // Register robot management commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LovelyCommand.register(dispatcher);
        });
    } // register()

} // Class: LovelyEvents