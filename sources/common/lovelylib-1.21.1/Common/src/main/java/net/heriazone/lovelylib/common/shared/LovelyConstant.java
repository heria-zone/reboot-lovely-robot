package net.heriazone.lovelylib.common.shared;

public class LovelyConstant {

    // -- Creative Tab --

    public static final String DEFAULT_TAB      = "lovely_robot";
    public static final String ALDARIAN_TECH_TAB = "aldarian_tech";

    // -- Items --

    // GENERAL
    public static final String ROBOT_CORE          = "robot_core";
    public static final String ROBOT_CORE_ALDARIAN = "robot_core_aldarian";

    // Spawn item key suffix — registry name is built as variant.getName() + "_spawn".
    // LovelyConstant.{VARIANT}_SPAWN string constants are removed; use
    // RobotDefinitionRegistry.get(variant).getSpawnItemKey() instead.
    public static final String ITEM_TAG_VARIANT = "variant";

    // -- Stats --

    public static final String STAT_CUSTOM_NAME = "custom_name";
    public static final String STAT_CREATOR = "creator";
    public static final String STAT_CREDITS = "credits";
    public static final String STAT_OWNER = "owner";
    /** Int texture ID (0–15) — used by item model predicates. */
    public static final String STAT_COLOR = "color";
    /** String texture variant key (e.g., "magenta") — used by entity restoration (ADR_012). */
    public static final String STAT_COLOR_VARIANT = "color_variant";
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

    // -- Textures (16) --

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

    // -- Textures (Additional) --

    public static final String TEX_DARK_MATTER = "dark_matter";
    public static final String TEX_SUPERNOVA = "supernova";
    public static final String TEX_COLD_GOLD = "cold_gold";
    public static final String TEX_EMBRYON = "embryon";
    public static final String TEX_DARK_GOLD = "dark_gold";
    public static final String TEX_GOLD_MATTER = "gold_matter";
    public static final String TEX_HESTIA = "hestia";
    public static final String TEX_COMMANDER = "commander";
    public static final String TEX_VALKYRIE = "valkyrie";

    // -- Entity Configuration Keys --

    public static final String CONFIG_MAX_LEVEL = "max-level";
    public static final String CONFIG_BASE_HP = "base-hp";
    public static final String CONFIG_BASE_ATTACK = "base-attack";
    public static final String CONFIG_ATTACK_SPEED = "attack-speed";
    public static final String CONFIG_BASE_DEFENSE = "base-defense";
    public static final String CONFIG_BASE_TOUGHNESS = "base-toughness";
    public static final String CONFIG_MOVEMENT_SPEED = "movement-speed";

    // -- Robot Variant Arrays --

    // LEGACY_VARIANTS, REBOOT_VARIANTS, and ALL_VARIANTS removed — use
    // RobotDefinitionRegistry.getVariantKeysForMod(ModTarget.LEGACY/REBOOT) instead.
    // TRIBUTE_VARIANTS is retained: Tribute has a fixed roster and is explicitly
    // excluded from RobotDefinitionRegistry. Its config loop reads this array directly.
    public static final String[] TRIBUTE_VARIANTS = {
            "bunny", "bunny2",
            "honey", "vanilla"
    };

} // Class: LovelyConstant