package net.heriazone.hzlib.api.rendering;

import net.heriazone.hzlib.api.entity.NativeEntity;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

/**
 * <p>Static factory for render-time {@link Predicate} conditions used with
 * {@link net.heriazone.hzlib.api.entity.features.overlay.OverlaySlot}.<p>
 * <p>
 * <b>Separation from {@code ExchangeConditions}:</b> {@code ExchangeConditions} guards
 * whether an item exchange should fire — a server-side, interaction-time decision.
 * {@code RenderConditions} guards whether a layer should render — a client-side,
 * per-frame decision. Both use {@link Predicate}&lt;T&gt; but serve different lifecycles
 * and contexts.
 * <p>
 * <b>Performance:</b> All predicates here are stateless and evaluated every render tick
 * per visible entity. Keep implementations O(1). {@link #inMonth} reads
 * {@link LocalDate#now()} which is a cheap system call cached by the JVM — safe per-frame.
 * <p>
 * <b>Composability:</b> All returned predicates are standard {@link Predicate} instances
 * and support {@code .and()}, {@code .or()}, {@code .negate()} chaining.
 */
public final class RenderConditions {

    // -- Constructor --

    private RenderConditions() {} // Static utility class

    // -------------------------------------------------------------------------
    // -- Calendar --
    // -------------------------------------------------------------------------

    /**
     * Passes when the current real-world month is any of the given months.
     * <p>
     * <b>Use cases:</b> seasonal costumes — Halloween in October, Christmas in December.
     * <p>
     * <b>Server note:</b> This predicate reads the system clock of the machine evaluating
     * it. In a client-server setup the client renders using its own local time, which is
     * intentional — seasonal cosmetics are a client-side visual, not a server authority.
     *
     * @param months one or more months that satisfy this condition
     * @param <T>    entity type (predicate is entity-agnostic; the entity parameter is ignored)
     * @return month-matching predicate
     */
    public static <T extends NativeEntity> Predicate<T> inMonth(Month... months) {
        Set<Month> monthSet = new HashSet<>(Arrays.asList(months));
        return entity -> monthSet.contains(LocalDate.now().getMonth());
    } // inMonth ()

    /**
     * Passes when the current real-world month is NOT any of the given months.
     *
     * @param months months that cause this condition to fail
     * @param <T>    entity type
     * @return negated month predicate
     */
    public static <T extends NativeEntity> Predicate<T> notInMonth(Month... months) {
        return RenderConditions.<T>inMonth(months).negate();
    } // notInMonth ()

    // -------------------------------------------------------------------------
    // -- Overlay slot state --
    // -------------------------------------------------------------------------

    /**
     * Passes when the named overlay slot is empty (no active texture).
     * <p>
     * <b>Primary use:</b> Jack'o face-cover layer — show the cover when NOT carved.
     * <pre>{@code
     * OverlaySlot.conditional("face_cover",
     *     OverlaySlot.entry(FACE_COVER_TEX, RenderConditions.overlaySlotEmpty("carving")))
     * }</pre>
     *
     * @param slotKey the slot key to inspect
     * @param <T>     entity type
     * @return predicate that passes when the named slot has no active texture
     */
    public static <T extends NativeEntity> Predicate<T> overlaySlotEmpty(String slotKey) {
        return entity -> {
            String val = entity.getOverlaySlot(slotKey);
            return val == null || val.isEmpty();
        };
    } // overlaySlotEmpty ()

    /**
     * Passes when the named overlay slot is non-empty (has an active texture).
     * <p>
     * <b>Primary use:</b> Jack'o carving glow — only show the inner glow when carved.
     *
     * @param slotKey the slot key to inspect
     * @param <T>     entity type
     * @return predicate that passes when the named slot has an active texture
     */
    public static <T extends NativeEntity> Predicate<T> overlaySlotActive(String slotKey) {
        return RenderConditions.<T>overlaySlotEmpty(slotKey).negate();
    } // overlaySlotActive ()

} // Class: RenderConditions
