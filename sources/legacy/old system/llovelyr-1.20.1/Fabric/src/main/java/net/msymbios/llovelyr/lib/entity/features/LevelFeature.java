package net.msymbios.llovelyr.lib.entity.features;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

/**
 * <p>Manages entity leveling, experience progression, and configurable XP strategies.</p>
 * <p>
 * <b>Architecture:</b> Provides composable leveling system that can be attached to any entity type
 * through the feature system. Decouples level progression logic from entity implementation,
 * enabling different robot types to have distinct progression curves.
 * <p>
 * <b>Design Decision:</b> XP strategies are pluggable to support varied progression patterns
 * (linear, exponential, custom formulas) without modifying core leveling logic.
 * <p>
 * <b>State Management:</b> Maintains level and experience independently, with validation
 * ensuring currentLevel never exceeds maxLevel and experience values remain non-negative.
 */
public class LevelFeature {

    // -- Fields --
    
    private int maxLevel;
    private int currentLevel;
    private int experience;
    private ExpCalculationStrategy expStrategy;

    // -- Constructors --
    
    /**
     * Creates level feature with specified max level and default linear XP strategy.
     * <p>
     * <b>Initial State:</b> currentLevel = 0, experience = 0
     * 
     * @param maxLevel maximum achievable level (clamped to 0 if negative)
     */
    public LevelFeature(int maxLevel) {
        this(maxLevel, new DefaultExpStrategy());
    }

    /**
     * Creates level feature with specified max level and custom XP strategy.
     * <p>
     * <b>Initial State:</b> currentLevel = 0, experience = 0
     * 
     * @param maxLevel maximum achievable level (clamped to 0 if negative)
     * @param expStrategy XP calculation strategy (must not be null)
     * @throws NullPointerException if expStrategy is null
     */
    public LevelFeature(int maxLevel, ExpCalculationStrategy expStrategy) {
        this.maxLevel = Math.max(0, maxLevel);
        this.currentLevel = 0;
        this.experience = 0;
        this.expStrategy = Objects.requireNonNull(expStrategy, "ExpCalculationStrategy cannot be null");
    }

    // -- Getters and Setters --
    
    /**
     * @return maximum achievable level
     */
    public int getMaxLevel() {
        return this.maxLevel;
    }

    /**
     * Updates maximum level and clamps current level if it exceeds new maximum.
     * <p>
     * <b>State Impact:</b> If currentLevel > newMaxLevel, currentLevel is reduced to newMaxLevel.
     * Experience is preserved.
     * 
     * @param maxLevel new maximum level (clamped to 0 if negative)
     */
    public void setMaxLevel(int maxLevel) {
        this.maxLevel = Math.max(0, maxLevel);
        if (this.currentLevel > this.maxLevel) {
            this.currentLevel = this.maxLevel;
        }
    }

    /**
     * @return current level (0 to maxLevel)
     */
    public int getCurrentLevel() {
        return this.currentLevel;
    }

    /**
     * Sets current level with validation.
     * <p>
     * <b>Validation:</b> Level is clamped to [0, maxLevel] range.
     * 
     * @param level new current level
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = Math.max(0, Math.min(level, this.maxLevel));
    }

    /**
     * @return accumulated experience points
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Sets experience with validation.
     * <p>
     * <b>Validation:</b> Experience is clamped to non-negative values.
     * 
     * @param exp new experience value
     */
    public void setExperience(int exp) {
        this.experience = Math.max(0, exp);
    }

    /**
     * Adds experience points to current total.
     * <p>
     * <b>State Impact:</b> Increases experience by specified amount. Does not automatically
     * trigger level up - caller must invoke <code>tryLevelUp()</code>.
     * 
     * @param exp experience to add (negative values are ignored)
     */
    public void addExperience(int exp) {
        if (exp > 0) {
            this.experience += exp;
        }
    }

    // -- Level Progression Logic --
    
    /**
     * Calculates experience required for next level.
     * <p>
     * <b>Performance:</b> Delegates to configured strategy. Result consistency
     * depends on strategy implementation.
     * 
     * @return XP required to reach next level, or 0 if at max level
     */
    public int getExpForNextLevel() {
        if (this.currentLevel >= this.maxLevel) {
            return 0;
        }
        return this.expStrategy.calculateExpForLevel(this.currentLevel + 1);
    }

    /**
     * Calculates experience required for specific level.
     * <p>
     * <b>Use Case:</b> Useful for displaying progression requirements or
     * calculating total XP needed for multiple levels.
     * 
     * @param level target level
     * @return XP required to reach specified level
     */
    public int getExpForLevel(int level) {
        return this.expStrategy.calculateExpForLevel(level);
    }

    /**
     * Checks if entity can level up with current experience.
     * <p>
     * <b>Conditions:</b> Returns true if:
     * <ul>
     * <li>currentLevel < maxLevel</li>
     * <li>experience >= getExpForNextLevel()</li>
     * </ul>
     * 
     * @return true if level up is possible, false otherwise
     */
    public boolean canLevelUp() {
        return this.currentLevel < this.maxLevel && 
               this.experience >= getExpForNextLevel();
    }

    /**
     * Attempts to level up, consuming required experience.
     * <p>
     * <b>State Impact:</b> If successful:
     * <ul>
     * <li>currentLevel increases by 1</li>
     * <li>experience decreases by getExpForNextLevel() amount</li>
     * </ul>
     * If unsuccessful, state remains unchanged.
     * <p>
     * <b>Design Decision:</b> XP is consumed rather than reset to enable
     * overflow XP to carry forward to next level.
     * 
     * @return true if level up succeeded, false if conditions not met
     */
    public boolean tryLevelUp() {
        if (!canLevelUp()) {
            return false;
        }
        
        int requiredExp = getExpForNextLevel();
        this.experience -= requiredExp;
        this.currentLevel++;
        return true;
    }

    // -- XP Strategy Management --
    
    /**
     * Updates XP calculation strategy.
     * <p>
     * <b>Design Decision:</b> Strategy can be changed at runtime to support dynamic
     * progression adjustments (e.g., harder progression after certain milestones).
     * 
     * @param strategy new XP calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public void setExpStrategy(ExpCalculationStrategy strategy) {
        this.expStrategy = Objects.requireNonNull(strategy, "ExpCalculationStrategy cannot be null");
    }

    // -- XP Calculation Strategy Interface --
    
    /**
     * <p>Defines contract for calculating experience requirements per level.</p>
     * <p>
     * <b>Design Intent:</b> Enables pluggable progression curves without modifying
     * core leveling logic. Implementations can provide linear, exponential, or
     * custom formula-based calculations.
     */
    public interface ExpCalculationStrategy {
        /**
         * Calculates experience required to reach specified level.
         * <p>
         * <b>Contract:</b> Must return consistent values for same level input.
         * Should handle edge cases (level 0, negative levels) gracefully.
         * 
         * @param level target level
         * @return experience required to reach level
         */
        int calculateExpForLevel(int level);
    }

    // -- Default XP Strategies --
    
    /**
     * <p>Linear XP progression: level * 100</p>
     * <p>
     * <b>Progression Curve:</b> Constant increase per level. Level 1 requires 100 XP,
     * level 2 requires 200 XP, etc.
     */
    public static class DefaultExpStrategy implements ExpCalculationStrategy {
        @Override
        public int calculateExpForLevel(int level) {
            return Math.max(0, level * 100);
        }
    } // Class: DefaultExpStrategy

    /**
     * <p>Exponential XP progression: 100 * base^(level/10)</p>
     * <p>
     * <b>Progression Curve:</b> Exponentially increasing requirements. Suitable for
     * long-term progression where later levels become significantly harder.
     * <p>
     * <b>Performance:</b> Uses Math.pow() which may be expensive in tight loops.
     */
    public static class ExponentialExpStrategy implements ExpCalculationStrategy {
        private final double base;

        /**
         * Creates exponential strategy with specified base.
         * 
         * @param base exponential base (e.g., 1.1 for 10% increase per level)
         */
        public ExponentialExpStrategy(double base) {
            this.base = base;
        }

        @Override
        public int calculateExpForLevel(int level) {
            if (level <= 0) return 0;
            double result = 100.0 * Math.pow(base, level / 10.0);
            // Clamp to Integer.MAX_VALUE to prevent overflow
            return result > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) result;
        }
    } // Class: ExponentialExpStrategy

    /**
     * <p>Custom formula-based XP progression using IntUnaryOperator.</p>
     * <p>
     * <b>Design Intent:</b> Maximum flexibility for custom progression curves.
     * Enables complex formulas like milestone-based progression (e.g., Kitsune tail unlocks).
     */
    public static class FormulaExpStrategy implements ExpCalculationStrategy {
        private final IntUnaryOperator formula;

        /**
         * Creates formula strategy with custom calculation function.
         * 
         * @param formula function mapping level to required XP (must not be null)
         * @throws NullPointerException if formula is null
         */
        public FormulaExpStrategy(IntUnaryOperator formula) {
            this.formula = Objects.requireNonNull(formula, "Formula cannot be null");
        }

        @Override
        public int calculateExpForLevel(int level) {
            return Math.max(0, formula.applyAsInt(level));
        }
    } // Class: FormulaExpStrategy

} // Class: LevelFeature
