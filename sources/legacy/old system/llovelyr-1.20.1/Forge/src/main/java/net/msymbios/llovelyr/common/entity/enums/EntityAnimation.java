package net.msymbios.llovelyr.common.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Defines animation states for robot entity rendering and behavior synchronization.
 * <p>
 * <b>Architecture:</b> Animation states bridge entity behavior with GeckoLib rendering,
 * triggering appropriate animation controllers based on robot actions. State changes
 * are synchronized client-server to ensure visual consistency.
 * <p>
 * <b>Design Decision:</b> Five core animations cover all robot behaviors without
 * overwhelming the animation system. More specific animations (running, jumping)
 * are handled through animation blending rather than discrete states.
 */
public enum EntityAnimation {

    // -- Animation States --

    /** Neutral standing pose, loops indefinitely. Default when no other animation active. */
    Idle(0, "idle"),
    
    /** Movement animation, speed scales with entity velocity. */
    Walk(1, "walk"),
    
    /** Lying down pose for sleeping or resting. Loops indefinitely. */
    Rest(2, "rest"),
    
    /** Sitting pose when commanded to stay. Loops with subtle breathing animation. */
    Sit(3, "sit"),
    
    /** Combat swing animation. Plays once per attack, synced with damage application. */
    Attack(4, "attack");

    // -- Deserialization Cache --

    private static final EntityAnimation[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityAnimation::getId))
            .toArray(EntityAnimation[]::new);

    // -- State Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds animation to persistent identifier and GeckoLib animation name.
     * <p>
     * <i>Note:</i> Animation names must match GeckoLib animation file definitions exactly.
     * 
     * @param id persistent identifier for serialization
     * @param name GeckoLib animation controller name (must match .animation.json)
     */
    EntityAnimation(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityAnimation

    // -- Deserialization --

    /**
     * Recovers animation from serialized identifier.
     * <p>
     * <b>Failure Mode:</b> Invalid IDs default to Idle, ensuring robots always have
     * a valid animation state even with corrupted data.
     * 
     * @param id serialized animation identifier
     * @return corresponding EntityAnimation, or Idle if invalid
     */
    public static EntityAnimation byId(int id) {
        if (id < 0 || id >= CODEC.length) {
            id = 0;
        }
        return CODEC[id];
    } // byId

    /**
     * Returns persistent identifier for serialization.
     * 
     * @return animation identifier
     */
    public int getId() {
        return this.m_id;
    } // getId

    /**
     * Finds animation by GeckoLib animation name.
     * <p>
     * <b>Usage:</b> Called by animation controllers to map behavior states to animations.
     * 
     * @param name GeckoLib animation name from .animation.json
     * @return matching EntityAnimation, or null if not found
     */
    public static EntityAnimation byName(String name) {
        for (EntityAnimation item : CODEC) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    } // byName

    /**
     * Returns GeckoLib animation name for rendering.
     * 
     * @return animation name matching .animation.json definitions
     */
    public String getName() {
        return this.m_name;
    } // getName

} // Enum: EntityAnimation
