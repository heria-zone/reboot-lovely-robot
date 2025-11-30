package net.msymbios.llovelyr.framework.entity.enums;

import net.msymbios.llovelyr.framework.common.InternalIdentifier;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * <p>Defines 16-color texture palette for customization.<p>
 * <p>
 * <b>Architecture:</b> Pure Java enum with zero Minecraft dependencies. Provides
 * type-safe keys for color texture mappings.
 * <p>
 * <b>Design Decision:</b> 16 colors match Minecraft's dye system, enabling intuitive
 * color selection and crafting recipes. RANDOM variant supports procedural generation.
 */
public enum EntityTexture {

    // -- Standard Colors (Minecraft Dye Palette) --

    WHITE(0, InternalIdentifier.TEX_WHITE),
    ORANGE(1, InternalIdentifier.TEX_ORANGE),
    MAGENTA(2, InternalIdentifier.TEX_MAGENTA),
    LIGHT_BLUE(3, InternalIdentifier.TEX_LIGHT_BLUE),
    YELLOW(4, InternalIdentifier.TEX_YELLOW),
    LIME(5, InternalIdentifier.TEX_LIME),
    PINK(6, InternalIdentifier.TEX_PINK),
    GRAY(7, InternalIdentifier.TEX_GRAY),
    LIGHT_GRAY(8, InternalIdentifier.TEX_LIGHT_GRAY),
    CYAN(9, InternalIdentifier.TEX_CYAN),
    PURPLE(10, InternalIdentifier.TEX_PURPLE),
    BLUE(11, InternalIdentifier.TEX_BLUE),
    BROWN(12, InternalIdentifier.TEX_BROWN),
    GREEN(13, InternalIdentifier.TEX_GREEN),
    RED(14, InternalIdentifier.TEX_RED),
    BLACK(15, InternalIdentifier.TEX_BLACK),

    /** Randomizes texture on spawn. Excludes special textures from random pool. */
    RANDOM(16, InternalIdentifier.TEX_RANDOM);

    // -- Deserialization Cache --

    private static final EntityTexture[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityTexture::getId))
            .toArray(EntityTexture[]::new);

    /** Immutable list for iteration. Avoids repeated array-to-list conversions. */
    public static final List<EntityTexture> VALUES = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityTexture::getId))
            .toList();

    // -- Color Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds texture to persistent identifier and resource location.
     *
     * @param id persistent identifier for serialization
     * @param name color name for resource paths
     */
    EntityTexture(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityTexture ()

    // -- Deserialization --

    /**
     * Recovers color from serialized identifier.
     *
     * @param id serialized color identifier
     * @return corresponding EntityTexture, or WHITE if invalid
     */
    public static EntityTexture byId(int id) {
        if (id < 0 || id >= CODEC.length) id = 0;
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for serialization.
     *
     * @return color identifier
     */
    public int getId() {return this.m_id;} // getId ()

    /**
     * Finds color by name.
     *
     * @param name color name
     * @return matching EntityTexture, or null if not found
     */
    public static EntityTexture byName(String name) {
        for (EntityTexture item : CODEC) {
            if (item.Name().equals(name)) return item;
        }
        return null;
    } // byName ()

    /**
     * Returns color name for resource paths.
     *
     * @return color name
     */
    public String Name() {
        return this.m_name;
    } // Name ()

    /**
     * Finds texture by key with fallback to RANDOM.
     * <p>
     * <b>Usage:</b> Safe texture lookup for user input or config files. Returns RANDOM
     * instead of null to ensure it always have a valid texture.
     *
     * @param key texture identifier key
     * @return matching EntityTexture, or RANDOM if not found
     */
    public static EntityTexture find(String key) {
        return VALUES.stream()
                .filter(type -> type.m_name.equals(key))
                .findFirst()
                .orElse(EntityTexture.RANDOM);
    } // find ()

} // Enum: EntityTexture