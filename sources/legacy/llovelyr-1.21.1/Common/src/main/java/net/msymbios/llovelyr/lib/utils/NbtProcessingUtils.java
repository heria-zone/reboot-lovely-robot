package net.msymbios.llovelyr.lib.utils;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;

/**
 * Provides NBT processing and data validation utilities for robot data.
 * <p>
 * <b>Architecture:</b> Centralizes NBT/Data Component operations that were
 * duplicated across loader implementations. Handles validation, conversion,
 * and safe data access patterns.
 * <p>
 * <b>Migration Support:</b> Abstracts Minecraft 1.21.1's transition from NBT
 * to typed data components, providing consistent API for both systems.
 */
public class NbtProcessingUtils {

    // -- Data Component Operations --

    /**
     * Safely retrieves custom data from ItemStack with null checking.
     * <p>
     * <b>Safety:</b> Handles missing data components gracefully, returning
     * null rather than throwing exceptions for defensive programming.
     *
     * @param itemStack item stack to read from
     * @return CompoundTag with custom data, or null if not present
     */
    public static CompoundTag getCustomData(ItemStack itemStack) {
        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                return customData.copyTag();
            }
        }
        return null;
    } // getCustomData()

    /**
     * Sets custom data on ItemStack using Data Components API.
     * <p>
     * <b>Conversion:</b> Wraps CompoundTag in CustomData for component storage.
     * Handles null input by removing the component entirely.
     *
     * @param itemStack item stack to modify
     * @param nbt NBT data to store, or null to remove
     */
    public static void setCustomData(ItemStack itemStack, CompoundTag nbt) {
        if (nbt != null) {
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        } else {
            itemStack.remove(DataComponents.CUSTOM_DATA);
        }
    } // setCustomData()

    // -- Data Validation --

    /**
     * Validates robot data integrity and applies corrections.
     * <p>
     * <b>Validation Rules:</b>
     * - Level must be non-negative
     * - Experience must be non-negative
     * - Health must be positive if specified
     * - Protection values must be non-negative
     * - Color must be valid EntityTexture ID
     * <p>
     * <b>Correction Strategy:</b> Clamps invalid values to valid ranges
     * rather than rejecting entire data set.
     *
     * @param nbt NBT data to validate and correct
     * @return true if data was valid, false if corrections were applied
     */
    public static boolean validateAndCorrectRobotData(CompoundTag nbt) {
        if (nbt == null) return false;
        
        boolean wasValid = true;
        
        // Validate and correct level
        int level = nbt.getInt(LovelyIdentifier.STAT_LEVEL);
        if (level < 0) {
            nbt.putInt(LovelyIdentifier.STAT_LEVEL, 0);
            wasValid = false;
        }
        
        // Validate and correct experience
        int exp = nbt.getInt(LovelyIdentifier.STAT_EXP);
        if (exp < 0) {
            nbt.putInt(LovelyIdentifier.STAT_EXP, 0);
            wasValid = false;
        }
        
        // Validate and correct health
        if (nbt.contains(LovelyIdentifier.STAT_HP)) {
            float hp = nbt.getFloat(LovelyIdentifier.STAT_HP);
            if (hp <= 0) {
                nbt.remove(LovelyIdentifier.STAT_HP); // Remove invalid health, let entity use default
                wasValid = false;
            }
        }
        
        // Validate and correct protection values
        String[] protectionKeys = {
            LovelyIdentifier.STAT_FIRE_PROTECTION,
            LovelyIdentifier.STAT_FALL_PROTECTION,
            LovelyIdentifier.STAT_BLAST_PROTECTION,
            LovelyIdentifier.STAT_PROJECTILE_PROTECTION
        };
        
        for (String key : protectionKeys) {
            int protection = nbt.getInt(key);
            if (protection < 0) {
                nbt.putInt(key, 0);
                wasValid = false;
            }
        }
        
        // Validate and correct color/texture
        int colorId = nbt.getInt(LovelyIdentifier.STAT_COLOR);
        EntityTexture texture = EntityTexture.byId(colorId);
        if (texture == null) {
            nbt.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
            wasValid = false;
        }
        
        return wasValid;
    } // validateAndCorrectRobotData()

    // -- Data Merging --

    /**
     * Merges two NBT compounds with conflict resolution.
     * <p>
     * <b>Merge Strategy:</b> Source data overwrites target data for matching keys.
     * Non-conflicting keys from both compounds are preserved.
     * <p>
     * <b>Usage:</b> Commonly used for combining robot data from multiple sources
     * (e.g., crafting ingredients with existing spawn egg data).
     *
     * @param target target NBT to merge into (modified in place)
     * @param source source NBT to merge from
     */
    public static void mergeNbtData(CompoundTag target, CompoundTag source) {
        if (target == null || source == null) return;
        
        // Copy all keys from source to target
        for (String key : source.getAllKeys()) {
            target.put(key, source.get(key).copy());
        }
    } // mergeNbtData()

    // -- Data Extraction Helpers --

    /**
     * Safely extracts string value with default fallback.
     * <p>
     * <b>Safety:</b> Handles missing keys and null values gracefully.
     *
     * @param nbt NBT compound to read from
     * @param key key to extract
     * @param defaultValue default value if key missing or empty
     * @return string value or default
     */
    public static String getStringOrDefault(CompoundTag nbt, String key, String defaultValue) {
        if (nbt == null || !nbt.contains(key)) return defaultValue;
        String value = nbt.getString(key);
        return value.isEmpty() ? defaultValue : value;
    } // getStringOrDefault()

    /**
     * Safely extracts integer value with range validation.
     * <p>
     * <b>Validation:</b> Clamps value to specified range if present.
     *
     * @param nbt NBT compound to read from
     * @param key key to extract
     * @param defaultValue default value if key missing
     * @param minValue minimum allowed value
     * @param maxValue maximum allowed value
     * @return integer value clamped to range
     */
    public static int getIntInRange(CompoundTag nbt, String key, int defaultValue, int minValue, int maxValue) {
        if (nbt == null || !nbt.contains(key)) return defaultValue;
        int value = nbt.getInt(key);
        return Math.max(minValue, Math.min(maxValue, value));
    } // getIntInRange()

} // Class: NbtProcessingUtils