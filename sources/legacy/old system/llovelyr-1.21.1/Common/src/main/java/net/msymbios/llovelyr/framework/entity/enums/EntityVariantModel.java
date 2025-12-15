package net.msymbios.llovelyr.framework.entity.enums;

/**
 * <p>Defines model variant types for entity geometry.<p>
 * <p>
 * <b>Architecture:</b> Pure Java enum with zero Minecraft dependencies. Provides
 * type-safe keys for model resource mapping in ResourceMap.
 * <p>
 * <b>Design Decision:</b> Separate enums for texture, model, and animator variants
 * rather than single unified enum, allowing independent extension of each resource
 * type without coupling.
 */
public enum EntityVariantModel {

    /**
     * Default model variant used for base entity geometry.
     */
    DEFAULT,

    /**
     * Armed model variant used when entity is equipped with weapons or tools.
     */
    ARMED

} // Enum: EntityVariantModel
