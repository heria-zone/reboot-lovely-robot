package net.heriazone.lovelylib.common.entity.enums;

import net.heriazone.lovelylib.common.shared.LovelyConstant;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Defines animation controller configurations for robot rendering systems.
 * <p>
 * <b>Architecture:</b> Animators map to GeckoLib animation controller files, allowing
 * different robot types to use distinct animation sets while sharing the same entity code.
 * <p>
 * <b>Design Decision:</b> Single default animator keeps initial implementation simple.
 * Future robot variants can add specialized animators without modifying existing entities.
 */
public enum EntityAnimator {

    // -- Animator Configurations --

    /** Standard animation controller used by all basic robot types. */
    Default(0, LovelyConstant.ANIM_DEFAULT);

    // -- Deserialization Cache --

    private static final EntityAnimator[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityAnimator::getId))
            .toArray(EntityAnimator[]::new);

    // -- Animator Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds animator to persistent identifier and controller resource location.
     *
     * @param id persistent identifier for serialization
     * @param name resource location of GeckoLib animation controller
     */
    EntityAnimator(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityVariantAnimator ()

    // -- Deserialization --

    /**
     * Recovers animator from serialized identifier.
     *
     * @param id serialized animator identifier
     * @return corresponding EntityVariantAnimator, or Default if invalid
     */
    public static EntityAnimator byId(int id) {
        if (id < 0 || id >= CODEC.length) {
            id = 0;
        }
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for serialization.
     *
     * @return animator identifier
     */
    public int getId() {
        return this.m_id;
    } // getId ()

    /**
     * Finds animator by resource location name.
     *
     * @param name resource location of animation controller
     * @return matching EntityVariantAnimator, or null if not found
     */
    public static EntityAnimator byName(String name) {
        for (EntityAnimator item : CODEC) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    } // byName ()

    /**
     * Returns animation controller resource location.
     *
     * @return resource location for GeckoLib controller lookup
     */
    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum: EntityVariantAnimator