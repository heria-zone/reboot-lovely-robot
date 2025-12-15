package net.msymbios.llovelyr.lib.items.helpers;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;

/**
 * Provides validation utilities for item data components and NBT processing.
 * <p>
 * <b>Architecture:</b> Centralizes item validation logic that was scattered across
 * loader implementations. Handles data component validation, NBT sanitization,
 * and consistency checks for spawn items.
 * <p>
 * <b>Data Integrity:</b> Ensures item data components contain valid values within
 * acceptable ranges, preventing invalid entity states from corrupted or modified items.
 * <p>
 * <b>Migration Support:</b> Provides compatibility layer for Minecraft 1.21.1's
 * transition from NBT tags to typed data components.
 */
public class ItemValidationHelper {

    // -- Data Component Validation --

    /**
     * Validates and sanitizes custom data component on ItemStack.
     * <p>
     * <b>Range Validation:</b> Ensures numeric values are within acceptable ranges
     * for entity properties. Invalid values are clamped or reset to defaults.
     * <p>
     * <b>Data Consistency:</b> Verifies required fields are present and properly
     * formatted, adding missing fields with default values.
     *
     * @param itemStack the ItemStack to validate and potentially modify
     * @return true if validation passed or corrections were applied, false if item is invalid
     */
    public static boolean validateAndSanitizeItemData(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        // Extract current custom data
        CompoundTag currentData = extractCustomData(itemStack);
        if (currentData == null) {
            // Initialize with defaults if no data present
            initializeDefaultItemData(itemStack);
            return true;
        }

        // Create sanitized copy
        CompoundTag sanitizedData = sanitizeNBTData(currentData);
        
        // Update ItemStack if data was modified
        if (!currentData.equals(sanitizedData)) {
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(sanitizedData));
        }

        return true;
    } // validateAndSanitizeItemData()

    /**
     * Extracts custom data from ItemStack, handling component API safely.
     * <p>
     * <b>Null Safety:</b> Returns null if no custom data component exists,
     * avoiding exceptions during data access.
     *
     * @param itemStack the ItemStack to extract data from
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
     * Initializes ItemStack with default custom data component.
     * <p>
     * <b>Default Values:</b> Sets random texture and level 0 as baseline state
     * for newly created or corrupted spawn items.
     *
     * @param itemStack the ItemStack to initialize
     */
    public static void initializeDefaultItemData(ItemStack itemStack) {
        CompoundTag defaultData = new CompoundTag();
        defaultData.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
        defaultData.putInt(LovelyIdentifier.STAT_LEVEL, 0);
        defaultData.putInt(LovelyIdentifier.STAT_EXP, 0);
        defaultData.putString(LovelyIdentifier.STAT_CUSTOM_NAME, "");
        
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(defaultData));
    } // initializeDefaultItemData()

    // -- NBT Data Sanitization --

    /**
     * Sanitizes NBT data by validating and clamping values to acceptable ranges.
     * <p>
     * <b>Value Clamping:</b> Ensures numeric values are within game-appropriate
     * ranges, preventing overflow or underflow issues in entity properties.
     * <p>
     * <b>Field Validation:</b> Checks for required fields and adds defaults
     * for missing properties to maintain data consistency.
     *
     * @param nbtData the NBT compound to sanitize
     * @return sanitized NBT compound with validated values
     */
    public static CompoundTag sanitizeNBTData(CompoundTag nbtData) {
        CompoundTag sanitized = nbtData.copy();

        // Validate and clamp color/texture ID
        int colorId = sanitized.getInt(LovelyIdentifier.STAT_COLOR);
        if (colorId < 0 || colorId > EntityTexture.values().length - 1) {
            sanitized.putInt(LovelyIdentifier.STAT_COLOR, EntityTexture.RANDOM.getId());
        }

        // Validate and clamp level (0-100 reasonable range)
        int level = sanitized.getInt(LovelyIdentifier.STAT_LEVEL);
        if (level < 0) {
            sanitized.putInt(LovelyIdentifier.STAT_LEVEL, 0);
        } else if (level > 100) {
            sanitized.putInt(LovelyIdentifier.STAT_LEVEL, 100);
        }

        // Validate and clamp experience (0-1000000 reasonable range)
        int exp = sanitized.getInt(LovelyIdentifier.STAT_EXP);
        if (exp < 0) {
            sanitized.putInt(LovelyIdentifier.STAT_EXP, 0);
        } else if (exp > 1000000) {
            sanitized.putInt(LovelyIdentifier.STAT_EXP, 1000000);
        }

        // Validate health (1.0-1000.0 reasonable range)
        if (sanitized.contains(LovelyIdentifier.STAT_HP)) {
            float health = sanitized.getFloat(LovelyIdentifier.STAT_HP);
            if (health <= 0.0f) {
                sanitized.putFloat(LovelyIdentifier.STAT_HP, 1.0f);
            } else if (health > 1000.0f) {
                sanitized.putFloat(LovelyIdentifier.STAT_HP, 1000.0f);
            }
        }

        // Validate protection levels (0-10 reasonable range for each)
        validateAndClampProtection(sanitized, LovelyIdentifier.STAT_FIRE_PROTECTION);
        validateAndClampProtection(sanitized, LovelyIdentifier.STAT_FALL_PROTECTION);
        validateAndClampProtection(sanitized, LovelyIdentifier.STAT_BLAST_PROTECTION);
        validateAndClampProtection(sanitized, LovelyIdentifier.STAT_PROJECTILE_PROTECTION);

        // Validate custom name (limit length to prevent issues)
        String customName = sanitized.getString(LovelyIdentifier.STAT_CUSTOM_NAME);
        if (customName.length() > 50) {
            sanitized.putString(LovelyIdentifier.STAT_CUSTOM_NAME, customName.substring(0, 50));
        }

        return sanitized;
    } // sanitizeNBTData()

    /**
     * Validates and clamps protection enchantment values to acceptable range.
     * <p>
     * <b>Range Enforcement:</b> Protection levels are clamped to 0-10 range,
     * matching typical Minecraft enchantment levels.
     *
     * @param nbtData the NBT compound to modify
     * @param protectionKey the protection field key to validate
     */
    private static void validateAndClampProtection(CompoundTag nbtData, String protectionKey) {
        if (nbtData.contains(protectionKey)) {
            int protection = nbtData.getInt(protectionKey);
            if (protection < 0) {
                nbtData.putInt(protectionKey, 0);
            } else if (protection > 10) {
                nbtData.putInt(protectionKey, 10);
            }
        }
    } // validateAndClampProtection()

    // -- Item State Validation --

    /**
     * Checks if ItemStack represents a valid spawn item with required data.
     * <p>
     * <b>Completeness Check:</b> Verifies item has custom data component and
     * contains minimum required fields for entity spawning.
     *
     * @param itemStack the ItemStack to check
     * @return true if item is valid for spawning, false otherwise
     */
    public static boolean isValidSpawnItem(ItemStack itemStack) {
        if (itemStack.isEmpty()) {
            return false;
        }

        CompoundTag customData = extractCustomData(itemStack);
        if (customData == null) {
            return false;
        }

        // Check for required fields
        return customData.contains(LovelyIdentifier.STAT_COLOR) &&
               customData.contains(LovelyIdentifier.STAT_LEVEL);
    } // isValidSpawnItem()

    /**
     * Checks if two ItemStacks have equivalent custom data for stacking purposes.
     * <p>
     * <b>Stacking Logic:</b> Items with identical custom data can be stacked
     * together, while items with different data must remain separate.
     *
     * @param stack1 first ItemStack to compare
     * @param stack2 second ItemStack to compare
     * @return true if custom data is equivalent, false otherwise
     */
    public static boolean hasEquivalentCustomData(ItemStack stack1, ItemStack stack2) {
        CompoundTag data1 = extractCustomData(stack1);
        CompoundTag data2 = extractCustomData(stack2);

        // Both null means equivalent
        if (data1 == null && data2 == null) {
            return true;
        }

        // One null, one not null means not equivalent
        if (data1 == null || data2 == null) {
            return false;
        }

        // Compare NBT data
        return data1.equals(data2);
    } // hasEquivalentCustomData()

} // Class: ItemValidationHelper