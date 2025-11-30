package net.msymbios.llovelyr.framework.entity.enums;

/**
 * <p>Defines animator variant types for entity animation controllers.<p>
 * <p>
 * <b>Architecture:</b> Pure Java enum with zero Minecraft dependencies. Provides
 * type-safe keys for animator resource mapping in ResourceMap.
 * <p>
 * <b>Design Decision:</b> Separate enums for texture, model, and animator variants
 * rather than single unified enum, allowing independent extension of each resource
 * type without coupling.
 */
public enum EntityVariantAnimator {
    
    /**
     * Default animator variant used for base entity animation controller.
     */
    DEFAULT

} // Enum: EntityVariantAnimator
