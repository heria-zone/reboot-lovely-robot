package net.heriazone.hzlib.framework.common;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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