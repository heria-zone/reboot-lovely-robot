package net.msymbios.llovelyr.framework.entity.enums;

/**
 * <p>Defines texture variant types for entity rendering.<p>
 * <p>
 * <b>Architecture:</b> Pure Java enum with zero Minecraft dependencies. Provides
 * type-safe keys for texture resource mapping in ResourceMap.
 * <p>
 * <b>Design Decision:</b> Separate enums for texture, model, and animator variants
 * rather than single unified enum, allowing independent extension of each resource
 * type without coupling.
 */
public enum EntityVariantTexture {

    /**
     * Default texture variant used for base entity appearance.
     */
    DEFAULT,

    /**
     * Armed texture variant used when entity is equipped with weapons or tools.
     */
    ARMED

} // Enum: EntityVariantTexture