package net.heriazone.hzlib.framework.entity.protection;

/**
 * <p>Defines calculation algorithms for automatic protection unlocking.<p>
 * <p>
 * <b>Design Intent:</b> Enables level-based automatic protection upgrades
 * while respecting configured maximum limits.
 */
public interface IProtectionCalculationStrategy {

    /**
     * Calculates fire protection level based on robot level and current protection.
     *
     * @param level current robot level
     * @param currentProtection current fire protection level
     * @return calculated fire protection level
     */
    int calculateFireProtection(int level, int currentProtection);

    /**
     * Calculates fall protection level based on robot level and current protection.
     *
     * @param level current robot level
     * @param currentProtection current fall protection level
     * @return calculated fall protection level
     */
    int calculateFallProtection(int level, int currentProtection);

    /**
     * Calculates blast protection level based on robot level and current protection.
     *
     * @param level current robot level
     * @param currentProtection current blast protection level
     * @return calculated blast protection level
     */
    int calculateBlastProtection(int level, int currentProtection);

    /**
     * Calculates projectile protection level based on robot level and current protection.
     *
     * @param level current robot level
     * @param currentProtection current projectile protection level
     * @return calculated projectile protection level
     */
    int calculateProjectileProtection(int level, int currentProtection);

} // Interface: IProtectionCalculationStrategy