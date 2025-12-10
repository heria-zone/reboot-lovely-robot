package net.msymbios.llovelyr.lib.rendering;

import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;

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
public class BaseTextureLayer<T extends LovelyRobotEntity> extends BaseInternalRenderLayer<T> {

    // -- Constructor --

    /**
     * Creates base texture layer with entity texture resolution.
     * <p>
     * <i>Note:</i> Base texture layer typically uses null texture as it relies
     * on the primary entity renderer for texture resolution.
     */
    public BaseTextureLayer() {
        super(null); // Base texture handled by primary renderer
    } // Constructor: BaseTextureLayer()

    // -- IInternalRenderLayer Implementation --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        return true; // Base texture always renders
    } // shouldRender()

    @Override
    public LayerRenderContext getRenderContext(T entity, float partialTick) {
        // Base texture layer uses primary entity texture
        // Actual texture resolution happens in loader-specific renderer
        return LayerRenderContext.texture(null);
    } // getRenderContext()

} // Class: BaseTextureLayer