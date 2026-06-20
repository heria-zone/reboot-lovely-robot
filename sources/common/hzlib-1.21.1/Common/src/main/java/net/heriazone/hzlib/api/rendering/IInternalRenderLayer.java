package net.heriazone.hzlib.api.rendering;

import net.heriazone.hzlib.api.entity.NativeEntity;

/**
 * Composable render layer interface for robot visual effects.
 * <p>
 * <b>Architecture:</b> Enables stacking multiple visual layers (base texture, emissives,
 * overlays, dynamic effects) without exponential texture file growth. Each layer renders
 * independently in sequence, compositing effects at runtime.
 * <p>
 * <b>Design Decision:</b> Interface rather than abstract class allows layers to extend
 * GeckoLib's GeoRenderLayer when needed while maintaining consistent API. Layers are
 * stateless and reusable across multiple entities.
 * <p>
 * <b>Performance:</b> Each layer adds one render pass. Keep layer count reasonable
 * (typically 2-4 layers). Layers should cache expensive calculations.
 *
 * @param <T> entity type extending LovelyRobotEntity
 */
public interface IInternalRenderLayer<T extends NativeEntity> {

    /**
     * Determines if this layer should render for the given entity state.
     * <p>
     * <b>Architecture:</b> Allows conditional rendering based on entity state, reducing
     * unnecessary render passes. Called before render() each frame.
     * <p>
     * <b>Performance:</b> Keep checks lightweight - this runs every frame per entity.
     *
     * @param entity robot entity being rendered
     * @param partialTick sub-tick interpolation for smooth animation
     * @return true if layer should render, false to skip
     */
    boolean shouldRender(T entity, float partialTick);

    /**
     * Gets the render context for this layer.
     * <p>
     * <b>Architecture:</b> Provides layer-specific rendering configuration including
     * texture location, color calculations, and render type preferences.
     *
     * @param entity robot entity being rendered
     * @param partialTick sub-tick interpolation for smooth animation
     * @return render context with layer configuration
     */
    LayerRenderContext getRenderContext(T entity, float partialTick);

} // Interface: IInternalRenderLayer