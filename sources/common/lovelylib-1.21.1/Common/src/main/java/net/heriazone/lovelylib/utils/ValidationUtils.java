package net.heriazone.lovelylib.utils;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.heriazone.hzlib.framework.utils.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Provides comprehensive data validation utilities for robot systems.
 * <p>
 * <b>Architecture:</b> Centralizes validation logic that was scattered across
 * loader implementations. Ensures data integrity and prevents invalid states
 * that could cause crashes or unexpected behavior.
 * <p>
 * <b>Design Philosophy:</b> Fail-safe validation that corrects invalid data
 * rather than rejecting it entirely, providing better user experience.
 */
public class ValidationUtils {

    // -- Constants --

    /** Maximum allowed robot level. */
    public static final int MAX_ROBOT_LEVEL = 100;

    /** Maximum allowed protection value. */
    public static final int MAX_PROTECTION_VALUE = 10;

    /** Maximum allowed health value. */
    public static final float MAX_HEALTH_VALUE = 1000.0f;

    /** Maximum allowed experience value. */
    public static final int MAX_EXPERIENCE_VALUE = 1000000;

    // -- Robot Data Validation --

    /**
     * Validates complete robot data structure with correction.
     * <p>
     * <b>Validation Scope:</b> Checks all robot attributes including level,
     * experience, health, protections, color, and name. Applies corrections
     * for out-of-range values while preserving valid data.
     * <p>
     * <b>Correction Strategy:</b> Clamps numeric values to valid ranges,
     * sanitizes strings, and replaces invalid enums with defaults.
     *
     * @param nbt robot data to validate
     * @return validation result with correction details
     */
    public static ValidationResult validateRobotData(CompoundTag nbt) {
        if (nbt == null) {
            return new ValidationResult(false, "NBT data is null");
        }

        ValidationResult result = new ValidationResult(true, "");
        StringBuilder issues = new StringBuilder();

        // Validate level
        int level = nbt.getInt(LovelyConstant.STAT_LEVEL);
        if (level < 0 || level > MAX_ROBOT_LEVEL) {
            int correctedLevel = MathUtils.clamp(level, 0, MAX_ROBOT_LEVEL);
            nbt.putInt(LovelyConstant.STAT_LEVEL, correctedLevel);
            issues.append("Level corrected from ").append(level).append(" to ").append(correctedLevel).append("; ");
            result.setValid(false);
        }

        // Validate experience
        int exp = nbt.getInt(LovelyConstant.STAT_EXP);
        if (exp < 0 || exp > MAX_EXPERIENCE_VALUE) {
            int correctedExp = MathUtils.clamp(exp, 0, MAX_EXPERIENCE_VALUE);
            nbt.putInt(LovelyConstant.STAT_EXP, correctedExp);
            issues.append("Experience corrected from ").append(exp).append(" to ").append(correctedExp).append("; ");
            result.setValid(false);
        }

        // Validate health
        if (nbt.contains(LovelyConstant.STAT_HP)) {
            float hp = nbt.getFloat(LovelyConstant.STAT_HP);
            if (hp <= 0 || hp > MAX_HEALTH_VALUE) {
                if (hp <= 0) {
                    nbt.remove(LovelyConstant.STAT_HP);
                    issues.append("Invalid health removed; ");
                } else {
                    nbt.putFloat(LovelyConstant.STAT_HP, MAX_HEALTH_VALUE);
                    issues.append("Health clamped to maximum; ");
                }
                result.setValid(false);
            }
        }

        // Validate protection values
        String[] protectionKeys = {
                LovelyConstant.STAT_FIRE_PROTECTION,
                LovelyConstant.STAT_FALL_PROTECTION,
                LovelyConstant.STAT_BLAST_PROTECTION,
                LovelyConstant.STAT_PROJECTILE_PROTECTION
        };

        for (String key : protectionKeys) {
            int protection = nbt.getInt(key);
            if (protection < 0 || protection > MAX_PROTECTION_VALUE) {
                int corrected = MathUtils.clamp(protection, 0, MAX_PROTECTION_VALUE);
                nbt.putInt(key, corrected);
                issues.append(key).append(" corrected from ").append(protection).append(" to ").append(corrected).append("; ");
                result.setValid(false);
            }
        }

        // Validate color/texture
        int colorId = nbt.getInt(LovelyConstant.STAT_COLOR);
        EntityTexture texture = EntityTexture.byId(colorId);
        if (texture == null) {
            nbt.putInt(LovelyConstant.STAT_COLOR, EntityTexture.RANDOM.getId());
            issues.append("Invalid color ID ").append(colorId).append(" replaced with RANDOM; ");
            result.setValid(false);
        }

        // Validate custom name
        String customName = nbt.getString(LovelyConstant.STAT_CUSTOM_NAME);
        if (!customName.isEmpty() && !StringUtils.isValidRobotName(customName)) {
            String sanitized = StringUtils.sanitize(customName);
            if (StringUtils.isValidRobotName(sanitized)) {
                nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, sanitized);
                issues.append("Custom name sanitized; ");
            } else {
                nbt.putString(LovelyConstant.STAT_CUSTOM_NAME, "");
                issues.append("Invalid custom name removed; ");
            }
            result.setValid(false);
        }

        // Validate owner name
        String ownerName = nbt.getString(LovelyConstant.STAT_OWNER);
        if (!ownerName.isEmpty() && !StringUtils.isValidOwnerName(ownerName)) {
            nbt.putString(LovelyConstant.STAT_OWNER, "");
            issues.append("Invalid owner name removed; ");
            result.setValid(false);
        }

        result.setMessage(issues.toString());
        return result;
    } // validateRobotData()

    // -- Item Stack Validation --

    /**
     * Validates ItemStack for robot-related operations.
     * <p>
     * <b>Checks:</b> Null safety, empty stack detection, and custom data validation.
     *
     * @param itemStack item stack to validate
     * @return true if item stack is valid for robot operations
     */
    public static boolean isValidRobotItemStack(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) {
            return false;
        }

        // Check if has custom data and validate it
        CompoundTag customData = NbtProcessingUtils.getCustomData(itemStack);
        if (customData != null) {
            ValidationResult result = validateRobotData(customData);
            if (!result.isValid()) {
                // Apply corrections to the item stack
                NbtProcessingUtils.setCustomData(itemStack, customData);
            }
        }

        return true;
    } // isValidRobotItemStack()

    // -- Player Validation --

    /**
     * Validates player for robot operations.
     * <p>
     * <b>Checks:</b> Null safety, online status, and permission validation.
     *
     * @param player player to validate
     * @return true if player can perform robot operations
     */
    public static boolean isValidPlayerForRobotOperations(Player player) {
        return player != null && player.isAlive() && !player.isSpectator();
    } // isValidPlayerForRobotOperations()

    // -- Range Validation --

    /**
     * Validates numeric value is within specified range.
     * <p>
     * <b>Usage:</b> Generic range validation for configuration values and user input.
     *
     * @param value value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @param name descriptive name for error messages
     * @return validation result
     */
    public static ValidationResult validateRange(int value, int min, int max, String name) {
        if (value >= min && value <= max) {
            return new ValidationResult(true, "");
        }

        String message = String.format("%s value %d is outside valid range [%d, %d]", name, value, min, max);
        return new ValidationResult(false, message);
    } // validateRange()

    /**
     * Validates float value is within specified range.
     *
     * @param value value to validate
     * @param min minimum allowed value
     * @param max maximum allowed value
     * @param name descriptive name for error messages
     * @return validation result
     */
    public static ValidationResult validateRange(float value, float min, float max, String name) {
        if (value >= min && value <= max) {
            return new ValidationResult(true, "");
        }

        String message = String.format("%s value %.2f is outside valid range [%.2f, %.2f]", name, value, min, max);
        return new ValidationResult(false, message);
    } // validateRange()

    // -- Validation Result Class --

    /**
     * Encapsulates validation results with success status and descriptive messages.
     * <p>
     * <b>Usage:</b> Provides detailed feedback about validation operations,
     * including what corrections were applied and why validation failed.
     */
    public static class ValidationResult {
        private boolean valid;
        private String message;

        public ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("ValidationResult{valid=%s, message='%s'}", valid, message);
        }
    } // Class: ValidationResult

} // Class: ValidationUtils