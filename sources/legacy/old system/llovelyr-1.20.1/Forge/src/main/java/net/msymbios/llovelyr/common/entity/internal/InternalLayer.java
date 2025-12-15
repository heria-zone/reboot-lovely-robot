package net.msymbios.llovelyr.common.entity.internal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.shared.LovelyResource;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.framework.entity.enums.EntityState;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

/**
 * Renders overlay textures on robot entities to indicate active modes.
 * <p>
 * <b>Architecture:</b> GeckoLib render layer system allows stacking multiple textures
 * on same model. This layer adds visual indicators for auto-attack and base defense
 * modes without modifying base model or texture.
 * <p>
 * <b>Design Decision:</b> Mode indicators as overlays rather than texture variants
 * avoids exponential texture file growth (16 colors × 2 models × 3 modes = 96 textures
 * per robot). Overlays are composited at render time.
 * <p>
 * <b>Performance:</b> Additional render pass per robot, but overlay textures are small
 * and cached. Negligible impact unless rendering 50+ robots simultaneously.
 */
public abstract class InternalLayer<T extends LovelyRobotEntity & GeoEntity> extends GeoRenderLayer<T> {

    // -- Overlay Textures --

    /** Empty/transparent overlay when no modes active. */
    protected ResourceLocation layerIdle = LovelyResource.GENERAL_LAYER_EMPTY;
    
    /** Overlay indicating base defense mode (territorial guardian). */
    protected ResourceLocation layerBaseDefence = LovelyResource.GENERAL_LAYER_BASE_DEFENSE;
    
    /** Overlay indicating auto-attack mode (aggressive combat). */
    protected ResourceLocation layerAutoAttack = LovelyResource.GENERAL_LAYER_AUTO_ATTACK;

    // -- Constructor --

    /**
     * Initializes render layer with parent renderer.
     * 
     * @param entityRendererIn parent GeoRenderer managing this entity
     */
    public InternalLayer(GeoRenderer<T> entityRendererIn) {
        super(entityRendererIn);
    } // Constructor: InternalLayer()

    // -- Rendering --

    /**
     * Renders mode indicator overlay based on entity's current state and settings.
     * <p>
     * <b>Architecture:</b> Selects overlay texture based on entity state (Defense vs
     * Follow) and auto-attack setting. Re-renders model with overlay texture composited
     * on top of base texture.
     * <p>
     * <b>Design Decision:</b> Only renders overlay when auto-attack enabled to avoid
     * unnecessary render passes. Defense mode takes priority over standard auto-attack
     * indicator for visual clarity.
     * 
     * @param poseStack transformation matrix stack for positioning
     * @param animatable robot entity being rendered
     * @param bakedModel pre-processed model geometry
     * @param renderType base render type (unused, overlay determines type)
     * @param bufferSource vertex buffer provider
     * @param buffer vertex consumer (unused, overlay creates new buffer)
     * @param partialTick sub-tick interpolation for smooth animation
     * @param packedLight light level at entity position
     * @param packedOverlay overlay UV coordinates (unused, NO_OVERLAY used)
     */
    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel, RenderType renderType, 
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, 
                       int packedLight, int packedOverlay) {
        
        // Select overlay texture based on entity state
        RenderType armorRenderType = RenderType.armorCutoutNoCull(layerIdle);
        
        if (animatable.getAutoAttack()) {
            if (animatable.getCurrentState() == EntityState.Defense) {
                armorRenderType = RenderType.armorCutoutNoCull(layerBaseDefence);
            } else {
                armorRenderType = RenderType.armorCutoutNoCull(layerAutoAttack);
            }
        }
        
        // Re-render model with overlay texture
        getRenderer().reRender(
            getDefaultBakedModel(animatable), 
            poseStack, 
            bufferSource, 
            animatable, 
            armorRenderType, 
            bufferSource.getBuffer(armorRenderType), 
            partialTick, 
            packedLight, 
            OverlayTexture.NO_OVERLAY, 
            1, 1, 1, 1  // RGBA multipliers (white = no tint)
        );
    } // render()

} // Class: InternalLayer
