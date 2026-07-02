package net.heriazone.lovelylib.common.entity.enums;

import net.heriazone.lovelylib.common.shared.LovelyConstant;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Texture palette identifiers for robot customization.
 * <p>
 * <b>Standard palette (IDs 0–16):</b> 16 Minecraft dye colors + RANDOM. All robots
 * support this range; dye recipes and color-cycling operate only within these IDs.
 * <p>
 * <b>Extended palette (IDs 17–25):</b> Named textures for Reboot-exclusive restricted-palette
 * robots (Prime, Hyperion, Empyrium). Never entered into dye-reaction or random-spawn pools.
 * CODEC is sorted by ID, so appending these entries requires no other structural change.
 */
public enum EntityTexture {

    // -- Standard Colors (Minecraft Dye Palette) --

    WHITE(0, LovelyConstant.TEX_WHITE),
    ORANGE(1, LovelyConstant.TEX_ORANGE),
    MAGENTA(2, LovelyConstant.TEX_MAGENTA),
    LIGHT_BLUE(3, LovelyConstant.TEX_LIGHT_BLUE),
    YELLOW(4, LovelyConstant.TEX_YELLOW),
    LIME(5, LovelyConstant.TEX_LIME),
    PINK(6, LovelyConstant.TEX_PINK),
    GRAY(7, LovelyConstant.TEX_GRAY),
    LIGHT_GRAY(8, LovelyConstant.TEX_LIGHT_GRAY),
    CYAN(9, LovelyConstant.TEX_CYAN),
    PURPLE(10, LovelyConstant.TEX_PURPLE),
    BLUE(11, LovelyConstant.TEX_BLUE),
    BROWN(12, LovelyConstant.TEX_BROWN),
    GREEN(13, LovelyConstant.TEX_GREEN),
    RED(14, LovelyConstant.TEX_RED),
    BLACK(15, LovelyConstant.TEX_BLACK),

    /** Randomizes texture on spawn. Excludes special textures from random pool. */
    RANDOM(16, LovelyConstant.TEX_RANDOM),

    // -- Extended Palette (Reboot-exclusive named textures, IDs 17–25) --
    // Used only by restricted-palette robots; never entered into the dye-reaction pool.
    // COLD_GOLD (19) is shared by both Prime and Empyrium — disambiguate by entity type key
    // during migration (MigrationStep_V1_1204) and item model predicate dispatch.

    DARK_MATTER(17, LovelyConstant.TEX_DARK_MATTER),  // Prime #02 — void purple
    SUPERNOVA(18,   LovelyConstant.TEX_SUPERNOVA),    // Prime #01 — burst gold-white
    COLD_GOLD(19,   LovelyConstant.TEX_COLD_GOLD),    // Prime #00 / Empyrium #00 — ice blue-gold
    EMBRYON(20,     LovelyConstant.TEX_EMBRYON),      // Prime #05 — embryonic green
    DARK_GOLD(21,   LovelyConstant.TEX_DARK_GOLD),    // Prime #04 — dark brass
    GOLD_MATTER(22, LovelyConstant.TEX_GOLD_MATTER),  // Prime #03 — molten gold
    HESTIA(23,      LovelyConstant.TEX_HESTIA),       // Prime #06 — hearth red
    COMMANDER(24,   LovelyConstant.TEX_COMMANDER),    // Hyperion #00 — command blue
    VALKYRIE(25,    LovelyConstant.TEX_VALKYRIE);     // Hyperion #01 — valkyrie silver

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

    // -- Methods --

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