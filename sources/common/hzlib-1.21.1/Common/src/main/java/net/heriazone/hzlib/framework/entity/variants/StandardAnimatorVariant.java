package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.animation.AnimationProfile;
import net.heriazone.hzlib.api.entity.variants.interfaces.IAnimatorVariant;
import net.minecraft.resources.ResourceLocation;
import java.util.Objects;

/**
 * <p>Standard animator variant implementation using path templates.<p>
 * <p>
 * <b>Architecture:</b> Provides template-based animator path generation with placeholder
 * substitution for entity keys and variant names. Enables consistent animator organization
 * across different entity types while maintaining flexibility for custom naming schemes.
 * <p>
 * <b>Design Decision:</b> Uses string templates with {entity} and {variant} placeholders
 * rather than hardcoded paths, allowing the same variant definition to work across
 * multiple entity types with different base paths.
 * <p>
 * <b>Animation Profile:</b> Optionally carries an {@link AnimationProfile} that declares
 * which animations exist in the animator file and how they should be selected. When present,
 * the loader-specific {@code InternalAnimation} reads the profile to construct
 * {@code RawAnimation} objects. When absent, the loader falls back to the legacy
 * hardcoded animation name constants.
 * <p>
 * <b>Use Cases:</b> Standard entity animators following conventional naming patterns
 * like "animations/{entity}.animation.json" or "animations/{entity}.{variant}.animation.json".
 */
public class StandardAnimatorVariant implements IAnimatorVariant {

    // -- Fields --

    private final String key;
    private final String displayName;
    private final String pathTemplate;
    private final int priority;
    private final AnimationProfile animationProfile; // nullable — optional

    // -- Constructors --

    /**
     * Creates standard animator variant with an animation profile.
     * <p>
     * <b>Path Template Format:</b> Use {entity} for entity key substitution and
     * {variant} for variant key substitution. Example: "animations/{entity}.{variant}.animation.json"
     *
     * @param key              unique identifier for this variant
     * @param displayName      human-readable name for display
     * @param pathTemplate     path template with {entity} and {variant} placeholders
     * @param animationProfile animation profile declaring available animations (may be null)
     * @param priority         priority for default selection (higher = more preferred)
     * @throws NullPointerException     if key, displayName, or pathTemplate is null
     * @throws IllegalArgumentException if key or pathTemplate is empty
     */
    public StandardAnimatorVariant(String key, String displayName, String pathTemplate,
                                   AnimationProfile animationProfile, int priority) {
        this.key              = Objects.requireNonNull(key, "Key cannot be null");
        this.displayName      = Objects.requireNonNull(displayName, "Display name cannot be null");
        this.pathTemplate     = Objects.requireNonNull(pathTemplate, "Path template cannot be null");
        this.animationProfile = animationProfile; // nullable
        this.priority         = priority;

        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }
        if (pathTemplate.trim().isEmpty()) {
            throw new IllegalArgumentException("Path template cannot be empty");
        }
    } // Constructor: StandardAnimatorVariant ()

    /**
     * Creates standard animator variant with specified parameters and no animation profile.
     * Backward-compatible constructor — existing registrations continue to work.
     *
     * @param key          unique identifier for this variant
     * @param displayName  human-readable name for display
     * @param pathTemplate path template with {entity} and {variant} placeholders
     * @param priority     priority for default selection (higher = more preferred)
     */
    public StandardAnimatorVariant(String key, String displayName, String pathTemplate, int priority) {
        this(key, displayName, pathTemplate, null, priority);
    } // Constructor: StandardAnimatorVariant ()

    /**
     * Creates standard animator variant with default priority (0) and no animation profile.
     *
     * @param key          unique identifier for this variant
     * @param displayName  human-readable name for display
     * @param pathTemplate path template with {entity} and {variant} placeholders
     */
    public StandardAnimatorVariant(String key, String displayName, String pathTemplate) {
        this(key, displayName, pathTemplate, null, 0);
    } // Constructor: StandardAnimatorVariant ()

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

    // -- Animation Profile --

    /**
     * Returns the animation profile for this animator variant, if one was registered.
     * <p>
     * <b>Usage:</b> The loader-specific {@code InternalAnimation} calls this to get
     * the profile, then uses {@link AnimationProfile#getIdle()},
     * {@link AnimationProfile#getAttack()}, etc. to resolve animation names.
     * Returns {@code null} for variants registered without a profile — the loader
     * falls back to legacy hardcoded animation name constants in that case.
     *
     * @return animation profile, or {@code null} if not configured
     */
    public AnimationProfile getAnimationProfile() {
        return animationProfile;
    } // getAnimationProfile ()

    /**
     * Returns whether this variant has an animation profile configured.
     *
     * @return true if an animation profile is present
     */
    public boolean hasAnimationProfile() {
        return animationProfile != null;
    } // hasAnimationProfile ()

    // -- Object Overrides --

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        StandardAnimatorVariant that = (StandardAnimatorVariant) obj;
        return Objects.equals(key, that.key);
    } // equals ()

    @Override
    public int hashCode() {
        return Objects.hash(key);
    } // hashCode ()

    @Override
    public String toString() {
        return "StandardAnimatorVariant{" +
                "key='" + key + '\'' +
                ", displayName='" + displayName + '\'' +
                ", pathTemplate='" + pathTemplate + '\'' +
                ", priority=" + priority +
                ", hasProfile=" + (animationProfile != null) +
                '}';
    } // toString ()

} // Class: StandardAnimatorVariant