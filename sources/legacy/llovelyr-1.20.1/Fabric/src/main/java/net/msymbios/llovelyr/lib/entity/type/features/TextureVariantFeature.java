package net.msymbios.llovelyr.lib.entity.type.features;

import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.framework.entity.type.ResourceMap;

/**
 * <p>Manages additional texture variants beyond base entity textures.</p>
 * <p>
 * <b>Architecture:</b> Provides composable texture variant management that can be attached to any entity type
 * through the feature system. Maps variant names to texture identifiers, enabling entities to have
 * multiple texture options for visual variety.
 * <p>
 * <b>Design Decision:</b> Uses string keys for variant names to support arbitrary variant naming schemes
 * (e.g., "body_1", "body_2", "seasonal_winter"). More flexible than enum-based approach.
 * <p>
 * <b>Use Case:</b> Enables entities to have configurable texture variants (body types, seasonal variants,
 * special skins) without hardcoding texture paths in entity classes.
 */
public class TextureVariantFeature {

    // -- Fields --
    
    private final ResourceMap<String, Identifier> additionalTextures;

    // -- Constructors --
    
    /**
     * Creates empty texture variant feature with no configured variants.
     */
    public TextureVariantFeature() {
        this.additionalTextures = new ResourceMap<>();
    }

    // -- Texture Management --
    
    /**
     * Associates texture identifier with specified variant name.
     * <p>
     * <b>State Impact:</b> Adds or replaces texture mapping for specified variant.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param variant variant name (e.g., "body_1", "seasonal_winter")
     * @param texture texture identifier to associate with variant
     * @return this feature instance for chaining
     */
    public TextureVariantFeature withTexture(String variant, Identifier texture) {
        if (variant != null && texture != null) {
            this.additionalTextures.put(variant, texture);
        }
        return this;
    }

    /**
     * Convenience method for adding multiple body variants with sequential naming.
     * <p>
     * <b>Naming Pattern:</b> Creates variants named "body_1", "body_2", etc. using provided base path.
     * <p>
     * <b>Use Case:</b> Simplifies adding numbered body variants without manual iteration.
     * <p>
     * <b>Example:</b>
     * <pre>
     * withBodyVariants("entity/robot", "default", "armored", "damaged")
     * // Creates: body_1 -> entity/robot/default
     * //          body_2 -> entity/robot/armored
     * //          body_3 -> entity/robot/damaged
     * </pre>
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param basePath base path for texture identifiers (e.g., "entity/robot")
     * @param variants variant names to append to base path
     * @return this feature instance for chaining
     */
    public TextureVariantFeature withBodyVariants(String basePath, String... variants) {
        if (basePath == null || variants == null) {
            return this;
        }
        
        for (int i = 0; i < variants.length; i++) {
            if (variants[i] != null) {
                String variantKey = "body_" + (i + 1);
                String texturePath = basePath + "/" + variants[i];
                Identifier textureId = new Identifier(texturePath);
                this.additionalTextures.put(variantKey, textureId);
            }
        }
        
        return this;
    }

    // -- Texture Queries --
    
    /**
     * Returns texture identifier for specified variant.
     * <p>
     * <b>Fallback Behavior:</b> Returns null if variant not configured.
     * Callers should handle null or provide default texture as fallback.
     * 
     * @param variant variant name to retrieve
     * @return texture identifier for variant, or null if not configured
     */
    public Identifier getTexture(String variant) {
        return this.additionalTextures.get(variant);
    }

    /**
     * Checks if texture is configured for specified variant.
     * 
     * @param variant variant name to check
     * @return true if texture is configured for variant, false otherwise
     */
    public boolean hasTexture(String variant) {
        return this.additionalTextures.has(variant);
    }

    /**
     * Returns randomly selected texture identifier from configured variants.
     * <p>
     * <b>Use Case:</b> Useful for adding visual variety by randomly selecting texture on spawn.
     * <p>
     * <b>Performance:</b> O(1) after initial cache build due to ResourceMap caching.
     * 
     * @return randomly selected texture identifier, or null if no variants configured
     */
    public Identifier getRandomTexture() {
        return this.additionalTextures.getRandom();
    }

} // Class: TextureVariantFeature
