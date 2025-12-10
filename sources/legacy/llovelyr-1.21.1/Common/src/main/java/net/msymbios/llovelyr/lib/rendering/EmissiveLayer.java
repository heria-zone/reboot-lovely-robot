package net.msymbios.llovelyr.lib.rendering;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Emissive layer for glowing visual effects.
 * <p>
 * <b>Architecture:</b> Renders emissive textures that appear to glow by ignoring
 * lighting conditions. Common uses include LED indicators, energy cores, eyes,
 * or magical effects that should remain bright in darkness.
 * <p>
 * <b>Design Decision:</b> Separate emissive layer vs emissive materials. Layer
 * approach allows mixing emissive and non-emissive parts of the same texture
 * while maintaining compatibility with existing models.
 * <p>
 * <b>Performance:</b> Emissive rendering typically uses fullbright lighting,
 * which may have different performance characteristics than normal lighting.
 */
public class EmissiveLayer<T extends LovelyRobotEntity> extends BaseInternalRenderLayer<T> {

    // -- Constructors --

    /**
     * Creates emissive layer that always renders.
     *
     * @param emissiveTexture emissive texture resource location
     */
    public EmissiveLayer(ResourceLocation emissiveTexture) {
        super(emissiveTexture);
    } // Constructor: EmissiveLayer()

    /**
     * Creates emissive layer with conditional rendering.
     *
     * @param emissiveTexture emissive texture resource location
     * @param renderCondition predicate determining if layer should render
     */
    public EmissiveLayer(ResourceLocation emissiveTexture, Predicate<T> renderCondition) {
        super(emissiveTexture, renderCondition);
    } // Constructor: EmissiveLayer()

    /**
     * Creates emissive layer with color modulation.
     *
     * @param emissiveTexture emissive texture resource location
     * @param colorProvider function calculating ARGB color from entity state
     */
    public EmissiveLayer(ResourceLocation emissiveTexture, Function<T, Integer> colorProvider) {
        super(emissiveTexture, entity -> true, colorProvider);
    } // Constructor: EmissiveLayer()

    /**
     * Creates emissive layer with full configuration.
     *
     * @param emissiveTexture emissive texture resource location
     * @param renderCondition predicate determining if layer should render
     * @param colorProvider function calculating ARGB color from entity state
     */
    public EmissiveLayer(ResourceLocation emissiveTexture, Predicate<T> renderCondition, Function<T, Integer> colorProvider) {
        super(emissiveTexture, renderCondition, colorProvider);
    } // Constructor: EmissiveLayer()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        LayerRenderContext baseContext = super.getRenderContext(entity, partialTick);
        // Emissive layers should not use overlay lighting
        return new LayerRenderContext(
                baseContext.texture(),
                baseContext.color(),
                baseContext.alpha(),
                false, // No overlay for emissive
                baseContext.additionalConditions()
        );
    } // getRenderContext()

} // Class: EmissiveLayer