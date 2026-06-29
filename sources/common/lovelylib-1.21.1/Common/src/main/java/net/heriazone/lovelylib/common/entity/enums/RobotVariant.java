package net.heriazone.lovelylib.common.entity.enums;

import net.heriazone.lovelylib.common.shared.LovelyConstant;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Defines robot type variants with distinct models, behaviors, and characteristics.
 * <p>
 * <b>Architecture:</b> Variants determine which entity class, model, and behavior set
 * to use. Each variant has unique specializations (Bunny speed, Dragon combat, etc.)
 * while sharing core robot systems.
 * <p>
 * <b>Design Decision:</b> Ten variants balance variety with maintainability. Original
 * four (Bunny, Bunny2, Honey, Vanilla) provide base functionality. Additional variants
 * (Dragon, Kitsune, Neko) add specialized roles. Premium variants (Prime, Hyperion,
 * Empyrium) offer advanced features for progression.
 */
public enum RobotVariant {

    // -- Original Variants (Base Functionality) --

    /** Original bunny design with balanced stats. */
    Bunny(0, LovelyConstant.VARIANT_BUNNY),

    /** Alternative bunny design with different aesthetic. */
    Bunny2(1, LovelyConstant.VARIANT_BUNNY2),

    /** Dragon variant with enhanced combat capabilities and knockback resistance. */
    Dragon(2, LovelyConstant.VARIANT_DRAGON),

    /** Honey variant with support-oriented capabilities. */
    Honey(3, LovelyConstant.VARIANT_HONEY),

    /** Kitsune variant with tail-unlock progression system. */
    Kitsune(4, LovelyConstant.VARIANT_KITSUNE),

    /** Neko variant with agile combat specialization. */
    Neko(5, LovelyConstant.VARIANT_NEKO),

    /** General-purpose companion with belt pouch and patrol abilities. */
    Vanilla(6, LovelyConstant.VARIANT_VANILLA),

    /** Third-generation bunny with improved all-round stats and a 5-color pastel palette. */
    Bunny3(7, LovelyConstant.VARIANT_BUNNY3);

    // -- Deserialization Cache --

    private static final RobotVariant[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(RobotVariant::getId))
            .toArray(RobotVariant[]::new);

    // -- Variant Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds variant to persistent identifier and registry name.
     *
     * @param id persistent identifier for serialization
     * @param name registry name for entity type lookup
     */
    RobotVariant(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: RobotVariant ()

    // -- Deserialization --

    /**
     * Recovers variant from serialized identifier.
     *
     * @param id serialized variant identifier
     * @return corresponding RobotVariant, or Bunny if invalid
     */
    public static RobotVariant byId(int id) {
        if (id < 0 || id >= CODEC.length) {
            id = 0;
        }
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for serialization.
     *
     * @return variant identifier
     */
    public int getId() {
        return this.m_id;
    } // getId ()

    /**
     * Finds variant by registry name.
     *
     * @param name registry name of variant
     * @return matching RobotVariant, or null if not found
     */
    public static RobotVariant byName(String name) {
        for (RobotVariant item : CODEC) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    } // byName ()

    /**
     * Returns variant registry name.
     *
     * @return registry name for entity type lookup
     */
    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum: RobotVariant