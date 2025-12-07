package net.msymbios.llovelyr.lib.entity.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Conditional overlay layer that swaps textures based on entity state.
 * <p>
 * <b>Architecture:</b> Single layer that evaluates conditions in priority order
 * and renders the first matching texture. Replaces multiple conditional DetailOverlayLayers
 * with one intelligent layer that selects appropriate texture.
 * <p>
 * <b>Design Decision:</b> Headphone overlay as example - displays different patterns
 * for idle, auto-attack, base defense, combat mode, etc. Single layer with texture
 * swapping is more efficient than multiple conditional layers.
 * <p>
 * <b>Usage Example:</b>
 * <pre>{@code
 * new HeadphoneOverlayLayer<>(renderer, HEADPHONE_IDLE)
 *     .addConditionalTexture(entity -> entity.getCurrentState() == EntityState.Defense, HEADPHONE_DEFENSE)
 *     .addConditionalTexture(entity -> entity.getAutoAttack(), HEADPHONE_ATTACK)
 *     .addConditionalTexture(entity -> entity.isInCombat(), HEADPHONE_COMBAT);
 * }</pre>
 * <p>
 * <b>Performance:</b> Single render pass with texture selection. Conditions evaluated
 * in order until first match. More efficient than multiple overlay layers.
 */
public class HeadphoneOverlayLayer<T extends LovelyRobotEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Conditional Texture Entry --

    /**
     * Pairs a condition with its corresponding texture.
     */
    private static class ConditionalTexture<T extends LovelyRobotEntity & GeoEntity> {
        final Predicate<T> condition;
        final ResourceLocation texture;

        ConditionalTexture(Predicate<T> condition, ResourceLocation texture) {
            this.condition = condition;
            this.texture = texture;
        }
    } // Class: ConditionalTexture

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final ResourceLocation defaultTexture;
    private final List<ConditionalTexture<T>> conditionalTextures = new ArrayList<>();

    // -- Constructor --

    /**
     * Creates headphone overlay layer with default texture.
     * <p>
     * <b>Architecture:</b> Default texture renders when no conditions match.
     * Use addConditionalTexture() to add state-specific overlays.
     *
     * @param renderer parent GeoRenderer managing this entity
     * @param defaultTexture texture to render when no conditions match (idle state)
     */
    public HeadphoneOverlayLayer(GeoRenderer<T> renderer, ResourceLocation defaultTexture) {
        this.renderer = renderer;
        this.defaultTexture = defaultTexture;
    } // Constructor: HeadphoneOverlayLayer()

    // -- Configuration --

    /**
     * Adds conditional texture that renders when condition is met.
     * <p>
     * <b>Architecture:</b> Conditions evaluated in order added. First matching
     * condition determines rendered texture. Add highest priority conditions first.
     * <p>
     * <b>Design Decision:</b> Builder pattern allows fluent configuration during
     * renderer construction.
     *
     * @param condition predicate determining if this texture should render
     * @param texture texture to render when condition is true
     * @return this layer for method chaining
     */
    public HeadphoneOverlayLayer<T> addConditionalTexture(Predicate<T> condition, ResourceLocation texture) {
        conditionalTextures.add(new ConditionalTexture<>(condition, texture));
        return this;
    } // addConditionalTexture()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return true; // Always render (texture selection happens in render())
    } // shouldRender()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel, RenderType renderType,
                       MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        // Select texture based on conditions (first match wins)
        ResourceLocation selectedTexture = defaultTexture;
        for (ConditionalTexture<T> conditional : conditionalTextures) {
            if (conditional.condition.test(entity)) {
                selectedTexture = conditional.texture;
                break; // First matching condition wins
            }
        }

        // Render with selected texture
        RenderType overlayRenderType = RenderType.armorCutoutNoCull(selectedTexture);

        renderer.reRender(
                bakedModel,
                poseStack,
                bufferSource,
                entity,
                overlayRenderType,
                bufferSource.getBuffer(overlayRenderType),
                partialTick,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                -1 // White tint
        );
    } // render()

} // Class: HeadphoneOverlayLayer
