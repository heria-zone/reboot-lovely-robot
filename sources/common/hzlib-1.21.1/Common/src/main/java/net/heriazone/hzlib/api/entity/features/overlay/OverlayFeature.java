package net.heriazone.hzlib.api.entity.features.overlay;

import java.util.*;

/**
 * <p>Declares the composable visual overlay stack for an entity type.<p>
 * <p>
 * <b>Architecture:</b> Pure Common-module data declaration. No GeckoLib dependency,
 * no {@code ResourceLocation} construction at declaration time. The loader-specific
 * renderer calls {@link #buildLayers} (defined in the loader layer) which materialises
 * the slots into {@code IInternalRenderLayer} instances using the loader's layer classes.
 * <p>
 * <b>Slot ordering:</b> Slots render in declaration order. Declare them in the intended
 * stacking order: base detail → belly → seasonal costume → carving → emissive glow.
 * <p>
 * <b>Persistence contract:</b> {@link #getPersistentSlots()} returns only slots whose
 * {@link SlotMode} requires synced entity data ({@code RANDOM} and {@code INTERACTIVE}).
 * The entity base class iterates this list to register {@code SynchedEntityData} accessors
 * and include them in NBT save/load.
 * <p>
 * <b>Usage:</b>
 * <pre>{@code
 * .withFeature(OverlayFeature.class, new OverlayFeature()
 *     .addSlot("hair", OverlaySlot.random("hair",
 *         "mandrake_hair_straight.png",
 *         "mandrake_hair_curly.png"))
 *     .addSlot("belly", OverlaySlot.conditional("belly",
 *         OverlaySlot.entry("belly_chonky.png", e -> belly(e) >= CHONKY),
 *         OverlaySlot.entry("belly_tummy.png",  e -> belly(e) >= TUMMY)))
 *     .addSlot("carving", OverlaySlot.interactive("carving",
 *         OverlaySlot.entry(""),
 *         OverlaySlot.entry("carving_smile_mask.png", GourdragoraType::getCarvingColor))))
 * }</pre>
 */
public class OverlayFeature {

    // -- Fields --

    private final List<OverlaySlot> slots = new ArrayList<>();

    // -- Fluent configuration --

    /**
     * Adds a pre-built {@link OverlaySlot} to this feature.
     * <p>
     * Slots render in declaration order — call {@code addSlot} in bottom-to-top order.
     * The slot's key is already embedded in the {@link OverlaySlot}; pass the same key
     * as the first argument to make call sites readable and consistent.
     *
     * @param slot the slot declaration (key must be set via the factory)
     * @return this feature for method chaining
     */
    public OverlayFeature addSlot(OverlaySlot slot) {
        Objects.requireNonNull(slot, "OverlaySlot cannot be null");
        slots.add(slot);
        return this;
    } // addSlot ()

    // -- Access --

    /**
     * Returns all declared overlay slots in declaration order.
     *
     * @return unmodifiable view of the slot list
     */
    public List<OverlaySlot> getSlots() {
        return Collections.unmodifiableList(slots);
    } // getSlots ()

    /**
     * Returns the slot registered under {@code key}, or {@code null} if absent.
     *
     * @param key slot key (use the constant from the entity type class)
     * @return matching slot, or null
     */
    public OverlaySlot getSlot(String key) {
        for (OverlaySlot s : slots) {
            if (s.getKey().equals(key)) return s;
        }
        return null;
    } // getSlot ()

    /**
     * Returns only slots whose mode requires persistent synced entity data.
     * <p>
     * The entity base class iterates this list to register {@code SynchedEntityData}
     * accessors and include them in NBT serialization.
     *
     * @return list of {@link SlotMode#RANDOM} and {@link SlotMode#INTERACTIVE} slots
     */
    public List<OverlaySlot> getPersistentSlots() {
        List<OverlaySlot> result = new ArrayList<>();
        for (OverlaySlot s : slots) {
            if (s.getMode() == SlotMode.RANDOM || s.getMode() == SlotMode.INTERACTIVE) {
                result.add(s);
            }
        }
        return result;
    } // getPersistentSlots ()

    /**
     * Returns {@code true} if this entity type has at least one persistent overlay slot.
     */
    public boolean hasPersistentSlots() {
        return !getPersistentSlots().isEmpty();
    } // hasPersistentSlots ()

} // Class: OverlayFeature
