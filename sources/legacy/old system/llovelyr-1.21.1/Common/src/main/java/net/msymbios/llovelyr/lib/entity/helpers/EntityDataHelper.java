package net.msymbios.llovelyr.lib.entity.helpers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.common.utils.Utility;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;

/**
 * Provides utility methods for entity data processing and validation.
 * <p>
 * <b>Architecture:</b> Centralizes data handling logic that was duplicated across
 * entity implementations. Handles NBT serialization, data validation, and
 * component data processing.
 * <p>
 * <b>Design Decision:</b> Static utility methods provide consistent data handling
 * across all entity types while avoiding inheritance complexity.
 */
public class EntityDataHelper {

    // -- Data Extraction Methods --

    /**
     * Extracts entity data to NBT compound for serialization.
     * <p>
     * <b>Data Mapping:</b> Converts entity state to NBT format for storage in
     * items, saves, or network transmission. Includes all customizable properties.
     * <p>
     * <b>Completeness:</b> Captures custom name, owner, type, texture, level,
     * experience, health, and protection enchantments for full state preservation.
     *
     * @param entity the robot entity to extract data from
     * @return CompoundTag containing entity data
     */
    public static CompoundTag extractEntityData(LovelyRobotEntity entity) {
        CompoundTag nbt = new CompoundTag();

        // Basic entity information
        String customName = Utility.getEntityCustomName(entity);
        if (!customName.isEmpty()) {
            nbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName);
        }

        String ownerName = Utility.getEntityOwnerName(entity);
        if (!ownerName.isEmpty()) {
            nbt.putString(LovelyIdentifier.STAT_OWNER, ownerName);
        }

        // Entity type and appearance
        nbt.putString(LovelyIdentifier.STAT_TYPE, entity.nativeEntity.getKey());
        nbt.putInt(LovelyIdentifier.STAT_COLOR, entity.getTextureID());

        // Level and experience
        nbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, entity.getMaxLevel());
        nbt.putInt(LovelyIdentifier.STAT_LEVEL, entity.getCurrentLevel());
        nbt.putInt(LovelyIdentifier.STAT_EXP, entity.getExp());

        // Health information
        nbt.putFloat(LovelyIdentifier.STAT_HP, entity.getHealth());

        // Protection enchantments
        nbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, entity.getFireProtection());
        nbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, entity.getFallProtection());
        nbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, entity.getBlastProtection());
        nbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, entity.getProjectileProtection());

        return nbt;
    } // extractEntityData()

    // -- Data Validation Methods --

    /**
     * Validates NBT data for entity initialization.
     * <p>
     * <b>Safety Checks:</b> Ensures data values are within valid ranges and
     * types before applying to entity. Prevents crashes from corrupted data.
     * <p>
     * <b>Auto-Correction:</b> Clamps invalid values to valid ranges rather
     * than rejecting entire data set.
     *
     * @param nbt the NBT compound to validate
     * @return validated and corrected NBT compound
     */
    public static CompoundTag validateEntityData(CompoundTag nbt) {
        CompoundTag validatedNbt = nbt.copy();

        // Validate texture ID
        int textureId = validatedNbt.getInt(LovelyIdentifier.STAT_COLOR);
        // Allow RANDOM (16) as a valid texture ID, only fix invalid values
        if (textureId < 0 || (textureId > 15 && textureId != EntityTexture.RANDOM.getId())) {
            validatedNbt.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.WHITE.getId());
        }

        // Validate level (non-negative)
        int level = validatedNbt.getInt(LovelyIdentifier.STAT_LEVEL);
        if (level < 0) {
            validatedNbt.putInt(LovelyIdentifier.STAT_LEVEL, 0);
        }

        // Validate experience (non-negative)
        int exp = validatedNbt.getInt(LovelyIdentifier.STAT_EXP);
        if (exp < 0) {
            validatedNbt.putInt(LovelyIdentifier.STAT_EXP, 0);
        }

        // Validate health (positive) - only if health was explicitly set
        if (validatedNbt.contains(LovelyIdentifier.STAT_HP)) {
            float health = validatedNbt.getFloat(LovelyIdentifier.STAT_HP);
            if (health <= 0) {
                validatedNbt.putFloat(LovelyIdentifier.STAT_HP, 1.0F);
            }
        }
        // Don't add default health if it wasn't there originally

        // Validate protection values (non-negative)
        validateProtectionValue(validatedNbt, LovelyIdentifier.STAT_FIRE_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyIdentifier.STAT_FALL_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyIdentifier.STAT_BLAST_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyIdentifier.STAT_PROJECTILE_PROTECTION);

        return validatedNbt;
    } // validateEntityData()

    /**
     * Validates a single protection value in NBT data.
     * <p>
     * <b>Range Checking:</b> Ensures protection values are non-negative.
     * Upper bounds are enforced during application to entity.
     *
     * @param nbt the NBT compound to validate
     * @param key the protection value key to validate
     */
    private static void validateProtectionValue(CompoundTag nbt, String key) {
        int value = nbt.getInt(key);
        if (value < 0) {
            nbt.putInt(key, 0);
        }
    } // validateProtectionValue()

    // -- Data Comparison Methods --

    /**
     * Compares two NBT compounds for entity data equality.
     * <p>
     * <b>Usage:</b> Determines if entity data has changed for optimization
     * purposes or change detection in UI systems.
     * <p>
     * <b>Comparison Scope:</b> Checks all significant entity properties that
     * affect gameplay or display.
     *
     * @param nbt1 first NBT compound to compare
     * @param nbt2 second NBT compound to compare
     * @return true if entity data is equivalent, false otherwise
     */
    public static boolean compareEntityData(CompoundTag nbt1, CompoundTag nbt2) {
        if (nbt1 == null && nbt2 == null) return true;
        if (nbt1 == null || nbt2 == null) return false;

        // Compare significant fields
        return nbt1.getString(LovelyIdentifier.STAT_CUSTOM_NAME).equals(nbt2.getString(LovelyIdentifier.STAT_CUSTOM_NAME)) &&
               nbt1.getString(LovelyIdentifier.STAT_TYPE).equals(nbt2.getString(LovelyIdentifier.STAT_TYPE)) &&
               nbt1.getInt(LovelyIdentifier.STAT_COLOR) == nbt2.getInt(LovelyIdentifier.STAT_COLOR) &&
               nbt1.getInt(LovelyIdentifier.STAT_LEVEL) == nbt2.getInt(LovelyIdentifier.STAT_LEVEL) &&
               nbt1.getInt(LovelyIdentifier.STAT_EXP) == nbt2.getInt(LovelyIdentifier.STAT_EXP) &&
               Math.abs(nbt1.getFloat(LovelyIdentifier.STAT_HP) - nbt2.getFloat(LovelyIdentifier.STAT_HP)) < 0.01F &&
               nbt1.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION) == nbt2.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION) &&
               nbt1.getInt(LovelyIdentifier.STAT_FALL_PROTECTION) == nbt2.getInt(LovelyIdentifier.STAT_FALL_PROTECTION) &&
               nbt1.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION) == nbt2.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION) &&
               nbt1.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION) == nbt2.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION);
    } // compareEntityData()

    // -- Display Helpers --

    /**
     * Generates display name for entity based on custom name and type.
     * <p>
     * <b>Format:</b> "CustomName (Type)" if named, "Type" if unnamed.
     * Provides consistent naming across UI elements.
     *
     * @param entity the robot entity to generate name for
     * @return formatted display name
     */
    public static String getDisplayName(LovelyRobotEntity entity) {
        String customName = Utility.getEntityCustomName(entity);
        String typeName = entity.nativeEntity.getKey();
        
        // Capitalize first letter of type name
        typeName = typeName.substring(0, 1).toUpperCase() + typeName.substring(1);

        if (!customName.isEmpty()) {
            return customName + " (" + typeName + ")";
        }
        return typeName;
    } // getDisplayName()

    /**
     * Creates formatted component for entity display name.
     * <p>
     * <b>Usage:</b> Provides Component-based display name for chat messages,
     * tooltips, and other text rendering systems.
     *
     * @param entity the robot entity to generate component for
     * @return Component with formatted display name
     */
    public static Component getDisplayNameComponent(LovelyRobotEntity entity) {
        return Component.literal(getDisplayName(entity));
    } // getDisplayNameComponent()

} // Class: EntityDataHelper