package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.IAppearanceVariant;

/**
 * <p>Standard implementation of {@link IAppearanceVariant}.<p>
 * <p>
 * <b>Key design:</b> The composite key is the appearance identity.
 * Texture, model, and animator keys are declared independently — model
 * and animator are typically shared across color families for the same size,
 * while texture is color-specific.
 */
public class StandardAppearanceVariant implements IAppearanceVariant {

    // -- Fields --

    private final String key;
    private final String display;
    private final String textureKey;
    private final String modelKey;
    private final String animatorKey;
    private final int priority;

    // -- Constructor --

    /**
     * Creates a new entity appearance.
     *
     * @param key         unique identifier for this entity appearance
     * @param display     human-readable display name
     * @param textureKey  texture variant key
     * @param modelKey    model variant key
     * @param animatorKey animator variant key
     * @param priority    selection priority (higher = preferred as default)
     */
    public StandardAppearanceVariant(String key, String display,
                                     String textureKey, String modelKey, String animatorKey,
                                     int priority) {
        this.key = key;
        this.display = display;
        this.textureKey = textureKey;
        this.modelKey = modelKey;
        this.animatorKey = animatorKey;
        this.priority = priority;
    } // Constructor: StandardAppearanceVariant ()

    // -- IAppearanceVariant --

    @Override
    public String getTextureKey() {
        return textureKey;
    } // getTextureKey ()

    @Override
    public String getModelKey() {
        return modelKey;
    } // getModelKey ()

    @Override
    public String getAnimatorKey() {
        return animatorKey;
    } // getAnimatorKey ()

    // -- IVariant --

    @Override
    public String getKey() {
        return key;
    } // getKey ()

    @Override
    public String getDisplay() {
        return display;
    } // getDisplay ()

    @Override
    public int getPriority() {
        return priority;
    } // getPriority ()

    @Override
    public boolean isAvailable(String entityKey) {
        return true;
    } // isAvailable ()

} // Class: StandardAppearanceVariant