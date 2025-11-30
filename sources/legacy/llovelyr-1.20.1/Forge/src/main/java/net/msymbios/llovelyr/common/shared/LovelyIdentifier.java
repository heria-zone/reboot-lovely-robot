package net.msymbios.llovelyr.common.shared;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.framework.common.InternalIdentifier;
import net.msymbios.llovelyr.framework.entity.enums.EntityTexture;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;

/**
 * Central registry for mod identifiers, translation keys, and resource paths.
 * <p>
 * <b>Architecture:</b> Consolidates all string constants to prevent typos and enable
 * IDE refactoring. Translation methods provide type-safe access to localized text.
 * <p>
 * <b>Design Decision:</b> Single constants class rather than scattered strings improves
 * maintainability and makes it easy to audit all identifiers used by the mod.
 */
public class LovelyIdentifier {

    // -- Creative Tab --

    public static final String DEFAULT_TAB = "lovely_robot";

    // -- Items --

    // GENERAL
    public static final String ROBOT_CORE = "robot_core";

    // SPAWN
    public static final String BUNNY2_SPAWN = "bunny2_spawn";
    public static final String VANILLA_SPAWN = "vanilla_spawn";
    public static final String ITEM_TAG_VARIANT = "variant";

    // -- Entities --

    public static final String VARIANT_BUNNY2 = "bunny2";
    public static final String VARIANT_DRAGON = "dragon";
    public static final String VARIANT_KITSUNE = "kitsune";
    public static final String VARIANT_VANILLA = "vanilla";

    // -- Stats --

    public static final String STAT_CUSTOM_NAME = "custom_name";
    public static final String STAT_CREATOR = "creator";
    public static final String STAT_CREDITS = "credits";
    public static final String STAT_OWNER = "owner";
    public static final String STAT_COLOR = "color";
    public static final String STAT_TYPE = "type";
    public static final String STAT_MAX_LEVEL = "max_level";
    public static final String STAT_LEVEL = "level";
    public static final String STAT_EXP = "exp";
    public static final String STAT_HP = "hp";
    public static final String STAT_FIRE_PROTECTION = "fire_protection";
    public static final String STAT_FALL_PROTECTION = "fall_protection";
    public static final String STAT_BLAST_PROTECTION = "blast_protection";
    public static final String STAT_PROJECTILE_PROTECTION = "projectile_protection";

    // -- Messages --

    public static final String MSG_HEAL = "heal";
    public static final String MSG_WARY = "wary";
    public static final String MSG_MAX_LEVEL = "max_level";
    public static final String MSG_LEVEL_UP = "level_up";
    public static final String MSG_HEALTH = "health";
    public static final String MSG_ATTACK = "attack";
    public static final String MSG_EXPERIENCE = "experience";
    public static final String MSG_BAR = "bar";
    public static final String MSG_LOOTING = "looting";
    public static final String MSG_STATE = "state";
    public static final String MSG_DEFENCE = "defence";
    public static final String MSG_FOLLOW = "follow";
    public static final String MSG_STANDBY = "standby";
    public static final String MSG_BASE_DEFENCE = "base_defence";

    public static final String MSG_ENCHANTMENT = "enchantment";
    public static final String MSG_FIRE_PROTECTION = "fire_protection";
    public static final String MSG_FALL_PROTECTION = "fall_protection";
    public static final String MSG_BLAST_PROTECTION = "blast_protection";
    public static final String MSG_PROJECTILE_PROTECTION = "projectile_protection";

    public static final String MSG_NOTIFICATION = "notification";
    public static final String MSG_AUTO_ATTACK = "auto_attack";
    public static final String MSG_OFF = "off";
    public static final String MSG_ON = "on";

    public static final String MSG_OWNER = "owner";
    public static final String MSG_CUSTOM_NAME = "name";
    public static final String MSG_TYPE = "type";
    public static final String MSG_COLOR = "color";
    public static final String MSG_DESIGN = "design";
    public static final String MSG_LEVEL = "level";

    // -- Path --

    public static final String TEXTURE_ENTITY_PATH = "textures/entity/";
    public static final String TEXTURE_LAYER_PATH = "textures/layer/";

    // -- Animators --

    public static final String ANIM_DEFAULT = "default";

    // -- Models --

    public static final String MOD_DEFAULT = "default";
    public static final String MOD_ARMED = "armed";



    public static final String TEX_DARK_MATTER = "dark_matter";
    public static final String TEX_SUPERNOVA = "supernova";
    public static final String TEX_COLD_GOLD = "cold_gold";
    public static final String TEX_EMBRYON = "embryon";
    public static final String TEX_DARK_GOLD = "dark_gold";
    public static final String TEX_GOLD_MATTER = "gold_matter";
    public static final String TEX_HESTIA = "hestia";
    public static final String TEX_COMMANDER = "commander";
    public static final String TEX_VALKYRIE = "valkyrie";

    // -- Translation Methods --

    /**
     * Creates translation component for entity variant.
     * 
     * @param key variant identifier key
     * @return translatable component for variant name
     */
    public static MutableComponent getVariantTranslation(final String key) {
        return getTranslation("variant.", key);
    } // getVariantTranslation ()

    /**
     * Creates translation component for creative tab.
     * 
     * @param key tab identifier key
     * @return translatable component for tab name
     */
    public static MutableComponent getTabTranslation(final String key) {
        return getTranslation("tab.", key);
    } // getTabTranslation

    /**
     * Creates translation component for message.
     * 
     * @param key message identifier key
     * @return translatable component for message text
     */
    public static MutableComponent getMessageTranslation(final String key) {
        return getTranslation("msg.", key);
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
            case Bunny2 -> getVariantTranslation(VARIANT_BUNNY2);
            case Dragon -> getVariantTranslation(VARIANT_DRAGON);
            case Kitsune -> getVariantTranslation(VARIANT_KITSUNE);
            case Vanilla -> getVariantTranslation(VARIANT_VANILLA);
            default -> getVariantTranslation(VARIANT_VANILLA);
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
            case RANDOM -> getMessageTranslation(InternalIdentifier.TEX_RANDOM);
            case WHITE -> getMessageTranslation(InternalIdentifier.TEX_WHITE);
            case ORANGE -> getMessageTranslation(InternalIdentifier.TEX_ORANGE);
            case MAGENTA -> getMessageTranslation(InternalIdentifier.TEX_MAGENTA);
            case LIGHT_BLUE -> getMessageTranslation(InternalIdentifier.TEX_LIGHT_BLUE);
            case YELLOW -> getMessageTranslation(InternalIdentifier.TEX_YELLOW);
            case LIME -> getMessageTranslation(InternalIdentifier.TEX_LIME);
            case PINK -> getMessageTranslation(InternalIdentifier.TEX_PINK);
            case GRAY -> getMessageTranslation(InternalIdentifier.TEX_GRAY);
            case LIGHT_GRAY -> getMessageTranslation(InternalIdentifier.TEX_LIGHT_GRAY);
            case CYAN -> getMessageTranslation(InternalIdentifier.TEX_CYAN);
            case PURPLE -> getMessageTranslation(InternalIdentifier.TEX_PURPLE);
            case BLUE -> getMessageTranslation(InternalIdentifier.TEX_BLUE);
            case BROWN -> getMessageTranslation(InternalIdentifier.TEX_BROWN);
            case GREEN -> getMessageTranslation(InternalIdentifier.TEX_GREEN);
            case RED -> getMessageTranslation(InternalIdentifier.TEX_RED);
            case BLACK -> getMessageTranslation(InternalIdentifier.TEX_BLACK);
            default -> getMessageTranslation(InternalIdentifier.TEX_PINK);
        };
    } // getTranslation ()

    /**
     * Creates basic translation component.
     * 
     * @param key translation key
     * @return translatable component
     */
    public static MutableComponent getTranslation(final String key) {
        return Component.translatable(LovelyLegacy.MODID + "." + key);
    } // getTranslation ()

    /**
     * Creates translation component with category prefix.
     * 
     * @param category translation category (e.g., "item.", "block.")
     * @param key translation key
     * @return translatable component
     */
    public static MutableComponent getTranslation(final String category, final String key) {
        return Component.translatable(category + LovelyLegacy.MODID + "." + key);
    } // getTranslation ()

    /**
     * Creates translation component with format arguments.
     * 
     * @param key translation key
     * @param objects format arguments for translation
     * @return translatable component with formatted text
     */
    public static MutableComponent getTranslation(final String key, Object... objects) {
        return Component.translatable(LovelyLegacy.MODID + "." + key, objects);
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
        return Component.translatable(category + LovelyLegacy.MODID + "." + key, objects);
    } // getTranslation ()

    /**
     * Creates ResourceLocation for mod resource.
     * 
     * @param path resource path
     * @return ResourceLocation with mod namespace
     */
    public static ResourceLocation getId(final String path) {
        return new ResourceLocation(LovelyLegacy.MODID, path);
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
        if (namespace == null || namespace.isEmpty()) {
            return getId(path);
        }
        return new ResourceLocation(namespace, path);
    } // getId ()

} // Class: LovelyIdentifier
