package net.msymbios.llovelyr.common.entity.internal;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.msymbios.llovelyr.source.LovelyConfigs;

/**
 * Centralizes robot stat calculation and attribute management logic.
 * <p>
 * <b>Architecture:</b> Provides pure functions for RPG-style stat progression
 * (HP, attack, defense, armor) with level-based scaling. Separates calculation
 * logic from entity state management for testability and reusability.
 * <p>
 * <b>Scaling Formula:</b> Uses linear progression (base + level * base / 50)
 * for HP/attack/defense, with armor capping at 30 and overflow converting to
 * armor toughness for diminishing returns on high-level robots.
 * <p>
 * <b>Design Decision:</b> Static utility class rather than instance methods to
 * avoid coupling with specific entity implementations, enabling shared logic
 * across all robot variants.
 */
public class InternalLogic {

    // -- Methods --

    // GETTERS

    /**
     * Calculates scaled HP based on level progression.
     * <p>
     * <b>Formula:</b> base + (level * base / 50) provides 2% HP increase per level.
     *
     * @param level the robot's current level
     * @param value the base HP value
     * @return the calculated HP
     */
    public static int calculateHp(int level, int value) {
        return (value + level * value / 50);
    } // calculateHp()

    /**
     * Calculates scaled attack damage based on level progression.
     * <p>
     * <b>Formula:</b> base + (level * base / 50) provides 2% attack increase per level.
     *
     * @param level the robot's current level
     * @param value the base attack value
     * @return the calculated attack value
     */
    public static int calculateAttack(int level, int value) {
        return (value + level * value / 50);
    } // calculateAttack()

    /**
     * Calculates scaled defense based on level progression.
     * <p>
     * <b>Formula:</b> base + (level * base / 50) provides 2% defense increase per level.
     *
     * @param level the robot's current level
     * @param value the base defense value
     * @return the calculated defense value
     */
    public static int calculateDefense(int level, int value) {
        return (value + level * value / 50);
    } // calculateDefense()

    /**
     * Converts defense to armor with hard cap at 30.
     * <p>
     * <b>Design Decision:</b> Minecraft armor caps at 30 for damage reduction
     * formula. Excess defense converts to armor toughness via calculateArmorToughness.
     *
     * @param defense the defense value to convert
     * @return the armor value (capped at 30)
     */
    public static double calculateArmor(int defense) {
        int armor = defense;
        if (armor > 30) armor = 30;
        return armor;
    } // calculateArmor()

    /**
     * Calculates armor toughness from excess armor beyond cap.
     * <p>
     * <b>Diminishing Returns:</b> Armor toughness provides additional protection
     * against high-damage attacks without breaking armor cap balance.
     *
     * @param armorLevel the total armor level (may exceed 30)
     * @return the armor toughness (0 if armor ≤ 30)
     */
    public static double calculateArmorToughness(double armorLevel) {
        double armor_tou = 0;
        if (armorLevel > 30) armor_tou = armorLevel - 30;
        return armor_tou;
    } // calculateArmorToughness()

    /**
     * Calculates looting enchantment level from robot level.
     * <p>
     * <b>Config-Driven:</b> Respects LootEnchantment toggle and caps at
     * MaxLootEnchantment to prevent excessive drop rates.
     *
     * @param level the robot's current level
     * @return the looting enchantment level (0 if disabled)
     */
    public static int calculateLooting(int level) {
        int enchantmentLevel = 0;
        if (LovelyConfigs.LootEnchantment) {
            enchantmentLevel = level / LovelyConfigs.LootEnchantmentLevel;
            if (enchantmentLevel > LovelyConfigs.MaxLootEnchantment) {
                enchantmentLevel = LovelyConfigs.MaxLootEnchantment;
            }
        }
        return enchantmentLevel;
    } // calculateLooting()

    /**
     * Calculates experience required for next level.
     * <p>
     * <b>Formula:</b> base + (level * multiplier) provides increasing XP curve.
     *
     * @param level the current level
     * @return the experience needed for next level
     */
    public static int calculateNextExp(int level) {
        return LovelyConfigs.ExperienceBase + level * LovelyConfigs.ExperienceMultiplier;
    } // calculateNextExp()

    // LOGIC

    /**
     * Applies calculated stats to entity attributes.
     * <p>
     * <b>Side Effect:</b> Mutates entity attribute base values. Call after
     * level-up or stat recalculation to sync entity with computed values.
     *
     * @param entity the entity to update
     * @param hp the new max health
     * @param attack the new attack damage
     * @param armorLevel the new armor value
     * @param armorToughness the new armor toughness
     */
    public static void handleLevel(LivingEntity entity, int hp, int attack, double armorLevel, double armorToughness) {
        updateEntityAttribute(entity, Attributes.MAX_HEALTH, hp);
        updateEntityAttribute(entity, Attributes.ATTACK_DAMAGE, attack);
        updateEntityAttribute(entity, Attributes.ARMOR, armorLevel);
        updateEntityAttribute(entity, Attributes.ARMOR_TOUGHNESS, armorToughness);
    } // handleLevel()

    /**
     * Checks if level-up is permitted.
     *
     * @param level the current level
     * @param maxLevel the maximum level allowed
     * @return true if level < maxLevel
     */
    public static boolean handleLevelUp(int level, int maxLevel) {
        return level < maxLevel;
    } // handleLevelUp()

    /**
     * Checks if fire protection can be upgraded.
     *
     * @param protectionLevel the current fire protection level
     * @return true if below config limit
     */
    public static boolean handleFireProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyConfigs.ProtectionLimitFire;
    } // handleFireProtectionLevelUp()

    /**
     * Checks if fall protection can be upgraded.
     *
     * @param protectionLevel the current fall protection level
     * @return true if below config limit
     */
    public static boolean handleFallProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyConfigs.ProtectionLimitFall;
    } // handleFallProtectionLevelUp()

    /**
     * Checks if blast protection can be upgraded.
     *
     * @param protectionLevel the current blast protection level
     * @return true if below config limit
     */
    public static boolean handleBlastProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyConfigs.ProtectionLimitBlast;
    } // handleBlastProtectionLevelUp()

    /**
     * Checks if projectile protection can be upgraded.
     *
     * @param protectionLevel the current projectile protection level
     * @return true if below config limit
     */
    public static boolean handleProjectileProtectionLevelUp(int protectionLevel) {
        return protectionLevel < LovelyConfigs.ProtectionLimitProjectile;
    } // handleProjectileProtectionLevelUp()

    // UTILITY

    /**
     * Updates entity attribute with integer value.
     *
     * @param entity the entity to modify
     * @param attribute the attribute to update
     * @param value the new base value
     */
    private static void updateEntityAttribute(LivingEntity entity, Attribute attribute, int value) {
        AttributeInstance defaultAttributeValue = entity.getAttribute(attribute);
        assert defaultAttributeValue != null;
        defaultAttributeValue.setBaseValue(value);
    } // updateEntityAttribute()

    /**
     * Updates entity attribute with double value.
     *
     * @param entity the entity to modify
     * @param attribute the attribute to update
     * @param value the new base value
     */
    private static void updateEntityAttribute(LivingEntity entity, Attribute attribute, double value) {
        AttributeInstance defaultAttributeValue = entity.getAttribute(attribute);
        assert defaultAttributeValue != null;
        defaultAttributeValue.setBaseValue(value);
    } // updateEntityAttribute()

    // DISPLAY

    /**
     * Sends message to robot owner's action bar or chat.
     * <p>
     * <b>Usage:</b> For level-up notifications, stat changes, or status updates.
     *
     * @param entity the robot entity
     * @param message the message component
     * @param overlay true for action bar, false for chat
     */
    public static void displayInfo(TamableAnimal entity, Component message, boolean overlay) {
        if (entity.getOwner() != null) {
            Player player = (Player) entity.getOwner();
            player.displayClientMessage(message, overlay);
        }
    } // displayInfo()

    /**
     * Convenience overload accepting string message.
     *
     * @param entity the robot entity
     * @param message the message string
     * @param overlay true for action bar, false for chat
     */
    public static void displayInfo(TamableAnimal entity, String message, boolean overlay) {
        displayInfo(entity, Component.nullToEmpty(message), overlay);
    } // displayInfo()

} // Class: InternalLogic
