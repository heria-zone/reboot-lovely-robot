package net.msymbios.llovelyr.framework.entity.combat;

/**
 * <p>Defines calculation algorithms for robot combat attributes.<p>
 * <p>
 * <b>Design Intent:</b> Enables different robot types to use different scaling
 * formulas (linear, exponential, custom) without modifying entity code.
 * <p>
 * <b>Implementation Requirements:</b> All calculations must be deterministic
 * and produce consistent results for the same inputs.
 */
public interface IAttributeCalculationStrategy {
    
    /**
     * Calculates HP based on level and base value.
     * 
     * @param level current robot level
     * @param baseValue base HP value for the robot type
     * @return calculated HP value
     */
    int calculateHp(int level, int baseValue);
    
    /**
     * Calculates attack damage based on level and base value.
     * 
     * @param level current robot level
     * @param baseValue base attack value for the robot type
     * @return calculated attack value
     */
    int calculateAttack(int level, int baseValue);
    
    /**
     * Calculates defense based on level and base value.
     * 
     * @param level current robot level
     * @param baseValue base defense value for the robot type
     * @return calculated defense value
     */
    int calculateDefense(int level, int baseValue);
    
    /**
     * Calculates armor value based on defense.
     * 
     * @param defense current defense value
     * @return calculated armor value
     */
    double calculateArmor(int defense);
    
    /**
     * Calculates armor toughness based on armor level.
     * 
     * @param armorLevel current armor level
     * @return calculated armor toughness value
     */
    double calculateArmorToughness(double armorLevel);
    
} // Interface: IAttributeCalculationStrategy
