package net.msymbios.llovelyr.common.configs;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.LovelyLegacy;

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

    // -- Methods --

    /**
     * A translation text component for an entity variant.
     */
    public static MutableText getVariantTranslation(final String key) {
        return getTranslation("variant.", key);
    } // getVariantTranslation ()

    /**
     * A translation text component for a tab.
     */
    public static MutableText getTabTranslation(final String key) {
        return getTranslation("tab.", key);
    } // getTabTranslation ()

    /**
     * A translation text component for a message.
     */
    public static MutableText getMessageTranslation(final String key) {
        return getTranslation("msg.", key);
    } // getMessageTranslation ()

    /**
     * Retrieves the translatable string based on the given entity variant.
     *
     * @param  variant  the entity variant for which the translatable string is to be retrieved
     * @return          the translatable message key corresponding to the entity variant
     */
    public static MutableText getTranslation(EntityVariant variant) {
        var value = getVariantTranslation(LovelyIdentifier.VARIANT_VANILLA);
        switch (variant) {
            case Bunny2 -> value = getVariantTranslation(LovelyRobotID.VARIANT_BUNNY2);
            case Vanilla -> value = getVariantTranslation(LovelyRobotID.VARIANT_VANILLA);
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the corresponding translatable message key based a given Texture.
     *
     * @param  texture the entity texture for which the translatable message key is needed
     * @return         the message key corresponding to the EntityTexture
     */
    public static MutableText getTranslation(EntityTexture texture) {
        MutableText value = getMessageTranslation(LovelyIdentifier.TEX_PINK);
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
        }
        return value;
    } // getTranslation ()

    public static MutableText getTranslation(final String key) {
        return Text.translatable(LovelyLegacy.MODID + "." + key);
    } // getTranslation ()

    public static MutableText getTranslation(final String category, final String key) {
        return Text.translatable(category + LovelyLegacy.MODID + "." + key);
    } // getTranslation ()

    public static MutableText getTranslation(final String key, Object... objects) {
        return Text.translatable(LovelyLegacy.MODID + "." + key, objects);
    } // getTranslation ()

    public static MutableText getTranslation(final String category, final String key, Object... objects) {
        return Text.translatable(category + LovelyLegacy.MODID + "." + key, objects);
    } // getTranslation ()

    public static Identifier getId(final String path) {
        return Identifier.of(LovelyLegacy.MODID, path);
    } // getId ()

    public static Identifier getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty())
            return getId(path);
        return Identifier.of(namespace, path);
    } // getId ()

} // Class: LovelyIdentifier