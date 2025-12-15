package net.msymbios.llovelyr.framework.entity.data;

/**
 * <p>Encapsulates protection statistics for robot entities.<p>
 * <p>
 * <b>Responsibility:</b> Maintains fire, fall, blast, and projectile
 * protection levels with validation methods.
 */
public class ProtectionStats {
    
    // -- Fields --
    
    private int fireProtection;
    private int fallProtection;
    private int blastProtection;
    private int projectileProtection;
    
    // -- Constructors --
    
    /**
     * Creates protection stats with default values (all zeros).
     */
    public ProtectionStats() {
        this(0, 0, 0, 0);
    }
    
    /**
     * Creates protection stats with specified values.
     * 
     * @param fireProtection fire protection level
     * @param fallProtection fall protection level
     * @param blastProtection blast protection level
     * @param projectileProtection projectile protection level
     */
    public ProtectionStats(int fireProtection, int fallProtection, 
                          int blastProtection, int projectileProtection) {
        this.fireProtection = fireProtection;
        this.fallProtection = fallProtection;
        this.blastProtection = blastProtection;
        this.projectileProtection = projectileProtection;
    }
    
    // -- Public Methods --
    
    /**
     * Gets the fire protection level.
     * 
     * @return fire protection
     */
    public int getFireProtection() {
        return fireProtection;
    }
    
    /**
     * Sets the fire protection level.
     * 
     * @param fireProtection new fire protection
     * @throws IllegalArgumentException if fireProtection < 0
     */
    public void setFireProtection(int fireProtection) {
        if (fireProtection < 0) {
            throw new IllegalArgumentException("Fire protection cannot be negative");
        }
        this.fireProtection = fireProtection;
    }
    
    /**
     * Gets the fall protection level.
     * 
     * @return fall protection
     */
    public int getFallProtection() {
        return fallProtection;
    }
    
    /**
     * Sets the fall protection level.
     * 
     * @param fallProtection new fall protection
     * @throws IllegalArgumentException if fallProtection < 0
     */
    public void setFallProtection(int fallProtection) {
        if (fallProtection < 0) {
            throw new IllegalArgumentException("Fall protection cannot be negative");
        }
        this.fallProtection = fallProtection;
    }
    
    /**
     * Gets the blast protection level.
     * 
     * @return blast protection
     */
    public int getBlastProtection() {
        return blastProtection;
    }
    
    /**
     * Sets the blast protection level.
     * 
     * @param blastProtection new blast protection
     * @throws IllegalArgumentException if blastProtection < 0
     */
    public void setBlastProtection(int blastProtection) {
        if (blastProtection < 0) {
            throw new IllegalArgumentException("Blast protection cannot be negative");
        }
        this.blastProtection = blastProtection;
    }
    
    /**
     * Gets the projectile protection level.
     * 
     * @return projectile protection
     */
    public int getProjectileProtection() {
        return projectileProtection;
    }
    
    /**
     * Sets the projectile protection level.
     * 
     * @param projectileProtection new projectile protection
     * @throws IllegalArgumentException if projectileProtection < 0
     */
    public void setProjectileProtection(int projectileProtection) {
        if (projectileProtection < 0) {
            throw new IllegalArgumentException("Projectile protection cannot be negative");
        }
        this.projectileProtection = projectileProtection;
    }
    
    /**
     * Checks if fire protection can be upgraded.
     * 
     * @param maxLevel maximum allowed fire protection level
     * @return true if upgrade is possible
     */
    public boolean canUpgradeFire(int maxLevel) {
        return fireProtection < maxLevel;
    }
    
    /**
     * Checks if fall protection can be upgraded.
     * 
     * @param maxLevel maximum allowed fall protection level
     * @return true if upgrade is possible
     */
    public boolean canUpgradeFall(int maxLevel) {
        return fallProtection < maxLevel;
    }
    
    /**
     * Checks if blast protection can be upgraded.
     * 
     * @param maxLevel maximum allowed blast protection level
     * @return true if upgrade is possible
     */
    public boolean canUpgradeBlast(int maxLevel) {
        return blastProtection < maxLevel;
    }
    
    /**
     * Checks if projectile protection can be upgraded.
     * 
     * @param maxLevel maximum allowed projectile protection level
     * @return true if upgrade is possible
     */
    public boolean canUpgradeProjectile(int maxLevel) {
        return projectileProtection < maxLevel;
    }
    
    /**
     * Validates that all protection levels are non-negative.
     * 
     * @return true if stats are valid
     */
    public boolean isValid() {
        return fireProtection >= 0 
            && fallProtection >= 0 
            && blastProtection >= 0 
            && projectileProtection >= 0;
    }
    
} // Class: ProtectionStats
