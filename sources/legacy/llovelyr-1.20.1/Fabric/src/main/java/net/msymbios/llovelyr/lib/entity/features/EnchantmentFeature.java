package net.msymbios.llovelyr.lib.entity.features;

import net.msymbios.llovelyr.framework.entity.enchantment.DefaultEnchantmentStrategy;
import net.msymbios.llovelyr.framework.entity.enchantment.EnchantmentCalculationStrategy;

import java.util.Objects;

/**
 * <p>Calculates enchantment levels based on robot level using pluggable strategies.</p>
 * <p>
 * <b>Architecture:</b> Bridges framework layer enchantment strategies with Minecraft's
 * enchantment system. Enables different robot types to have distinct enchantment progression
 * rates and maximum levels without modifying entity code.
 * <p>
 * <b>Design Decision:</b> Each enchantment type (looting, sharpness, knockback) has
 * independent enable/disable flags and maximum levels, allowing fine-grained control
 * over robot capabilities per type.
 * <p>
 * <b>State Management:</b> All calculations are stateless and deterministic - same inputs
 * always produce same outputs. Configuration changes require entities to recalculate
 * enchantment levels.
 */
public class EnchantmentFeature {

    // -- Fields --
    
    private EnchantmentCalculationStrategy strategy;
    
    // Looting configuration
    private boolean lootingEnabled;
    private int maxLootingLevel;
    private int lootingLevelDivisor;
    
    // Sharpness configuration
    private boolean sharpnessEnabled;
    private int maxSharpnessLevel;
    private int sharpnessLevelDivisor;
    
    // Knockback configuration
    private boolean knockbackEnabled;
    private int maxKnockbackLevel;
    private int knockbackLevelDivisor;

    // -- Constructors --
    
    /**
     * Creates enchantment feature with default configuration and strategy.
     * <p>
     * <b>Initial State:</b>
     * <ul>
     * <li>All enchantments enabled</li>
     * <li>Max levels: looting=3, sharpness=5, knockback=2</li>
     * <li>Divisors: looting=10, sharpness=10, knockback=20</li>
     * <li>Uses DefaultEnchantmentStrategy</li>
     * </ul>
     */
    public EnchantmentFeature() {
        this(new DefaultEnchantmentStrategy());
    }

    /**
     * Creates enchantment feature with custom strategy and default configuration.
     * <p>
     * <b>Initial State:</b> Same defaults as no-arg constructor with custom strategy
     * 
     * @param strategy enchantment calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public EnchantmentFeature(EnchantmentCalculationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "EnchantmentCalculationStrategy cannot be null");
        
        // Default looting configuration
        this.lootingEnabled = true;
        this.maxLootingLevel = 3;
        this.lootingLevelDivisor = 10;
        
        // Default sharpness configuration
        this.sharpnessEnabled = true;
        this.maxSharpnessLevel = 5;
        this.sharpnessLevelDivisor = 10;
        
        // Default knockback configuration
        this.knockbackEnabled = true;
        this.maxKnockbackLevel = 2;
        this.knockbackLevelDivisor = 20;
    }

    public EnchantmentFeature withLooting(boolean enabled, int maxLevel, int enchantmentLevel) {
        this.setLootingEnabled(enabled);
        this.setMaxLootingLevel(maxLevel);
        this.setLootingLevelDivisor(enchantmentLevel);
        return (EnchantmentFeature) this;
    } // withLooting ()

    // -- Calculation Methods --
    
    /**
     * Calculates looting enchantment level for specified robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns 0 if looting is disabled. Caps result at configured maximum.
     * <p>
     * <b>Formula:</b> Delegates to strategy, typically robotLevel / divisor, capped at max
     * 
     * @param robotLevel robot level (0 or higher)
     * @return calculated looting level (0 to maxLootingLevel)
     */
    public int calculateLooting(int robotLevel) {
        if (!lootingEnabled) {
            return 0;
        }
        int calculated = strategy.calculateLooting(robotLevel);
        return Math.min(calculated, maxLootingLevel);
    } // calculateLooting ()

    /**
     * Calculates sharpness enchantment level for specified robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns 0 if sharpness is disabled. Caps result at configured maximum.
     * <p>
     * <b>Formula:</b> Delegates to strategy, typically robotLevel / divisor, capped at max
     * 
     * @param robotLevel robot level (0 or higher)
     * @return calculated sharpness level (0 to maxSharpnessLevel)
     */
    public int calculateSharpness(int robotLevel) {
        if (!sharpnessEnabled) {
            return 0;
        }
        int calculated = strategy.calculateSharpness(robotLevel);
        return Math.min(calculated, maxSharpnessLevel);
    }

    /**
     * Calculates knockback enchantment level for specified robot level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Returns 0 if knockback is disabled. Caps result at configured maximum.
     * <p>
     * <b>Formula:</b> Delegates to strategy, typically robotLevel / divisor, capped at max
     * 
     * @param robotLevel robot level (0 or higher)
     * @return calculated knockback level (0 to maxKnockbackLevel)
     */
    public int calculateKnockback(int robotLevel) {
        if (!knockbackEnabled) {
            return 0;
        }
        int calculated = strategy.calculateKnockback(robotLevel);
        return Math.min(calculated, maxKnockbackLevel);
    }

    // -- Runtime Configuration - Looting --
    
    /**
     * Enables or disables looting enchantment.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param enabled true to enable looting, false to disable
     */
    public void setLootingEnabled(boolean enabled) {
        this.lootingEnabled = enabled;
    }

    /**
     * Updates maximum looting level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum looting level (clamped to 0 minimum)
     */
    public void setMaxLootingLevel(int max) {
        this.maxLootingLevel = Math.max(0, max);
    }

    /**
     * Updates looting level divisor.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Higher divisor means slower progression.
     * 
     * @param divisor new looting divisor (clamped to 1 minimum)
     */
    public void setLootingLevelDivisor(int divisor) {
        this.lootingLevelDivisor = Math.max(1, divisor);
    }

    // -- Runtime Configuration - Sharpness --
    
    /**
     * Enables or disables sharpness enchantment.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param enabled true to enable sharpness, false to disable
     */
    public void setSharpnessEnabled(boolean enabled) {
        this.sharpnessEnabled = enabled;
    }

    /**
     * Updates maximum sharpness level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum sharpness level (clamped to 0 minimum)
     */
    public void setMaxSharpnessLevel(int max) {
        this.maxSharpnessLevel = Math.max(0, max);
    }

    /**
     * Updates sharpness level divisor.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Higher divisor means slower progression.
     * 
     * @param divisor new sharpness divisor (clamped to 1 minimum)
     */
    public void setSharpnessLevelDivisor(int divisor) {
        this.sharpnessLevelDivisor = Math.max(1, divisor);
    }

    // -- Runtime Configuration - Knockback --
    
    /**
     * Enables or disables knockback enchantment.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param enabled true to enable knockback, false to disable
     */
    public void setKnockbackEnabled(boolean enabled) {
        this.knockbackEnabled = enabled;
    }

    /**
     * Updates maximum knockback level.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * 
     * @param max new maximum knockback level (clamped to 0 minimum)
     */
    public void setMaxKnockbackLevel(int max) {
        this.maxKnockbackLevel = Math.max(0, max);
    }

    /**
     * Updates knockback level divisor.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Higher divisor means slower progression.
     * 
     * @param divisor new knockback divisor (clamped to 1 minimum)
     */
    public void setKnockbackLevelDivisor(int divisor) {
        this.knockbackLevelDivisor = Math.max(1, divisor);
    }

    // -- Strategy Configuration --
    
    /**
     * Updates enchantment calculation strategy.
     * <p>
     * <b>Design Decision:</b> Strategy can be changed at runtime to support dynamic
     * enchantment progression adjustments. Existing entities should recalculate
     * enchantment levels after strategy changes.
     * 
     * @param strategy new calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public void setStrategy(EnchantmentCalculationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "EnchantmentCalculationStrategy cannot be null");
    }

    // -- Getters --
    
    /**
     * @return true if looting enchantment is enabled
     */
    public boolean isLootingEnabled() {
        return lootingEnabled;
    }

    /**
     * @return maximum looting level
     */
    public int getMaxLootingLevel() {
        return maxLootingLevel;
    }

    /**
     * @return looting level divisor
     */
    public int getLootingLevelDivisor() {
        return lootingLevelDivisor;
    }

    /**
     * @return true if sharpness enchantment is enabled
     */
    public boolean isSharpnessEnabled() {
        return sharpnessEnabled;
    }

    /**
     * @return maximum sharpness level
     */
    public int getMaxSharpnessLevel() {
        return maxSharpnessLevel;
    }

    /**
     * @return sharpness level divisor
     */
    public int getSharpnessLevelDivisor() {
        return sharpnessLevelDivisor;
    }

    /**
     * @return true if knockback enchantment is enabled
     */
    public boolean isKnockbackEnabled() {
        return knockbackEnabled;
    }

    /**
     * @return maximum knockback level
     */
    public int getMaxKnockbackLevel() {
        return maxKnockbackLevel;
    }

    /**
     * @return knockback level divisor
     */
    public int getKnockbackLevelDivisor() {
        return knockbackLevelDivisor;
    }

    /**
     * @return current calculation strategy
     */
    public EnchantmentCalculationStrategy getStrategy() {
        return strategy;
    }

} // Class: EnchantmentFeature
