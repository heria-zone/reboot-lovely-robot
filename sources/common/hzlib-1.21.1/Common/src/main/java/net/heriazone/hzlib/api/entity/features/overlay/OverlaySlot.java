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
     * <p>
     * <b>Dynamic texture paths:</b> When the texture path depends on runtime entity
     * state (e.g. Gourdragora's carving texture changes per model size variant),
     * use {@link #entryDynamic(Function, Function)} to supply a path resolver instead
     * of a fixed string. The resolver receives the entity and returns the full path.
     */
    public static final class Entry {

        private final String texturePath;                                    // fixed path, or "" for dynamic
        private final Function<InternalEntity, String> pathResolver;         // null = use fixed texturePath
        private final Predicate<InternalEntity> condition;
        private final Function<InternalEntity, Integer> colorProvider;

        private Entry(String texturePath,
                      Function<InternalEntity, String> pathResolver,
                      Predicate<InternalEntity> condition,
                      Function<InternalEntity, Integer> colorProvider) {
            this.texturePath   = texturePath != null ? texturePath : "";
            this.pathResolver  = pathResolver;
            this.condition     = condition;
            this.colorProvider = colorProvider;
        } // Constructor: Entry ()

        /** Convenience 3-arg constructor (no dynamic path resolver). */
        private Entry(String texturePath,
                      Predicate<InternalEntity> condition,
                      Function<InternalEntity, Integer> colorProvider) {
            this(texturePath, null, condition, colorProvider);
        } // Constructor: Entry (no pathResolver)

        /**
         * Returns the texture path for this entry, resolving dynamically if a path
         * resolver is present, otherwise returning the fixed path.
         *
         * @param entity entity at render time (may be null during static lookup)
         */
        public String getTexturePath(InternalEntity entity) {
            if (pathResolver != null && entity != null) return pathResolver.apply(entity);
            return texturePath;
        } // getTexturePath ()

        /**
         * Returns the fixed texture path. For dynamically-resolved entries this
         * returns the stage key stored at construction time (used as the slot state value).
         */
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

        /** Returns {@code true} if this entry resolves its texture path dynamically. */
        public boolean hasDynamicPath() { return pathResolver != null; }

    } // Class: Entry

    // -- Fields --

    private final String key;
    private final SlotMode mode;
    private final List<Entry> pool;
    /**
     * When true, this slot renders with {@code RenderType.eyes} at maximum brightness
     * instead of the standard {@code armorCutoutNoCull} render type.
     * Set via {@link #asEmissive()}.
     */
    private boolean emissive = false;

    /**
     * Optional per-frame render gate for RANDOM and INTERACTIVE slots.
     * <p>
     * When non-null, the slot only renders (via {@code OverlayLayer}) when this predicate
     * passes. Used to suppress a RANDOM slot outside a seasonal window — e.g., the mushroom
     * hat slot stores which hat was chosen at spawn, but only renders it in October.
     * <p>
     * CONDITIONAL and ALWAYS slots have their own condition mechanisms; this field is
     * ignored for those modes.
     */
    private Predicate<InternalEntity> renderCondition = null;

    // -- Constructor --

    private OverlaySlot(String key, SlotMode mode, List<Entry> pool) {
        this.key  = Objects.requireNonNull(key,  "Slot key cannot be null");
        this.mode = Objects.requireNonNull(mode, "SlotMode cannot be null");
        this.pool = Collections.unmodifiableList(new ArrayList<>(pool));
    } // Constructor: OverlaySlot ()

    // -- Fluent modifiers --

    /**
     * Marks this slot as emissive — rendered at full brightness using {@code RenderType.eyes},
     * independent of world light level. Use for glow effects, inner light sources, etc.
     *
     * @return this slot for method chaining
     */
    public OverlaySlot asEmissive() {
        this.emissive = true;
        return this;
    } // asEmissive ()

    /**
     * Adds a per-frame render gate for RANDOM and INTERACTIVE slots.
     * The slot's texture is only rendered when {@code condition} passes.
     * <p>
     * Use when a persistently-stored slot choice (RANDOM/INTERACTIVE) should only
     * visually appear under specific conditions — e.g. a hat chosen at spawn that is
     * only shown during October.
     *
     * @param condition per-frame predicate; slot is hidden when this returns {@code false}
     * @return this slot for method chaining
     */
    public OverlaySlot withRenderCondition(Predicate<InternalEntity> condition) {
        this.renderCondition = condition;
        return this;
    } // withRenderCondition ()

    // -- Accessors --

    /** Returns the unique key identifying this slot on this entity type. */
    public String getKey()       { return key;  }

    /** Returns the selection mode governing how this slot's active texture is chosen. */
    public SlotMode getMode()    { return mode; }

    /** Returns the ordered pool of entries (textures / conditions / color providers). */
    public List<Entry> getPool() { return pool; }

    /** Returns {@code true} if this slot should render at full brightness (emissive). */
    public boolean isEmissive()  { return emissive; }

    /**
     * Returns {@code true} if this slot should render for the given entity.
     * Always returns {@code true} when no render condition is set.
     * For CONDITIONAL/ALWAYS slots the pool entries carry their own conditions;
     * this method is only meaningful for RANDOM and INTERACTIVE slots.
     */
    public boolean testRenderCondition(InternalEntity entity) {
        return renderCondition == null || renderCondition.test(entity);
    } // testRenderCondition ()

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
        return new Entry(texturePath, null, condition, null);
    } // entry (conditional) ()

    /**
     * Creates a {@code CONDITIONAL} entry — texture path + predicate + color tint.
     * The color provider runs every render tick; keep it lightweight.
     */
    public static Entry entry(String texturePath,
                              Predicate<InternalEntity> condition,
                              Function<InternalEntity, Integer> colorProvider) {
        return new Entry(texturePath, null, condition, colorProvider);
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

    /**
     * Creates a dynamic-path entry for {@code INTERACTIVE} or {@code RANDOM} slots where the
     * texture path depends on entity runtime state (e.g. the entity's current size variant).
     * <p>
     * <b>Use cases:</b> Gourdragora carving patterns — the layer texture file name includes a
     * size segment ({@code mini} / {@code default} / {@code big}) read from the entity's
     * current {@code MODEL_VARIANT} at render time. Authoring separate static entries per size
     * is impossible when the entity can change size after registration.
     * <p>
     * <b>INTERACTIVE cycling:</b> Because the pool stores entries by {@link Entry#getTexturePath()}
     * (fixed path) for {@link #cycleNext(String)}, dynamic entries must still carry a non-null
     * stage key as the fixed path — typically a short stage identifier like {@code "stage_01"}.
     * The renderer calls {@link Entry#getTexturePath(InternalEntity)} which returns the dynamic
     * path for rendering, while {@link #cycleNext(String)} uses the stable stage key for state.
     *
     * @param stageKey     stable stage identifier stored in {@code SynchedEntityData} (e.g. {@code "stage_01"})
     * @param pathResolver function from entity → full resource location string
     * @return dynamic interactive entry
     */
    public static Entry entryDynamic(String stageKey, Function<InternalEntity, String> pathResolver) {
        return new Entry(stageKey, pathResolver, null, null);
    } // entryDynamic ()

    /**
     * Creates a dynamic-path entry with a color tint provider.
     * Combines {@link #entryDynamic(String, Function)} with a per-frame ARGB color.
     * Used for Golden/Lumina carving: dynamic path (size-dependent) + fixed tint color.
     *
     * @param stageKey      stable stage identifier stored in {@code SynchedEntityData}
     * @param pathResolver  function from entity → full resource location string
     * @param colorProvider function from entity → ARGB tint color
     * @return dynamic interactive entry with color tint
     */
    public static Entry entryDynamic(String stageKey,
                                     Function<InternalEntity, String> pathResolver,
                                     Function<InternalEntity, Integer> colorProvider) {
        return new Entry(stageKey, pathResolver, null, colorProvider);
    } // entryDynamic (with color) ()

    /**
     * Creates a dynamic-path {@code CONDITIONAL} entry — path resolver + predicate.
     * <p>
     * Deliberately named differently from {@link #entryDynamic(String, Function, Function)}
     * to avoid Java overload ambiguity when the third argument is a lambda (the compiler
     * cannot distinguish {@code Function<InternalEntity, Integer>} from
     * {@code Predicate<InternalEntity>} without a target type).
     * <p>
     * Use for CONDITIONAL slots where the rendered texture path also depends on runtime
     * entity state (e.g. Jack'o face-cover: dynamic size path, only rendered when the
     * carving slot is empty).
     *
     * @param stageKey     stable key used as the fixed path (also used for {@link #findEntry})
     * @param pathResolver function from entity → full resource location string
     * @param condition    predicate that must pass for this entry to render
     * @return dynamic conditional entry
     */
    public static Entry entryDynamicConditional(String stageKey,
                                                Function<InternalEntity, String> pathResolver,
                                                Predicate<InternalEntity> condition) {
        return new Entry(stageKey, pathResolver, condition, null);
    } // entryDynamicConditional ()

    /**
     * Creates a dynamic-path {@code ALWAYS} slot.
     * <p>
     * The single pool entry carries an empty fixed path (not used for rendering) and a
     * path resolver that is called every render tick with the live entity. Use when the
     * "always-on" texture depends on entity runtime state such as its current size.
     *
     * @param key          slot key constant
     * @param pathResolver function from entity → full resource location string
     * @return ALWAYS slot with a size-dependent (or otherwise dynamic) texture
     */
    public static OverlaySlot alwaysDynamic(String key, Function<InternalEntity, String> pathResolver) {
        return new OverlaySlot(key, SlotMode.ALWAYS,
                List.of(new Entry("", pathResolver, null, null)));
    } // alwaysDynamic ()

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
     * Dynamic entries call {@link Entry#getTexturePath(InternalEntity)} with the entity.
     * Returns empty string if no entry matches.
     *
     * @param entity entity to evaluate predicates against
     * @return active texture path, or empty string
     */
    public String resolveConditional(InternalEntity entity) {
        for (Entry e : pool) {
            if (e.test(entity)) return e.getTexturePath(entity);
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
