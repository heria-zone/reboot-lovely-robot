package net.msymbios.rlovelyr.config;

import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.internal.enums.EntityState;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;
import net.msymbios.rlovelyr.entity.internal.enums.EntityVariant;

public class LovelyRobotID {

    // -- Items Group --
    public static final String DEFAULT_GROUP = "lovely_robot";

    // -- Items --

    // GENERAL
    public static final String ROBOT_CORE = "robot_core";

    // SPAWN
    public static final String BUNNY_SPAWN = "bunny_spawn";
    public static final String BUNNY2_SPAWN = "bunny2_spawn";
    public static final String DRAGON_SPAWN = "dragon_spawn";
    public static final String HONEY_SPAWN = "honey_spawn";
    public static final String KITSUNE_SPAWN = "kitsune_spawn";
    public static final String NEKO_SPAWN = "neko_spawn";
    public static final String VANILLA_SPAWN = "vanilla_spawn";

    public static final String ITEM_TAG_VARIANT = "variant";

    // -- Entities --

    // ROBOTS
    public static final String VARIANT_BUNNY = "bunny";
    public static final String VARIANT_BUNNY2 = "bunny2";
    public static final String VARIANT_DRAGON = "dragon";
    public static final String VARIANT_HONEY = "honey";
    public static final String VARIANT_KITSUNE = "kitsune";
    public static final String VARIANT_NEKO = "neko";
    public static final String VARIANT_VANILLA = "vanilla";

    // BLOCKLING CAPABILITY
    public static final String BLOCK_SELECT = "block_select_capability";

    //  TRANSLATABLE
    public static final String VARIANT_PREFIX = "variant.";

    public static final String TRANS_DIRECTION_FRONT = "direction.front";
    public static final String TRANS_DIRECTION_BACK = "direction.back";
    public static final String TRANS_DIRECTION_LEFT = "direction.left";
    public static final String TRANS_DIRECTION_RIGHT = "direction.right";
    public static final String TRANS_DIRECTION_TOP = "direction.top";
    public static final String TRANS_DIRECTION_BOTTOM = "direction.bottom";

    public static final String TRANS_CONFIG_CONTAINER_REMOVE = "config.container.remove";
    public static final String TRANS_CONFIG_CONTAINER_BLANK = "config.container.blank";

    public static final String TRANS_CONFIG_CONTAINER_PRIORITY_NAME = "config.container.side_priority.name";
    public static final String TRANS_CONFIG_CONTAINER_PRIORITY_SIDE = "config.container.side_priority.side";
    public static final String TRANS_CONFIG_CONTAINER_PRIORITY_DESC = "config.container.side_priority.desc";

    // -- Stats --
    public static final String STAT_CUSTOM_NAME = "custom_name";
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

    // -- Animators --
    public static final String ANIM_DEFAULT = "default";

    // -- Attributes --
    public static final String ATTR_MAX_HEALTH = "max_health";
    public static final String ATTR_MAX_LEVEL = "max_level";
    public static final String ATTR_ATTACK_DAMAGE = "attack_damage";
    public static final String ATTR_ATTACK_SPEED = "attack_speed";
    public static final String ATTR_MOVEMENT_SPEED = "movement_speed";
    public static final String ATTR_DEFENSE = "defense";
    public static final String ATTR_ARMOR = "armor";
    public static final String ATTR_ARMOR_TOUGHNESS = "armor_toughness";
    public static final String ATTR_BASE_DEFENSE = "base_defense_range";
    public static final String ATTR_BASE_DEFENSE_WARP = "base_defense_warp_range";

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

    // PATH
    public static final String TEXTURE_ENTITY_PATH = "textures/entity/";

    // MISC
    public static final String TEXTURE_EXTENSION = ".png";
    public static final String TEXTURE_COMBINED = "_merged_with_";
    public static final String TEXTURE_SEPARATOR = "_";

    public static final int RENDER_COLOUR_WHITE = 0xffffffff;
    public static final int RENDER_COLOUR_FADE_WHITE = 0x55ffffff;




    // -- Translations --
    public static final String TRANS_ITEM_GROUP = "itemGroup.lovely_robot";

    public static final String TRANS_MSG_HEAL = "msg.rlovelyr.heal";
    public static final String TRANS_MSG_WARY = "msg.rlovelyr.wary";
    public static final String TRANS_MSG_LEVEL_UP = "msg.rlovelyr.level_up";
    public static final String TRANS_MSG_HEALTH = "msg.rlovelyr.health";
    public static final String TRANS_MSG_ATTACK = "msg.rlovelyr.attack";
    public static final String TRANS_MSG_EXPERIENCE = "msg.rlovelyr.experience";
    public static final String TRANS_MSG_ENCHANTMENT = "msg.rlovelyr.enchantment";
    public static final String TRANS_MSG_BAR = "msg.rlovelyr.bar";
    public static final String TRANS_MSG_LOOTING = "msg.rlovelyr.looting";
    public static final String TRANS_MSG_STATE = "msg.rlovelyr.state";
    public static final String TRANS_MSG_DEFENCE = "msg.rlovelyr.defence";
    public static final String TRANS_MSG_FOLLOW = "msg.rlovelyr.follow";
    public static final String TRANS_MSG_STANDBY = "msg.rlovelyr.standby";
    public static final String TRANS_MSG_BASE_DEFENCE = "msg.rlovelyr.base_defence";
    public static final String TRANS_MSG_OWNER = "msg.rlovelyr.owner";

    public static final String TRANS_MSG_NOTIFICATION = "msg.rlovelyr.notification";
    public static final String TRANS_MSG_AUTO_ATTACK = "msg.rlovelyr.auto_attack";

    public static final String TRANS_MSG_OFF = "msg.rlovelyr.off";
    public static final String TRANS_MSG_ON = "msg.rlovelyr.on";

    public static final String TRANS_MSG_CUSTOM_NAME = "msg.item.name";
    public static final String TRANS_MSG_COLOR = "msg.item.color";
    public static final String TRANS_MSG_TYPE = "msg.item.type";
    public static final String TRANS_MSG_MAX_LEVEL = "msg.item.max_level";
    public static final String TRANS_MSG_LEVEL = "msg.item.level";
    public static final String TRANS_MSG_EXP = "msg.item.exp";

    public static final String TRANS_MSG_FIRE_PROTECTION = "msg.rlovelyr.fire_protection";
    public static final String TRANS_MSG_FALL_PROTECTION = "msg.rlovelyr.fall_protection";
    public static final String TRANS_MSG_BLAST_PROTECTION = "msg.rlovelyr.blast_protection";
    public static final String TRANS_MSG_PROJECTILE_PROTECTION = "msg.rlovelyr.projectile_protection";

    public static final String TRANS_MSG_BUNNY = "variant.bunny";
    public static final String TRANS_MSG_BUNNY2 = "variant.bunny2";
    public static final String TRANS_MSG_DRAGON = "variant.dragon";
    public static final String TRANS_MSG_HONEY = "variant.honey";
    public static final String TRANS_MSG_KITSUNE = "variant.kitsune";
    public static final String TRANS_MSG_NEKO = "variant.neko";
    public static final String TRANS_MSG_VANILLA = "variant.vanilla";

    public static final String TRANS_MSG_RANDOM = "msg.item.random";
    public static final String TRANS_MSG_WHITE = "msg.item.white";
    public static final String TRANS_MSG_ORANGE = "msg.item.orange";
    public static final String TRANS_MSG_MAGENTA = "msg.item.magenta";
    public static final String TRANS_MSG_LIGHT_BLUE = "msg.item.light_blue";
    public static final String TRANS_MSG_YELLOW = "msg.item.yellow";
    public static final String TRANS_MSG_LIME = "msg.item.lime";
    public static final String TRANS_MSG_PINK = "msg.item.pink";
    public static final String TRANS_MSG_GRAY = "msg.item.gray";
    public static final String TRANS_MSG_LIGHT_GRAY = "msg.item.light_gray";
    public static final String TRANS_MSG_CYAN  = "msg.item.cyan";
    public static final String TRANS_MSG_PURPLE = "msg.item.purple";
    public static final String TRANS_MSG_BLUE = "msg.item.blue";
    public static final String TRANS_MSG_BROWN = "msg.item.brown";
    public static final String TRANS_MSG_GREEN  = "msg.item.green";
    public static final String TRANS_MSG_RED = "msg.item.red";
    public static final String TRANS_MSG_BLACK = "msg.item.black";

    // -- Methods --

    /**
     * A translation text component for a tab.
     */
    public static TranslatableComponent getTabTranslation(final String key) {
        return getTranslation("tab." + key);
    } // getTabTranslation ()

    /**
     * Represents a translation text component for an attribute.
     */
    public static TranslatableComponent getAttributeTranslation(final String key, Object... objects) {
        return getTranslation("attribute." + key, objects);
    } // getAttributeTranslation ()

    /**
     * Represents a translation text component for a skill.
     */
    public static TranslatableComponent getSkillTranslation(final String key) {
        return getTranslation("skill." + key);
    } // getSkillTranslation ()

    /**
     * Represents a translation text component for a task.
     */
    public static TranslatableComponent getTaskTranslation(final String key) {
        return getTranslation("task." + key);
    } // getSkillTranslation ()


    /**
     * Retrieves the translatable string based on the given entity variant.
     *
     * @param  variant  the entity variant for which the translatable string is to be retrieved
     * @return          the translatable message key corresponding to the entity variant
     */
    public static TranslatableComponent getTranslation(EntityVariant variant) {
        var value = getTranslation(LovelyRobotID.TRANS_MSG_BUNNY);
        switch (variant) {
            case Bunny -> value = getTranslation(LovelyRobotID.TRANS_MSG_BUNNY);
            case Bunny2 -> value = getTranslation(LovelyRobotID.TRANS_MSG_BUNNY2);
            case Dragon -> value = getTranslation(LovelyRobotID.TRANS_MSG_DRAGON);
            case Honey -> value = getTranslation(LovelyRobotID.TRANS_MSG_HONEY);
            case Kitsune -> value = getTranslation(LovelyRobotID.TRANS_MSG_KITSUNE);
            case Neko -> value = getTranslation(LovelyRobotID.TRANS_MSG_NEKO);
            case Vanilla -> value = getTranslation(LovelyRobotID.TRANS_MSG_VANILLA);
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the translatable message key based on the given entity State.
     *
     * @param  state  the entity state for which the translatable message key is needed
     * @return        the translatable message key corresponding to the entity state
     */
    public static String getTranslation(EntityState state){
        String value = LovelyRobotID.TRANS_MSG_FOLLOW;
        switch (state) {
            case Follow -> value = LovelyRobotID.TRANS_MSG_FOLLOW;
            case Defense -> value = LovelyRobotID.TRANS_MSG_DEFENCE;
            case Standby -> value = LovelyRobotID.TRANS_MSG_STANDBY;
        }
        return value;
    } // getTranslation ()

    /**
     * Retrieves the corresponding translatable message key based a given Texture.
     *
     * @param  texture the entity texture for which the translatable message key is needed
     * @return         the message key corresponding to the EntityTexture
     */
    public static String getTranslation(EntityTexture texture) {
        String value = LovelyRobotID.TRANS_MSG_PINK;
        switch (texture) {
            case RANDOM -> value = LovelyRobotID.TRANS_MSG_RANDOM;
            case WHITE -> value = LovelyRobotID.TRANS_MSG_WHITE;
            case ORANGE -> value = LovelyRobotID.TRANS_MSG_ORANGE;
            case MAGENTA -> value = LovelyRobotID.TRANS_MSG_MAGENTA;
            case LIGHT_BLUE -> value = LovelyRobotID.TRANS_MSG_LIGHT_BLUE;
            case YELLOW -> value = LovelyRobotID.TRANS_MSG_YELLOW;
            case LIME -> value = LovelyRobotID.TRANS_MSG_LIME;
            case PINK -> value = LovelyRobotID.TRANS_MSG_PINK;
            case GRAY -> value = LovelyRobotID.TRANS_MSG_GRAY;
            case LIGHT_GRAY -> value = LovelyRobotID.TRANS_MSG_LIGHT_GRAY;
            case CYAN -> value = LovelyRobotID.TRANS_MSG_CYAN;
            case PURPLE -> value = LovelyRobotID.TRANS_MSG_PURPLE;
            case BLUE -> value = LovelyRobotID.TRANS_MSG_BLUE;
            case BROWN -> value = LovelyRobotID.TRANS_MSG_BROWN;
            case GREEN -> value = LovelyRobotID.TRANS_MSG_GREEN;
            case RED -> value = LovelyRobotID.TRANS_MSG_RED;
            case BLACK -> value = LovelyRobotID.TRANS_MSG_BLACK;
        }
        return value;
    } // getTranslation ()


    public static TranslatableComponent getTranslation(final String key) {
        return new TranslatableComponent(LovelyRobot.MODID + "." + key);
    } // getTranslation ()

    public static TranslatableComponent getTranslation(final String key, Object... objects) {
        return new TranslatableComponent(LovelyRobot.MODID + "." + key, objects);
    } // getTranslation ()

    public static ResourceLocation getTexture (final String key) {
        return getId(TEXTURE_ENTITY_PATH + key + TEXTURE_EXTENSION);
    } // getTexture ()

    public static ResourceLocation getId(final String path) {
        return new ResourceLocation(LovelyRobot.MODID, path);
    } // getId ()

    public static ResourceLocation getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty())
            return getId(path);
        return new ResourceLocation(namespace, path);
    } // getId ()

} // Class LovelyRobotID