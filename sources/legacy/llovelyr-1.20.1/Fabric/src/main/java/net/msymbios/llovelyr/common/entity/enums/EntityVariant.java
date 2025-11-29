package net.msymbios.llovelyr.common.entity.enums;

import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

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
public enum EntityVariant {

    // -- Original Variants (Base Functionality) --

    /** Alternative bunny design with different aesthetic. */
    Bunny2(1, LovelyIdentifier.VARIANT_BUNNY2),
    
    /** General-purpose companion with belt pouch and patrol abilities. */
    Vanilla(6, LovelyIdentifier.VARIANT_VANILLA);

    // -- Deserialization Cache --

    private static final EntityVariant[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityVariant::getId))
            .toArray(EntityVariant[]::new);

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
    EntityVariant(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityVariant ()

    // -- Deserialization --

    /**
     * Recovers variant from serialized identifier.
     * 
     * @param id serialized variant identifier
     * @return corresponding EntityVariant, or Bunny if invalid
     */
    public static EntityVariant byId(int id) {
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
     * @return matching EntityVariant, or null if not found
     */
    public static EntityVariant byName(String name) {
        for (EntityVariant item : CODEC) {
            if (item.getName().equals(name)) return item;
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

} // Enum: EntityVariant
