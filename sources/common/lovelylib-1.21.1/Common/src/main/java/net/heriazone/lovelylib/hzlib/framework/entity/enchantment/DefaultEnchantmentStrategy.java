package net.heriazone.lovelylib.hzlib.framework.entity.enchantment;

/**
 * <p>Implements default enchantment calculation using level-based divisors.<p>
 * <p>
 * <b>Formula:</b> enchantmentLevel = robotLevel / divisor
 * <p>
 * <b>Design Decision:</b> Maintains compatibility with existing looting formula
 * while providing configurable divisors for each enchantment type.
 */
public class DefaultEnchantmentStrategy implements IEnchantmentCalculationStrategy {

    // -- Constants --

    private static final int DEFAULT_LOOTING_DIVISOR = 10;
    private static final int DEFAULT_SHARPNESS_DIVISOR = 15;
    private static final int DEFAULT_KNOCKBACK_DIVISOR = 20;

    // -- Variables --

    private final int lootingDivisor;
    private final int sharpnessDivisor;
    private final int knockbackDivisor;

    // -- Constructors --

    /**
     * Creates default enchantment strategy with standard divisors.
     */
    public DefaultEnchantmentStrategy() {
        this(DEFAULT_LOOTING_DIVISOR, DEFAULT_SHARPNESS_DIVISOR, DEFAULT_KNOCKBACK_DIVISOR);
    } // Constructor: DefaultEnchantmentStrategy ()

    /**
     * Creates enchantment strategy with custom divisors.
     *
     * @param lootingDivisor divisor for looting calculation
     * @param sharpnessDivisor divisor for sharpness calculation
     * @param knockbackDivisor divisor for knockback calculation
     * @throws IllegalArgumentException if any divisor <= 0
     */
    public DefaultEnchantmentStrategy(int lootingDivisor, int sharpnessDivisor, int knockbackDivisor) {
        if (lootingDivisor <= 0 || sharpnessDivisor <= 0 || knockbackDivisor <= 0) {
            throw new IllegalArgumentException("Divisors must be greater than 0");
        }
        this.lootingDivisor = lootingDivisor;
        this.sharpnessDivisor = sharpnessDivisor;
        this.knockbackDivisor = knockbackDivisor;
    } // Constructor: DefaultEnchantmentStrategy ()

    // -- Inherited Methods --

    @Override
    public int calculateLooting(int level) {
        return level / lootingDivisor;
    } // calculateLooting ()

    @Override
    public int calculateSharpness(int level) {
        return level / sharpnessDivisor;
    } // calculateSharpness ()

    @Override
    public int calculateKnockback(int level) {
        return level / knockbackDivisor;
    } // calculateKnockback ()

    // -- Custom Methods --

    /**
     * Gets the looting divisor used by this strategy.
     *
     * @return looting divisor
     */
    public int getLootingDivisor() {
        return lootingDivisor;
    } // getLootingDivisor ()

    /**
     * Gets the sharpness divisor used by this strategy.
     *
     * @return sharpness divisor
     */
    public int getSharpnessDivisor() {
        return sharpnessDivisor;
    } // getSharpnessDivisor ()

    /**
     * Gets the knockback divisor used by this strategy.
     *
     * @return knockback divisor
     */
    public int getKnockbackDivisor() {
        return knockbackDivisor;
    } // getKnockbackDivisor ()

} // Class: DefaultEnchantmentStrategy