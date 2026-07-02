package net.heriazone.lovelylib.common.entity.definition;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Identity token for a robot variant — final class with static constants.
 * <p>
 * <b>Design:</b> Replaces the enum in common.entity.enums. Static constants register
 * themselves at class-load time via register(), maintaining the same usage pattern
 * (RobotVariant.Bunny, RobotVariant.Kitsune, etc.) while removing the compile-time
 * constraint that prevented runtime-defined variants.
 * <p>
 * <b>ID Stability:</b> IDs are preserved from the previous enum (Bunny=0 … Bunny3=7)
 * for NBT and network serialization backward-compatibility. IDs 0–99 are reserved
 * for lovelylib built-ins; addon mods must use IDs ≥ 100.
 * <p>
 * <b>Extensibility:</b> Addon mods call RobotVariant.register(id, "key") during their
 * own initializer — before Lovely.onInitialize() seals RobotDefinitionRegistry — to
 * introduce new variants with zero changes to lovelylib source.
 */
public final class RobotVariant {

    // -- Internal Registries --
    // Must be declared BEFORE the static constants below, because register()
    // is called during constant field initialization and reads these maps.

    private static final Map<Integer, RobotVariant> BY_ID   = new LinkedHashMap<>();
    private static final Map<String,  RobotVariant> BY_NAME = new LinkedHashMap<>();

    // -- Built-in Variants (IDs preserved from previous enum for NBT compatibility) --

    public static final RobotVariant Bunny   = register(0, "bunny");
    public static final RobotVariant Bunny2  = register(1, "bunny2");
    public static final RobotVariant Dragon  = register(2, "dragon");
    public static final RobotVariant Honey   = register(3, "honey");
    public static final RobotVariant Kitsune = register(4, "kitsune");
    public static final RobotVariant Neko    = register(5, "neko");
    public static final RobotVariant Vanilla = register(6, "vanilla");
    public static final RobotVariant Bunny3  = register(7, "bunny3");

    // -- Reboot-Exclusive Variants (IDs 9-11) --
    // Aldarian Tech tier — restricted palettes, no standard dye interaction.
    // IDs 8 is reserved for future shared use; 9-11 assigned here for Reboot.

    /** Prime — 7-colour Aldarian palette, Blaze Rod cycles forward through it. */
    public static final RobotVariant Prime    = register(9,  "prime");

    /** Hyperion — 2-colour toggle (COMMANDER / VALKYRIE), Blaze Rod switches between them. */
    public static final RobotVariant Hyperion = register(10, "hyperion");

    /** Empyrium — single COLD_GOLD texture, all item interaction blocked. */
    public static final RobotVariant Empyrium = register(11, "empyrium");

    // -- Fields --

    private final int    m_id;
    private final String m_name;

    // -- Constructor --

    private RobotVariant(int id, String name) {
        this.m_id   = id;
        this.m_name = name;
    } // Constructor: RobotVariant()

    // -- Registration --

    /**
     * Creates and registers a new variant, returning it.
     * <p>
     * Built-in variants invoke this from their static field initializers.
     * Addon mods call this during their own initializer, before
     * Lovely.onInitialize() seals RobotDefinitionRegistry.
     *
     * @throws IllegalArgumentException if the id or name is already registered
     */
    public static synchronized RobotVariant register(int id, String name) {
        if (BY_ID.containsKey(id))
            throw new IllegalArgumentException(
                "Duplicate RobotVariant id: " + id
                + " (already registered as '" + BY_ID.get(id).m_name + "')");
        if (BY_NAME.containsKey(name))
            throw new IllegalArgumentException(
                "Duplicate RobotVariant name: '" + name + "'");
        RobotVariant variant = new RobotVariant(id, name);
        BY_ID.put(id, variant);
        BY_NAME.put(name, variant);
        return variant;
    } // register()

    // -- Accessors --

    public int    getId()   { return m_id; }
    public String getName() { return m_name; }

    // -- Lookups --

    /**
     * Looks up a variant by integer ID.
     * Used by NBT/network deserialization — replaces the old CODEC array approach.
     *
     * @throws NoSuchElementException if no variant is registered with this id
     */
    public static RobotVariant byId(int id) {
        RobotVariant v = BY_ID.get(id);
        if (v == null)
            throw new NoSuchElementException("Unknown RobotVariant id: " + id);
        return v;
    } // byId()

    /**
     * Looks up a variant by key string.
     * Used by config loading and command arguments.
     *
     * @throws NoSuchElementException if no variant is registered with this name
     */
    public static RobotVariant byName(String name) {
        RobotVariant v = BY_NAME.get(name);
        if (v == null)
            throw new NoSuchElementException("Unknown RobotVariant name: '" + name + "'");
        return v;
    } // byName()

    /** All registered variants in registration order. */
    public static Collection<RobotVariant> values() {
        return Collections.unmodifiableCollection(BY_ID.values());
    } // values()

    public static boolean isKnown(int id)      { return BY_ID.containsKey(id); }
    public static boolean isKnown(String name) { return BY_NAME.containsKey(name); }

    // -- Identity --

    /** Variants are singletons — reference equality is correct and sufficient. */
    @Override public boolean equals(Object o) { return this == o; }
    @Override public int     hashCode()       { return m_id; }
    @Override public String  toString()       { return m_name + "(" + m_id + ")"; }

} // Class: RobotVariant
