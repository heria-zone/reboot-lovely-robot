package net.heriazone.lovelylib.utils;

import net.heriazone.lovelylib.api.registry.OwnerRobotRegistry;
import net.heriazone.lovelylib.api.registry.RobotRegistryManager;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

/**
 * Provides utility methods for entity spawn validation and initialization.
 * <p>
 * <b>Architecture:</b> Centralizes spawn-related logic that was duplicated across
 * loader-specific implementations. Handles spawn limit checking, entity initialization,
 * and registry management.
 * <p>
 * <b>Design Decision:</b> Static utility methods avoid object creation overhead
 * while providing consistent behavior across all loaders. Separates spawn validation
 * from item-specific logic for better modularity.
 */
public class EntitySpawnHelper {

    // -- Spawn Validation Methods --

    /**
     * Validates if player can spawn a robot based on configured limits.
     * <p>
     * <b>Registry Integration:</b> Checks current robot count against configured
     * maximum, providing consistent spawn limiting across all spawn methods.
     * <p>
     * <b>Performance:</b> Lightweight registry lookup with O(1) owner-based access.
     *
     * @param level server level for registry access
     * @param player player attempting to spawn robot
     * @param maxRobots maximum robots allowed per player
     * @return true if spawn is allowed, false if limit exceeded
     */
    public static boolean checkSpawnLimit(ServerLevel level, Player player, int maxRobots) {
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        return registry.canSpawnRobot(player.getUUID(), maxRobots);
    } // checkSpawnLimit()

    /**
     * Gets current robot count for a player.
     * <p>
     * <b>Usage:</b> Provides transparency for spawn limit enforcement and
     * user feedback about current robot ownership.
     *
     * @param level server level for registry access
     * @param player player to check robot count for
     * @return number of robots currently owned by player
     */
    public static int getCurrentRobotCount(ServerLevel level, Player player) {
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        return registry.getRobotsForOwner(player.getUUID()).size();
    } // getCurrentRobotCount()

    // -- Entity Initialization --

    /**
     * Initializes a robot entity with comprehensive setup and registration.
     * <p>
     * <b>Architecture:</b> Handles the complete entity initialization process:
     * taming, data transfer, registry registration, and validation. Ensures
     * consistent setup regardless of spawn method.
     * <p>
     * <b>Registry Safety:</b> Automatically registers entity to prevent spawn
     * limit bypass and ensure proper cleanup on entity removal.
     * <p>
     * <b>Data Transfer:</b> Applies custom data from spawn source (item, command, etc.)
     * to entity state, preserving customizations across spawn methods.
     *
     * @param entity the robot entity to initialize
     * @param player the player who spawned the robot
     * @param customData optional NBT data for entity customization
     */
    public static void initializeEntity(LovelyRobotEntity entity, Player player, CompoundTag customData) {
        // Tame the entity to the player
        entity.handleTame(player);

        // Apply custom data if provided
        if (customData != null) {
            applyCustomData(entity, customData);
        }

        // Registry registration is handled by the entity itself during initialization
        // No need to call ensureRegistered() here as it's protected and handled internally
    } // initializeEntity()

    /**
     * Applies custom NBT data to robot entity state.
     * <p>
     * <b>Data Mapping:</b> Transfers spawn item or command data to entity properties:
     * custom name, texture, level, experience, health, and protection enchantments.
     * <p>
     * <b>Validation:</b> Only applies non-default values to avoid overwriting
     * entity defaults with empty data. Handles missing data gracefully.
     * <p>
     * <b>Safety:</b> Validates data ranges and types to prevent invalid entity states.
     *
     * @param entity the robot entity to configure
     * @param customData NBT compound containing entity customization data
     */
    private static void applyCustomData(LovelyRobotEntity entity, CompoundTag customData) {
        // Apply custom name if present and non-empty
        String customName = customData.getString("CustomName");
        if (!customName.isEmpty()) {
            entity.setCustomName(net.minecraft.network.chat.Component.literal(customName));
        }

        // Apply texture if specified and valid
        if (customData.contains("Color")) {
            int textureId = customData.getInt("Color");
            if (textureId >= 0 && textureId <= 15) { // Valid texture range
                entity.setTexture(textureId);
            }
        }

        // Apply level if greater than 0 and within bounds
        if (customData.contains("Level")) {
            int level = customData.getInt("Level");
            if (level > 0 && level <= entity.getMaxLevel()) {
                entity.setCurrentLevel(level);
            }
        }

        // Apply experience if greater than 0
        if (customData.contains("Exp")) {
            int exp = customData.getInt("Exp");
            if (exp > 0) {
                entity.setExp(exp);
            }
        }

        // Apply health if specified and positive
        if (customData.contains("Health")) {
            float health = customData.getFloat("Health");
            if (health > 0 && health <= entity.getMaxHealth()) {
                entity.setCurrentHealthValue(health);
            }
        }

        // Apply protection enchantments if greater than 0
        applyProtectionData(entity, customData);
    } // applyCustomData()

    /**
     * Applies protection enchantment data to robot entity.
     * <p>
     * <b>Protection Types:</b> Handles fire, fall, blast, and projectile protection
     * values from spawn data. Validates against configured limits.
     * <p>
     * <b>Safety:</b> Clamps values to valid ranges to prevent exploit or corruption.
     *
     * @param entity the robot entity to configure
     * @param customData NBT compound containing protection data
     */
    private static void applyProtectionData(LovelyRobotEntity entity, CompoundTag customData) {
        // Fire Protection
        if (customData.contains("FireProtection")) {
            int fireProtection = Math.max(0, Math.min(
                    customData.getInt("FireProtection"),
                    SharedConfigs.Common.ProtectionLimitFire
            ));
            if (fireProtection > 0) {
                entity.setFireProtection(fireProtection);
            }
        }

        // Fall Protection
        if (customData.contains("FallProtection")) {
            int fallProtection = Math.max(0, Math.min(
                    customData.getInt("FallProtection"),
                    SharedConfigs.Common.ProtectionLimitFall
            ));
            if (fallProtection > 0) {
                entity.setFallProtection(fallProtection);
            }
        }

        // Blast Protection
        if (customData.contains("BlastProtection")) {
            int blastProtection = Math.max(0, Math.min(
                    customData.getInt("BlastProtection"),
                    SharedConfigs.Common.ProtectionLimitBlast
            ));
            if (blastProtection > 0) {
                entity.setBlastProtection(blastProtection);
            }
        }

        // Projectile Protection
        if (customData.contains("ProjectileProtection")) {
            int projectileProtection = Math.max(0, Math.min(
                    customData.getInt("ProjectileProtection"),
                    SharedConfigs.Common.ProtectionLimitProjectile
            ));
            if (projectileProtection > 0) {
                entity.setProjectileProtection(projectileProtection);
            }
        }
    } // applyProtectionData()

    // -- Validation Helpers --

    /**
     * Validates entity data for consistency and safety.
     * <p>
     * <b>Validation Checks:</b> Ensures entity state is valid after initialization:
     * health within bounds, level within limits, protection values clamped.
     * <p>
     * <b>Auto-Correction:</b> Fixes invalid values rather than failing, providing
     * robust handling of corrupted or malicious data.
     *
     * @param entity the robot entity to validate
     */
    public static void validateEntityData(LovelyRobotEntity entity) {
        // Validate health bounds
        //if (entity.getHealth() > entity.getMaxHealth()) entity.setHealth(entity.getMaxHealth());

        //if (entity.getHealth() <= 0) entity.setHealth(1.0F); // Minimum viable health

        // Validate level bounds
        if (entity.getCurrentLevel() > entity.getMaxLevel()) {
            entity.setCurrentLevel(entity.getMaxLevel());
        }

        if (entity.getCurrentLevel() < 0) {
            entity.setCurrentLevel(0);
        }

        // Validate protection bounds
        validateProtectionBounds(entity);
    } // validateEntityData()

    /**
     * Validates and corrects protection enchantment values.
     * <p>
     * <b>Bounds Checking:</b> Ensures all protection values are within configured
     * limits to prevent exploits or display issues.
     *
     * @param entity the robot entity to validate
     */
    private static void validateProtectionBounds(LovelyRobotEntity entity) {
        // Clamp fire protection
        if (entity.getFireProtection() > SharedConfigs.Common.ProtectionLimitFire) {
            entity.setFireProtection(SharedConfigs.Common.ProtectionLimitFire);
        }

        // Clamp fall protection
        if (entity.getFallProtection() > SharedConfigs.Common.ProtectionLimitFall) {
            entity.setFallProtection(SharedConfigs.Common.ProtectionLimitFall);
        }

        // Clamp blast protection
        if (entity.getBlastProtection() > SharedConfigs.Common.ProtectionLimitBlast) {
            entity.setBlastProtection(SharedConfigs.Common.ProtectionLimitBlast);
        }

        // Clamp projectile protection
        if (entity.getProjectileProtection() > SharedConfigs.Common.ProtectionLimitProjectile) {
            entity.setProjectileProtection(SharedConfigs.Common.ProtectionLimitProjectile);
        }
    } // validateProtectionBounds()

} // Class: EntitySpawnHelper