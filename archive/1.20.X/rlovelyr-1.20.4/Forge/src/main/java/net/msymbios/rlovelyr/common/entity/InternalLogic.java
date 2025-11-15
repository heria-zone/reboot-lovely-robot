package net.msymbios.rlovelyr.common.entity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;

public class InternalLogic {

    // -- Methods --

    // GETTERS

    /**
     * Calculate the HP based on the level and value.
     *
     * @param level The level of the character
     * @param value The base HP value
     * @return the calculated HP
     */
    public static int calculateHp(int level, int value) { return (value + level * value / 50); } // calculateHp ()

    /**
     * Calculates the attack value based on the level and base value.
     *
     * @param level The level of the entity
     * @param value The base attack value
     * @return the calculated attack value
     */
    public static int calculateAttack(int level, int value) { return (value + level * value / 50); } // calculateAttack ()

    /**
     * Calculates the defense value based on the level and base value.
     *
     * @param level The level of the character.
     * @param value The base defense value.
     * @return The calculated defense value.
     */
    public static int calculateDefense(int level, int value) { return (value + level * value / 50); } // calculateDefense ()

    /**
     * Calculates the armor based on the defense value.
     *
     * @param defense The defense value to calculate armor from.
     * @return The calculated armor value.
     */
    public static double calculateArmor(int defense) {
        int armor = defense;
        if (armor > 30) armor = 30;
        return armor;
    } // calculateArmor ()

    /**
     * Calculates the armor toughness based on the armor level.
     *
     * @param armorLevel the level of armor
     * @return the calculated armor toughness
     */
    public static double calculateArmorToughness(double armorLevel) {
        double armor_tou = 0;

        // Calculate armor toughness if armor level is greater than 30
        if (armorLevel > 30) armor_tou = armorLevel - 30;
        return armor_tou;
    } // calculateArmorToughness ()

    /**
     * Calculates the looting enchantment level based on the provided level.
     *
     * @param level the level to calculate the enchantment for
     * @return the calculated enchantment level
     */
    public static int calculateLooting(int level) {
        int enchantmentLevel = 0;
        // Check if looting enchantment is enabled
        if (LovelyRobotConfig.Common.LootEnchantment) {
            enchantmentLevel = level / LovelyRobotConfig.Common.LootEnchantmentLevel;
            // Ensure enchantment level does not exceed the maximum allowed
            if (enchantmentLevel > LovelyRobotConfig.Common.MaxLootEnchantment) {
                enchantmentLevel = LovelyRobotConfig.Common.MaxLootEnchantment;
            }
        }
        return enchantmentLevel;
    } // calculateLooting ()

    /**
     * Calculates the experience needed for the next level based on the current level.
     *
     * @param level the current level of the entity
     * @return the experience needed for the next level
     */
    public static int calculateNextExp(int level) {
        // Calculate the experience needed for the next level
        return LovelyRobotConfig.Common.ExperienceBase + level * LovelyRobotConfig.Common.ExperienceMultiplier;
    } // calculateNextExp ()

    // LOGIC

    /**
     * Updates the attributes of a LivingEntity based on the provided parameters.
     *
     * @param entity The LivingEntity whose attributes will be updated.
     * @param hp The new health points value.
     * @param attack The new attack value.
     * @param armorLevel The new armor level value.
     * @param armorToughness The new armor toughness value.
     */
    public static void handleLevel(LivingEntity entity, int hp, int attack, double armorLevel, double armorToughness) {
        // Update the maximum health attribute
        updateEntityAttribute(entity, Attributes.MAX_HEALTH, hp);
        // Update the attack damage attribute
        updateEntityAttribute(entity, Attributes.ATTACK_DAMAGE, attack);
        // Update the armor attribute
        updateEntityAttribute(entity, Attributes.ARMOR, armorLevel);
        // Update the armor toughness attribute
        updateEntityAttribute(entity, Attributes.ARMOR_TOUGHNESS, armorToughness);
    } // handleLevel ()

    /**
     * Checks if the given level is less than the maximum level.
     *
     * @param level the current level
     * @param maxLevel the maximum level allowed
     * @return true if the level is less than the maximum level, false otherwise
     */
    public static boolean handleLevelUp(int level, int maxLevel) {
        return level < maxLevel;
    } // handleLevelUp ()

    /**
     * Checks if the protection level for fire needs to be upgraded based on the provided protection level.
     *
     * @param protectionLevel The current protection level for fire.
     * @return true if the protection level needs to be upgraded, false otherwise.
     */
    public static boolean handleFireProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyRobotConfig.Common.ProtectionLimitFire;
    } // handleFireProtectionLevelUp ()

    /**
     * Checks if the protection level for fall needs to be upgraded based on the provided protection level.
     *
     * @param protectionLevel The current protection level for fall.
     * @return true if the protection level needs to be upgraded, false otherwise.
     */
    public static boolean handleFallProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyRobotConfig.Common.ProtectionLimitFall;
    } // handleFallProtectionLevelUp ()

    /**
     * Checks if the protection level for blast needs to be upgraded based on the provided protection level.
     *
     * @param protectionLevel The current protection level for blast.
     * @return true if the protection level needs to be upgraded, false otherwise.
     */
    public static boolean handleBlastProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyRobotConfig.Common.ProtectionLimitBlast;
    } // handleBlastProtectionLevelUp ()

    /**
     * Check if the provided protection level is below the projectile protection limit.
     *
     * @param protectionLevel The protection level to be checked
     * @return true if the protection level is below the projectile protection limit, false otherwise
     */
    public static boolean handleProjectileProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyRobotConfig.Common.ProtectionLimitProjectile;
    } // handleProjectileProtectionLevelUp ()

    // UTILITY

    /**
     * Updates the specified entity's attribute with the given value.
     *
     * @param entity The living entity whose attribute needs to be updated.
     * @param attribute The entity attribute to update.
     * @param value The new value for the attribute.
     */
    private static void updateEntityAttribute(LivingEntity entity, Attribute attribute, int value) {
        // Get the entity's attribute instance for the specified attribute
        AttributeInstance defaultAttributeValue = entity.getAttribute(attribute);

        // Ensure the attribute instance is not null
        assert defaultAttributeValue != null;

        // Set the base value of the attribute to the provided value
        defaultAttributeValue.setBaseValue(value);
    } // updateEntityAttribute ()

    /**
     * Updates the specified entity's attribute with the given double value.
     *
     * @param entity The living entity whose attribute needs to be updated.
     * @param attribute The entity attribute to update.
     * @param value The new double value for the attribute.
     */
    private static void updateEntityAttribute(LivingEntity entity, Attribute attribute, double value) {
        // Get the entity's attribute instance for the specified attribute
        AttributeInstance defaultAttributeValue = entity.getAttribute(attribute);

        // Ensure the attribute instance is not null
        assert defaultAttributeValue != null;

        // Set the base value of the attribute to the provided double value
        defaultAttributeValue.setBaseValue(value);
    } // updateEntityAttribute ()

    // DISPLAY

    /**
     * Displays the given message to the owner of the entity, if the owner exists.
     *
     * @param entity The entity to display the message owner.
     * @param message The message to display.
     * @param overlay Indicates whether the message should be displayed as an overlay.
     */
    public static void displayInfo(TamableAnimal entity, Component message, boolean overlay) {
        // Check if the entity has an owner
        if(entity.getOwner() != null) {
            // Get the owner as a PlayerEntity
            Player player = (Player)entity.getOwner();
            // Send the message to the player
            player.displayClientMessage(message, overlay);
        }
    } // displayInfo ()

    /**
     * Displays the given message to the owner of the entity, if the owner exists.
     *
     * @param entity The entity to display the message owner.
     * @param message The message to display.
     * @param overlay Indicates whether the message should be displayed as an overlay.
     */
    public static void displayInfo(TamableAnimal entity, String message, boolean overlay) { displayInfo(entity, Component.nullToEmpty(message), overlay); } // displayInfo ()

} // Class InternalLogic