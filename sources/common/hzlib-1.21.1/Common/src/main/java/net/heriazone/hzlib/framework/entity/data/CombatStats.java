package net.heriazone.hzlib.framework.entity.data;

/**
 * <p>Encapsulates combat-related statistics for robot entities.<p>
 * <p>
 * <b>Architecture:</b> Replaces scattered level/exp/hp fields with cohesive
 * data object, enabling feature-based attribute calculations and consistent
 * stat management across robot implementations.
 * <p>
 * <b>Design Decision:</b> Separates combat data from entity implementation,
 * allowing stat calculations to be tested independently and reused across
 * different robot types.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Modifications should be performed
 * on the server thread where entity updates occur.
 */
public class CombatStats {

    // -- Fields --

    private int level = 1;
    private float experience = 0.0f;
    private float maxHealth = 20.0f;
    private float attackDamage = 2.0f;
    private float attackSpeed = 1.0f;
    private float armor = 0.0f;
    private float armorToughness = 0.0f;
    private float knockbackResistance = 0.0f;
    private float moveSpeed = 0.25f;

    // -- Constructor --

    /**
     * Creates combat stats with default values.
     */
    public CombatStats() {
        // Default values set in field declarations
    } // Constructor: CombatStats ()

    // -- Level and Experience --

    /**
     * Returns current level.
     *
     * @return robot level (1-200)
     */
    public int getLevel() {
        return level;
    } // getLevel ()

    /**
     * Sets robot level with validation.
     * <p>
     * <b>Validation:</b> Clamps level to valid range (1-200).
     *
     * @param level new level (will be clamped to 1-200)
     */
    public void setLevel(int level) {
        this.level = Math.max(1, Math.min(200, level));
    } // setLevel ()

    /**
     * Returns current experience points.
     *
     * @return experience points
     */
    public float getExperience() {
        return experience;
    } // getExperience ()

    /**
     * Sets experience points with validation.
     * <p>
     * <b>Validation:</b> Ensures experience is non-negative.
     *
     * @param experience new experience points (minimum 0)
     */
    public void setExperience(float experience) {
        this.experience = Math.max(0.0f, experience);
    } // setExperience ()

    /**
     * Adds experience points and handles level-up.
     * <p>
     * <b>Level-Up Logic:</b> Automatically increases level when experience
     * threshold is reached, using standard robot experience formula.
     *
     * @param expGain experience points to add
     * @return true if level increased, false otherwise
     */
    public boolean addExperience(float expGain) {
        if (expGain <= 0 || level >= 200) return false;

        this.experience += expGain;
        int oldLevel = level;

        // Check for level-up using standard formula: Base (50) × Multiplier (2) × Level
        while (level < 200) {
            float requiredExp = 50.0f * 2.0f * level;
            if (experience >= requiredExp) {
                experience -= requiredExp;
                level++;
            } else {
                break;
            }
        }

        return level > oldLevel;
    } // addExperience ()

    // -- Health Stats --

    /**
     * Returns maximum health.
     *
     * @return max health points
     */
    public float getMaxHealth() {
        return maxHealth;
    } // getMaxHealth ()

    /**
     * Sets maximum health with validation.
     *
     * @param maxHealth new max health (minimum 1.0)
     */
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(1.0f, maxHealth);
    } // setMaxHealth ()

    // -- Attack Stats --

    /**
     * Returns attack damage.
     *
     * @return attack damage points
     */
    public float getAttackDamage() {
        return attackDamage;
    } // getAttackDamage ()

    /**
     * Sets attack damage with validation.
     *
     * @param attackDamage new attack damage (minimum 0.0)
     */
    public void setAttackDamage(float attackDamage) {
        this.attackDamage = Math.max(0.0f, attackDamage);
    } // setAttackDamage ()

    /**
     * Returns attack speed.
     *
     * @return attacks per second
     */
    public float getAttackSpeed() {
        return attackSpeed;
    } // getAttackSpeed ()

    /**
     * Sets attack speed with validation.
     *
     * @param attackSpeed new attack speed (minimum 0.1)
     */
    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = Math.max(0.1f, attackSpeed);
    } // setAttackSpeed ()

    // -- Defense Stats --

    /**
     * Returns armor points.
     *
     * @return armor points
     */
    public float getArmor() {
        return armor;
    } // getArmor ()

    /**
     * Sets armor points with validation.
     *
     * @param armor new armor points (minimum 0.0)
     */
    public void setArmor(float armor) {
        this.armor = Math.max(0.0f, armor);
    } // setArmor ()

    /**
     * Returns armor toughness.
     *
     * @return armor toughness points
     */
    public float getArmorToughness() {
        return armorToughness;
    } // getArmorToughness ()

    /**
     * Sets armor toughness with validation.
     *
     * @param armorToughness new armor toughness (minimum 0.0)
     */
    public void setArmorToughness(float armorToughness) {
        this.armorToughness = Math.max(0.0f, armorToughness);
    } // setArmorToughness ()

    /**
     * Returns knockback resistance.
     *
     * @return knockback resistance (0.0-1.0)
     */
    public float getKnockbackResistance() {
        return knockbackResistance;
    } // getKnockbackResistance ()

    /**
     * Sets knockback resistance with validation.
     *
     * @param knockbackResistance new knockback resistance (0.0-1.0)
     */
    public void setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = Math.max(0.0f, Math.min(1.0f, knockbackResistance));
    } // setKnockbackResistance ()

    // -- Movement Stats --

    /**
     * Returns movement speed.
     *
     * @return movement speed multiplier
     */
    public float getMoveSpeed() {
        return moveSpeed;
    } // getMoveSpeed ()

    /**
     * Sets movement speed with validation.
     *
     * @param moveSpeed new movement speed (minimum 0.1)
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = Math.max(0.1f, moveSpeed);
    } // setMoveSpeed ()

    // -- Utility Methods --

    /**
     * Resets all stats to default values.
     */
    public void reset() {
        this.level = 1;
        this.experience = 0.0f;
        this.maxHealth = 20.0f;
        this.attackDamage = 2.0f;
        this.attackSpeed = 1.0f;
        this.armor = 0.0f;
        this.armorToughness = 0.0f;
        this.knockbackResistance = 0.0f;
        this.moveSpeed = 0.25f;
    } // reset ()

    /**
     * Copies stats from another CombatStats instance.
     *
     * @param other stats to copy from
     */
    public void copyFrom(CombatStats other) {
        if (other == null) return;

        this.level = other.level;
        this.experience = other.experience;
        this.maxHealth = other.maxHealth;
        this.attackDamage = other.attackDamage;
        this.attackSpeed = other.attackSpeed;
        this.armor = other.armor;
        this.armorToughness = other.armorToughness;
        this.knockbackResistance = other.knockbackResistance;
        this.moveSpeed = other.moveSpeed;
    } // copyFrom ()

} // Class: CombatStats