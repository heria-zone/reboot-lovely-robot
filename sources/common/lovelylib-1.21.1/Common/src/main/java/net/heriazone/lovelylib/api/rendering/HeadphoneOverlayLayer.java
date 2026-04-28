package net.heriazone.lovelylib.api.rendering;

import net.heriazone.lovelylib.common.entity.RobotEntity;
import net.heriazone.hzlib.api.rendering.IInternalRenderLayer;
import net.heriazone.hzlib.api.rendering.LayerRenderContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Conditional headphone overlay layer that swaps textures based on entity state.
 * <p>
 * <b>Architecture:</b> Single layer that evaluates conditions in priority order
 * and renders the first matching texture. Replaces multiple conditional layers
 * with one intelligent layer that selects appropriate texture.
 * <p>
 * <b>Design Decision:</b> Headphone overlay with state-based texture swapping.
 * Displays different patterns for idle, auto-attack, base defense, combat mode, etc.
 * Single layer with texture swapping is more efficient than multiple conditional layers.
 */
public class HeadphoneOverlayLayer<T extends RobotEntity> implements IInternalRenderLayer<T> {

    // -- Conditional Texture Entry --

    /**
     * Pairs a condition with its corresponding texture path.
     */
    private static class ConditionalTexture<T extends RobotEntity> {
        final Predicate<T> condition;
        final String texturePath;

        ConditionalTexture(Predicate<T> condition, String texturePath) {
            this.condition = condition;
            this.texturePath = texturePath;
        }
    } // Class: ConditionalTexture

    // -- Fields --

    private final String defaultTexturePath;
    private final List<ConditionalTexture<T>> conditionalTextures = new ArrayList<>();

    // -- Constructor --

    /**
     * Creates headphone overlay layer with default texture.
     *
     * @param defaultTexturePath texture path to render when no conditions match (idle state)
     */
    public HeadphoneOverlayLayer(String defaultTexturePath) {
        this.defaultTexturePath = defaultTexturePath;
    } // Constructor: HeadphoneOverlayLayer()

    // -- Configuration --

    /**
     * Adds conditional texture that renders when condition is met.
     *
     * @param condition predicate determining if this texture should render
     * @param texturePath texture path to render when condition is true
     * @return this layer for method chaining
     */
    public HeadphoneOverlayLayer<T> addConditionalTexture(Predicate<T> condition, String texturePath) {
        conditionalTextures.add(new ConditionalTexture<>(condition, texturePath));
        return this;
    } // addConditionalTexture()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return shouldShowHeadphones(entity);
    } // shouldRender()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        // Get the selected texture for the current entity state
        String selectedTexturePath = getSelectedTexture(LayerRenderContext.texture(null, entity));

        // Convert string path to ResourceLocation
        net.minecraft.resources.ResourceLocation texture = null;
        if (selectedTexturePath != null && !selectedTexturePath.isEmpty()) {
            texture = net.minecraft.resources.ResourceLocation.parse(selectedTexturePath);
        }

        return LayerRenderContext.texture(texture, entity);
    } // getRenderContext()

    /**
     * Gets the selected texture path based on entity conditions.
     *
     * @param context layer render context
     * @return texture path for current entity state
     */
    public String getSelectedTexture(LayerRenderContext context) {
        T entity = (T) context.getEntity();

        // Select texture based on conditions (first match wins)
        for (ConditionalTexture<T> conditional : conditionalTextures) {
            if (conditional.condition.test(entity)) {
                return conditional.texturePath;
            }
        }

        return defaultTexturePath;
    } // getSelectedTexture()

    // -- Headphone Logic --

    /**
     * Determines if headphones should be visible on this entity.
     *
     * @param entity robot entity to check
     * @return true if headphones should render, false otherwise
     */
    private static boolean shouldShowHeadphones(RobotEntity entity) {
        // Check if entity supports headphones
        if (!entity.supportsHeadphones()) return false;

        // Check entity-specific headphone setting
        if (!entity.hasHeadphonesEnabled()) return false;

        return true;
    } // shouldShowHeadphones()

    /**
     * Helper method for backward compatibility with LayerRenderContext-based shouldRender.
     *
     * @param context layer render context
     * @return true if layer should render
     */
    public boolean shouldRender(LayerRenderContext context) {
        return shouldRender((T) context.getEntity(), 0.0f);
    } // shouldRender()

} // Class: HeadphoneOverlayLayer