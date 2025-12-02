package net.msymbios.llovelyr.framework.entity.data;

/**
 * <p>Encapsulates combat-related statistics for robot entities.<p>
 * <p>
 * <b>Responsibility:</b> Maintains level, experience, HP, attack, and defense
 * values with type-safe accessors.
 */
public class CombatStats {
    
    // -- Fields --
    
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
        this(1, 0, 20, 20, 5, 5);
    }
    
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
    }
    
    // -- Public Methods --
    
    /**
     * Gets the robot level.
     * 
     * @return level
     */
    public int getLevel() {
        return level;
    }
    
    /**
     * Sets the robot level.
     * 
     * @param level new level
     * @throws IllegalArgumentException if level < 1
     */
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be at least 1");
        }
        this.level = level;
    }
    
    /**
     * Gets the current experience points.
     * 
     * @return experience
     */
    public int getExperience() {
        return experience;
    }
    
    /**
     * Sets the current experience points.
     * 
     * @param experience new experience
     * @throws IllegalArgumentException if experience < 0
     */
    public void setExperience(int experience) {
        if (experience < 0) {
            throw new IllegalArgumentException("Experience cannot be negative");
        }
        this.experience = experience;
    }
    
    /**
     * Gets the current health points.
     * 
     * @return current HP
     */
    public int getCurrentHp() {
        return currentHp;
    }
    
    /**
     * Sets the current health points.
     * 
     * @param currentHp new current HP
     * @throws IllegalArgumentException if currentHp < 0 or currentHp > maxHp
     */
    public void setCurrentHp(int currentHp) {
        if (currentHp < 0) {
            throw new IllegalArgumentException("Current HP cannot be negative");
        }
        if (currentHp > maxHp) {
            throw new IllegalArgumentException("Current HP cannot exceed max HP");
        }
        this.currentHp = currentHp;
    }
    
    /**
     * Gets the maximum health points.
     * 
     * @return max HP
     */
    public int getMaxHp() {
        return maxHp;
    }
    
    /**
     * Sets the maximum health points.
     * 
     * @param maxHp new max HP
     * @throws IllegalArgumentException if maxHp < 1
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
    }
    
    /**
     * Gets the attack damage.
     * 
     * @return attack
     */
    public int getAttack() {
        return attack;
    }
    
    /**
     * Sets the attack damage.
     * 
     * @param attack new attack
     * @throws IllegalArgumentException if attack < 0
     */
    public void setAttack(int attack) {
        if (attack < 0) {
            throw new IllegalArgumentException("Attack cannot be negative");
        }
        this.attack = attack;
    }
    
    /**
     * Gets the defense value.
     * 
     * @return defense
     */
    public int getDefense() {
        return defense;
    }
    
    /**
     * Sets the defense value.
     * 
     * @param defense new defense
     * @throws IllegalArgumentException if defense < 0
     */
    public void setDefense(int defense) {
        if (defense < 0) {
            throw new IllegalArgumentException("Defense cannot be negative");
        }
        this.defense = defense;
    }
    
    /**
     * Validates that stat relationships are consistent.
     * 
     * @return true if stats are valid
     */
    public boolean isValid() {
        return level >= 1 
            && experience >= 0 
            && currentHp >= 0 
            && currentHp <= maxHp 
            && maxHp >= 1
            && attack >= 0
            && defense >= 0;
    }
    
} // Class: CombatStats
