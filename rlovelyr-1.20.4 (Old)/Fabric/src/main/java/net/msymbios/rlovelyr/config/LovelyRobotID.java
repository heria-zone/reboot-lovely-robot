package net.msymbios.rlovelyr.config;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.attribute.attributes.EnumAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.FloatAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.IntAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.ModifiableFloatAttribute;
import net.msymbios.rlovelyr.entity.attribute.attributes.numbers.ModifiableIntAttribute;
import net.msymbios.rlovelyr.entity.attribute.messages.IsEnabledMessage;
import net.msymbios.rlovelyr.network.message.NameMessage;

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

    // -- Entities --

    // ROBOTS
    public static final String BUNNY = "bunny";
    public static final String BUNNY2 = "bunny2";
    public static final String DRAGON = "dragon";
    public static final String HONEY = "honey";
    public static final String KITSUNE = "kitsune";
    public static final String NEKO = "neko";
    public static final String VANILLA = "vanilla";

    // -- Stats --
    public static final String STAT_CUSTOM_NAME = "custom_name";
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

    public static final String TRANS_MSG_BUNNY = "entity.rlovelyr.bunny";
    public static final String TRANS_MSG_BUNNY2 = "entity.rlovelyr.bunny2";
    public static final String TRANS_MSG_DRAGON = "entity.rlovelyr.dragon";
    public static final String TRANS_MSG_HONEY = "entity.rlovelyr.honey";
    public static final String TRANS_MSG_KITSUNE = "entity.rlovelyr.kitsune";
    public static final String TRANS_MSG_NEKO = "entity.rlovelyr.neko";
    public static final String TRANS_MSG_VANILLA = "entity.rlovelyr.vanilla";

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

    // MESSAGES
    public static final Identifier NET_MESSAGE_ENABLED = getId("net_message.is_enabled");
    public static final Identifier NET_MESSAGE_ENUM_ATTRIBUTE = getId("net_message.enum_attribute");
    public static final Identifier NET_MESSAGE_FLOAT_VALUE = getId("net_message.float_value");
    public static final Identifier NET_MESSAGE_MODIFIABLE_FLOAT = getId("net_message.modifiable_float_base_value");
    public static final Identifier NET_MESSAGE_INT_VALUE = getId("net_message.int_value");
    public static final Identifier NET_MESSAGE_MODIFIABLE_INT = getId("net_message.modifiable_int_base_value");

    public static final Identifier NET_MESSAGE_NAME = getId("net_message.name");

    // -- Methods --

    /**
     * A translation text component for a tab.
     */
    public static MutableText getTabTranslation(final String key) {
        return getTranslation("tab." + key);
    } // getTabTranslation ()

    /**
     * Represents a translation text component for an attribute.
     */
    public static MutableText getAttributeTranslation(final String key, Object... objects) {
        return getTranslation("attribute." + key, objects);
    } // getAttributeTranslation ()

    /**
     * Represents a translation text component for a skill.
     */
    public static MutableText getSkillTranslation(final String key) {
        return getTranslation("skill." + key);
    } // getSkillTranslation ()

    public static MutableText getTranslation(final String key) {
        return Text.translatable(LovelyRobot.MODID + "." + key);
    } // getTranslation ()

    public static MutableText getTranslation(final String key, Object... objects) {
        return Text.translatable(LovelyRobot.MODID + "." + key, objects);
    } // getTranslation ()

    public static Identifier getId(final String path) {
        return new Identifier(LovelyRobot.MODID, path);
    } // getId ()

    public static Identifier getId(final String namespace, final String path) {
        if (namespace == null || namespace.isEmpty())
            return getId(path);
        return new Identifier(namespace, path);
    } // getId ()

} // Class LovelyRobotID