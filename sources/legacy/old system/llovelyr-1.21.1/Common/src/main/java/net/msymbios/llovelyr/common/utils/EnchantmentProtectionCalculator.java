package net.msymbios.llovelyr.common.utils;

import net.msymbios.llovelyr.common.Configs.SharedConfigs;

import java.util.Random;

/**
 * <p>Calculates protection value gains from enchanted book consumption.<p>
 * <p>
 * <b>Architecture:</b> Provides stateless calculation functions for converting
 * enchantment levels into robot protection points using percentage-based contribution.
 * <p>
 * <b>Formula:</b> protectionGain = enchantmentLevel × contributionPercentage × maxProtection
 * <p>
 * <b>Design Decision:</b> Percentage-based scaling ensures books remain valuable
 * regardless of config changes. A Fire Protection IV book always grants the same
 * proportion of maximum protection, whether max is 80, 50, or 25.
 * <p>
 * <b>Thread Safety:</b> All methods are stateless and thread-safe.
 */
public class EnchantmentProtectionCalculator {

    // -- Constants --

    private static final Random RANDOM = new Random();

    /**
     * Protection types for random selection when applying generic Protection enchantment.
     */
    public enum ProtectionType {
        FIRE,
        FALL,
        BLAST,
        PROJECTILE
    } // Enum: ProtectionType

    // -- Calculation Methods --

    /**
     * Calculates protection points gained from enchantment level.
     * <p>
     * <b>Formula:</b> gain = enchantmentLevel × contributionPercentage × maxProtection
     * <p>
     * <b>Examples</b> (with 25% contribution and max=80):
     * <ul>
     * <li>Level I: 1 × 0.25 × 80 = 20 points</li>
     * <li>Level II: 2 × 0.25 × 80 = 40 points</li>
     * <li>Level III: 3 × 0.25 × 80 = 60 points</li>
     * <li>Level IV: 4 × 0.25 × 80 = 80 points</li>
     * </ul>
     *
     * @param enchantmentLevel enchantment level (1-4 for protection enchantments)
     * @param maxProtection configured maximum protection value
     * @param contributionPercentage contribution percentage (0.25 = 25%)
     * @return calculated protection points to add (rounded to nearest integer)
     */
    public static int calculateProtectionGain(int enchantmentLevel, int maxProtection, double contributionPercentage) {
        double rawGain = enchantmentLevel * contributionPercentage * maxProtection;
        return (int) Math.round(rawGain);
    } // calculateProtectionGain()

    /**
     * Calculates protection points gained using default config values.
     * <p>
     * <b>Convenience Method:</b> Uses SharedConfigs.Common.EnchantedBookContributionPercentage
     *
     * @param enchantmentLevel enchantment level (1-4 for protection enchantments)
     * @param maxProtection configured maximum protection value
     * @return calculated protection points to add
     */
    public static int calculateProtectionGain(int enchantmentLevel, int maxProtection) {
        return calculateProtectionGain(enchantmentLevel, maxProtection, SharedConfigs.Common.EnchantedBookContributionPercentage);
    } // calculateProtectionGain()

    // -- Validation Methods --

    /**
     * Checks if protection can be increased from current level.
     * <p>
     * <b>Contract:</b> Returns true if currentProtection < maxProtection
     * <p>
     * <b>Use Case:</b> Prevents feeding books when already at maximum protection.
     *
     * @param currentProtection current protection value
     * @param maxProtection configured maximum protection value
     * @return true if protection can be increased, false if already at max
     */
    public static boolean canApplyProtection(int currentProtection, int maxProtection) {
        return currentProtection < maxProtection;
    } // canApplyProtection()

    /**
     * Calculates final protection value after applying gain, capped at maximum.
     * <p>
     * <b>Contract:</b> Result is always clamped to [currentProtection, maxProtection]
     *
     * @param currentProtection current protection value
     * @param protectionGain points to add
     * @param maxProtection configured maximum protection value
     * @return new protection value (capped at max)
     */
    public static int applyProtectionGain(int currentProtection, int protectionGain, int maxProtection) {
        int newProtection = currentProtection + protectionGain;
        return Math.min(newProtection, maxProtection);
    } // applyProtectionGain()

    // -- Random Selection --

    /**
     * Selects random protection type for generic Protection enchantment.
     * <p>
     * <b>Use Case:</b> When book has generic "Protection" enchantment (not fire/blast/fall/projectile),
     * randomly choose which protection type to boost.
     * <p>
     * <b>Distribution:</b> Equal probability for all four types.
     *
     * @return randomly selected protection type
     */
    public static ProtectionType getRandomProtectionType() {
        ProtectionType[] types = ProtectionType.values();
        return types[RANDOM.nextInt(types.length)];
    } // getRandomProtectionType()

} // Class: EnchantmentProtectionCalculator
