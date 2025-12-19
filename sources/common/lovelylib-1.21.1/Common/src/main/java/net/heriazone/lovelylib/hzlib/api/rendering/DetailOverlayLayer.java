package net.heriazone.lovelylib.hzlib.api.rendering;

import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Predicate;

/**
 * Detail overlay layer for additional visual elements.
 * <p>
 * <b>Architecture:</b> Renders static overlay textures that add visual detail
 * without affecting the base model. Common uses include decals, markings,
 * wear patterns, or decorative elements.
 * <p>
 * <b>Design Decision:</b> Static overlay vs dynamic generation. Static overlays
 * provide consistent visual quality and performance. Use DynamicColorLayer for
 * state-dependent visual effects.
 */
public class DetailOverlayLayer<T extends LovelyRobotEntity> extends BaseInternalRenderLayer<T> {

    // -- Constructors --

    /**
     * Creates detail overlay layer that always renders.
     *
     * @param overlayTexture overlay texture resource location
     */
    public DetailOverlayLayer(ResourceLocation overlayTexture) {
        super(overlayTexture);
    } // Constructor: DetailOverlayLayer()

    /**
     * Creates detail overlay layer with conditional rendering.
     *
     * @param overlayTexture overlay texture resource location
     * @param renderCondition predicate determining if layer should render
     */
    public DetailOverlayLayer(ResourceLocation overlayTexture, Predicate<T> renderCondition) {
        super(overlayTexture, renderCondition);
    } // Constructor: DetailOverlayLayer()

} // Class: DetailOverlayLayer