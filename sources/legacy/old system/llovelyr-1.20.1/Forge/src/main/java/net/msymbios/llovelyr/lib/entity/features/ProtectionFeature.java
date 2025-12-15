package net.msymbios.llovelyr.lib.entity.features;

import net.msymbios.llovelyr.framework.entity.protection.LevelBasedProtectionStrategy;
import net.msymbios.llovelyr.framework.entity.protection.ProtectionCalculationStrategy;

import java.util.Objects;

/**
 * <p>Validates and auto-unlocks protection levels based on robot level.</p>
 * <p>
 * <b>Architecture:</b> Bridges framework layer protection strategies with Minecraft's
 * damage protection system. Provides maximum protection limits for validation and
 * optional automatic unlocking at level thresholds.
 * <p>
 * <b>Design Decision:</b> Each protection type (fire, fall, blast, projectile) has
 * independent maximum levels, allowing fine-grained control over robot defensive
 * capabilities per type. Auto-upgrade feature enables level-gated progression.
 * <p>
 * <b>State Management:</b> All calculations are stateless and deterministic - same inputs
 * always produce same outputs. Configuration changes require entities to recalculate
 * protection levels.
 */
public class ProtectionFeature {

    // -- Fields --
    
    private ProtectionCalculationStrategy strategy;
    
    // Maximum protection levels
    private int maxFireProtection;
    private int maxFallProtection;
    private int maxBlastProtection;
    private int maxProjectileProtection;
    
    // Auto-upgrade configuration
    private boolean autoUpgradeEnabled;

    // -- Constructors --
    
    /**
     * Creates protection feature with default configuration and strategy.
     * <p>
     * <b>Initial State:</b>
     * <ul>
     * <li>Max levels: fire=4, fall=4, blast=4, projectile=4</li>
     * <li>Auto-upgrade disabled</li>
     * <li>Uses LevelBasedProtectionStrategy</li>
     * </ul>
     */
    public ProtectionFeature() {
        this(new LevelBasedProtectionStrategy());
    }

    /**
     * Creates protection feature with custom strategy and default configuration.
     * <p>
     * <b>Initial State:</b> Same defaults as no-arg constructor with custom strategy
     * 
     * @param strategy protection calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public ProtectionFeature(ProtectionCalculationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "ProtectionCalculationStrategy cannot be null");
        
        // Default maximum levels (Minecraft standard)
        this.maxFireProtection = 4;
        this.maxFallProtection = 4;
        this.maxBlastProtection = 4;
        this.maxProjectileProtection = 4;
        
        // Auto-upgrade disabled by default
        this.autoUpgradeEnabled = false;
    }

    public ProtectionFeature withMax(int maxFire, int maxFall, int maxBlast, int maxProjectile) {
        this.setMaxFireProtection(maxFire);
        this.setMaxFallProtection(maxFall);
        this.setMaxBlastProtection(maxBlast);
        this.setMaxProjectileProtection(maxProjectile);
        return (ProtectionFeature) this;
    } // withMax ()

    // -- Validation Methods --
    
    /**
     * Checks if fire protection can be upgraded from current level.
     * <p>
     * <b>Contract:</b> Returns true if currentLevel < maxFireProtection
     * 
     * @param currentLevel current fire protection level
     * @return true if upgrade is possible, false otherwise
     */
    public boolean canUpgradeFireProtection(int currentLevel) {
        return currentLevel < maxFireProtection;
    }

    /**
     * Checks if fall protection can be upgraded from current level.
     * <p>
     * <b>Contract:</b> Returns true if currentLevel < maxFallProtection
     * 
     * @param currentLevel current fall protection level
     * @return true if upgrade is possible, false otherwise
     */
    public boolean canUpgradeFallProtection(int currentLevel) {
        return currentLevel < maxFallProtection;
    }

    /**
     * Checks if blast protection can be upgraded from current level.
     * <p>
     * <b>Contract:</b> Returns true if currentLevel < maxBlastProtection
     * 
     * @param currentLevel current blast protection level
     * @return true if upgrade is possible, false otherwise
     */
    public boolean canUpgradeBlastProtection(int currentLevel) {
        return currentLevel < maxBlastProtection;
    }

    /**
     * Checks if projectile protection can be upgraded from current level.
     * <p>
     * <b>Contract:</b> Returns true if currentLevel < maxProjectileProtection
     * 
     * @param currentLevel current projectile protection level
     * @return true if upgrade is possible, false otherwise
     */
    public boolean canUpgradeProjectileProtection(int currentLevel) {
        return currentLevel < maxProjectileProtection;
    }

    // -- Auto-Upgrade Calculation Methods --
    
    /**
     * Calculates fire protection level for auto-upgrade based on robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns currentProtection if auto-upgrade disabled. Caps result at configured maximum.
     * <p>
     * <b>Design Decision:</b> Auto-upgrade only increases protection, never decreases.
     * 
     * @param robotLevel robot level (0 or higher)
     * @param currentProtection current fire protection level
     * @return calculated fire protection level (currentProtection to maxFireProtection)
     */
    public int calculateAutoFireProtection(int robotLevel, int currentProtection) {
        if (!autoUpgradeEnabled) {
            return currentProtection;
        }
        int calculated = strategy.calculateFireProtection(robotLevel, currentProtection);
        return Math.min(Math.max(calculated, currentProtection), maxFireProtection);
    }

    /**
     * Calculates fall protection level for auto-upgrade based on robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns currentProtection if auto-upgrade disabled. Caps result at configured maximum.
     * <p>
     * <b>Design Decision:</b> Auto-upgrade only increases protection, never decreases.
     * 
     * @param robotLevel robot level (0 or higher)
     * @param currentProtection current fall protection level
     * @return calculated fall protection level (currentProtection to maxFallProtection)
     */
    public int calculateAutoFallProtection(int robotLevel, int currentProtection) {
        if (!autoUpgradeEnabled) {
            return currentProtection;
        }
        int calculated = strategy.calculateFallProtection(robotLevel, currentProtection);
        return Math.min(Math.max(calculated, currentProtection), maxFallProtection);
    }

    /**
     * Calculates blast protection level for auto-upgrade based on robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns currentProtection if auto-upgrade disabled. Caps result at configured maximum.
     * <p>
     * <b>Design Decision:</b> Auto-upgrade only increases protection, never decreases.
     * 
     * @param robotLevel robot level (0 or higher)
     * @param currentProtection current blast protection level
     * @return calculated blast protection level (currentProtection to maxBlastProtection)
     */
    public int calculateAutoBlastProtection(int robotLevel, int currentProtection) {
        if (!autoUpgradeEnabled) {
            return currentProtection;
        }
        int calculated = strategy.calculateBlastProtection(robotLevel, currentProtection);
        return Math.min(Math.max(calculated, currentProtection), maxBlastProtection);
    }

    /**
     * Calculates projectile protection level for auto-upgrade based on robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns currentProtection if auto-upgrade disabled. Caps result at configured maximum.
     * <p>
     * <b>Design Decision:</b> Auto-upgrade only increases protection, never decreases.
     * 
     * @param robotLevel robot level (0 or higher)
     * @param currentProtection current projectile protection level
     * @return calculated projectile protection level (currentProtection to maxProjectileProtection)
     */
    public int calculateAutoProjectileProtection(int robotLevel, int currentProtection) {
        if (!autoUpgradeEnabled) {
            return currentProtection;
        }
        int calculated = strategy.calculateProjectileProtection(robotLevel, currentProtection);
        return Math.min(Math.max(calculated, currentProtection), maxProjectileProtection);
    }

    // -- Runtime Configuration --
    
    /**
     * Updates maximum fire protection level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum fire protection level (clamped to 0 minimum)
     */
    public void setMaxFireProtection(int max) {
        this.maxFireProtection = Math.max(0, max);
    }

    /**
     * Updates maximum fall protection level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum fall protection level (clamped to 0 minimum)
     */
    public void setMaxFallProtection(int max) {
        this.maxFallProtection = Math.max(0, max);
    }

    /**
     * Updates maximum blast protection level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum blast protection level (clamped to 0 minimum)
     */
    public void setMaxBlastProtection(int max) {
        this.maxBlastProtection = Math.max(0, max);
    }

    /**
     * Updates maximum projectile protection level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum projectile protection level (clamped to 0 minimum)
     */
    public void setMaxProjectileProtection(int max) {
        this.maxProjectileProtection = Math.max(0, max);
    }

    /**
     * Enables or disables automatic protection upgrades.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * When enabled, protections automatically increase at level thresholds.
     * 
     * @param enabled true to enable auto-upgrade, false to disable
     */
    public void setAutoUpgradeEnabled(boolean enabled) {
        this.autoUpgradeEnabled = enabled;
    }

    /**
     * Updates protection calculation strategy.
     * <p>
     * <b>Design Decision:</b> Strategy can be changed at runtime to support dynamic
     * protection progression adjustments. Existing entities should recalculate
     * protection levels after strategy changes.
     * 
     * @param strategy new calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public void setStrategy(ProtectionCalculationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "ProtectionCalculationStrategy cannot be null");
    }

    // -- Getters --
    
    /**
     * @return maximum fire protection level
     */
    public int getMaxFireProtection() {
        return maxFireProtection;
    }

    /**
     * @return maximum fall protection level
     */
    public int getMaxFallProtection() {
        return maxFallProtection;
    }

    /**
     * @return maximum blast protection level
     */
    public int getMaxBlastProtection() {
        return maxBlastProtection;
    }

    /**
     * @return maximum projectile protection level
     */
    public int getMaxProjectileProtection() {
        return maxProjectileProtection;
    }

    /**
     * @return true if auto-upgrade is enabled
     */
    public boolean isAutoUpgradeEnabled() {
        return autoUpgradeEnabled;
    }

    /**
     * @return current calculation strategy
     */
    public ProtectionCalculationStrategy getStrategy() {
        return strategy;
    }

} // Class: ProtectionFeature
