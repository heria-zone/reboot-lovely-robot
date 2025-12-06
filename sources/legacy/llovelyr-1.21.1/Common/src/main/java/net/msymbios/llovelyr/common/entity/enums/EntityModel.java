package net.msymbios.llovelyr.common.entity.enums;

import net.msymbios.llovelyr.common.shared.LovelyIdentifier;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Defines robot model variants for rendering different equipment states.
 * <p>
 * <b>Architecture:</b> Model variants allow robots to switch between visual states
 * (unarmed vs armed) without changing entity type. Rendering system selects appropriate
 * GeckoLib model based on current equipment.
 * <p>
 * <b>Design Decision:</b> Two models (Default, Armed) handle most equipment scenarios.
 * Armed model shows weapon attachment points and combat-ready pose.
 */
public enum EntityModel {

    // -- Model Variants --

    /** Standard unarmed model. Used when robot has no weapon equipped. */
    Default(0, LovelyIdentifier.MOD_DEFAULT),

    /** Combat-ready model with weapon attachment points. Used when robot is armed. */
    Armed(1, LovelyIdentifier.MOD_ARMED);

    // -- Deserialization Cache --

    private static final EntityModel[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityModel::getId))
            .toArray(EntityModel[]::new);

    // -- Model Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds model to persistent identifier and resource location.
     *
     * @param id persistent identifier for serialization
     * @param name resource location of GeckoLib model file
     */
    EntityModel(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityModel ()

    // -- Deserialization --

    /**
     * Recovers model from serialized identifier.
     *
     * @param id serialized model identifier
     * @return corresponding EntityModel, or Default if invalid
     */
    public static EntityModel byId(int id) {
        if (id < 0 || id >= CODEC.length) {
            id = 0;
        }
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for serialization.
     *
     * @return model identifier
     */
    public int getId() {
        return this.m_id;
    } // getId ()

    /**
     * Finds model by resource location name.
     *
     * @param name resource location of model file
     * @return matching EntityModel, or null if not found
     */
    public static EntityModel byName(String name) {
        for (EntityModel item : CODEC) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    } // byName ()

    /**
     * Returns model resource location.
     *
     * @return resource location for GeckoLib model lookup
     */
    public String getName() {
        return this.m_name;
    } // getName ()

} // Enum: EntityModel