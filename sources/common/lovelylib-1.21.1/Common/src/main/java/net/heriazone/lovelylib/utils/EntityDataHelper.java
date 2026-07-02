package net.heriazone.lovelylib.utils;

import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.hzlib.utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

/**
 * Provides utility methods for entity data processing and validation.
 * <p>
 * <b>Architecture:</b> Centralizes data handling logic that was duplicated across
 * entity implementations. Handles NBT serialization, data validation, and
 * component data processing.
 * <p>
 * <b>Design Decision:</b> Static utility methods provide consistent data handling
 * across all entity types while avoiding inheritance complexity.
 * <p>
 * <b>STAT_COLOR dual format:</b> STAT_COLOR is stored as a string key in the new
 * system (ADR_012) but many item/tooltip systems need the int ID. Use
 * {@link #getTextureId(CompoundTag)} to read either format safely.
 */
public class EntityDataHelper {

    // -- Color Resolution --

    /**
     * Reads the texture ID from NBT, supporting all three storage formats:
     * <ol>
     *   <li>{@code STAT_COLOR_VARIANT} string (new system, ADR_012) — highest priority</li>
     *   <li>{@code STAT_COLOR} string (transitional) — second priority</li>
     *   <li>{@code STAT_COLOR} int (legacy / item model) — fallback</li>
     * </ol>
     * <p>
     * <b>Entity-specific keys:</b> New system stores keys like {@code "bunny_white"}.
     * This method strips the entity prefix and resolves the bare color name to an int ID.
     *
     * @param nbt the NBT compound to read from
     * @return int texture ID (0–15) for use with {@link EntityTexture#byId(int)}
     */
    public static int getTextureId(CompoundTag nbt) {
        // Priority 1: STAT_COLOR_VARIANT string (new system — ADR_012, entity-specific key)
        String colorVariant = nbt.getString(LovelyConstant.STAT_COLOR_VARIANT);
        if (!colorVariant.isEmpty()) {
            EntityTexture texture = resolveTextureFromKey(colorVariant);
            if (texture != null && texture != EntityTexture.RANDOM) return texture.getId();
        }
        // Priority 2: STAT_COLOR string (transitional saves)
        String colorKey = nbt.getString(LovelyConstant.STAT_COLOR);
        if (!colorKey.isEmpty()) {
            EntityTexture texture = resolveTextureFromKey(colorKey);
            if (texture != null && texture != EntityTexture.RANDOM) return texture.getId();
        }
        // Priority 3: STAT_COLOR int (legacy / item model predicate)
        return nbt.getInt(LovelyConstant.STAT_COLOR);
    } // getTextureId ()

    /**
     * Resolves an {@link EntityTexture} from a variant key that may be entity-specific
     * (e.g., {@code "bunny_white"}) or a bare color name (e.g., {@code "white"}).
     * <p>
     * Tries the full key first, then strips everything up to and including the last
     * underscore to get the bare color name.
     *
     * @param key variant key to resolve
     * @return matching {@link EntityTexture}, or {@code null} if not found
     */
    /**
     * Resolves an {@link EntityTexture} from a variant key that may be entity-specific
     * (e.g., {@code "bunny_white"}) or a bare color name (e.g., {@code "white"}).
     * <p>
     * Tries the full key first, then strips everything up to and including each
     * underscore to find the bare color name.
     *
     * @param key variant key to resolve
     * @return matching {@link EntityTexture}, or {@code null} if not found
     */
    public static EntityTexture resolveTextureFromKey(String key) {
        EntityTexture texture = EntityTexture.byName(key);
        if (texture != null) return texture;
        // Strip entity prefix: "bunny_white" → "white", "bunny_light_blue" → "light_blue"
        int lastUnderscore = key.indexOf('_');
        while (lastUnderscore >= 0) {
            String suffix = key.substring(lastUnderscore + 1);
            texture = EntityTexture.byName(suffix);
            if (texture != null) return texture;
            lastUnderscore = key.indexOf('_', lastUnderscore + 1);
        }
        return null;
    } // resolveTextureFromKey ()

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
    public static CompoundTag extractEntityData(RobotEntity entity) {
        CompoundTag nbt = new CompoundTag();

        // Basic entity information
        String customName = Utils.getEntityCustomName(entity);
        if (!customName.isEmpty()) {
            nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, customName);
        }

        String ownerName = Utils.getEntityOwnerName(entity);
        if (!ownerName.isEmpty()) {
            nbt.putString(LovelyConstant.STAT_OWNER, ownerName);
        }

        // Entity type and appearance
        nbt.putString(LovelyConstant.STAT_TYPE, entity.nativeEntity.getKey());
        // STAT_COLOR as int → item model predicate; STAT_COLOR_VARIANT as string → entity restoration
        EntityTexture tex = resolveTextureFromKey(entity.getTextureVariant());
        nbt.putInt(LovelyConstant.STAT_COLOR, (tex != null && tex != EntityTexture.RANDOM) ? tex.getId() : EntityTexture.WHITE.getId());
        nbt.putString(LovelyConstant.STAT_COLOR_VARIANT, entity.getTextureVariant());

        // Level and experience
        nbt.putInt(LovelyConstant.STAT_MAX_LEVEL, entity.getMaxLevel());
        nbt.putInt(LovelyConstant.STAT_LEVEL, entity.getCurrentLevel());
        nbt.putInt(LovelyConstant.STAT_EXP, entity.getExp());

        // Health information
        nbt.putFloat(LovelyConstant.STAT_HP, entity.getHealth());

        // Protection enchantments
        nbt.putInt(LovelyConstant.STAT_FIRE_PROTECTION, entity.getFireProtection());
        nbt.putInt(LovelyConstant.STAT_FALL_PROTECTION, entity.getFallProtection());
        nbt.putInt(LovelyConstant.STAT_BLAST_PROTECTION, entity.getBlastProtection());
        nbt.putInt(LovelyConstant.STAT_PROJECTILE_PROTECTION, entity.getProjectileProtection());

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

        // Validate texture — skip if already stored as a valid string key (new system, ADR_012)
        String colorKey = validatedNbt.getString(LovelyConstant.STAT_COLOR);
        if (colorKey.isEmpty()) {
            // Legacy int path — validate the int value
            int textureId = validatedNbt.getInt(LovelyConstant.STAT_COLOR);
            // Allow any registered EntityTexture ID, including extended palette (IDs 17–25).
            // Only fix genuinely invalid values (negative or beyond the full enum range).
            if (textureId < 0 || textureId >= EntityTexture.values().length) {
                validatedNbt.putInt(LovelyConstant.STAT_COLOR, EntityTexture.WHITE.getId());
            }
        }

        // Validate level (non-negative)
        int level = validatedNbt.getInt(LovelyConstant.STAT_LEVEL);
        if (level < 0) {
            validatedNbt.putInt(LovelyConstant.STAT_LEVEL, 0);
        }

        // Validate experience (non-negative)
        int exp = validatedNbt.getInt(LovelyConstant.STAT_EXP);
        if (exp < 0) {
            validatedNbt.putInt(LovelyConstant.STAT_EXP, 0);
        }

        // Validate health (positive) - only if health was explicitly set
        if (validatedNbt.contains(LovelyConstant.STAT_HP)) {
            float health = validatedNbt.getFloat(LovelyConstant.STAT_HP);
            if (health <= 0) {
                validatedNbt.putFloat(LovelyConstant.STAT_HP, 1.0F);
            }
        }
        // Don't add default health if it wasn't there originally

        // Validate protection values (non-negative)
        validateProtectionValue(validatedNbt, LovelyConstant.STAT_FIRE_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyConstant.STAT_FALL_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyConstant.STAT_BLAST_PROTECTION);
        validateProtectionValue(validatedNbt, LovelyConstant.STAT_PROJECTILE_PROTECTION);

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
        return nbt1.getString(LovelyConstant.STAT_CUSTOM_NAME).equals(nbt2.getString(LovelyConstant.STAT_CUSTOM_NAME)) &&
                nbt1.getString(LovelyConstant.STAT_TYPE).equals(nbt2.getString(LovelyConstant.STAT_TYPE)) &&
                getTextureId(nbt1) == getTextureId(nbt2) &&
                nbt1.getInt(LovelyConstant.STAT_LEVEL) == nbt2.getInt(LovelyConstant.STAT_LEVEL) &&
                nbt1.getInt(LovelyConstant.STAT_EXP) == nbt2.getInt(LovelyConstant.STAT_EXP) &&
                Math.abs(nbt1.getFloat(LovelyConstant.STAT_HP) - nbt2.getFloat(LovelyConstant.STAT_HP)) < 0.01F &&
                nbt1.getInt(LovelyConstant.STAT_FIRE_PROTECTION) == nbt2.getInt(LovelyConstant.STAT_FIRE_PROTECTION) &&
                nbt1.getInt(LovelyConstant.STAT_FALL_PROTECTION) == nbt2.getInt(LovelyConstant.STAT_FALL_PROTECTION) &&
                nbt1.getInt(LovelyConstant.STAT_BLAST_PROTECTION) == nbt2.getInt(LovelyConstant.STAT_BLAST_PROTECTION) &&
                nbt1.getInt(LovelyConstant.STAT_PROJECTILE_PROTECTION) == nbt2.getInt(LovelyConstant.STAT_PROJECTILE_PROTECTION);
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
    public static String getDisplayName(RobotEntity entity) {
        String customName = Utils.getEntityCustomName(entity);
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
    public static Component getDisplayNameComponent(RobotEntity entity) {
        return Component.literal(getDisplayName(entity));
    } // getDisplayNameComponent()

} // Class: EntityDataHelper