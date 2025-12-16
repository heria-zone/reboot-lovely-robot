package net.heriazone.lovelylib.hzlib.framework.entity.data;

/**
 * <p>Container for entity type combat statistics and identification metadata.<p>
 * <p>
 * <b>Architecture:</b> Pure Java data structure with zero Minecraft dependencies,
 * enabling portability and testability. Serves as the foundation for entity type
 * configuration across all loader implementations.
 * <p>
 * <b>Design Decision:</b> Mutable fields with validation in setters rather than
 * immutable builder pattern, allowing runtime configuration updates from config
 * system without object recreation.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Callers must synchronize access if
 * shared across threads.
 */
public class CombatData {

    // -- Identification Fields --

    private String key;
    private String translationKey;

    // -- Combat Statistics --

    private float maxHealth;
    private float attackDamage;
    private float attackSpeed;
    private float armor;
    private float armorToughness;
    private float knockbackResistance;
    private float moveSpeed;

    // -- Constructors --

    /**
     * Creates entity type data with default values.
     * <p>
     * All combat stats initialize to 0.0f except moveSpeed (0.25f default).
     */
    public CombatData() {
        this.moveSpeed = 0.25f;
    } // Constructor: CombatData ()

    /**
     * Creates entity type data with specified identification.
     *
     * @param key unique identifier for this entity type
     * @param translationKey localization key for display name
     */
    public CombatData(String key, String translationKey) {
        this();
        this.key = key;
        this.translationKey = translationKey;
    } // Constructor: CombatData ()

    // -- Identification Accessors --

    public String getKey() {
        return key;
    } // getKey ()

    public void setKey(String key) {
        this.key = key;
    } // setKey ()

    public String getTranslationKey() {
        return translationKey;
    } // getTranslationKey ()

    public void setTranslationKey(String translationKey) {
        this.translationKey = translationKey;
    } // setTranslationKey ()

    // -- Combat Stat Accessors --

    public float getMaxHealth() {
        return maxHealth;
    } // getMaxHealth ()

    /**
     * Sets maximum health, clamping negative values to 0.
     *
     * @param maxHealth maximum health points
     */
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = Math.max(0.0f, maxHealth);
    } // setMaxHealth ()

    public float getAttackDamage() {
        return attackDamage;
    } // getAttackDamage ()

    /**
     * Sets attack damage, clamping negative values to 0.
     *
     * @param attackDamage damage per attack
     */
    public void setAttackDamage(float attackDamage) {
        this.attackDamage = Math.max(0.0f, attackDamage);
    } // setAttackDamage ()

    public float getAttackSpeed() {
        return attackSpeed;
    } // getAttackSpeed ()

    /**
     * Sets attack speed, clamping negative values to 0.
     *
     * @param attackSpeed attacks per second
     */
    public void setAttackSpeed(float attackSpeed) {
        this.attackSpeed = Math.max(0.0f, attackSpeed);
    } // setAttackSpeed ()

    public float getArmor() {
        return armor;
    } // getArmor ()

    /**
     * Sets armor value, clamping negative values to 0.
     *
     * @param armor armor points
     */
    public void setArmor(float armor) {
        this.armor = Math.max(0.0f, armor);
    } // setArmor ()

    public float getArmorToughness() {
        return armorToughness;
    } // getArmorToughness ()

    /**
     * Sets armor toughness, clamping negative values to 0.
     *
     * @param armorToughness armor toughness points
     */
    public void setArmorToughness(float armorToughness) {
        this.armorToughness = Math.max(0.0f, armorToughness);
    } // setArmorToughness ()

    public float getKnockbackResistance() {
        return knockbackResistance;
    } // getKnockbackResistance ()

    /**
     * Sets knockback resistance, clamping to [0.0, 1.0] range.
     *
     * @param knockbackResistance resistance value (0.0 = no resistance, 1.0 = full resistance)
     */
    public void setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = Math.max(0.0f, Math.min(1.0f, knockbackResistance));
    } // setKnockbackResistance ()

    public float getMoveSpeed() {
        return moveSpeed;
    } // getMoveSpeed ()

    /**
     * Sets movement speed, clamping negative values to 0.
     *
     * @param moveSpeed movement speed multiplier
     */
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = Math.max(0.0f, moveSpeed);
    } // setMoveSpeed ()

} // Class: CombatData