package net.msymbios.rlovelyr.config;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.enums.EntityVariant;

public class LovelyRobotID {

    // -- Creative Tab --

    public static final String DEFAULT_TAB = "lovely_robot";
    public static final String ALDARIAN_TECH_TAB = "aldarian_tech";

    // -- Items --

    // GENERAL
    public static final String ROBOT_CORE = "robot_core";
    public static final String ROBOT_CORE_ALDARIAN = "robot_core_aldarian";

    // SPAWN
    public static final String BUNNY_SPAWN = "bunny_spawn";
    public static final String BUNNY2_SPAWN = "bunny2_spawn";
    public static final String DRAGON_SPAWN = "dragon_spawn";
    public static final String HONEY_SPAWN = "honey_spawn";
    public static final String KITSUNE_SPAWN = "kitsune_spawn";
    public static final String NEKO_SPAWN = "neko_spawn";
    public static final String VANILLA_SPAWN = "vanilla_spawn";
    public static final String PRIME_SPAWN = "prime_spawn";
    public static final String HYPERION_SPAWN = "hyperion_spawn";
    public static final String EMPYRIUM_SPAWN = "empyrium_spawn";

    public static final String ITEM_TAG_VARIANT = "variant";

    // -- Entities --

    public static final String VARIANT_BUNNY = "bunny";
    public static final String VARIANT_BUNNY2 = "bunny2";
    public static final String VARIANT_DRAGON = "dragon";
    public static final String VARIANT_HONEY = "honey";
    public static final String VARIANT_KITSUNE = "kitsune";
    public static final String VARIANT_NEKO = "neko";
    public static final String VARIANT_VANILLA = "vanilla";
    public static final String VARIANT_PRIME = "prime";
    public static final String VARIANT_HYPERION = "hyperion";
    public static final String VARIANT_EMPYRIUM = "empyrium";

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

    // -- Animators --

    public static final String ANIM_DEFAULT = "default";

    // -- Models --

    public static final String MOD_DEFAULT = "default";
    public static final String MOD_ARMED = "armed";

    // -- Textures --

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

    public static final String TEX_DARK_MATTER = "dark-matter";
    public static final String TEX_SUPERNOVA = "supernova";
    public static final String TEX_COLD_GOLD = "cold-gold";
    public static final String TEX_EMBRYON = "embryon";
    public static final String TEX_DARK_GOLD = "dark-gold";
    public static final String TEX_GOLD_MATTER = "gold-matter";
    public static final String TEX_HESTIA = "hestia";
    public static final String TEX_COMMANDER = "commander";
    public static final String TEX_VALKYRIE = "valkyrie";

    // -- Methods --

    /**
     * A translation text component for an entity variant.
     */
    public static MutableComponent getVariantTranslation(final String key) {
        return getTranslation("variant.", key);
    } // getVariantTranslation ()

    /**
     * A translation text component for a tab.
     */
    public static MutableComponent getTabTranslation(final String key) {
        return getTranslation("tab.", key);
    } // getTabTranslation ()

    /**
     * A translation text component for a message.
     */
    public static MutableComponent getMessageTranslation(final String key) {
        return getTranslation("msg.", key);
    } // getMessageTranslation ()

    /**
     * Retrieves the translatable string based on the given entity variant.
     *
     * @param  variant  the entity variant for which the translatable string is to be retrieved
     * @return          the translatable message key corresponding to the entity variant
     */
    public static MutableComponent getTranslation(EntityVariant variant) {
        var value = getVariantTranslation(LovelyRobotID.VARIANT_BUNNY);
        switch (variant) {
            case Bunny -> value = getVariantTranslation(LovelyRobotID.VARIANT_BUNNY);
            case Bunny2 -> value = getVariantTranslation(LovelyRobotID.VARIANT_BUNNY2);
            case Dragon -> value = getVariantTranslation(LovelyRobotID.VARIANT_DRAGON);
            case Honey -> value = getVariantTranslation(LovelyRobotID.VARIANT_HONEY);
            case Kitsune -> value = getVariantTranslation(LovelyRobotID.VARIANT_KITSUNE);
            case Neko -> value = getVariantTranslation(LovelyRobotID.VARIANT_NEKO);
            case Vanilla -> value = getVariantTranslation(LovelyRobotID.VARIANT_VANILLA);

            case Prime -> value = getVariantTranslation(LovelyRobotID.VARIANT_PRIME);
            case Hyperion -> value = getVariantTranslation(LovelyRobotID.VARIANT_HYPERION);
            case Empyrium -> value = getVariantTranslation(LovelyRobotID.VARIANT_EMPYRIUM);
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the corresponding translatable message key based a given Texture.
     *
     * @param  texture the entity texture for which the translatable message key is needed
     * @return         the message key corresponding to the EntityTexture
     */
    public static MutableComponent getTranslation(EntityTexture texture) {
        MutableComponent value = getMessageTranslation(LovelyRobotID.TEX_PINK);
        switch (texture) {
            case RANDOM -> value = getMessageTranslation(LovelyRobotID.TEX_RANDOM);
            case WHITE -> value = getMessageTranslation(LovelyRobotID.TEX_WHITE);
            case ORANGE -> value = getMessageTranslation(LovelyRobotID.TEX_ORANGE);
            case MAGENTA -> value = getMessageTranslation(LovelyRobotID.TEX_MAGENTA);
            case LIGHT_BLUE -> value = getMessageTranslation(LovelyRobotID.TEX_LIGHT_BLUE);
            case YELLOW -> value = getMessageTranslation(LovelyRobotID.TEX_YELLOW);
            case LIME -> value = getMessageTranslation(LovelyRobotID.TEX_LIME);
            case PINK -> value = getMessageTranslation(LovelyRobotID.TEX_PINK);
            case GRAY -> value = getMessageTranslation(LovelyRobotID.TEX_GRAY);
            case LIGHT_GRAY -> value = getMessageTranslation(LovelyRobotID.TEX_LIGHT_GRAY);
            case CYAN -> value = getMessageTranslation(LovelyRobotID.TEX_CYAN);
            case PURPLE -> value = getMessageTranslation(LovelyRobotID.TEX_PURPLE);
            case BLUE -> value = getMessageTranslation(LovelyRobotID.TEX_BLUE);
            case BROWN -> value = getMessageTranslation(LovelyRobotID.TEX_BROWN);
            case GREEN -> value = getMessageTranslation(LovelyRobotID.TEX_GREEN);
            case RED -> value = getMessageTranslation(LovelyRobotID.TEX_RED);
            case BLACK -> value = getMessageTranslation(LovelyRobotID.TEX_BLACK);

            case DARK_MATTER -> value = getMessageTranslation(LovelyRobotID.TEX_DARK_MATTER);
            case SUPERNOVA -> value = getMessageTranslation(LovelyRobotID.TEX_SUPERNOVA);
            case COLD_GOLD -> value = getMessageTranslation(LovelyRobotID.TEX_COLD_GOLD);
            case EMBRYON -> value = getMessageTranslation(LovelyRobotID.TEX_EMBRYON);
            case DARK_GOLD -> value = getMessageTranslation(LovelyRobotID.TEX_DARK_GOLD);
            case GOLD_MATTER -> value = getMessageTranslation(LovelyRobotID.TEX_GOLD_MATTER);
            case HESTIA -> value = getMessageTranslation(LovelyRobotID.TEX_HESTIA);
            case COMMANDER -> value = getMessageTranslation(LovelyRobotID.TEX_COMMANDER);
            case VALKYRIE -> value = getMessageTranslation(LovelyRobotID.TEX_VALKYRIE);
        }
        return value;
    } // getTranslation ()

    public static MutableComponent getTranslation(final String key) {
        return Component.translatable(LovelyRobot.MODID + "." + key);
    } // getTranslation ()

    public static MutableComponent getTranslation(final String category, final String key) {
        return Component.translatable(category + LovelyRobot.MODID + "." + key);
    } // getTranslation ()

    public static MutableComponent getTranslation(final String key, Object... objects) {
        return Component.translatable(LovelyRobot.MODID + "." + key, objects);
    } // getTranslation ()

    public static MutableComponent getTranslation(final String category, final String key, Object... objects) {
        return Component.translatable(category + LovelyRobot.MODID + "." + key, objects);
    } // getTranslation ()

    public static ResourceLocation getId(final String path) {
        return ResourceLocation.fromNamespaceAndPath(LovelyRobot.MODID, path);
    } // getId ()

    public static ResourceLocation getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty()) return getId(path);
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    } // getId ()

} // Class LovelyRobotID