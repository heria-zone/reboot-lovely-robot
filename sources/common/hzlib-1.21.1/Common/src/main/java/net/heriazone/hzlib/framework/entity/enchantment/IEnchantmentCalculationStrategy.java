package net.heriazone.hzlib.framework.entity.enchantment;

/**
 * <p>Defines calculation algorithms for robot enchantment levels.<p>
 * <p>
 * <b>Design Intent:</b> Enables different robot types to have different
 * enchantment progression rates and maximum levels.
 */
public interface IEnchantmentCalculationStrategy {

    /**
     * Calculates looting enchantment level based on robot level.
     *
     * @param level current robot level
     * @return calculated looting level
     */
    int calculateLooting(int level);

    /**
     * Calculates sharpness enchantment level based on robot level.
     *
     * @param level current robot level
     * @return calculated sharpness level
     */
    int calculateSharpness(int level);

    /**
     * Calculates knockback enchantment level based on robot level.
     *
     * @param level current robot level
     * @return calculated knockback level
     */
    int calculateKnockback(int level);

} // Interface: IEnchantmentCalculationStrategy