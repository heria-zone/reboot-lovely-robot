package net.msymbios.llovelyr.lib.rendering;

import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages and coordinates multiple render layers for robot entities.
 * <p>
 * <b>Architecture:</b> Central coordinator for layer-based rendering system.
 * Maintains ordered list of layers and provides iteration interface for
 * loader-specific renderers. Separates layer management from GeckoLib integration.
 * <p>
 * <b>Design Decision:</b> Helper class rather than inheritance allows flexible
 * integration with existing renderer hierarchies. Layers are processed in
 * registration order for predictable rendering results.
 * <p>
 * <b>Performance:</b> Layer list is built once during initialization and reused
 * across all rendering calls. shouldRender() checks prevent unnecessary work.
 */
public class InternalLayerRenderer<T extends LovelyRobotEntity> {

    // -- Fields --

    private final List<IInternalRenderLayer<T>> layers;

    // -- Constructor --

    /**
     * Creates layer renderer with empty layer list.
     */
    public InternalLayerRenderer() {
        this.layers = new ArrayList<>();
    } // Constructor: InternalLayerRenderer()

    // -- Layer Management --

    /**
     * Adds a render layer to the rendering pipeline.
     * <p>
     * <b>Order:</b> Layers render in the order they are added. Typically:
     * 1. Base texture layer
     * 2. Detail overlays
     * 3. Dynamic color indicators
     * 4. Emissive effects
     * 5. Special overlays (headphones, etc.)
     *
     * @param layer render layer to add
     * @return this renderer for method chaining
     */
    public InternalLayerRenderer<T> addLayer(IInternalRenderLayer<T> layer) {
        if (layer != null) {
            layers.add(layer);
        }
        return this;
    } // addLayer()

    /**
     * Removes a render layer from the rendering pipeline.
     *
     * @param layer render layer to remove
     * @return this renderer for method chaining
     */
    public InternalLayerRenderer<T> removeLayer(IInternalRenderLayer<T> layer) {
        layers.remove(layer);
        return this;
    } // removeLayer()

    /**
     * Clears all render layers.
     *
     * @return this renderer for method chaining
     */
    public InternalLayerRenderer<T> clearLayers() {
        layers.clear();
        return this;
    } // clearLayers()

    // -- Layer Access --

    /**
     * Gets all render layers that should render for the given entity.
     * <p>
     * <b>Performance:</b> Filters layers based on shouldRender() to avoid
     * unnecessary rendering work. Called once per entity per frame.
     *
     * @param entity robot entity being rendered
     * @param partialTick sub-tick interpolation for smooth animation
     * @return list of layers that should render
     */
    public List<IInternalRenderLayer<T>> getActiveLayersFor(T entity, float partialTick) {
        return layers.stream()
                .filter(layer -> layer.shouldRender(entity, partialTick))
                .toList();
    } // getActiveLayersFor()

    /**
     * Gets all registered render layers (regardless of render conditions).
     *
     * @return unmodifiable list of all layers
     */
    public List<IInternalRenderLayer<T>> getAllLayers() {
        return List.copyOf(layers);
    } // getAllLayers()

    /**
     * Gets the number of registered layers.
     *
     * @return layer count
     */
    public int getLayerCount() {
        return layers.size();
    } // getLayerCount()

    // -- Utility Methods --

    /**
     * Checks if any layers are registered.
     *
     * @return true if layers exist, false if empty
     */
    public boolean hasLayers() {
        return !layers.isEmpty();
    } // hasLayers()

    /**
     * Checks if any layers would render for the given entity.
     *
     * @param entity robot entity to check
     * @param partialTick sub-tick interpolation
     * @return true if at least one layer would render
     */
    public boolean hasActiveLayersFor(T entity, float partialTick) {
        return layers.stream().anyMatch(layer -> layer.shouldRender(entity, partialTick));
    } // hasActiveLayersFor()

} // Class: InternalLayerRenderer