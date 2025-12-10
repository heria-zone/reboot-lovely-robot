package net.msymbios.llovelyr.lib.rendering;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Rendering configuration for a single layer pass.
 * <p>
 * <b>Architecture:</b> Encapsulates all rendering parameters needed by loader-specific
 * rendering implementations. Separates business logic (what to render) from platform
 * integration (how to render with GeckoLib).
 * <p>
 * <b>Design Decision:</b> Immutable record ensures thread safety and prevents
 * accidental modification during rendering. Suppliers allow lazy evaluation of
 * expensive calculations.
 */
public record LayerRenderContext(
        ResourceLocation texture,
        int color,
        float alpha,
        boolean useOverlay,
        Supplier<Boolean> additionalConditions,
        net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity entity
) {

    // -- Factory Methods --

    /**
     * Creates basic texture layer context.
     *
     * @param texture texture resource location
     * @param entity robot entity for context
     * @return render context with default settings
     */
    public static LayerRenderContext texture(ResourceLocation texture, net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity entity) {
        return new LayerRenderContext(texture, 0xFFFFFFFF, 1.0f, true, () -> true, entity);
    } // texture()

    /**
     * Creates colored texture layer context.
     *
     * @param texture texture resource location
     * @param color ARGB color value
     * @param entity robot entity for context
     * @return render context with specified color
     */
    public static LayerRenderContext coloredTexture(ResourceLocation texture, int color, net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity entity) {
        return new LayerRenderContext(texture, color, 1.0f, true, () -> true, entity);
    } // coloredTexture()

    /**
     * Creates transparent texture layer context.
     *
     * @param texture texture resource location
     * @param alpha transparency (0.0 = transparent, 1.0 = opaque)
     * @param entity robot entity for context
     * @return render context with specified transparency
     */
    public static LayerRenderContext transparentTexture(ResourceLocation texture, float alpha, net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity entity) {
        return new LayerRenderContext(texture, 0xFFFFFFFF, alpha, true, () -> true, entity);
    } // transparentTexture()

    /**
     * Creates conditional texture layer context.
     *
     * @param texture texture resource location
     * @param condition additional rendering condition
     * @param entity robot entity for context
     * @return render context with conditional rendering
     */
    public static LayerRenderContext conditionalTexture(ResourceLocation texture, Supplier<Boolean> condition, net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity entity) {
        return new LayerRenderContext(texture, 0xFFFFFFFF, 1.0f, true, condition, entity);
    } // conditionalTexture()

    // -- Utility Methods --

    /**
     * Checks if this layer should render based on all conditions.
     *
     * @return true if layer should render, false otherwise
     */
    public boolean shouldRender() {
        return texture != null && additionalConditions.get();
    } // shouldRender()

    /**
     * Gets the effective alpha value (0.0 to 1.0).
     *
     * @return alpha value clamped to valid range
     */
    public float getEffectiveAlpha() {
        return Math.max(0.0f, Math.min(1.0f, alpha));
    } // getEffectiveAlpha()

    /**
     * Gets the entity associated with this render context.
     *
     * @return robot entity for this context
     */
    public net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity getEntity() {
        return entity;
    } // getEntity()

} // Record: LayerRenderContext