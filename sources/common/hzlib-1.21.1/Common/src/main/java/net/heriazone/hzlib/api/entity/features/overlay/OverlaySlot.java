package net.heriazone.hzlib.api.entity.features.overlay;

import net.heriazone.hzlib.api.entity.InternalEntity;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>Declares one visual overlay slot — its key, mode, texture pool, optional
 * per-entry conditions, and optional per-entry color tint providers.<p>
 * <p>
 * <b>Architecture:</b> Pure data declaration, no GeckoLib dependency. The loader-side
 * overlay layer reads slot state via {@link InternalEntity#getOverlaySlot(String)} and
 * uses the entry's color provider (if present) to tint the grayscale mask texture.
 * <p>
 * <b>Slot key constants:</b> Always declare slot keys as {@code public static final String}
 * constants on the entity type class (e.g., {@code MandrakeType.SLOT_HAIR = "hair"}) and
 * pass those constants to all call sites. Raw string literals at call sites are a typo risk
 * with no compile-time detection.
 * <p>
 * <b>Color tinting:</b> When an {@link Entry} carries a non-null {@code colorProvider},
 * the loader-side renderer materialises it as a {@code DynamicColorLayer} (grayscale mask ×
 * ARGB tint). When null, a plain {@code DetailOverlayLayer} is used. This is how Golden
 * and Lumina Gourdragora share one grayscale carving texture with different tints.
 */
public final class OverlaySlot {

    // -- Inner: Entry --

    /**
     * One candidate texture in a slot's pool, together with its optional predicate
     * (CONDITIONAL mode) and optional color provider (any mode).
     */
    public static final class Entry {

        private final String texturePath;
        private final Predicate<InternalEntity> condition;
        private final Function<InternalEntity, Integer> colorProvider;

        private Entry(String texturePath,
                      Predicate<InternalEntity> condition,
                      Function<InternalEntity, Integer> colorProvider) {
            this.texturePath   = texturePath != null ? texturePath : "";
            this.condition     = condition;
            this.colorProvider = colorProvider;
        } // Constructor: Entry ()

        /** Returns the texture path for this entry. Empty string = no render pass. */
        public String getTexturePath() { return texturePath; }

        /** Returns {@code true} if this entry's condition passes for the given entity. */
        public boolean test(InternalEntity entity) {
            return condition == null || condition.test(entity);
        } // test ()

        /** Returns {@code true} if this entry carries a color provider (grayscale tint). */
        public boolean hasColor() { return colorProvider != null; }

        /**
         * Calculates the ARGB tint color for this entry.
         * Returns {@code 0xFFFFFFFF} (white = no tint) when no provider is set.
         */
        public int getColor(InternalEntity entity) {
            return colorProvider != null ? colorProvider.apply(entity) : 0xFFFFFFFF;
        } // getColor ()

    } // Class: Entry

    // -- Fields --

    private final String key;
    private final SlotMode mode;
    private final List<Entry> pool;

    // -- Constructor --

    private OverlaySlot(String key, SlotMode mode, List<Entry> pool) {
        this.key  = Objects.requireNonNull(key,  "Slot key cannot be null");
        this.mode = Objects.requireNonNull(mode, "SlotMode cannot be null");
        this.pool = Collections.unmodifiableList(new ArrayList<>(pool));
    } // Constructor: OverlaySlot ()

    // -- Accessors --

    /** Returns the unique key identifying this slot on this entity type. */
    public String getKey()       { return key;  }

    /** Returns the selection mode governing how this slot's active texture is chosen. */
    public SlotMode getMode()    { return mode; }

    /** Returns the ordered pool of entries (textures / conditions / color providers). */
    public List<Entry> getPool() { return pool; }

    // -------------------------------------------------------------------------
    // -- ALWAYS factory --
    // -------------------------------------------------------------------------

    /**
     * Creates an unconditional slot that always renders {@code texturePath}.
     * No entity state is needed.
     *
     * @param key         slot key constant (e.g., {@code MyType.SLOT_GLOW})
     * @param texturePath full resource location string of the overlay texture
     */
    public static OverlaySlot always(String key, String texturePath) {
        return new OverlaySlot(key, SlotMode.ALWAYS,
                List.of(new Entry(texturePath, null, null)));
    } // always ()

    // -------------------------------------------------------------------------
    // -- CONDITIONAL factory --
    // -------------------------------------------------------------------------

    /**
     * Creates a conditional slot. Entries are evaluated in declaration order;
     * the first passing entry renders. No entity state is persisted.
     *
     * @param key     slot key constant
     * @param entries ordered predicate/texture pairs; use {@link #entry(String, Predicate)}
     */
    public static OverlaySlot conditional(String key, Entry... entries) {
        return new OverlaySlot(key, SlotMode.CONDITIONAL, Arrays.asList(entries));
    } // conditional ()

    /**
     * Creates a {@code CONDITIONAL} entry — texture path + predicate.
     */
    public static Entry entry(String texturePath, Predicate<InternalEntity> condition) {
        return new Entry(texturePath, condition, null);
    } // entry (conditional) ()

    /**
     * Creates a {@code CONDITIONAL} entry — texture path + predicate + color tint.
     * The color provider runs every render tick; keep it lightweight.
     */
    public static Entry entry(String texturePath,
                              Predicate<InternalEntity> condition,
                              Function<InternalEntity, Integer> colorProvider) {
        return new Entry(texturePath, condition, colorProvider);
    } // entry (conditional + color) ()

    // -------------------------------------------------------------------------
    // -- RANDOM factory --
    // -------------------------------------------------------------------------

    /**
     * Creates a random-at-spawn slot. One texture is chosen uniformly from
     * {@code texturePaths} at {@code finalizeSpawn} and persisted in synced entity data.
     *
     * @param key          slot key constant
     * @param texturePaths pool of texture paths to choose from
     */
    public static OverlaySlot random(String key, String... texturePaths) {
        List<Entry> entries = new ArrayList<>();
        for (String p : texturePaths) entries.add(new Entry(p, null, null));
        return new OverlaySlot(key, SlotMode.RANDOM, entries);
    } // random ()

    /**
     * Creates a random-at-spawn slot where all textures share one color provider
     * (evaluated at render time, not at spawn).
     * <p>
     * Example: a random hair texture tinted by the entity's appearance color.
     *
     * @param key           slot key constant
     * @param colorProvider ARGB provider evaluated every render tick
     * @param texturePaths  pool of grayscale mask textures
     */
    public static OverlaySlot randomColored(String key,
                                            Function<InternalEntity, Integer> colorProvider,
                                            String... texturePaths) {
        List<Entry> entries = new ArrayList<>();
        for (String p : texturePaths) entries.add(new Entry(p, null, colorProvider));
        return new OverlaySlot(key, SlotMode.RANDOM, entries);
    } // randomColored ()

    // -------------------------------------------------------------------------
    // -- INTERACTIVE factory --
    // -------------------------------------------------------------------------

    /**
     * Creates a player-cycled slot. A tool interaction (e.g., shears) advances the
     * active index. Persistent in synced entity data + NBT.
     * <p>
     * Convention: {@code entries[0]} is the "none active" state — use
     * {@code OverlaySlot.entry("")} (empty path = no render pass).
     *
     * @param key     slot key constant
     * @param entries ordered pool; use {@link #entry(String)} or
     *                {@link #entry(String, Function)} for each stage
     */
    public static OverlaySlot interactive(String key, Entry... entries) {
        return new OverlaySlot(key, SlotMode.INTERACTIVE, Arrays.asList(entries));
    } // interactive ()

    /**
     * Creates an {@code INTERACTIVE} entry — texture path only.
     * Empty string is a valid "uncarved / no overlay" sentinel.
     */
    public static Entry entry(String texturePath) {
        return new Entry(texturePath, null, null);
    } // entry (interactive) ()

    /**
     * Creates an {@code INTERACTIVE} entry — texture path + color tint provider.
     * Used for Golden/Lumina carving patterns: one grayscale texture tinted per variant.
     */
    public static Entry entry(String texturePath, Function<InternalEntity, Integer> colorProvider) {
        return new Entry(texturePath, null, colorProvider);
    } // entry (interactive + color) ()

    // -------------------------------------------------------------------------
    // -- Runtime helpers --
    // -------------------------------------------------------------------------

    /**
     * Picks a random texture path from the pool.
     * Only meaningful for {@link SlotMode#RANDOM} slots.
     *
     * @return randomly chosen path, or empty string if pool is empty
     */
    public String pickRandom() {
        if (pool.isEmpty()) return "";
        return pool.get(new Random().nextInt(pool.size())).getTexturePath();
    } // pickRandom ()

    /**
     * Returns the next texture path after {@code current} in the pool, wrapping at the end.
     * Only meaningful for {@link SlotMode#INTERACTIVE} slots.
     *
     * @param current currently active texture path
     * @return next texture path in the cycle
     */
    public String cycleNext(String current) {
        if (pool.isEmpty()) return "";
        int idx = 0;
        for (int i = 0; i < pool.size(); i++) {
            if (pool.get(i).getTexturePath().equals(current)) { idx = i; break; }
        }
        return pool.get((idx + 1) % pool.size()).getTexturePath();
    } // cycleNext ()

    /**
     * Evaluates CONDITIONAL entries in order and returns the first matching path.
     * Returns empty string if no entry matches.
     *
     * @param entity entity to evaluate predicates against
     * @return active texture path, or empty string
     */
    public String resolveConditional(InternalEntity entity) {
        for (Entry e : pool) {
            if (e.test(entity)) return e.getTexturePath();
        }
        return "";
    } // resolveConditional ()

    /**
     * Returns the {@link Entry} whose texture path equals {@code activePath}, or
     * {@code null} if not found. Used by the renderer to retrieve the color provider
     * for the currently active texture.
     *
     * @param activePath active texture path
     * @return matching entry, or null
     */
    public Entry findEntry(String activePath) {
        if (activePath == null || activePath.isEmpty()) return null;
        for (Entry e : pool) {
            if (e.getTexturePath().equals(activePath)) return e;
        }
        return null;
    } // findEntry ()

    /**
     * Returns the default (first pool entry) texture path.
     * Used as the NBT fallback for old-save entities that predate {@code OverlayFeature}.
     *
     * @return first entry's path, or empty string if pool is empty
     */
    public String getDefault() {
        return pool.isEmpty() ? "" : pool.get(0).getTexturePath();
    } // getDefault ()

} // Class: OverlaySlot
