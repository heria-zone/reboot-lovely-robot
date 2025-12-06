package net.msymbios.llovelyr.common.entity.internal;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.msymbios.llovelyr.common.Configs.SharedConfigs;

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
public class InternalLogic { // TODO: Replace the calculate methods with CombatLevelFeature, & ProtectionFeature

    // -- Methods --

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
     * Checks if fire protection can be upgraded.
     *
     * @param protectionLevel the current fire protection level
     * @return true if below config limit
     */
    public static boolean handleFireProtectionLevelUp(int protectionLevel) {
        return protectionLevel < SharedConfigs.Common.ProtectionLimitFire;
    } // handleFireProtectionLevelUp()

    /**
     * Checks if fall protection can be upgraded.
     *
     * @param protectionLevel the current fall protection level
     * @return true if below config limit
     */
    public static boolean handleFallProtectionLevelUp(int protectionLevel) {
        return protectionLevel < SharedConfigs.Common.ProtectionLimitFall;
    } // handleFallProtectionLevelUp()

    /**
     * Checks if blast protection can be upgraded.
     *
     * @param protectionLevel the current blast protection level
     * @return true if below config limit
     */
    public static boolean handleBlastProtectionLevelUp(int protectionLevel) {
        return protectionLevel < SharedConfigs.Common.ProtectionLimitBlast;
    } // handleBlastProtectionLevelUp()

    /**
     * Checks if projectile protection can be upgraded.
     *
     * @param protectionLevel the current projectile protection level
     * @return true if below config limit
     */
    public static boolean handleProjectileProtectionLevelUp(int protectionLevel) {
        return protectionLevel < SharedConfigs.Common.ProtectionLimitProjectile;
    } // handleProjectileProtectionLevelUp()

    // UTILITY

    /**
     * Updates entity attribute with integer value.
     * <p>
     * <b>1.21.1 API:</b> Uses Holder&lt;Attribute&gt; for attribute references.
     *
     * @param entity the entity to modify
     * @param attribute the attribute holder to update
     * @param value the new base value
     */
    private static void updateEntityAttribute(LivingEntity entity, Holder<Attribute> attribute, int value) {
        AttributeInstance defaultAttributeValue = entity.getAttribute(attribute);
        assert defaultAttributeValue != null;
        defaultAttributeValue.setBaseValue(value);
    } // updateEntityAttribute()

    /**
     * Updates entity attribute with double value.
     * <p>
     * <b>1.21.1 API:</b> Uses Holder&lt;Attribute&gt; for attribute references.
     *
     * @param entity the entity to modify
     * @param attribute the attribute holder to update
     * @param value the new base value
     */
    private static void updateEntityAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
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