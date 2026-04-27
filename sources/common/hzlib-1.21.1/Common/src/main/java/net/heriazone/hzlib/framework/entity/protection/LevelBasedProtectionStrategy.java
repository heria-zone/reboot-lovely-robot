package net.heriazone.hzlib.framework.entity.protection;

/**
 * <p>Implements level-based protection unlocking at configured thresholds.<p>
 * <p>
 * <b>Formula:</b> protectionLevel = level / threshold (capped at maximum)
 * <p>
 * <b>Design Decision:</b> Provides automatic protection upgrades as robots level up,
 * with configurable thresholds for each protection type.
 */
public class LevelBasedProtectionStrategy implements IProtectionCalculationStrategy {

    // -- Constants --

    private static final int DEFAULT_FIRE_THRESHOLD = 10;
    private static final int DEFAULT_FALL_THRESHOLD = 15;
    private static final int DEFAULT_BLAST_THRESHOLD = 20;
    private static final int DEFAULT_PROJECTILE_THRESHOLD = 25;

    // -- Variables --

    private final int fireThreshold;
    private final int fallThreshold;
    private final int blastThreshold;
    private final int projectileThreshold;

    // -- Constructors --

    /**
     * Creates level-based protection strategy with default thresholds.
     */
    public LevelBasedProtectionStrategy() {
        this(DEFAULT_FIRE_THRESHOLD, DEFAULT_FALL_THRESHOLD,
                DEFAULT_BLAST_THRESHOLD, DEFAULT_PROJECTILE_THRESHOLD);
    } // Constructor: LevelBasedProtectionStrategy ()

    /**
     * Creates protection strategy with custom level thresholds.
     *
     * @param fireThreshold levels per fire protection level
     * @param fallThreshold levels per fall protection level
     * @param blastThreshold levels per blast protection level
     * @param projectileThreshold levels per projectile protection level
     * @throws IllegalArgumentException if any threshold <= 0
     */
    public LevelBasedProtectionStrategy(int fireThreshold, int fallThreshold,
                                        int blastThreshold, int projectileThreshold) {
        if (fireThreshold <= 0 || fallThreshold <= 0 ||
                blastThreshold <= 0 || projectileThreshold <= 0) {
            throw new IllegalArgumentException("Thresholds must be greater than 0");
        }
        this.fireThreshold = fireThreshold;
        this.fallThreshold = fallThreshold;
        this.blastThreshold = blastThreshold;
        this.projectileThreshold = projectileThreshold;
    } // Constructor: LevelBasedProtectionStrategy ()

    // -- Inherited Methods --

    @Override
    public int calculateFireProtection(int level, int currentProtection) {
        int calculatedLevel = level / fireThreshold;
        return Math.max(currentProtection, calculatedLevel);
    } // calculateFireProtection ()

    @Override
    public int calculateFallProtection(int level, int currentProtection) {
        int calculatedLevel = level / fallThreshold;
        return Math.max(currentProtection, calculatedLevel);
    } // calculateFallProtection ()

    @Override
    public int calculateBlastProtection(int level, int currentProtection) {
        int calculatedLevel = level / blastThreshold;
        return Math.max(currentProtection, calculatedLevel);
    } // calculateBlastProtection ()

    @Override
    public int calculateProjectileProtection(int level, int currentProtection) {
        int calculatedLevel = level / projectileThreshold;
        return Math.max(currentProtection, calculatedLevel);
    } // calculateProjectileProtection ()

    // -- Custom Methods --

    /**
     * Gets the fire protection threshold.
     *
     * @return fire threshold
     */
    public int getFireThreshold() {
        return fireThreshold;
    } // getFireThreshold ()

    /**
     * Gets the fall protection threshold.
     *
     * @return fall threshold
     */
    public int getFallThreshold() {
        return fallThreshold;
    } // getFallThreshold ()

    /**
     * Gets the blast protection threshold.
     *
     * @return blast threshold
     */
    public int getBlastThreshold() {
        return blastThreshold;
    } // getBlastThreshold ()

    /**
     * Gets the projectile protection threshold.
     *
     * @return projectile threshold
     */
    public int getProjectileThreshold() {
        return projectileThreshold;
    } // getProjectileThreshold ()

} // Class: LevelBasedProtectionStrategy