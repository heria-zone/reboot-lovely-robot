package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.heriazone.lovelylib.common.commands.LovelyCommands;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.minecraft.world.entity.LivingEntity;

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
public class LegacyEvents {

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
            LovelyCommands.register(dispatcher);
        });

        // Register entity death handler for experience claiming
        ServerEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            // Check if entity is dead (not just unloading)
            if (entity instanceof LivingEntity livingEntity && !livingEntity.isAlive()) {
                java.util.UUID deadEntityId = entity.getUUID();

                // Find all robots in the world and let them claim exp from this entity
                world.getEntitiesOfClass(
                        RobotEntity.class,
                        entity.getBoundingBox().inflate(100.0),
                        robot -> true
                ).forEach(robot -> robot.claimAccumulatedExp(deadEntityId));
            }
        });
    } // register()

} // Class: LegacyEvents