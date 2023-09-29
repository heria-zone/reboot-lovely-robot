package net.msymbios.rlovelyr.config;

import com.mojang.datafixers.util.Pair;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.config.internal.SimpleConfig;

public class ModConfigs {

    // -- Variables --
    public static SimpleConfig CONSTRUCTOR;
    private static ModConfigProvider CONFIG;

    // -- Custom Methods --
    public static void registerConfigs() {
        CONFIG = new ModConfigProvider();
        createConfigs();

        CONSTRUCTOR = SimpleConfig.of(LovelyRobotMod.MODID + "-config").provider(CONFIG).request();

        assignConfigs();
    } // registerConfigs ()

    private static void createConfigs() {
        String additional = "\n";

        // GENERAL
        CONFIG.addKeyValuePair(new Pair<>("general.movement-melee-attack", 0.4F), "Movement speed when it is melee attacking | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.movement-follow-owner", 0.4F), "Movement speed when following player | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.movement-wander-around", 0.4F),"Movement speed while it is wandering around | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.general-armor", 0F),"General Armor | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.general-armor-toughness", 0F),"General Armor Toughness | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.follow-distance-max", 10F),  "Maximum distance allowed while following | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.follow-distance-min", 2F),   "Minimum distance allowed while following | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.look-range", 8F),            "How much should the head rotate while looking | Float", additional);

        // LEVEL & EXPERIENCE
        CONFIG.addKeyValuePair(new Pair<>("level.experience-base", 50),         "Basic experience required to level up | Integer");
        CONFIG.addKeyValuePair(new Pair<>("level.experience-mult", 2),          "Increase value for each level | Integer", additional);

        // COMBAT
        CONFIG.addKeyValuePair(new Pair<>("combat.attack-chance", 5),           "Probability of attacking when attacked | Integer");
        CONFIG.addKeyValuePair(new Pair<>("combat.heal-interval", 50),          "Automatic recovery interval | Integer");
        CONFIG.addKeyValuePair(new Pair<>("combat.wary-time", 50),              "Time while being in combat mode | Integer");
        CONFIG.addKeyValuePair(new Pair<>("combat.loot-enchantment", true),     "Enable looting enchantments | Boolean [true/false]]");
        CONFIG.addKeyValuePair(new Pair<>("combat.loot-enchantment-level", 10), "Levels required for looting enchantments | Integer");
        CONFIG.addKeyValuePair(new Pair<>("combat.max-loot-enchantment", 3),    "Maximum level of looting enchantments | Integer", additional);

        // PROTECTION
        CONFIG.addKeyValuePair(new Pair<>("protection.limit-fire", 80),         "Fire protection upper limit | Integer");
        CONFIG.addKeyValuePair(new Pair<>("protection.limit-fall", 80),         "Fall protection upper limit | Integer");
        CONFIG.addKeyValuePair(new Pair<>("protection.limit-blast", 80),        "Blast protection upper limit | Integer");
        CONFIG.addKeyValuePair(new Pair<>("protection.limit-projectile", 80),   "Projectile protection upper limit | Integer", additional);

        // ENTITY | BUNNY
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | BUNNY2
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.bunny2.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | DRAGON
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.dragon.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | HONEY
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.honey.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | KITSUNE
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.kitsune.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | NEKO
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.neko.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float", additional);

        // ENTITY | VANILLA
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.max-level", 200F),              "Maximum Level | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.max-health", 30F),              "Maximum Health | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.attack-damage", 5F),            "Attack Damage | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.attack-speed", 1.2F),           "Attack Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.defence", 5F),                  "Defence | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.movement-speed", 0.4F),         "Movement Speed | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.base-defence-range", 15F),      "Total area to be defended | Float");
        CONFIG.addKeyValuePair(new Pair<>("entity.vanilla.base-defence-warp-range", 10F), "Distance from base allowed until teleported | Float");
    } // createConfigs ()

    private static void assignConfigs() {
        // GENERAL
        InternalMetrics.MOVEMENT_MELEE_ATTACK = CONSTRUCTOR.getOrDefault("general.movement-melee-attack", 0.4F);
        InternalMetrics.MOVEMENT_FOLLOW_OWNER = CONSTRUCTOR.getOrDefault("general.movement-follow-owner", 0.4F);
        InternalMetrics.MOVEMENT_WANDER_AROUND = CONSTRUCTOR.getOrDefault("general.movement-wander-around", 0.4F);
        InternalMetrics.GENERAL_ARMOR = CONSTRUCTOR.getOrDefault("general.general-armor", 0F);
        InternalMetrics.GENERAL_ARMOR_TOUGHNESS = CONSTRUCTOR.getOrDefault("general.general-armor-toughness", 0F);
        InternalMetrics.FOLLOW_DISTANCE_MAX = CONSTRUCTOR.getOrDefault("general.follow-distance-max", 10F);
        InternalMetrics.FOLLOW_DISTANCE_MIN = CONSTRUCTOR.getOrDefault("general.follow-distance-min", 2F);
        InternalMetrics.LOOK_RANGE = CONSTRUCTOR.getOrDefault("general.look-range", 8F);

        // LEVEL & EXPERIENCE
        InternalMetrics.EXPERIENCE_BASE = CONSTRUCTOR.getOrDefault("level.experience-base", 50);
        InternalMetrics.EXPERIENCE_MULTIPLIER = CONSTRUCTOR.getOrDefault("level.experience-mult", 2);

        // COMBAT
        InternalMetrics.ATTACK_CHANCE = CONSTRUCTOR.getOrDefault("combat.attack-chance", 5);
        InternalMetrics.HEAL_INTERVAL = CONSTRUCTOR.getOrDefault("combat.heal-interval", 50);
        InternalMetrics.WARY_TIME = CONSTRUCTOR.getOrDefault("combat.wary-time", 50);
        InternalMetrics.LOOT_ENCHANTMENT = CONSTRUCTOR.getOrDefault("combat.loot-enchantment", true);
        InternalMetrics.LOOT_ENCHANTMENT_LEVEL = CONSTRUCTOR.getOrDefault("combat.loot-enchantment-level", 10);
        InternalMetrics.MAX_LOOT_ENCHANTMENT = CONSTRUCTOR.getOrDefault("combat.max-loot-enchantment", 3);

        // PROTECTION
        InternalMetrics.PROTECTION_LIMIT_FIRE = CONSTRUCTOR.getOrDefault("protection.limit-fire", 80);
        InternalMetrics.PROTECTION_LIMIT_FALL = CONSTRUCTOR.getOrDefault("protection.limit-fall", 80);
        InternalMetrics.PROTECTION_LIMIT_BLAST = CONSTRUCTOR.getOrDefault("protection.limit-blast", 80);
        InternalMetrics.PROTECTION_LIMIT_PROJECTILE = CONSTRUCTOR.getOrDefault("protection.limit-projectile", 80);

        // ENTITY | BUNNY
        InternalMetrics.BUNNY_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.bunny.max-level", 200F);
        InternalMetrics.BUNNY_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.bunny.max-health", 30F);
        InternalMetrics.BUNNY_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.bunny.attack-damage", 5F);
        InternalMetrics.BUNNY_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny.attack-speed", 1.2F);
        InternalMetrics.BUNNY_DEFENCE = CONSTRUCTOR.getOrDefault("entity.bunny.defence", 5F);
        InternalMetrics.BUNNY_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny.movement-speed", 0.4F);
        InternalMetrics.BUNNY_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny.base-defence-range", 15F);
        InternalMetrics.BUNNY_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny.base-defence-warp-range", 10F);

        // ENTITY | BUNNY2
        InternalMetrics.BUNNY2_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.bunny2.max-level", 200F);
        InternalMetrics.BUNNY2_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.bunny2.max-health", 30F);
        InternalMetrics.BUNNY2_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.bunny2.attack-damage", 5F);
        InternalMetrics.BUNNY2_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny2.attack-speed", 1.2F);
        InternalMetrics.BUNNY2_DEFENCE = CONSTRUCTOR.getOrDefault("entity.bunny2.defence", 6F);
        InternalMetrics.BUNNY2_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny2.movement-speed", 0.4F);
        InternalMetrics.BUNNY2_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny2.base-defence-range", 15F);
        InternalMetrics.BUNNY2_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny2.base-defence-warp-range", 10F);

        // ENTITY | DRAGON
        InternalMetrics.DRAGON_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.dragon.max-level", 200F);
        InternalMetrics.DRAGON_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.dragon.max-health", 30F);
        InternalMetrics.DRAGON_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.dragon.attack-damage", 7F);
        InternalMetrics.DRAGON_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.dragon.attack-speed", 1.2F);
        InternalMetrics.DRAGON_DEFENCE = CONSTRUCTOR.getOrDefault("entity.dragon.defence", 7F);
        InternalMetrics.DRAGON_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.dragon.movement-speed", 0.4F);
        InternalMetrics.DRAGON_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.dragon.base-defence-range", 15F);
        InternalMetrics.DRAGON_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.dragon.base-defence-warp-range", 10F);

        // ENTITY | HONEY
        InternalMetrics.HONEY_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.honey.max-level", 200F);
        InternalMetrics.HONEY_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.honey.max-health", 30F);
        InternalMetrics.HONEY_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.honey.attack-damage", 6F);
        InternalMetrics.HONEY_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.honey.attack-speed", 1.2F);
        InternalMetrics.HONEY_DEFENCE = CONSTRUCTOR.getOrDefault("entity.honey.defence", 5F);
        InternalMetrics.HONEY_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.honey.movement-speed", 0.4F);
        InternalMetrics.HONEY_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.honey.base-defence-range", 15F);
        InternalMetrics.HONEY_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.honey.base-defence-warp-range", 10F);

        // ENTITY | KITSUNE
        InternalMetrics.KITSUNE_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.kitsune.max-level", 200F);
        InternalMetrics.KITSUNE_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.kitsune.max-health", 30F);
        InternalMetrics.KITSUNE_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.kitsune.attack-damage", 5F);
        InternalMetrics.KITSUNE_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.kitsune.attack-speed", 1.2F);
        InternalMetrics.KITSUNE_DEFENCE = CONSTRUCTOR.getOrDefault("entity.kitsune.defence", 5F);
        InternalMetrics.KITSUNE_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.kitsune.movement-speed", 0.4F);
        InternalMetrics.KITSUNE_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.kitsune.base-defence-range", 15F);
        InternalMetrics.KITSUNE_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.kitsune.base-defence-warp-range", 10F);

        // ENTITY | NEKO
        InternalMetrics.NEKO_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.neko.max-level", 200F);
        InternalMetrics.NEKO_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.neko.max-health", 30F);
        InternalMetrics.NEKO_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.neko.attack-damage", 5F);
        InternalMetrics.NEKO_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.neko.attack-speed", 1.2F);
        InternalMetrics.NEKO_DEFENCE = CONSTRUCTOR.getOrDefault("entity.neko.defence", 5F);
        InternalMetrics.NEKO_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.neko.movement-speed", 0.4F);
        InternalMetrics.NEKO_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.neko.base-defence-range", 15F);
        InternalMetrics.NEKO_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.neko.base-defence-warp-range", 10F);

        // ENTITY | VANILLA
        InternalMetrics.VANILLA_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.vanilla.max-level", 200F);
        InternalMetrics.VANILLA_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.vanilla.max-health", 30F);
        InternalMetrics.VANILLA_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.vanilla.attack-damage", 5F);
        InternalMetrics.VANILLA_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.vanilla.attack-speed", 1.2F);
        InternalMetrics.VANILLA_DEFENCE = CONSTRUCTOR.getOrDefault("entity.vanilla.defence", 6F);
        InternalMetrics.VANILLA_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.vanilla.movement-speed", 0.4F);
        InternalMetrics.VANILLA_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.vanilla.base-defence-range", 15F);
        InternalMetrics.VANILLA_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.vanilla.base-defence-warp-range", 10F);

        // MESSAGE
        System.out.println("All [" + CONFIG.getConfigsList().size() + "] config settings loaded!");
    } // assignConfigs ()

} // Class ModConfigs