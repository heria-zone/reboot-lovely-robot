package net.heriazone.hzlib.api.rendering;

import net.heriazone.hzlib.api.entity.NativeEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Abstract base class for internal render layers with common functionality.
 * <p>
 * <b>Architecture:</b> Provides shared implementation for layer management while
 * allowing subclasses to focus on specific rendering logic. Handles texture
 * resolution, condition checking, and color calculation patterns.
 * <p>
 * <b>Design Decision:</b> Abstract class rather than interface allows sharing
 * common implementations while maintaining flexibility for specialized layers.
 */
public abstract class BaseInternalRenderLayer<T extends NativeEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    protected final ResourceLocation texture;
    protected final Predicate<T> renderCondition;
    protected final Function<T, Integer> colorProvider;

    // -- Constructors --

    /**
     * Creates layer with texture and default settings.
     *
     * @param texture layer texture resource location
     */
    protected BaseInternalRenderLayer(ResourceLocation texture) {
        this(texture, entity -> true, entity -> 0xFFFFFFFF);
    } // Constructor: BaseInternalRenderLayer()

    /**
     * Creates layer with texture and render condition.
     *
     * @param texture layer texture resource location
     * @param renderCondition predicate determining if layer should render
     */
    protected BaseInternalRenderLayer(ResourceLocation texture, Predicate<T> renderCondition) {
        this(texture, renderCondition, entity -> 0xFFFFFFFF);
    } // Constructor: BaseInternalRenderLayer()

    /**
     * Creates layer with full configuration.
     *
     * @param texture layer texture resource location
     * @param renderCondition predicate determining if layer should render
     * @param colorProvider function calculating ARGB color from entity state
     */
    protected BaseInternalRenderLayer(ResourceLocation texture, Predicate<T> renderCondition, Function<T, Integer> colorProvider) {
        this.texture = texture;
        this.renderCondition = renderCondition;
        this.colorProvider = colorProvider;
    } // Constructor: BaseInternalRenderLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return texture != null && renderCondition.test(entity);
    } // shouldRender()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        int color = colorProvider.apply(entity);
        return LayerRenderContext.coloredTexture(texture, color, entity);
    } // getRenderContext()

    // -- Utility Methods --

    /**
     * Gets the texture resource location for this layer.
     *
     * @return texture resource location
     */
    public ResourceLocation getTexture() {
        return texture;
    } // getTexture()

} // Class: BaseInternalRenderLayer