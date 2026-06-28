package net.heriazone.hzlib.framework.entity.variants;

import net.heriazone.hzlib.api.entity.variants.interfaces.ICompositeAppearance;

/**
 * Standard implementation of {@link ICompositeAppearance}.
 * <p>
 * <b>Key design:</b> The composite key is the appearance identity. Texture, model,
 * and animator keys are declared independently — model and animator are typically
 * shared across color families for the same size, while texture is color-specific.
 * <p>
 * {@link #getSizeConfig()} returns {@link java.util.Optional#empty()} — override or
 * use for size-coupled appearances that carry a {@link net.heriazone.hzlib.api.entity.features.SizeVariantFeature.SizeConfig}.
 */
public class StandardAppearanceVariant implements ICompositeAppearance {

    // -- Fields --

    private final String key;
    private final String display;
    private final String textureKey;
    private final String modelKey;
    private final String animatorKey;
    private final int priority;

    // -- Constructor --

    /**
     * Creates a new composite appearance.
     *
     * @param key         unique identifier for this appearance
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

    // -- ICompositeAppearance --

    @Override public String getTextureKey()  { return textureKey;  }
    @Override public String getModelKey()    { return modelKey;    }
    @Override public String getAnimatorKey() { return animatorKey; }

    // -- IVariant --

    @Override public String  getKey()                    { return key;      }
    @Override public String  getDisplay()                { return display;  }
    @Override public int     getPriority()               { return priority; }
    @Override public boolean isAvailable(String entity)  { return true;     }

} // Class: StandardAppearanceVariant