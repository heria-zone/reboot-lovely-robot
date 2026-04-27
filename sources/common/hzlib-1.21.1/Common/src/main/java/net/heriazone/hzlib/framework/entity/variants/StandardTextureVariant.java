package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.ITextureVariant;
import net.minecraft.resources.ResourceLocation;
import java.util.Objects;

/**
 * <p>Standard texture variant implementation using path templates.<p>
 * <p>
 * <b>Architecture:</b> Provides template-based texture path generation with placeholder
 * substitution for entity keys and variant names. Enables consistent texture organization
 * across different entity types while maintaining flexibility for custom naming schemes.
 * <p>
 * <b>Design Decision:</b> Uses string templates with {entity} and {variant} placeholders
 * rather than hardcoded paths, allowing the same variant definition to work across
 * multiple entity types with different base paths.
 * <p>
 * <b>Use Cases:</b> Standard entity textures following conventional naming patterns
 * like "textures/entity/{entity}/{variant}.png" or "textures/entity/{entity}/default.png".
 */
public class StandardTextureVariant implements ITextureVariant {

    // -- Fields --

    private final String key;
    private final String displayName;
    private final String pathTemplate;
    private final int priority;

    // -- Constructor --

    /**
     * Creates standard texture variant with specified parameters.
     * <p>
     * <b>Path Template Format:</b> Use {entity} for entity key substitution and
     * {variant} for variant key substitution. Example: "textures/entity/{entity}/{variant}.png"
     *
     * @param key unique identifier for this variant
     * @param displayName human-readable name for display
     * @param pathTemplate path template with {entity} and {variant} placeholders
     * @param priority priority for default selection (higher = more preferred)
     * @throws NullPointerException if any parameter is null
     * @throws IllegalArgumentException if key or pathTemplate is empty
     */
    public StandardTextureVariant(String key, String displayName, String pathTemplate, int priority) {
        this.key = Objects.requireNonNull(key, "Key cannot be null");
        this.displayName = Objects.requireNonNull(displayName, "Display name cannot be null");
        this.pathTemplate = Objects.requireNonNull(pathTemplate, "Path template cannot be null");
        this.priority = priority;

        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (pathTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("Path template cannot be empty");
        }
    } // Constructor: StandardTextureVariant ()

    /**
     * Creates standard texture variant with default priority (0).
     *
     * @param key unique identifier for this variant
     * @param displayName human-readable name for display
     * @param pathTemplate path template with {entity} and {variant} placeholders
     */
    public StandardTextureVariant(String key, String displayName, String pathTemplate) {
        this(key, displayName, pathTemplate, 0);
    } // Constructor: StandardTextureVariant ()

    // -- IVariant Implementation --

    @Override
    public String getKey() {
        return key;
    } // getKey ()

    @Override
    public String getDisplay() {
        return displayName;
    } // getDisplay ()

    @Override
    public ResourceLocation getResource(String entityKey) {
        Objects.requireNonNull(entityKey, "Entity key cannot be null");
        
        String path = pathTemplate
                .replace("{entity}", entityKey)
                .replace("{variant}", key);
        
        return ResourceLocation.parse(path);
    } // getResource ()

    @Override
    public int getPriority() {
        return priority;
    } // getPriority ()

    @Override
    public boolean isAvailable(String entityKey) {
        // Standard variants are always considered available
        // Subclasses can override for conditional availability
        return entityKey != null && !entityKey.trim().isEmpty();
    } // isAvailable ()

    // -- Object Overrides --

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StandardTextureVariant that = (StandardTextureVariant) obj;
        return Objects.equals(key, that.key);
    } // equals ()

    @Override
    public int hashCode() {
        return Objects.hash(key);
    } // hashCode ()

    @Override
    public String toString() {
        return "StandardTextureVariant{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", pathTemplate='" + pathTemplate + '\'' +
                ", priority=" + priority +
                '}';
    } // toString ()

} // Class: StandardTextureVariant