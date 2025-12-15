package net.msymbios.llovelyr.framework.entity.data;

/**
 * <p>Encapsulates combat-related statistics for robot entities.<p>
 * <p>
 * <b>Responsibility:</b> Maintains level, experience, HP, attack, and defense
 * values with type-safe accessors.
 */
public class CombatStats {

    // -- Variables --

    private int level;
    private int experience;
    private int currentHp;
    private int maxHp;
    private int attack;
    private int defense;

    // -- Constructors --

    /**
     * Creates combat stats with default values.
     */
    public CombatStats() {
        this(0, 0, 20, 20, 5, 5);
    } // Constructor: CombatStats ()

    /**
     * Creates combat stats with specified values.
     *
     * @param level robot level
     * @param experience current experience points
     * @param currentHp current health points
     * @param maxHp maximum health points
     * @param attack attack damage
     * @param defense defense value
     */
    public CombatStats(int level, int experience, int currentHp, int maxHp, int attack, int defense) {
        this.level = level;
        this.experience = experience;
        this.currentHp = currentHp;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
    } // Constructor: CombatStats ()

    // -- Custom Methods --

    /**
     * Gets the robot level.
     *
     * @return level
     */
    public int getLevel() {
        return level;
    } // getLevel ()

    /**
     * Sets the robot level.
     * <p>
     * <b>Design Note:</b> Level 0 is allowed for newly spawned robots that haven't
     * been initialized yet. Level will be set to 1 or higher during first interaction.
     *
     * @param level new level (0 or higher)
     * @throws IllegalArgumentException if level &lt; 0
     */
    public void setLevel(int level) {
        if (level < 0) {
            throw new IllegalArgumentException("Level cannot be negative");
        }
        this.level = level;
    } // setLevel ()

    /**
     * Gets the current experience points.
     *
     * @return experience
     */
    public int getExperience() {
        return experience;
    } // getExperience ()

    /**
     * Sets the current experience points.
     *
     * @param experience new experience
     * @throws IllegalArgumentException if experience &lt; 0
     */
    public void setExperience(int experience) {
        if (experience < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
        this.experience = experience;
    } // setExperience ()

    /**
     * Gets the current health points.
     *
     * @return current HP
     */
    public int getCurrentHp() {
        return currentHp;
    } // getCurrentHp ()

    /**
     * Sets the current health points.
     *
     * @param currentHp new current HP
     * @throws IllegalArgumentException if currentHp &lt; 0 or currentHp &gt; maxHp
     */
    public void setCurrentHp(int currentHp) {
        if (currentHp < 0) {
            throw new IllegalArgumentException("Current HP cannot be negative");
        }
        if (currentHp > maxHp) {
            throw new IllegalArgumentException("Current HP cannot exceed max HP");
        }
        this.currentHp = currentHp;
    } // setCurrentHp ()

    /**
     * Gets the maximum health points.
     *
     * @return max HP
     */
    public int getMaxHp() {
        return maxHp;
    } // getMaxHp ()

    /**
     * Sets the maximum health points.
     *
     * @param maxHp new max HP
     * @throws IllegalArgumentException if maxHp &lt; 1
     */
    public void setMaxHp(int maxHp) {
        if (maxHp < 1) {
            throw new IllegalArgumentException("Max HP must be at least 1");
        }
        this.maxHp = maxHp;
        // Ensure current HP doesn't exceed new max
        if (currentHp > maxHp) {
            currentHp = maxHp;
        }
    } // setMaxHp ()

    /**
     * Gets the attack damage.
     *
     * @return attack
     */
    public int getAttack() {
        return attack;
    } // getAttack ()

    /**
     * Sets the attack damage.
     *
     * @param attack new attack
     * @throws IllegalArgumentException if attack &lt; 0
     */
    public void setAttack(int attack) {
        if (attack < 0) {
            throw new IllegalArgumentException("Attack cannot be negative");
        }
        this.attack = attack;
    } // setAttack ()

    /**
     * Gets the defense value.
     *
     * @return defense
     */
    public int getDefense() {
        return defense;
    } // getDefense ()

    /**
     * Sets the defense value.
     *
     * @param defense new defense
     * @throws IllegalArgumentException if defense &lt; 0
     */
    public void setDefense(int defense) {
        if (defense < 0) {
            throw new IllegalArgumentException("Defense cannot be negative");
        }
        this.defense = defense;
    } // setDefense ()

    /**
     * Validates that stat relationships are consistent.
     * <p>
     * <b>Design Note:</b> Level 0 is valid for newly spawned robots.
     *
     * @return true if stats are valid
     */
    public boolean isValid() {
        return level >= 0
                && experience >= 0
                && currentHp >= 0
                && currentHp <= maxHp
                && maxHp >= 1
                && attack >= 0
                && defense >= 0;
    } // isValid ()

    /**
     * Creates deep copy of combat stats.
     *
     * @return new CombatStats instance with copied values
     */
    public CombatStats copy() {
        return new CombatStats(level, experience, currentHp, maxHp, attack, defense);
    } // copy ()

    @Override
    public String toString() {
        return "CombatStats{" +
                "level=" + level +
                ", exp=" + experience +
                ", hp=" + currentHp + "/" + maxHp +
                ", atk=" + attack +
                ", def=" + defense +
                '}';
    } // toString ()

} // Class: CombatStats