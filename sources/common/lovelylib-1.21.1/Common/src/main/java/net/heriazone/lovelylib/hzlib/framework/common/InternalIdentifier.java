package net.heriazone.lovelylib.hzlib.framework.common;

import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;

/**
 * Base identifier class providing core utility methods for resource management.
 * <p>
 * <b>Architecture:</b> Abstract base class that defines common identifier patterns
 * and utility methods. Subclasses provide mod-specific MODID through abstract method.
 * <p>
 * <b>Design Decision:</b> Abstract class allows method reuse while enforcing
 * mod-specific namespace through inheritance.
 */
public abstract class InternalIdentifier {

    // -- Textures -- TODO: Consider moving these to either Lib

    public static final String TEX_WHITE = "white";
    public static final String TEX_ORANGE = "orange";
    public static final String TEX_MAGENTA = "magenta";
    public static final String TEX_LIGHT_BLUE = "light_blue";
    public static final String TEX_YELLOW = "yellow";
    public static final String TEX_LIME = "lime";
    public static final String TEX_PINK = "pink";
    public static final String TEX_GRAY = "gray";
    public static final String TEX_LIGHT_GRAY = "light_gray";
    public static final String TEX_CYAN = "cyan";
    public static final String TEX_PURPLE = "purple";
    public static final String TEX_BLUE = "blue";
    public static final String TEX_BROWN = "brown";
    public static final String TEX_GREEN = "green";
    public static final String TEX_RED = "red";
    public static final String TEX_BLACK = "black";
    public static final String TEX_RANDOM = "random";

    // -- Abstract Methods --

    /**
     * Provides the mod ID for resource namespacing.
     * <p>
     * <b>Implementation:</b> Subclasses must return their specific mod ID.
     *
     * @return mod identifier string
     */
    protected abstract String getModId();

    // -- Translation Methods --

    /**
     * Creates basic translation component.
     *
     * @param key translation key
     * @return translatable component
     */
    protected MutableComponent getTranslationInternal(final String key) {
        return Component.translatable(getModId() + "." + key);
    } // getTranslationInternal ()

    /**
     * Creates translation component with category prefix.
     *
     * @param category translation category (e.g., "item.", "block.")
     * @param key translation key
     * @return translatable component
     */
    protected MutableComponent getTranslationInternal(final String category, final String key) {
        return Component.translatable(category + getModId() + "." + key);
    } // getTranslationInternal ()

    /**
     * Creates translation component with format arguments.
     *
     * @param key translation key
     * @param objects format arguments for translation
     * @return translatable component with formatted text
     */
    protected MutableComponent getTranslationInternal(final String key, Object... objects) {
        return Component.translatable(getModId() + "." + key, objects);
    } // getTranslationInternal ()

    /**
     * Creates translation component with category and format arguments.
     *
     * @param category translation category
     * @param key translation key
     * @param objects format arguments for translation
     * @return translatable component with formatted text
     */
    protected MutableComponent getTranslationInternal(final String category, final String key, Object... objects) {
        return Component.translatable(category + getModId() + "." + key, objects);
    } // getTranslationInternal ()

    // -- Resource Location Methods --

    /**
     * Creates ResourceLocation for mod resource.
     *
     * @param path resource path
     * @return ResourceLocation with mod namespace
     */
    protected ResourceLocation getIdInternal(final String path) {
        return ResourceLocation.fromNamespaceAndPath(getModId(), path);
    } // getIdInternal ()

    /**
     * Creates ResourceLocation with custom namespace.
     * <p>
     * <b>Usage:</b> Allows referencing resources from other mods or Minecraft itself.
     *
     * @param namespace resource namespace (mod ID)
     * @param path resource path
     * @return ResourceLocation with specified namespace
     */
    protected ResourceLocation getIdInternal(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty()) return getIdInternal(path);
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    } // getIdInternal ()

} // Class: InternalIdentifier