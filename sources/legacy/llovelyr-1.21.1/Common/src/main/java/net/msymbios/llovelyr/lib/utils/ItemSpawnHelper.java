package net.msymbios.llovelyr.lib.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;
import net.msymbios.llovelyr.lib.registry.RobotRegistryManager;

/**
 * Provides utility methods for spawn item operations and entity initialization.
 * <p>
 * <b>Architecture:</b> Centralizes spawn-related logic that was duplicated across
 * loader-specific spawn item implementations. Handles data component processing,
 * spawn limit validation, and entity initialization.
 * <p>
 * <b>Design Decision:</b> Static utility methods avoid object creation overhead
 * while providing consistent behavior across all loaders. Data component handling
 * is abstracted to support Minecraft 1.21.1's migration from NBT to typed components.
 */
public class ItemSpawnHelper {

    // -- Spawn Validation Methods --

    /**
     * Validates if player can spawn a robot based on configured limits.
     * <p>
     * <b>Registry Integration:</b> Checks current robot count against configured
     * maximum, providing consistent spawn limiting across all spawn methods.
     * <p>
     * <b>User Feedback:</b> Displays error message to player when limit exceeded,
     * including current count for transparency.
     *
     * @param level server level for registry access
     * @param player player attempting to spawn robot
     * @return true if spawn is allowed, false if limit exceeded
     */
    public static boolean canSpawnRobot(ServerLevel level, Player player) {
        OwnerRobotRegistry registry = RobotRegistryManager.getRegistry(level);
        
        if (!registry.canSpawnRobot(player.getUUID(), SharedConfigs.Common.OwnerMaxRobotNum)) {
            // Display error message to player
            player.displayClientMessage(
                    Component.literal("Cannot spawn robot: limit of " + SharedConfigs.Common.OwnerMaxRobotNum + " reached (" +
                            registry.getRobotsForOwner(player.getUUID()).size() + " active)"),
                    true
            );
            return false;
        }
        
        return true;
    } // canSpawnRobot()

    // -- Data Component Processing --

    /**
     * Extracts custom data from ItemStack using Data Components API.
     * <p>
     * <b>Migration Support:</b> Handles Minecraft 1.21.1's transition from NBT
     * to typed data components. Returns null if no custom data present.
     *
     * @param itemStack item stack to extract data from
     * @return CompoundTag with custom data, or null if not present
     */
    public static CompoundTag extractCustomData(ItemStack itemStack) {
        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                return customData.copyTag();
            }
        }
        return null;
    } // extractCustomData()

    /**
     * Initializes default data component if not present on ItemStack.
     * <p>
     * <b>Default Values:</b> Sets random color and level 0 as baseline robot state.
     * Used when spawn egg lacks custom data from crafting or other sources.
     *
     * @param itemStack item stack to initialize
     */
    public static void initializeDefaultData(ItemStack itemStack) {
        if (!itemStack.has(DataComponents.CUSTOM_DATA)) {
            CompoundTag compound = new CompoundTag();
            compound.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            compound.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(compound));
        }
    } // initializeDefaultData()

    // -- Entity Initialization --

    /**
     * Transfers NBT data from spawn item to spawned robot entity.
     * <p>
     * <b>Data Transfer:</b> Applies custom name, texture, level, experience,
     * health, and protection enchantments from item data to entity state.
     * <p>
     * <b>Validation:</b> Only applies non-default values to avoid overwriting
     * entity defaults with empty data. Handles missing data gracefully.
     *
     * @param dataNBT the NBT compound from spawn item
     * @param entity the spawned robot entity to initialize
     */
    public static void initializeEntityFromData(CompoundTag dataNBT, LovelyRobotEntity entity) {
        if (dataNBT == null) return;
        
        // Apply custom name if present
        if (!dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME).isEmpty()) {
            entity.setCustomName(Component.literal(dataNBT.getString(LovelyIdentifier.STAT_CUSTOM_NAME)));
        }
        
        // Apply texture if not random
        if (dataNBT.getInt(LovelyIdentifier.STAT_COLOR) != EntityTexture.RANDOM.getId()) {
            entity.setTexture(dataNBT.getInt(LovelyIdentifier.STAT_COLOR));
        }

        // Apply level and experience if greater than 0
        if (dataNBT.getInt(LovelyIdentifier.STAT_LEVEL) > 0) {
            entity.setCurrentLevel(dataNBT.getInt(LovelyIdentifier.STAT_LEVEL));
        }
        if (dataNBT.getInt(LovelyIdentifier.STAT_EXP) > 0) {
            entity.setExp(dataNBT.getInt(LovelyIdentifier.STAT_EXP));
        }
        
        // Apply health if specified
        if (dataNBT.contains(LovelyIdentifier.STAT_HP)) {
            entity.setCurrentHealthValue(dataNBT.getFloat(LovelyIdentifier.STAT_HP));
        }

        // Apply protection enchantments if greater than 0
        if (dataNBT.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION) > 0) {
            entity.setFireProtection(dataNBT.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION));
        }
        if (dataNBT.getInt(LovelyIdentifier.STAT_FALL_PROTECTION) > 0) {
            entity.setFallProtection(dataNBT.getInt(LovelyIdentifier.STAT_FALL_PROTECTION));
        }
        if (dataNBT.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION) > 0) {
            entity.setBlastProtection(dataNBT.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION));
        }
        if (dataNBT.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION) > 0) {
            entity.setProjectileProtection(dataNBT.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION));
        }
    } // initializeEntityFromData()

} // Class: ItemSpawnHelper