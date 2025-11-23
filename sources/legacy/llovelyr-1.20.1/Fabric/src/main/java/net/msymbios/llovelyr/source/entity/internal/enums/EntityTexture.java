package net.msymbios.llovelyr.source.entity.internal.enums;

import net.msymbios.llovelyr.source.configs.LovelyIdentifier;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Defines color and texture variants for robot customization.
 * <p>
 * <b>Architecture:</b> Texture variants enable visual customization without creating
 * separate entity types. Rendering system swaps texture files based on variant selection.
 * <p>
 * <b>Design Decision:</b> 16 standard colors match Minecraft dye system for intuitive
 * customization. Special textures (Dark Matter, Supernova, etc.) provide premium variants
 * for progression rewards or special achievements.
 */
public enum EntityTexture {

    // -- Standard Colors (Minecraft Dye Palette) --

    WHITE(0, LovelyIdentifier.TEX_WHITE),
    ORANGE(1, LovelyIdentifier.TEX_ORANGE),
    MAGENTA(2, LovelyIdentifier.TEX_MAGENTA),
    LIGHT_BLUE(3, LovelyIdentifier.TEX_LIGHT_BLUE),
    YELLOW(4, LovelyIdentifier.TEX_YELLOW),
    LIME(5, LovelyIdentifier.TEX_LIME),
    PINK(6, LovelyIdentifier.TEX_PINK),
    GRAY(7, LovelyIdentifier.TEX_GRAY),
    LIGHT_GRAY(8, LovelyIdentifier.TEX_LIGHT_GRAY),
    CYAN(9, LovelyIdentifier.TEX_CYAN),
    PURPLE(10, LovelyIdentifier.TEX_PURPLE),
    BLUE(11, LovelyIdentifier.TEX_BLUE),
    BROWN(12, LovelyIdentifier.TEX_BROWN),
    GREEN(13, LovelyIdentifier.TEX_GREEN),
    RED(14, LovelyIdentifier.TEX_RED),
    BLACK(15, LovelyIdentifier.TEX_BLACK),
    
    /** Randomizes texture on spawn. Excludes special textures from random pool. */
    RANDOM(16, LovelyIdentifier.TEX_RANDOM),

    // -- Special Textures (Premium Variants) --

    DARK_MATTER(17, LovelyIdentifier.TEX_DARK_MATTER),
    SUPERNOVA(18, LovelyIdentifier.TEX_SUPERNOVA),
    COLD_GOLD(19, LovelyIdentifier.TEX_COLD_GOLD),
    EMBRYON(20, LovelyIdentifier.TEX_EMBRYON),
    DARK_GOLD(21, LovelyIdentifier.TEX_DARK_GOLD),
    GOLD_MATTER(22, LovelyIdentifier.TEX_GOLD_MATTER),
    HESTIA(23, LovelyIdentifier.TEX_HESTIA),
    COMMANDER(24, LovelyIdentifier.TEX_COMMANDER),
    VALKYRIE(25, LovelyIdentifier.TEX_VALKYRIE);

    // -- Deserialization Cache --

    private static final EntityTexture[] CODEC = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityTexture::getId))
            .toArray(EntityTexture[]::new);
    
    /** Immutable list for iteration. Avoids repeated array-to-list conversions. */
    public static final List<EntityTexture> VALUES = Arrays.stream(values())
            .sorted(Comparator.comparingInt(EntityTexture::getId))
            .toList();

    // -- Texture Identity --

    private final int m_id;
    private final String m_name;

    // -- Constructor --

    /**
     * Binds texture to persistent identifier and resource location.
     * 
     * @param id persistent identifier for serialization
     * @param name resource location of texture file
     */
    EntityTexture(int id, String name) {
        this.m_id = id;
        this.m_name = name;
    } // Constructor: EntityTexture ()

    // -- Deserialization --

    /**
     * Recovers texture from serialized identifier.
     * 
     * @param id serialized texture identifier
     * @return corresponding EntityTexture, or WHITE if invalid
     */
    public static EntityTexture byId(int id) {
        if (id < 0 || id >= CODEC.length) {
            id = 0;
        }
        return CODEC[id];
    } // byId ()

    /**
     * Returns persistent identifier for serialization.
     * 
     * @return texture identifier
     */
    public int getId() {
        return this.m_id;
    } // getId ()

    /**
     * Finds texture by resource location name.
     * 
     * @param name resource location of texture file
     * @return matching EntityTexture, or null if not found
     */
    public static EntityTexture byName(String name) {
        for (EntityTexture item : CODEC) {
            if (item.getName().equals(name)) {
                return item;
            }
        }
        return null;
    } // byName ()

    /**
     * Returns texture resource location.
     * 
     * @return resource location for texture file lookup
     */
    public String getName() {
        return this.m_name;
    } // getName ()

    /**
     * Finds texture by key with fallback to RANDOM.
     * <p>
     * <b>Usage:</b> Safe texture lookup for user input or config files. Returns RANDOM
     * instead of null to ensure robots always have a valid texture.
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
