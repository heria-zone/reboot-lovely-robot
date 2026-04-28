package net.heriazone.lovelylib.api.items.utils;

import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.utils.EntityDataHelper;
import net.heriazone.lovelylib.utils.EntitySpawnHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
 * <p>
 * <b>Delegation Pattern:</b> Delegates to specialized helper classes for NBT processing
 * and validation while maintaining backward compatibility with existing code.
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
        if (!EntitySpawnHelper.checkSpawnLimit(level, player, SharedConfigs.Common.OwnerMaxRobotNum)) {
            // Display error message to player
            int currentCount = EntitySpawnHelper.getCurrentRobotCount(level, player);
            player.displayClientMessage(
                    Component.literal("Cannot spawn robot: limit of " + SharedConfigs.Common.OwnerMaxRobotNum + " reached (" +
                            currentCount + " active)"),
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
     * <p>
     * <b>Delegation:</b> Delegates to ItemValidationHelper for consistent data access.
     *
     * @param itemStack item stack to extract data from
     * @return CompoundTag with custom data, or null if not present
     */
    public static CompoundTag extractCustomData(ItemStack itemStack) {
        return ItemValidationHelper.extractCustomData(itemStack);
    } // extractCustomData()

    /**
     * Initializes default data component if not present on ItemStack.
     * <p>
     * <b>Default Values:</b> Sets random color and level 0 as baseline robot state.
     * Used when spawn egg lacks custom data from crafting or other sources.
     * <p>
     * <b>Delegation:</b> Delegates to ItemValidationHelper for consistent initialization.
     *
     * @param itemStack item stack to initialize
     */
    public static void initializeDefaultData(ItemStack itemStack) {
        ItemValidationHelper.initializeDefaultItemData(itemStack);
    } // initializeDefaultData()

    // -- Enhanced Item Operations --

    /**
     * Validates and sanitizes item data, ensuring spawn item integrity.
     * <p>
     * <b>Data Integrity:</b> Ensures item contains valid data for entity spawning,
     * correcting invalid values and adding missing fields as needed.
     *
     * @param itemStack the ItemStack to validate
     * @return true if item is valid or was successfully corrected
     */
    public static boolean validateSpawnItem(ItemStack itemStack) {
        return ItemValidationHelper.validateAndSanitizeItemData(itemStack);
    } // validateSpawnItem()

    /**
     * Checks if ItemStack represents a valid spawn item.
     * <p>
     * <b>Completeness Check:</b> Verifies item has required data for spawning.
     *
     * @param itemStack the ItemStack to check
     * @return true if item is valid for spawning
     */
    public static boolean isValidSpawnItem(ItemStack itemStack) {
        return ItemValidationHelper.isValidSpawnItem(itemStack);
    } // isValidSpawnItem()

    // -- Entity Initialization --

    /**
     * Transfers NBT data from spawn item to spawned robot entity.
     * <p>
     * <b>Delegation:</b> Uses EntitySpawnHelper for comprehensive entity initialization
     * including data validation and registry management.
     *
     * @param dataNBT the NBT compound from spawn item
     * @param entity the spawned robot entity to initialize
     */
    public static void initializeEntityFromData(CompoundTag dataNBT, RobotEntity entity) {
        if (dataNBT == null || entity == null) {
            return;
        }

        // Validate data before applying
        CompoundTag validatedData = EntityDataHelper.validateEntityData(dataNBT);

        // Apply custom name if present
        if (!validatedData.getString(LovelyConstant.STAT_CUSTOM_NAME).isEmpty()) {
            entity.setCustomName(Component.literal(validatedData.getString(LovelyConstant.STAT_CUSTOM_NAME)));
        }

        // Apply texture — read STAT_COLOR_VARIANT (string, new system) first,
        // then fall back to STAT_COLOR (int, legacy/item-model system)
        String colorVariant = validatedData.getString(LovelyConstant.STAT_COLOR_VARIANT);
        if (!colorVariant.isEmpty()) {
            entity.setTextureVariant(colorVariant);
        } else {
            // Legacy int-based fallback — build entity-specific key
            int textureId = validatedData.getInt(LovelyConstant.STAT_COLOR);
            if (textureId != EntityTexture.RANDOM.getId()) {
                String colorName = EntityTexture.byId(textureId).Name();
                String entityKey = entity.nativeEntity != null ? entity.nativeEntity.getKey() : "";
                entity.setTextureVariant(entityKey.isEmpty() ? colorName : entityKey + "_" + colorName);
            }
        }
        // If neither is set, keep the entity's already-selected random texture

        // Apply level and experience if greater than 0
        if (validatedData.getInt(LovelyConstant.STAT_LEVEL) > 0) {
            entity.setCurrentLevel(validatedData.getInt(LovelyConstant.STAT_LEVEL));
        }
        if (validatedData.getInt(LovelyConstant.STAT_EXP) > 0) {
            entity.setExp(validatedData.getInt(LovelyConstant.STAT_EXP));
        }

        // Apply health if specified AND it's not the default 1.0F
        if (validatedData.contains(LovelyConstant.STAT_HP)) {
            float healthValue = validatedData.getFloat(LovelyConstant.STAT_HP);
            // Only apply health if it's not the default 1.0F value
            // This allows entities to keep their natural max health for "default" spawn items
            if (Math.abs(healthValue - 1.0F) > 0.01F) {
                entity.setCurrentHealthValue(healthValue);
            }
            // If health is 1.0F, keep the entity's natural max health
        }
        // If no health in NBT, keep the entity's natural max health

        // Apply protection enchantments if greater than 0
        if (validatedData.getInt(LovelyConstant.STAT_FIRE_PROTECTION) > 0) {
            entity.setFireProtection(validatedData.getInt(LovelyConstant.STAT_FIRE_PROTECTION));
        }
        if (validatedData.getInt(LovelyConstant.STAT_FALL_PROTECTION) > 0) {
            entity.setFallProtection(validatedData.getInt(LovelyConstant.STAT_FALL_PROTECTION));
        }
        if (validatedData.getInt(LovelyConstant.STAT_BLAST_PROTECTION) > 0) {
            entity.setBlastProtection(validatedData.getInt(LovelyConstant.STAT_BLAST_PROTECTION));
        }
        if (validatedData.getInt(LovelyConstant.STAT_PROJECTILE_PROTECTION) > 0) {
            entity.setProjectileProtection(validatedData.getInt(LovelyConstant.STAT_PROJECTILE_PROTECTION));
        }

        // Validate final entity state
        EntitySpawnHelper.validateEntityData(entity);
    } // initializeEntityFromData()

} // Class: ItemSpawnHelper