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

    /** Cached texture for ALWAYS slots — resolved once at construction (static path only). */
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

        // Pre-resolve texture for ALWAYS slots with a static (non-dynamic) path.
        // Dynamic ALWAYS slots (path depends on entity) must be resolved per-frame — no cache.
        if (slot.getMode() == SlotMode.ALWAYS && !slot.getPool().isEmpty()) {
            OverlaySlot.Entry e = slot.getPool().get(0);
            if (!e.hasDynamicPath()) {
                String path = e.getTexturePath();
                this.alwaysTexture = path.isEmpty() ? null : ResourceLocation.parse(path);
            } else {
                this.alwaysTexture = null; // resolved per-frame via entity
            }
        } else {
            this.alwaysTexture = null;
        }
    } // Constructor: OverlayLayer ()

    // -- IInternalRenderLayer --

    @Override
    public boolean shouldRender(T entity, float partialTick) {
        // For RANDOM/INTERACTIVE slots, check the render condition gate first
        // (e.g. hat slot only shows in October — the inMonth check lives here).
        if ((slot.getMode() == SlotMode.RANDOM || slot.getMode() == SlotMode.INTERACTIVE)
                && !slot.testRenderCondition(entity)) {
            return false;
        }
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

        if (slot.isEmissive()) {
            // Emissive path: full-brightness rendering, unaffected by world light.
            // Equivalent to EmissiveLayer — uses RenderType.eyes + max packed light.
            RenderType emissiveRenderType = RenderType.eyes(texture);
            renderer.reRender(
                    bakedModel,
                    poseStack,
                    bufferSource,
                    entity,
                    emissiveRenderType,
                    bufferSource.getBuffer(emissiveRenderType),
                    partialTick,
                    15728880, // 0xF000F0 — maximum packed light (full brightness)
                    OverlayTexture.NO_OVERLAY,
                    -1 // white tint — no colour modification
            );
        } else {
            // Standard overlay path: lit by world lighting, optional colour tint.
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
        }
    } // render ()

    // -- Private helpers --

    /**
     * Returns the active texture path for the current entity state, or empty string.
     * This is the single dispatch point for all four slot modes.
     */
    private String resolveTexturePath(T entity) {
        return switch (slot.getMode()) {
            case ALWAYS -> {
                // Static ALWAYS: use cached ResourceLocation; dynamic ALWAYS: call path resolver.
                if (alwaysTexture != null) yield alwaysTexture.toString();
                if (!slot.getPool().isEmpty()) yield slot.getPool().get(0).getTexturePath(entity);
                yield "";
            }
            case CONDITIONAL -> slot.resolveConditional(entity);
            case RANDOM, INTERACTIVE -> {
                // Stage key stored in SynchedEntityData — look up the matching entry to get
                // the dynamic or static render-time path.
                String stageKey = entity.getOverlaySlot(slot.getKey());
                if (stageKey == null || stageKey.isEmpty()) yield "";
                OverlaySlot.Entry entry = slot.findEntry(stageKey);
                if (entry == null) yield stageKey; // fallback: treat key as direct path
                yield entry.getTexturePath(entity);  // dynamic path resolver or fixed path
            }
        };
    } // resolveTexturePath ()

    /**
     * Returns the ARGB tint color for the active texture path.
     * For RANDOM/INTERACTIVE slots, looks up the entry by stage key (SynchedEntityData value)
     * so the color provider is found even when the rendered path is dynamic.
     * Falls back to {@code -1} (white = no tint) for entries without a provider.
     */
    private int resolveColor(T entity, String activePath) {
        OverlaySlot.Entry entry = switch (slot.getMode()) {
            case RANDOM, INTERACTIVE -> {
                String stageKey = entity.getOverlaySlot(slot.getKey());
                yield (stageKey != null && !stageKey.isEmpty()) ? slot.findEntry(stageKey) : null;
            }
            default -> slot.findEntry(activePath);
        };
        if (entry == null || !entry.hasColor()) return -1; // -1 = 0xFFFFFFFF white
        return entry.getColor(entity);
    } // resolveColor ()

} // Class: OverlayLayer
