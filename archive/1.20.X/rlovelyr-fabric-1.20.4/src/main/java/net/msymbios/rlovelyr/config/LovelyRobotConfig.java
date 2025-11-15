package net.msymbios.rlovelyr.config;

import com.mojang.datafixers.util.Pair;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.internal.ConfigProvider;
import net.msymbios.rlovelyr.config.internal.SimpleConfig;
import net.msymbios.rlovelyr.entity.enums.EntityAttribute;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

import java.util.HashMap;

public class LovelyRobotConfig {

    // -- Variables --
    public static SimpleConfig CONFIG;
    private static ConfigProvider PROVIDER;

    // -- Custom Methods --
    public static void register() {
        create();
        assign();
    } // register ()

    private static void create() {
        PROVIDER = new ConfigProvider();
        String additional = "\n";

        // GENERAL
        PROVIDER.addComment("[General]");
        PROVIDER.addKeyValuePair(new Pair<>("general.owner-max-robot", 0), "Maximum number of robots owned by an owner");
        PROVIDER.addKeyValuePair(new Pair<>("general.movement-melee-attack", 0.8F), "Movement speed when it is melee attacking");
        PROVIDER.addKeyValuePair(new Pair<>("general.movement-follow-owner", 0.7F), "Movement speed when following player");
        PROVIDER.addKeyValuePair(new Pair<>("general.movement-wander-around", 0.6F),"Movement speed while it is wandering around");
        PROVIDER.addKeyValuePair(new Pair<>("general.follow-distance-max", 10F),  "Maximum distance allowed while following");
        PROVIDER.addKeyValuePair(new Pair<>("general.follow-distance-min", 2F),   "Minimum distance allowed while following");
        PROVIDER.addKeyValuePair(new Pair<>("general.look-range", 8F),            "How much should the head rotate while looking", additional);

        // RENDERER
        PROVIDER.addComment("[Renderer]");
        PROVIDER.addKeyValuePair(new Pair<>("renderer.shadow-radius", 0.4F), "Entity shadow cast size on the ground");
        PROVIDER.addKeyValuePair(new Pair<>("renderer.width", 0.4F),         "Entity hit-box width");
        PROVIDER.addKeyValuePair(new Pair<>("renderer.height", 2F),          "Entity hit-box height", additional);

        // LEVEL & EXPERIENCE
        PROVIDER.addComment("[Level & Experience]");
        PROVIDER.addKeyValuePair(new Pair<>("level.experience-base", 50),         "Basic experience required to level up");
        PROVIDER.addKeyValuePair(new Pair<>("level.experience-mult", 2),          "Increase value for each level", additional);

        // COMBAT
        PROVIDER.addComment("[Combat]");
        PROVIDER.addKeyValuePair(new Pair<>("combat.attack-chance", 5),           "Probability of attacking when attacked");
        PROVIDER.addKeyValuePair(new Pair<>("combat.heal-interval", 50),          "Automatic recovery interval");
        PROVIDER.addKeyValuePair(new Pair<>("combat.wary-time", 50),              "Time while being in combat mode");
        PROVIDER.addKeyValuePair(new Pair<>("combat.loot-enchantment", true),     "Enable looting enchantments");
        PROVIDER.addKeyValuePair(new Pair<>("combat.loot-enchantment-level", 10), "Levels required for looting enchantments");
        PROVIDER.addKeyValuePair(new Pair<>("combat.max-loot-enchantment", 3),    "Maximum level of looting enchantments");
        PROVIDER.addKeyValuePair(new Pair<>("combat.base-defence-range", 15),     "Base range to defend");
        PROVIDER.addKeyValuePair(new Pair<>("combat.base-defence-warp-range", 10),"Range till teleport back to base", additional);

        // PROTECTION
        PROVIDER.addComment("[Protection]");
        PROVIDER.addKeyValuePair(new Pair<>("protection.limit-fire", 80),         "Fire protection upper limit");
        PROVIDER.addKeyValuePair(new Pair<>("protection.limit-fall", 80),         "Fall protection upper limit");
        PROVIDER.addKeyValuePair(new Pair<>("protection.limit-blast", 80),        "Blast protection upper limit");
        PROVIDER.addKeyValuePair(new Pair<>("protection.limit-projectile", 80),   "Projectile protection upper limit", additional);

        PROVIDER.addComment("[Entities]");

        // ENTITY | BUNNY
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | BUNNY2
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.bunny2.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | DRAGON
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.attack-damage", 7F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.defence", 7F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.dragon.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | HONEY
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.movement-speed", 0.4F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.honey.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | KITSUNE
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.kitsune.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | NEKO
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.neko.base-defence-warp-range", 10F), "Distance from base allowed until teleported", additional);

        // ENTITY | VANILLA
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.max-level", 200F),              "Maximum Level");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.max-health", 30F),              "Maximum Health");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.attack-damage", 5F),            "Attack Damage");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.attack-speed", 1.2F),           "Attack Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.defence", 5F),                  "Defence");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.armor", 0F),                    "Armor");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.armor-toughness", 0F),          "Armor Toughness");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.movement-speed", 0.6F),         "Movement Speed");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.base-defence-range", 15F),      "Total area to be defended");
        PROVIDER.addKeyValuePair(new Pair<>("entity.vanilla.base-defence-warp-range", 10F), "Distance from base allowed until teleported");

    } // create ()

    private static void assign() {
        CONFIG = SimpleConfig.of(LovelyRobot.MODID + "-config").provider(PROVIDER).request();

        // NEW
        InternalMetric.OWNER_MAX_MAID_NUM = CONFIG.getOrDefault("general.owner-max-robot", 0);

        // GENERAL
        InternalMetric.MOVEMENT_MELEE_ATTACK = CONFIG.getOrDefault("general.movement-melee-attack", 0.8F);
        InternalMetric.MOVEMENT_FOLLOW_OWNER = CONFIG.getOrDefault("general.movement-follow-owner", 0.7F);
        InternalMetric.MOVEMENT_WANDER_AROUND = CONFIG.getOrDefault("general.movement-wander-around", 0.6F);
        InternalMetric.FOLLOW_DISTANCE_MAX = CONFIG.getOrDefault("general.follow-distance-max", 10F);
        InternalMetric.FOLLOW_DISTANCE_MIN = CONFIG.getOrDefault("general.follow-distance-min", 2F);
        InternalMetric.LOOK_RANGE = CONFIG.getOrDefault("general.look-range", 8F);

        // RENDERER
        InternalMetric.SHADOW_RADIUS = CONFIG.getOrDefault("shadow-radius", 0.4F);
        InternalMetric.WIDTH = CONFIG.getOrDefault("renderer.width", 0.4F);
        InternalMetric.HEIGHT = CONFIG.getOrDefault("renderer.height", 2F);

        // LEVEL & EXPERIENCE
        InternalMetric.EXPERIENCE_BASE = CONFIG.getOrDefault("level.experience-base", 50);
        InternalMetric.EXPERIENCE_MULTIPLIER = CONFIG.getOrDefault("level.experience-mult", 2);

        // COMBAT
        InternalMetric.ATTACK_CHANCE = CONFIG.getOrDefault("combat.attack-chance", 5);
        InternalMetric.HEAL_INTERVAL = CONFIG.getOrDefault("combat.heal-interval", 50);
        InternalMetric.WARY_TIME = CONFIG.getOrDefault("combat.wary-time", 50);
        InternalMetric.LOOT_ENCHANTMENT = CONFIG.getOrDefault("combat.loot-enchantment", true);
        InternalMetric.LOOT_ENCHANTMENT_LEVEL = CONFIG.getOrDefault("combat.loot-enchantment-level", 10);
        InternalMetric.MAX_LOOT_ENCHANTMENT = CONFIG.getOrDefault("combat.max-loot-enchantment", 3);
        InternalMetric.BASE_DEFENCE_RANGE = CONFIG.getOrDefault("combat.base-defence-range", 15);
        InternalMetric.BASE_DEFENCE_WARP_RANGE = CONFIG.getOrDefault("combat.base-defence-warp-range", 10);

        // PROTECTION
        InternalMetric.PROTECTION_LIMIT_FIRE = CONFIG.getOrDefault("protection.limit-fire", 80);
        InternalMetric.PROTECTION_LIMIT_FALL = CONFIG.getOrDefault("protection.limit-fall", 80);
        InternalMetric.PROTECTION_LIMIT_BLAST = CONFIG.getOrDefault("protection.limit-blast", 80);
        InternalMetric.PROTECTION_LIMIT_PROJECTILE = CONFIG.getOrDefault("protection.limit-projectile", 80);

        // ENTITY | BUNNY
        InternalMetric.ATTRIBUTES.put(EntityVariant.Bunny, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.bunny.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.bunny.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.bunny.attack-damage", 5F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.bunny.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.bunny.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.bunny.defence", 5F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.bunny.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.bunny.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.bunny.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.bunny.base-defence-warp-range", 10F));
        }});

        // ENTITY | BUNNY2
        InternalMetric.ATTRIBUTES.put(EntityVariant.Bunny2, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.bunny2.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.bunny2.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.bunny2.attack-damage", 5F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.bunny2.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.bunny2.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.bunny2.defence", 6F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.bunny2.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.bunny2.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.bunny2.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.bunny2.base-defence-warp-range", 10F));
        }});

        // ENTITY | DRAGON
        InternalMetric.ATTRIBUTES.put(EntityVariant.Dragon, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.dragon.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.dragon.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.dragon.attack-damage", 7F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.dragon.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.dragon.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.dragon.defence", 7F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.dragon.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.dragon.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.dragon.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.dragon.base-defence-warp-range", 10F));
        }});

        // ENTITY | HONEY
        InternalMetric.ATTRIBUTES.put(EntityVariant.Honey, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.honey.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.honey.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.honey.attack-damage", 6F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.honey.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.honey.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.honey.defence", 5F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.honey.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.honey.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.honey.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.honey.base-defence-warp-range", 10F));
        }});

        // ENTITY | KITSUNE
        InternalMetric.ATTRIBUTES.put(EntityVariant.Kitsune, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.kitsune.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.kitsune.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.kitsune.attack-damage", 5F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.kitsune.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.kitsune.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.kitsune.defence", 5F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.kitsune.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.kitsune.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.kitsune.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.kitsune.base-defence-warp-range", 10F));
        }});

        // ENTITY | NEKO
        InternalMetric.ATTRIBUTES.put(EntityVariant.Neko, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.neko.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.neko.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.neko.attack-damage", 6F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.neko.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.neko.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.neko.defence", 5F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.neko.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.neko.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.neko.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.neko.base-defence-warp-range", 10F));
        }});

        // ENTITY | VANILLA
        InternalMetric.ATTRIBUTES.put(EntityVariant.Vanilla, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL,               CONFIG.getOrDefault("entity.vanilla.max-level", 200F));
            put(EntityAttribute.MAX_HEALTH,              CONFIG.getOrDefault("entity.vanilla.max-health", 30F));
            put(EntityAttribute.ATTACK_DAMAGE,           CONFIG.getOrDefault("entity.vanilla.attack-damage", 6F));
            put(EntityAttribute.ATTACK_SPEED,            CONFIG.getOrDefault("entity.vanilla.attack-speed", 1.2F));
            put(EntityAttribute.MOVEMENT_SPEED,          CONFIG.getOrDefault("entity.vanilla.movement-speed", 0.4F));
            put(EntityAttribute.DEFENSE,                 CONFIG.getOrDefault("entity.vanilla.defence", 6F));
            put(EntityAttribute.ARMOR,                   CONFIG.getOrDefault("entity.vanilla.armor", 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS,         CONFIG.getOrDefault("entity.vanilla.armor-toughness", 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE,      CONFIG.getOrDefault("entity.vanilla.base-defence-range", 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, CONFIG.getOrDefault("entity.vanilla.base-defence-warp-range", 10F));
        }});
    } // assign ()

} // Class LovelyRobotConfig