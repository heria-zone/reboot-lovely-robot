package net.msymbios.llovelyr.framework.entity.combat;

/**
 * <p>Implements linear attribute scaling with 2% growth per level.<p>
 * <p>
 * <b>Formula:</b> attribute = baseValue + (level * baseValue / 50)
 * <p>
 * <b>Design Decision:</b> Maintains compatibility with existing InternalLogic
 * calculations while providing a pluggable implementation.
 */
public class LinearAttributeStrategy implements IAttributeCalculationStrategy {
    
    // -- Constants --
    
    private static final int GROWTH_DIVISOR = 50; // 2% per level (1/50 = 0.02)
    private static final double ARMOR_MULTIPLIER = 0.5;
    private static final double ARMOR_TOUGHNESS_MULTIPLIER = 0.25;
    
    // -- Public Methods --
    
    @Override
    public int calculateHp(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    }
    
    @Override
    public int calculateAttack(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    }
    
    @Override
    public int calculateDefense(int level, int baseValue) {
        return baseValue + (level * baseValue / GROWTH_DIVISOR);
    }
    
    @Override
    public double calculateArmor(int defense) {
        return defense * ARMOR_MULTIPLIER;
    }
    
    @Override
    public double calculateArmorToughness(double armorLevel) {
        return armorLevel * ARMOR_TOUGHNESS_MULTIPLIER;
    }
    
} // Class: LinearAttributeStrategy
