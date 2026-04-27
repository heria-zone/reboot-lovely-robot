package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.minecraft.resources.ResourceLocation;
import java.util.Objects;

/**
 * <p>Color-based texture variant for palette-driven entity customization.<p>
 * <p>
 * <b>Architecture:</b> Provides color-indexed texture generation using numeric color IDs
 * and formatted path templates. Enables consistent color palette systems across different
 * entity types while supporting various naming conventions (e.g., "_01", "_red", etc.).
 * <p>
 * <b>Design Decision:</b> Uses numeric color ID with configurable formatting rather than
 * color names, allowing flexible naming schemes while maintaining consistent indexing.
 * Supports zero-padded formatting for consistent file organization.
 * <p>
 * <b>Use Cases:</b> Entity color variants following patterns like "entity_01.png",
 * "entity_red_02.png", or custom color naming schemes with numeric indexing.
 */
public class ColorTextureVariant implements ITextureVariant {

    // -- Fields --

    private final String colorKey;
    private final String displayName;
    private final int colorId;
    private final String pathTemplate;
    private final int priority;

    // -- Constructor --

    /**
     * Creates color texture variant with specified parameters.
     * <p>
     * <b>Path Template Format:</b> Use {entity} for entity key, {color} for color key,
     * and {id} for formatted color ID. Example: "textures/entity/{entity}/{entity}_{id}.png"
     * <p>
     * <b>Color ID Formatting:</b> Color ID is formatted as zero-padded 2-digit number
     * (e.g., 1 becomes "01", 15 becomes "15").
     *
     * @param colorKey unique color identifier (e.g., "white", "red")
     * @param displayName human-readable color name for display
     * @param colorId numeric color index for formatting
     * @param pathTemplate path template with {entity}, {color}, and {id} placeholders
     * @param priority priority for default selection (higher = more preferred)
     * @throws NullPointerException if any string parameter is null
     * @throws IllegalArgumentException if colorKey or pathTemplate is empty, or colorId is negative
     */
    public ColorTextureVariant(String colorKey, String displayName, int colorId, String pathTemplate, int priority) {
        this.colorKey = Objects.requireNonNull(colorKey, "Color key cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");
        this.pathTemplate = Objects.requireNonNull(pathTemplate, "Path template cannot be null");
        this.colorId = colorId;
        this.priority = priority;

        if (colorKey.trim().isEmpty()) {
            throw new IllegalArgumentException("Color key cannot be empty");
        }
        if (pathTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("Path template cannot be empty");
        }
        if (colorId < 0) {
            throw new IllegalArgumentException("Color ID cannot be negative");
        }
    } // Constructor: ColorTextureVariant ()

    /**
     * Creates color texture variant with default priority (0).
     *
     * @param colorKey unique color identifier
     * @param displayName human-readable color name for display
     * @param colorId numeric color index for formatting
     * @param pathTemplate path template with placeholders
     */
    public ColorTextureVariant(String colorKey, String displayName, int colorId, String pathTemplate) {
        this(colorKey, displayName, colorId, pathTemplate, 0);
    } // Constructor: ColorTextureVariant ()

    /**
     * Creates color texture variant with standard path template.
     * <p>
     * <b>Standard Template:</b> "textures/entity/{entity}/{entity}_{id}.png"
     *
     * @param colorKey unique color identifier
     * @param displayName human-readable color name for display
     * @param colorId numeric color index for formatting
     */
    public ColorTextureVariant(String colorKey, String displayName, int colorId) {
        this(colorKey, displayName, colorId, "textures/entity/{entity}/{entity}_{id}.png", 0);
    } // Constructor: ColorTextureVariant ()

    // -- IVariant Implementation --

    @Override
    public String getKey() {
        return colorKey;
    } // getKey ()

    @Override
    public String getDisplay() {
        return displayName;
    } // getDisplay ()

    @Override
    public ResourceLocation getResource(String entityKey) {
        Objects.requireNonNull(entityKey, "Entity key cannot be null");
        
        String formattedId = String.format("%02d", colorId);
        String path = pathTemplate
                .replace("{entity}", entityKey)
                .replace("{color}", colorKey)
                .replace("{id}", formattedId);
        
        return ResourceLocation.parse(path);
    } // getResource ()

    @Override
    public int getPriority() {
        return priority;
    } // getPriority ()

    @Override
    public boolean isAvailable(String entityKey) {
        // Color variants are available if entity key is valid
        return entityKey != null && !entityKey.trim().isEmpty();
    } // isAvailable ()

    // -- Accessors --

    /**
     * Returns numeric color ID.
     *
     * @return color ID
     */
    public int getColorId() {
        return colorId;
    } // getColorId ()

    /**
     * Returns formatted color ID (zero-padded 2 digits).
     *
     * @return formatted color ID
     */
    public String getFormattedColorId() {
        return String.format("%02d", colorId);
    } // getFormattedColorId ()

    // -- Object Overrides --

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ColorTextureVariant that = (ColorTextureVariant) obj;
        return colorId == that.colorId && Objects.equals(colorKey, that.colorKey);
    } // equals ()

    @Override
    public int hashCode() {
        return Objects.hash(colorKey, colorId);
    } // hashCode ()

    @Override
    public String toString() {
        return "ColorTextureVariant{" +
                "colorKey='" + colorKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", colorId=" + colorId +
                ", pathTemplate='" + pathTemplate + '\'' +
                ", priority=" + priority +
                '}';
    } // toString ()

} // Class: ColorTextureVariant