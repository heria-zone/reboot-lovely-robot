package net.heriazone.lovelylib.common.entity.combat;

import net.heriazone.hzlib.framework.entity.combat.IAttributeCalculationStrategy;

/**
 * <p>Implements linear attribute scaling with 2% growth per level.<p>
 * <p>
 * <b>Formula:</b> attribute = baseValue + (level * baseValue / 50)
 * <p>
 * <b>Armor Mechanics:</b> Defense converts directly to armor, capped at 30.
 * Any defense above 30 becomes armor toughness (defense - 30).
 * <p>
 * <b>Design Decision:</b> Minecraft armor caps at 30 for damage reduction formula.
 * Excess defense converts to armor toughness for protection against high-damage
 * attacks without breaking armor cap balance.
 */
public class LinearAttributeStrategy implements IAttributeCalculationStrategy {

    // -- Constants --

    private static final int GROWTH_DIVISOR = 50; // 2% per level (1/50 = 0.02)
    private static final int ARMOR_CAP = 30; // Minecraft armor hard cap

    // -- Inherited Methods --

    @Override
    public int calculateHp(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    } // calculateHp ()

    @Override
    public int calculateAttack(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    } // calculateAttack ()

    @Override
    public int calculateDefense(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    } // calculateDefense ()

    @Override
    public double calculateArmor(int defense) {
        // Armor caps at 30 - Minecraft's damage reduction formula limit
        return Math.min(defense, ARMOR_CAP);
    } // calculateArmor ()

    @Override
    public double calculateArmorToughness(double armorLevel) {
        // Surplus defense above 30 becomes armor toughness
        // Provides protection against high-damage attacks
        return armorLevel > ARMOR_CAP ? armorLevel - ARMOR_CAP : 0;
    } // calculateArmorToughness ()

} // Class: LinearAttributeStrategy