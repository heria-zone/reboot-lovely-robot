package net.msymbios.llovelyr.framework.entity.enums;

import java.util.Arrays;
import java.util.Comparator;

/**
 * <p>Coordinates robot behavioral priorities through discrete operational modes.<p>
 * <p>
 * <b>Architecture:</b> States sit above the goal system as the top-level decision layer.
 * State changes trigger goal set recalculation, allowing owner commands to alter behavior
 * without modifying individual AI goals.
 * <p>
 * <b>Design Decision:</b> Three states balance behavioral variety with player comprehension.
 * More granular options (Patrol, Guard, Escort) were rejected to avoid subtle distinctions.
 * <p>
 * <b>Serialization:</b> ID-based with O(1) CODEC array lookup. Invalid IDs default to
 * Follow rather than crashing, ensuring robots survive corrupted save data.
 */
public enum EntityState {

    // -- Behavioral Modes --

    /**
     * Active companion: FollowOwnerGoal prioritized, combat goals active.
     * Default state for exploration and combat.
     */
    Follow(0),

    /**
     * Territorial guardian: FollowOwnerGoal disabled, BaseDefenseGoal anchors to location.
     * Used for base defense and preventing robots from following into danger.
     */
    Defense(1),

    /**
     * Passive observer: All movement/combat goals disabled except sitting.
     * Used for decoration or keeping robots non-threatening to mobs.
     */
    Standby(2);

    // -- Deserialization Cache --

    private static final EntityState[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityState::getId))
            .toArray(EntityState[]::new);

    // -- State Identity --

    private final int m_id;

    // -- Constructor --

    /**
     * Binds state to persistent identifier for network/NBT serialization.
     * <p>
     * <i>Note:</i> IDs must remain stable across versions. Never reorder existing IDs.
     * 
     * @param id persistent identifier (must be unique and stable across versions)
     */
    EntityState(int id) {
        this.m_id = id;
    } // Constructor: EntityState ()

    // -- Deserialization --

    /**
     * Recovers state from serialized identifier with graceful degradation.
     * <p>
     * <b>Failure Mode:</b> Invalid IDs default to Follow rather than crashing, preventing
     * issues from corrupted saves or version mismatches.
     * <p>
     * <b>Performance:</b> O(1) lookup via CODEC array, called every tick for goal evaluation.
     * 
     * @param id serialized state identifier from network packet or NBT
     * @return corresponding EntityState, or Follow if id is invalid
     */
    public static EntityState byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for network/NBT serialization.
     * 
     * @return state identifier (stable across game sessions)
     */
    public int getId() {
        return this.m_id;
    } // getId ()

} // Enum: EntityState
