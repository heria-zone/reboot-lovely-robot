package net.heriazone.hzlib.api.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.heriazone.hzlib.api.entity.InternalEntity;
import net.heriazone.hzlib.api.entity.features.overlay.OverlayFeature;
import net.heriazone.hzlib.api.entity.features.overlay.OverlaySlot;
import net.heriazone.hzlib.api.entity.features.overlay.SlotMode;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

/**
 * Fabric render layer for a single {@link OverlaySlot} declared on an entity type's
 * {@link OverlayFeature}.
 * <p>
 * <b>Architecture:</b> One {@code OverlayLayer} instance per slot. The renderer's
 * {@code buildOverlayLayers()} call creates one instance per slot declared on the
 * entity type and registers them in declaration order via {@code addLayer()}.
 * <p>
 * <b>Slot mode routing per frame:</b>
 * <ul>
 *   <li>{@link SlotMode#ALWAYS} — texture is fixed at construction; rendered unconditionally.</li>
 *   <li>{@link SlotMode#CONDITIONAL} — evaluates entries in declaration order; first passing
 *       entry's path is rendered. Stateless — no entity data involved.</li>
 *   <li>{@link SlotMode#RANDOM} — reads {@link InternalEntity#getOverlaySlot(String)} for the
 *       path chosen at spawn. If the entry has a color provider, uses {@code DynamicColorLayer}
 *       render path (grayscale mask × ARGB tint).</li>
 *   <li>{@link SlotMode#INTERACTIVE} — same as RANDOM read path; state was set by player action.</li>
 * </ul>
 * <p>
 * <b>Color tinting:</b> When the active entry has a non-null color provider, the texture is
 * treated as a grayscale mask and the color is passed to {@code reRender} as the tint ARGB,
 * using {@code RenderType.armorCutoutNoCull}. This is identical to {@link DynamicColorLayer}'s
 * render path. When no color provider is present, {@code -1} (white/no tint) is passed.
 */
public class OverlayLayer<T extends InternalEntity & GeoEntity> implements IInternalRenderLayer<T> {

    // -- Fields --

    private final GeoRenderer<T> renderer;
    private final OverlaySlot slot;

    /** Cached texture for ALWAYS slots — resolved once at construction. */
    private final ResourceLocation alwaysTexture;

    // -- Constructor --

    /**
     * Creates an overlay layer for the given slot.
     *
     * @param renderer parent GeoRenderer
     * @param slot     the overlay slot declaration to render
     */
    public OverlayLayer(GeoRenderer<T> renderer, OverlaySlot slot) {
        this.renderer = renderer;
        this.slot = slot;

        // Pre-resolve texture for ALWAYS slots so we don't parse the string every frame.
        if (slot.getMode() == SlotMode.ALWAYS && !slot.getPool().isEmpty()) {
            String path = slot.getPool().get(0).getTexturePath();
            this.alwaysTexture = path.isEmpty() ? null : ResourceLocation.parse(path);
        } else {
            this.alwaysTexture = null;
        }
    } // Constructor: OverlayLayer ()

    // -- IInternalRenderLayer --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        // Resolve the active texture path without rendering — return false if empty.
        return !resolveTexturePath(entity).isEmpty();
    } // shouldRender ()

    @Override
    public void render(PoseStack poseStack, T entity, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        String path = resolveTexturePath(entity);
        if (path.isEmpty()) return;

        ResourceLocation texture = ResourceLocation.parse(path);
        int color = resolveColor(entity, path);

        RenderType overlayRenderType = RenderType.armorCutoutNoCull(texture);
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
                color
        );
    } // render ()

    // -- Private helpers --

    /**
     * Returns the active texture path for the current entity state, or empty string.
     * This is the single dispatch point for all four slot modes.
     */
    private String resolveTexturePath(T entity) {
        return switch (slot.getMode()) {
            case ALWAYS -> alwaysTexture != null ? alwaysTexture.toString() : "";
            case CONDITIONAL -> slot.resolveConditional(entity);
            case RANDOM, INTERACTIVE -> entity.getOverlaySlot(slot.getKey());
        };
    } // resolveTexturePath ()

    /**
     * Returns the ARGB tint color for the active texture path.
     * Looks up the matching {@link OverlaySlot.Entry} and calls its color provider if present.
     * Falls back to {@code -1} (white = no tint) for entries without a provider.
     */
    private int resolveColor(T entity, String activePath) {
        OverlaySlot.Entry entry = slot.findEntry(activePath);
        if (entry == null || !entry.hasColor()) return -1; // -1 = 0xFFFFFFFF white
        return entry.getColor(entity);
    } // resolveColor ()

} // Class: OverlayLayer
