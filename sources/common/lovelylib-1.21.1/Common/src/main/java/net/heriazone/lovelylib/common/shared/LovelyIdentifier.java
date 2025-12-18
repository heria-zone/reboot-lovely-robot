package net.heriazone.lovelylib.common.shared;

import net.heriazone.lovelylib.Lovely;
import net.heriazone.lovelylib.common.entity.enums.*;
import net.heriazone.lovelylib.hzlib.framework.common.InternalIdentifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * Central registry for mod identifiers, translation keys, and resource paths.
 * <p>
 * <b>Architecture:</b> Consolidates all string constants to prevent typos and enable
 * IDE refactoring. Inherits utility methods from InternalIdentifier while providing
 * mod-specific constants and namespace.
 * <p>
 * <b>Design Decision:</b> Single constants class rather than scattered strings improves
 * maintainability. Static instance pattern allows both static constant access and
 * instance method usage with proper mod ID injection.
 */
public class LovelyIdentifier extends InternalIdentifier {

    // -- Singleton Instance --

    protected static final LovelyIdentifier INSTANCE = new LovelyIdentifier();
    
    // -- Abstract Method Implementation --

    @Override
    protected String getModId() {
        return Lovely.MODID;
    } // getModId ()

    // -- Static Accessor Methods --

    public static String MODID () {
        return INSTANCE.getModId();
    } // MODID ()

    /**
     * Creates translation component for entity variant.
     *
     * @param key variant identifier key
     * @return translatable component for variant name
     */
    public static MutableComponent getVariantTranslation(final String key) {
        return INSTANCE.getTranslation("variant.", key);
    } // getVariantTranslation ()

    /**
     * Creates translation component for creative tab.
     *
     * @param key tab identifier key
     * @return translatable component for tab name
     */
    public static MutableComponent getTabTranslation(final String key) {
        return INSTANCE.getTranslation("tab.", key);
    } // getTabTranslation ()

    /**
     * Creates translation component for message.
     *
     * @param key message identifier key
     * @return translatable component for message text
     */
    public static MutableComponent getMessageTranslation(final String key) {
        return INSTANCE.getTranslation("msg.", key);
    } // getMessageTranslation ()

    /**
     * Creates translation component for entity variant enum.
     * <p>
     * <b>Usage:</b> Provides localized variant names for UI display.
     *
     * @param variant entity variant to translate
     * @return translatable component for variant name
     */
    public static MutableComponent getTranslation(EntityVariant variant) {
        return switch (variant) {
            case Bunny -> getVariantTranslation(LovelyConstant.VARIANT_BUNNY);
            case Bunny2 -> getVariantTranslation(LovelyConstant.VARIANT_BUNNY2);
            case Dragon -> getVariantTranslation(LovelyConstant.VARIANT_DRAGON);
            case Honey -> getVariantTranslation(LovelyConstant.VARIANT_HONEY);
            case Kitsune -> getVariantTranslation(LovelyConstant.VARIANT_KITSUNE);
            case Neko -> getVariantTranslation(LovelyConstant.VARIANT_NEKO);
            case Vanilla -> getVariantTranslation(LovelyConstant.VARIANT_VANILLA);
            default -> getVariantTranslation(LovelyConstant.VARIANT_VANILLA);
        };
    } // getTranslation ()

    /**
     * Creates translation component for texture enum.
     * <p>
     * <b>Usage:</b> Provides localized color names for UI display.
     *
     * @param texture entity texture to translate
     * @return translatable component for color name
     */
    public static MutableComponent getTranslation(EntityTexture texture) {
        return switch (texture) {
            case RANDOM -> getMessageTranslation(LovelyConstant.TEX_RANDOM);
            case WHITE -> getMessageTranslation(LovelyConstant.TEX_WHITE);
            case ORANGE -> getMessageTranslation(LovelyConstant.TEX_ORANGE);
            case MAGENTA -> getMessageTranslation(LovelyConstant.TEX_MAGENTA);
            case LIGHT_BLUE -> getMessageTranslation(LovelyConstant.TEX_LIGHT_BLUE);
            case YELLOW -> getMessageTranslation(LovelyConstant.TEX_YELLOW);
            case LIME -> getMessageTranslation(LovelyConstant.TEX_LIME);
            case PINK -> getMessageTranslation(LovelyConstant.TEX_PINK);
            case GRAY -> getMessageTranslation(LovelyConstant.TEX_GRAY);
            case LIGHT_GRAY -> getMessageTranslation(LovelyConstant.TEX_LIGHT_GRAY);
            case CYAN -> getMessageTranslation(LovelyConstant.TEX_CYAN);
            case PURPLE -> getMessageTranslation(LovelyConstant.TEX_PURPLE);
            case BLUE -> getMessageTranslation(LovelyConstant.TEX_BLUE);
            case BROWN -> getMessageTranslation(LovelyConstant.TEX_BROWN);
            case GREEN -> getMessageTranslation(LovelyConstant.TEX_GREEN);
            case RED -> getMessageTranslation(LovelyConstant.TEX_RED);
            case BLACK -> getMessageTranslation(LovelyConstant.TEX_BLACK);
            default -> getMessageTranslation(LovelyConstant.TEX_PINK);
        };
    } // getTranslation ()

    /**
     * Creates basic translation component.
     *
     * @param key translation key
     * @return translatable component
     */
    public static MutableComponent getTranslation(final String key) {
        return INSTANCE.getTranslationInternal(key);
    } // getTranslation ()

    /**
     * Creates translation component with category prefix.
     *
     * @param category translation category (e.g., "item.", "block.")
     * @param key translation key
     * @return translatable component
     */
    public static MutableComponent getTranslation(final String category, final String key) {
        return INSTANCE.getTranslationInternal(category, key);
    } // getTranslation ()

    /**
     * Creates translation component with format arguments.
     *
     * @param key translation key
     * @param objects format arguments for translation
     * @return translatable component with formatted text
     */
    public static MutableComponent getTranslation(final String key, Object... objects) {
        return INSTANCE.getTranslationInternal(key, objects);
    } // getTranslation ()

    /**
     * Creates translation component with category and format arguments.
     *
     * @param category translation category
     * @param key translation key
     * @param objects format arguments for translation
     * @return translatable component with formatted text
     */
    public static MutableComponent getTranslation(final String category, final String key, Object... objects) {
        return INSTANCE.getTranslationInternal(category, key, objects);
    } // getTranslation ()

    /**
     * Creates ResourceLocation for mod resource.
     *
     * @param path resource path
     * @return ResourceLocation with mod namespace
     */
    public static ResourceLocation getId(final String path) {
        return INSTANCE.getIdInternal(path);
    } // getId ()

    /**
     * Creates ResourceLocation with custom namespace.
     * <p>
     * <b>Usage:</b> Allows referencing resources from other mods or Minecraft itself.
     *
     * @param namespace resource namespace (mod ID)
     * @param path resource path
     * @return ResourceLocation with specified namespace
     */
    public static ResourceLocation getId(final String namespace, final String path) {
        return INSTANCE.getIdInternal(namespace, path);
    } // getId ()

} // Class: LovelyIdentifier
