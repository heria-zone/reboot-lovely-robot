package net.heriazone.lovelylib.hzlib.api.rendering;

import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;

import java.util.function.Predicate;

/**
 * Base texture layer rendering the primary robot appearance.
 * <p>
 * <b>Architecture:</b> Foundation layer that renders the main textured model.
 * Always renders first in layer stack. Texture is resolved from entity's
 * variant and color configuration.
 * <p>
 * <b>Design Decision:</b> Separated from renderer to allow texture swapping
 * without renderer changes. Entity controls texture selection based on state.
 */
public class BaseTextureLayer<T extends LovelyRobotEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final String texturePath;
    private final Predicate<T> renderCondition;

    // -- Constructors --

    /**
     * Creates base texture layer with entity texture resolution.
     * <p>
     * <i>Note:</i> Base texture layer typically uses null texture as it relies
     * on the primary entity renderer for texture resolution.
     */
    public BaseTextureLayer() {
        this.texturePath = null;
        this.renderCondition = entity -> true;
    } // Constructor: BaseTextureLayer()

    /**
     * Creates base texture layer with specific texture path.
     *
     * @param texturePath texture path string
     */
    public BaseTextureLayer(String texturePath) {
        this.texturePath = texturePath;
        this.renderCondition = entity -> true;
    } // Constructor: BaseTextureLayer()

    /**
     * Creates base texture layer with conditional rendering.
     *
     * @param texturePath texture path string
     * @param renderCondition predicate determining if layer should render
     */
    public BaseTextureLayer(String texturePath, Predicate<T> renderCondition) {
        this.texturePath = texturePath;
        this.renderCondition = renderCondition;
    } // Constructor: BaseTextureLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return renderCondition.test(entity);
    } // shouldRender()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        // Convert string path to ResourceLocation
        net.minecraft.resources.ResourceLocation texture = null;
        if (texturePath != null && !texturePath.isEmpty()) {
            texture = net.minecraft.resources.ResourceLocation.parse(texturePath);
        }

        return LayerRenderContext.texture(texture, entity);
    } // getRenderContext()

    /**
     * Gets the texture path for this layer.
     *
     * @return texture path string
     */
    public String getTexturePath() {
        return texturePath;
    } // getTexturePath()

    /**
     * Helper method for backward compatibility with LayerRenderContext-based shouldRender.
     *
     * @param context layer render context
     * @return true if layer should render
     */
    public boolean shouldRender(LayerRenderContext context) {
        return shouldRender((T) context.getEntity(), 0.0f);
    } // shouldRender()

} // Class: BaseTextureLayer