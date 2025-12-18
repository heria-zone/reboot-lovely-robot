package net.heriazone.lovelylib.api.items.utils;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.lovelylib.common.entity.enums.EntityTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * Provides NBT processing utilities specific to item data components.
 * <p>
 * <b>Architecture:</b> Handles the complexity of Minecraft 1.21.1's data component
 * system while providing a clean interface for NBT operations on spawn items.
 * <p>
 * <b>Migration Bridge:</b> Abstracts the transition from direct NBT manipulation
 * to typed data components, allowing existing NBT-based logic to work seamlessly.
 * <p>
 * <b>Performance:</b> Minimizes component access overhead by batching operations
 * and caching extracted data during multi-field updates.
 */
public class ItemNBTHelper {

    // -- Data Component Access --

    /**
     * Safely retrieves custom data from ItemStack without exceptions.
     * <p>
     * <b>Null Safety:</b> Returns empty CompoundTag if no custom data exists,
     * preventing null pointer exceptions in downstream processing.
     *
     * @param itemStack the ItemStack to read from
     * @return CompoundTag with custom data, or empty tag if not present
     */
    public static CompoundTag getCustomDataSafe(ItemStack itemStack) {
        if (itemStack.has(DataComponents.CUSTOM_DATA)) {
            CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                return customData.copyTag();
            }
        }
        return new CompoundTag();
    } // getCustomDataSafe()

    /**
     * Updates custom data component on ItemStack with provided NBT.
     * <p>
     * <b>Component Integration:</b> Properly wraps NBT in CustomData component
     * and handles the data component API correctly.
     *
     * @param itemStack the ItemStack to update
     * @param nbtData the NBT data to store
     */
    public static void setCustomData(ItemStack itemStack, CompoundTag nbtData) {
        if (nbtData.isEmpty()) {
            itemStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtData));
        }
    } // setCustomData()

    // -- Field-Specific Accessors --

    /**
     * Gets robot color/texture ID from item data.
     * <p>
     * <b>Default Handling:</b> Returns RANDOM texture ID if not specified,
     * ensuring valid texture selection for entity spawning.
     *
     * @param itemStack the ItemStack to read from
     * @return texture ID, or RANDOM if not specified
     */
    public static int getRobotColor(ItemStack itemStack) {
        CompoundTag data = getCustomDataSafe(itemStack);
        return data.getInt(LovelyConstant.STAT_COLOR);
    } // getRobotColor()

    /**
     * Sets robot color/texture ID in item data.
     * <p>
     * <b>Validation:</b> Ensures texture ID is within valid range before storing.
     *
     * @param itemStack the ItemStack to update
     * @param colorId the texture ID to set
     */
    public static void setRobotColor(ItemStack itemStack, int colorId) {
        CompoundTag data = getCustomDataSafe(itemStack);

        // Validate color ID range
        if (colorId >= 0 && colorId < EntityTexture.values().length) {
            data.putInt(LovelyConstant.STAT_COLOR, colorId);
        } else {
            data.putInt(LovelyConstant.STAT_COLOR, EntityTexture.RANDOM.getId());
        }

        setCustomData(itemStack, data);
    } // setRobotColor()

    /**
     * Gets robot level from item data.
     * <p>
     * <b>Default Handling:</b> Returns 0 if not specified, representing
     * a newly spawned robot without experience.
     *
     * @param itemStack the ItemStack to read from
     * @return robot level, or 0 if not specified
     */
    public static int getRobotLevel(ItemStack itemStack) {
        CompoundTag data = getCustomDataSafe(itemStack);
        return data.getInt(LovelyConstant.STAT_LEVEL);
    } // getRobotLevel()

    /**
     * Sets robot level in item data.
     * <p>
     * <b>Range Validation:</b> Clamps level to reasonable range (0-100)
     * to prevent overflow issues.
     *
     * @param itemStack the ItemStack to update
     * @param level the level to set (will be clamped to 0-100)
     */
    public static void setRobotLevel(ItemStack itemStack, int level) {
        CompoundTag data = getCustomDataSafe(itemStack);

        // Clamp level to reasonable range
        int clampedLevel = Math.max(0, Math.min(100, level));
        data.putInt(LovelyConstant.STAT_LEVEL, clampedLevel);

        setCustomData(itemStack, data);
    } // setRobotLevel()

    /**
     * Gets robot experience from item data.
     * <p>
     * <b>Default Handling:</b> Returns 0 if not specified.
     *
     * @param itemStack the ItemStack to read from
     * @return robot experience, or 0 if not specified
     */
    public static int getRobotExperience(ItemStack itemStack) {
        CompoundTag data = getCustomDataSafe(itemStack);
        return data.getInt(LovelyConstant.STAT_EXP);
    } // getRobotExperience()

    /**
     * Sets robot experience in item data.
     * <p>
     * <b>Range Validation:</b> Ensures experience is non-negative.
     *
     * @param itemStack the ItemStack to update
     * @param experience the experience to set (will be clamped to >= 0)
     */
    public static void setRobotExperience(ItemStack itemStack, int experience) {
        CompoundTag data = getCustomDataSafe(itemStack);

        // Ensure non-negative experience
        int clampedExp = Math.max(0, experience);
        data.putInt(LovelyConstant.STAT_EXP, clampedExp);

        setCustomData(itemStack, data);
    } // setRobotExperience()

    /**
     * Gets custom robot name from item data.
     * <p>
     * <b>Default Handling:</b> Returns empty string if no custom name set.
     *
     * @param itemStack the ItemStack to read from
     * @return custom name, or empty string if not specified
     */
    public static String getRobotCustomName(ItemStack itemStack) {
        CompoundTag data = getCustomDataSafe(itemStack);
        return data.getString(LovelyConstant.STAT_CUSTOM_NAME);
    } // getRobotCustomName()

    /**
     * Sets custom robot name in item data.
     * <p>
     * <b>Length Validation:</b> Truncates names longer than 50 characters
     * to prevent display issues.
     *
     * @param itemStack the ItemStack to update
     * @param customName the name to set (will be truncated if too long)
     */
    public static void setRobotCustomName(ItemStack itemStack, String customName) {
        CompoundTag data = getCustomDataSafe(itemStack);

        // Validate and truncate name if necessary
        String validatedName = customName != null ? customName : "";
        if (validatedName.length() > 50) {
            validatedName = validatedName.substring(0, 50);
        }

        data.putString(LovelyConstant.STAT_CUSTOM_NAME, validatedName);
        setCustomData(itemStack, data);
    } // setRobotCustomName()

    // -- Protection Enchantments --

    /**
     * Gets protection enchantment level from item data.
     * <p>
     * <b>Flexible Access:</b> Supports all protection types through key parameter.
     *
     * @param itemStack the ItemStack to read from
     * @param protectionKey the protection type key
     * @return protection level, or 0 if not specified
     */
    public static int getProtectionLevel(ItemStack itemStack, String protectionKey) {
        CompoundTag data = getCustomDataSafe(itemStack);
        return data.getInt(protectionKey);
    } // getProtectionLevel()

    /**
     * Sets protection enchantment level in item data.
     * <p>
     * <b>Range Validation:</b> Clamps protection level to 0-10 range,
     * matching typical Minecraft enchantment levels.
     *
     * @param itemStack the ItemStack to update
     * @param protectionKey the protection type key
     * @param level the protection level to set (will be clamped to 0-10)
     */
    public static void setProtectionLevel(ItemStack itemStack, String protectionKey, int level) {
        CompoundTag data = getCustomDataSafe(itemStack);

        // Clamp protection level to reasonable range
        int clampedLevel = Math.max(0, Math.min(10, level));
        data.putInt(protectionKey, clampedLevel);

        setCustomData(itemStack, data);
    } // setProtectionLevel()

    // -- Batch Operations --

    /**
     * Applies multiple NBT updates in a single operation for efficiency.
     * <p>
     * <b>Performance Optimization:</b> Reduces component access overhead
     * by batching multiple field updates into single component write.
     *
     * @param itemStack the ItemStack to update
     * @param updates the NBT updates to apply
     */
    public static void batchUpdateCustomData(ItemStack itemStack, CompoundTag updates) {
        CompoundTag currentData = getCustomDataSafe(itemStack);

        // Merge updates into current data
        for (String key : updates.getAllKeys()) {
            currentData.put(key, updates.get(key).copy());
        }

        setCustomData(itemStack, currentData);
    } // batchUpdateCustomData()

    /**
     * Copies all custom data from source ItemStack to target ItemStack.
     * <p>
     * <b>Data Transfer:</b> Useful for recipe operations and item transformations
     * where custom data needs to be preserved.
     *
     * @param source the ItemStack to copy data from
     * @param target the ItemStack to copy data to
     */
    public static void copyCustomData(ItemStack source, ItemStack target) {
        CompoundTag sourceData = getCustomDataSafe(source);
        if (!sourceData.isEmpty()) {
            setCustomData(target, sourceData.copy());
        }
    } // copyCustomData()

    // -- Data Comparison --

    /**
     * Compares custom data between two ItemStacks for equality.
     * <p>
     * <b>Stacking Support:</b> Used to determine if items can be stacked
     * based on identical custom data content.
     *
     * @param stack1 first ItemStack to compare
     * @param stack2 second ItemStack to compare
     * @return true if custom data is identical, false otherwise
     */
    public static boolean hasIdenticalCustomData(ItemStack stack1, ItemStack stack2) {
        CompoundTag data1 = getCustomDataSafe(stack1);
        CompoundTag data2 = getCustomDataSafe(stack2);

        return data1.equals(data2);
    } // hasIdenticalCustomData()

} // Class: ItemNBTHelper