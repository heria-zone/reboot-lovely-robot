package net.heriazone.hzlib.api.rendering;

import net.heriazone.hzlib.api.entity.NativeEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Ordered container of {@link IInternalRenderLayer} instances for a {@link NativeEntity}.
 * <p>
 * <b>Architecture:</b> Separates layer management from the loader-specific renderer.
 * The renderer holds a {@code LayerRenderPipeline}, asks it for the active layers each
 * frame, and iterates them. This keeps GeckoLib-specific code out of the pipeline and
 * the pipeline free of renderer coupling.
 * <p>
 * Layers are stored and evaluated in registration order, which determines draw order.
 * The list is built once at renderer construction and reused across all render calls.
 */
public class LayerRenderPipeline<T extends NativeEntity> {

    // -- Fields --

    private final List<IInternalRenderLayer<T>> layers;

    // -- Constructor --

    public LayerRenderPipeline() {
        this.layers = new ArrayList<>();
    } // Constructor: LayerRenderPipeline ()

    // -- Layer Management --

    /**
     * Appends a layer to the end of the pipeline.
     * Null layers are silently ignored.
     */
    public LayerRenderPipeline<T> addLayer(IInternalRenderLayer<T> layer) {
        if (layer != null) layers.add(layer);
        return this;
    } // addLayer ()

    /** Removes a layer from the pipeline. No-op if the layer is not present. */
    public LayerRenderPipeline<T> removeLayer(IInternalRenderLayer<T> layer) {
        layers.remove(layer);
        return this;
    } // removeLayer ()

    /** Removes all layers from the pipeline. */
    public LayerRenderPipeline<T> clearLayers() {
        layers.clear();
        return this;
    } // clearLayers ()

    // -- Layer Access --

    /**
     * Returns the subset of layers whose {@link IInternalRenderLayer#shouldRender}
     * returns {@code true} for the given entity and partial tick.
     * Called once per entity per frame by the renderer.
     */
    public List<IInternalRenderLayer<T>> getActiveLayersFor(T entity, float partialTick) {
        return layers.stream()
                .filter(layer -> layer.shouldRender(entity, partialTick))
                .toList();
    } // getActiveLayersFor ()

    /** Returns a snapshot of all registered layers regardless of render conditions. */
    public List<IInternalRenderLayer<T>> getAllLayers() {
        return List.copyOf(layers);
    } // getAllLayers ()

    /** Returns the total number of registered layers. */
    public int getLayerCount() {
        return layers.size();
    } // getLayerCount ()

    /** Returns {@code true} if at least one layer is registered. */
    public boolean hasLayers() {
        return !layers.isEmpty();
    } // hasLayers ()

    /** Returns {@code true} if at least one layer would render for the given entity. */
    public boolean hasActiveLayersFor(T entity, float partialTick) {
        return layers.stream().anyMatch(layer -> layer.shouldRender(entity, partialTick));
    } // hasActiveLayersFor ()

} // Class: LayerRenderPipeline