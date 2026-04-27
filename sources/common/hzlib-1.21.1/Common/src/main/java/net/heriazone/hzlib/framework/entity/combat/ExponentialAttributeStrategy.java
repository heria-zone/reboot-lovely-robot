package net.heriazone.hzlib.framework.entity.combat;

/**
 * <p>Implements exponential attribute scaling for powerful robot types.<p>
 * <p>
 * <b>Formula:</b> attribute = baseValue * (multiplier ^ level)
 * <p>
 * <b>Use Case:</b> Dragon and other advanced robots that should scale
 * more aggressively than standard robots.
 * <p>
 * <b>Performance:</b> Uses Math.pow for exponential calculation. For very high
 * levels, consider caching or alternative approaches.
 */
public class ExponentialAttributeStrategy implements IAttributeCalculationStrategy {

    // -- Constants --

    private static final double DEFAULT_MULTIPLIER = 1.05; // 5% growth per level
    private static final double ARMOR_MULTIPLIER = 0.5;
    private static final double ARMOR_TOUGHNESS_MULTIPLIER = 0.25;

    // -- Variables --

    private final double multiplier;

    // -- Constructors --

    /**
     * Creates exponential strategy with default 5% growth per level.
     */
    public ExponentialAttributeStrategy() {
        this(DEFAULT_MULTIPLIER);
    } // Constructor: ExponentialAttributeStrategy ()

    /**
     * Creates exponential strategy with custom growth multiplier.
     *
     * @param multiplier growth multiplier per level (e.g., 1.05 for 5% growth)
     * @throws IllegalArgumentException if multiplier <= 1.0
     */
    public ExponentialAttributeStrategy(double multiplier) {
        if (multiplier <= 1.0) {
            throw new IllegalArgumentException("Multiplier must be greater than 1.0");
        }
        this.multiplier = multiplier;
    } // Constructor: ExponentialAttributeStrategy ()

    // -- Inherited Methods --

    @Override
    public int calculateHp(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    } // calculateHp ()

    @Override
    public int calculateAttack(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    } // calculateAttack ()

    @Override
    public int calculateDefense(int level, int baseValue) {
        return (int) (baseValue * Math.pow(multiplier, level));
    } // calculateDefense ()

    @Override
    public double calculateArmor(int defense) {
        return defense * ARMOR_MULTIPLIER;
    } // calculateArmor ()

    @Override
    public double calculateArmorToughness(double armorLevel) {
        return armorLevel * ARMOR_TOUGHNESS_MULTIPLIER;
    } // calculateArmorToughness ()

    // -- Custom Methods --

    /**
     * Gets the growth multiplier used by this strategy.
     *
     * @return growth multiplier
     */
    public double getMultiplier() {
        return multiplier;
    } // getMultiplier ()

} // Class: ExponentialAttributeStrategy