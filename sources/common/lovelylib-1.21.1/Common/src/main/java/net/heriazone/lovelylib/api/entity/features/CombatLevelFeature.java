package net.heriazone.lovelylib.api.entity.features;

import net.heriazone.lovelylib.common.entity.combat.LinearAttributeStrategy;
import net.heriazone.lovelylib.hzlib.framework.entity.combat.IAttributeCalculationStrategy;

import java.util.Objects;

/**
 * <p>Calculates combat attributes based on robot level using pluggable strategies.</p>
 * <p>
 * <b>Architecture:</b> Bridges framework layer calculation strategies with Minecraft entity
 * attribute system. Enables different robot types to use distinct scaling formulas (linear,
 * exponential, custom) without modifying entity code.
 * <p>
 * <b>Design Decision:</b> Strategy pattern allows runtime configuration changes and
 * per-type customization. Base values are configurable to support different robot tiers
 * (vanilla, dragon, kitsune) with appropriate starting stats.
 * <p>
 * <b>State Management:</b> All calculations are stateless and deterministic - same inputs
 * always produce same outputs. This ensures consistency across client-server synchronization
 * and save/load cycles.
 */
public class CombatLevelFeature {

    // -- Variables --

    private IAttributeCalculationStrategy strategy;
    private int baseHp;
    private int baseAttack;
    private int baseDefense;

    // -- Constructors --

    /**
     * Creates combat level feature with specified base values and default linear strategy.
     * <p>
     * <b>Initial State:</b> Uses LinearAttributeStrategy with 2% growth per level
     *
     * @param baseHp base health points (clamped to 1 minimum)
     * @param baseAttack base attack damage (clamped to 0 minimum)
     * @param baseDefense base defense value (clamped to 0 minimum)
     */
    public CombatLevelFeature(int baseHp, int baseAttack, int baseDefense) {
        this(baseHp, baseAttack, baseDefense, new LinearAttributeStrategy());
    } // Constructor: CombatLevelFeature ()

    /**
     * Creates combat level feature with specified base values and custom strategy.
     * <p>
     * <b>Initial State:</b> Uses provided strategy for all calculations
     *
     * @param baseHp base health points (clamped to 1 minimum)
     * @param baseAttack base attack damage (clamped to 0 minimum)
     * @param baseDefense base defense value (clamped to 0 minimum)
     * @param strategy attribute calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public CombatLevelFeature(int baseHp, int baseAttack, int baseDefense, IAttributeCalculationStrategy strategy) {
        this.baseHp = Math.max(1, baseHp);
        this.baseAttack = Math.max(0, baseAttack);
        this.baseDefense = Math.max(0, baseDefense);
        this.strategy = Objects.requireNonNull(strategy, "IAttributeCalculationStrategy cannot be null");
    } // Constructor: CombatLevelFeature ()

    // -- Calculation Methods --

    /**
     * Calculates maximum health points for specified level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Delegates to configured strategy for scaling formula.
     *
     * @param level robot level (0 or higher)
     * @return calculated maximum HP
     */
    public int calculateHp(int level) {
        return strategy.calculateHp(level, baseHp);
    } // calculateHp ()

    /**
     * Calculates attack damage for specified level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Delegates to configured strategy for scaling formula.
     *
     * @param level robot level (0 or higher)
     * @return calculated attack damage
     */
    public int calculateAttack(int level) {
        return strategy.calculateAttack(level, baseAttack);
    } // calculateAttack ()

    /**
     * Calculates defense value for specified level.
     * <p>
     * <b>Contract:</b> Deterministic - same level always produces same result.
     * Delegates to configured strategy for scaling formula.
     *
     * @param level robot level (0 or higher)
     * @return calculated defense value
     */
    public int calculateDefense(int level) {
        return strategy.calculateDefense(level, baseDefense);
    } // calculateDefense ()

    /**
     * Calculates armor points based on defense value.
     * <p>
     * <b>Design Decision:</b> Armor is derived from defense rather than level directly,
     * allowing defense to serve as intermediate stat that influences multiple attributes.
     * Delegates to strategy for conversion formula.
     *
     * @param level robot level (0 or higher)
     * @return calculated armor points
     */
    public double calculateArmor(int level) {
        int defense = calculateDefense(level);
        return strategy.calculateArmor(defense);
    } // calculateArmor ()

    /**
     * Calculates armor toughness based on armor level.
     * <p>
     * <b>Design Decision:</b> Toughness is derived from armor, creating a progression
     * where higher armor also provides better damage reduction. Delegates to strategy
     * for conversion formula.
     *
     * @param level robot level (0 or higher)
     * @return calculated armor toughness
     */
    public double calculateArmorToughness(int level) {
        double armor = calculateArmor(level);
        return strategy.calculateArmorToughness(armor);
    } // calculateArmorToughness ()

    // -- Runtime Configuration --

    /**
     * Updates base health points.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Existing entities should recalculate attributes after base value changes.
     *
     * @param baseHp new base health (clamped to 1 minimum)
     */
    public void setBaseHp(int baseHp) {
        this.baseHp = Math.max(1, baseHp);
    } // setBaseHp (0

    /**
     * Updates base attack damage.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Existing entities should recalculate attributes after base value changes.
     *
     * @param baseAttack new base attack (clamped to 0 minimum)
     */
    public void setBaseAttack(int baseAttack) {
        this.baseAttack = Math.max(0, baseAttack);
    } // setBaseAttack ()

    /**
     * Updates base defense value.
     * <p>
     * <b>Use Case:</b> Enables runtime configuration changes from config reload.
     * Existing entities should recalculate attributes after base value changes.
     *
     * @param baseDefense new base defense (clamped to 0 minimum)
     */
    public void setBaseDefense(int baseDefense) {
        this.baseDefense = Math.max(0, baseDefense);
    } // setBaseDefense ()

    /**
     * Updates calculation strategy.
     * <p>
     * <b>Design Decision:</b> Strategy can be changed at runtime to support dynamic
     * scaling adjustments (e.g., switching from linear to exponential at high levels).
     * Existing entities should recalculate attributes after strategy changes.
     *
     * @param strategy new calculation strategy (must not be null)
     * @throws NullPointerException if strategy is null
     */
    public void setStrategy(IAttributeCalculationStrategy strategy) {
        this.strategy = Objects.requireNonNull(strategy, "IAttributeCalculationStrategy cannot be null");
    } // setStrategy ()

    // -- Getters --

    /**
     * @return current base health points
     */
    public int getBaseHp() {
        return baseHp;
    } // getBaseHp ()

    /**
     * @return current base attack damage
     */
    public int getBaseAttack() {
        return baseAttack;
    } // getBaseAttack ()

    /**
     * @return current base defense value
     */
    public int getBaseDefense() {
        return baseDefense;
    } // getBaseDefense ()

    /**
     * @return current calculation strategy
     */
    public IAttributeCalculationStrategy getStrategy() {
        return strategy;
    } // getStrategy ()

} // Class: CombatLevelFeature