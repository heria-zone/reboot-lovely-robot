package net.heriazone.lovelylib.hzlib.framework.entity.data;

/**
 * <p>Encapsulates enchantment statistics for robot entities.<p>
 * <p>
 * <b>Responsibility:</b> Maintains looting, sharpness, and knockback
 * enchantment levels.
 */
public class EnchantmentStats {

    // -- Variables --

    private int lootingLevel;
    private int sharpnessLevel;
    private int knockbackLevel;

    // -- Constructors --

    /**
     * Creates enchantment stats with default values (all zeros).
     */
    public EnchantmentStats() {
        this(0, 0, 0);
    } // Constructor: EnchantmentStats ()

    /**
     * Creates enchantment stats with specified values.
     *
     * @param lootingLevel looting enchantment level
     * @param sharpnessLevel sharpness enchantment level
     * @param knockbackLevel knockback enchantment level
     */
    public EnchantmentStats(int lootingLevel, int sharpnessLevel, int knockbackLevel) {
        this.lootingLevel = lootingLevel;
        this.sharpnessLevel = sharpnessLevel;
        this.knockbackLevel = knockbackLevel;
    } // Constructor: EnchantmentStats ()

    // -- Custom Methods --

    /**
     * Gets the looting enchantment level.
     *
     * @return looting level
     */
    public int getLootingLevel() {
        return lootingLevel;
    } // getLootingLevel ()

    /**
     * Sets the looting enchantment level.
     *
     * @param lootingLevel new looting level
     * @throws IllegalArgumentException if lootingLevel < 0
     */
    public void setLootingLevel(int lootingLevel) {
        if (lootingLevel < 0) {
            throw new IllegalArgumentException("Looting level cannot be negative");
        }
        this.lootingLevel = lootingLevel;
    } // setLootingLevel ()

    /**
     * Gets the sharpness enchantment level.
     *
     * @return sharpness level
     */
    public int getSharpnessLevel() {
        return sharpnessLevel;
    } // getSharpnessLevel ()

    /**
     * Sets the sharpness enchantment level.
     *
     * @param sharpnessLevel new sharpness level
     * @throws IllegalArgumentException if sharpnessLevel < 0
     */
    public void setSharpnessLevel(int sharpnessLevel) {
        if (sharpnessLevel < 0) {
            throw new IllegalArgumentException("Sharpness level cannot be negative");
        }
        this.sharpnessLevel = sharpnessLevel;
    } // setSharpnessLevel ()

    /**
     * Gets the knockback enchantment level.
     *
     * @return knockback level
     */
    public int getKnockbackLevel() {
        return knockbackLevel;
    } // getKnockbackLevel ()

    /**
     * Sets the knockback enchantment level.
     *
     * @param knockbackLevel new knockback level
     * @throws IllegalArgumentException if knockbackLevel < 0
     */
    public void setKnockbackLevel(int knockbackLevel) {
        if (knockbackLevel < 0) {
            throw new IllegalArgumentException("Knockback level cannot be negative");
        }
        this.knockbackLevel = knockbackLevel;
    } // setKnockbackLevel ()

    /**
     * Validates that all enchantment levels are non-negative.
     *
     * @return true if stats are valid
     */
    public boolean isValid() {
        return lootingLevel >= 0
                && sharpnessLevel >= 0
                && knockbackLevel >= 0;
    } // isValid ()

    /**
     * Creates deep copy of enchantment stats.
     *
     * @return new EnchantmentStats instance with copied values
     */
    public EnchantmentStats copy() {
        return new EnchantmentStats(lootingLevel, sharpnessLevel, knockbackLevel);
    } // copy ()

    @Override
    public String toString() {
        return "EnchantmentStats{" +
                "looting=" + lootingLevel +
                ", sharpness=" + sharpnessLevel +
                ", knockback=" + knockbackLevel +
                '}';
    } // toString ()

} // Class: EnchantmentStats