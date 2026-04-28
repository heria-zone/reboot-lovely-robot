package net.heriazone.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.hzlib.api.entity.InternalEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;

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
 * @param <T> entity type extending LovelyRobotEntity and GeoEntity
 */
public interface IInternalRenderLayer<T extends InternalEntity & GeoEntity> {

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
     * Renders this layer's visual effect on the entity model.
     * <p>
     * <b>Architecture:</b> Called after shouldRender() returns true. Receives full
     * rendering context to apply textures, colors, or transformations.
     * <p>
     * <b>State Impact:</b> Should not modify entity state. Read-only access to entity
     * data for rendering decisions.
     *
     * @param poseStack transformation matrix stack for positioning
     * @param entity robot entity being rendered
     * @param bakedModel pre-processed model geometry
     * @param renderType base render type (it may be overridden by layer)
     * @param bufferSource vertex buffer provider
     * @param buffer vertex consumer for geometry
     * @param partialTick sub-tick interpolation for smooth animation
     * @param packedLight light level at entity position
     * @param packedOverlay overlay UV coordinates
     */
    void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                int packedLight, int packedOverlay);

} // Interface: IInternalRenderLayer