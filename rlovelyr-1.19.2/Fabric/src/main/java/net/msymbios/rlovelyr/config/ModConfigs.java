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
        CONFIG.addKeyValuePair(new Pair<>("general.movement-melee-attack", 1F), "Movement speed when it is melee attacking | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.movement-follow-owner", 1F), "Movement speed when following player | Float");
        CONFIG.addKeyValuePair(new Pair<>("general.movement-wander-around", 1F),"Movement speed while it is wandering around | Float");
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
        ConfigMetric.MOVEMENT_MELEE_ATTACK = CONSTRUCTOR.getOrDefault("general.movement-melee-attack", 1F);
        ConfigMetric.MOVEMENT_FOLLOW_OWNER = CONSTRUCTOR.getOrDefault("general.movement-follow-owner", 1F);
        ConfigMetric.MOVEMENT_WANDER_AROUND = CONSTRUCTOR.getOrDefault("general.movement-wander-around", 1F);
        ConfigMetric.FOLLOW_DISTANCE_MAX = CONSTRUCTOR.getOrDefault("general.follow-distance-max", 10F);
        ConfigMetric.FOLLOW_DISTANCE_MIN = CONSTRUCTOR.getOrDefault("general.follow-distance-min", 2F);
        ConfigMetric.LOOK_RANGE = CONSTRUCTOR.getOrDefault("general.look-range", 8F);

        // LEVEL & EXPERIENCE
        ConfigMetric.EXPERIENCE_BASE = CONSTRUCTOR.getOrDefault("level.experience-base", 50);
        ConfigMetric.EXPERIENCE_MULTIPLIER = CONSTRUCTOR.getOrDefault("level.experience-mult", 2);

        // COMBAT
        ConfigMetric.ATTACK_CHANCE = CONSTRUCTOR.getOrDefault("combat.attack-chance", 5);
        ConfigMetric.HEAL_INTERVAL = CONSTRUCTOR.getOrDefault("combat.heal-interval", 50);
        ConfigMetric.WARY_TIME = CONSTRUCTOR.getOrDefault("combat.wary-time", 50);
        ConfigMetric.LOOT_ENCHANTMENT = CONSTRUCTOR.getOrDefault("combat.loot-enchantment", true);
        ConfigMetric.LOOT_ENCHANTMENT_LEVEL = CONSTRUCTOR.getOrDefault("combat.loot-enchantment-level", 10);
        ConfigMetric.MAX_LOOT_ENCHANTMENT = CONSTRUCTOR.getOrDefault("combat.max-loot-enchantment", 3);

        // PROTECTION
        ConfigMetric.PROTECTION_LIMIT_FIRE = CONSTRUCTOR.getOrDefault("protection.limit-fire", 80);
        ConfigMetric.PROTECTION_LIMIT_FALL = CONSTRUCTOR.getOrDefault("protection.limit-fall", 80);
        ConfigMetric.PROTECTION_LIMIT_BLAST = CONSTRUCTOR.getOrDefault("protection.limit-blast", 80);
        ConfigMetric.PROTECTION_LIMIT_PROJECTILE = CONSTRUCTOR.getOrDefault("protection.limit-projectile", 80);

        // ENTITY | BUNNY
        ConfigMetric.BUNNY_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.bunny.max-level", 200F);
        ConfigMetric.BUNNY_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.bunny.max-health", 30F);
        ConfigMetric.BUNNY_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.bunny.attack-damage", 5F);
        ConfigMetric.BUNNY_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny.attack-speed", 1.2F);
        ConfigMetric.BUNNY_DEFENCE = CONSTRUCTOR.getOrDefault("entity.bunny.defence", 5F);
        ConfigMetric.BUNNY_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny.movement-speed", 0.4F);
        ConfigMetric.BUNNY_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny.base-defence-range", 15F);
        ConfigMetric.BUNNY_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny.base-defence-warp-range", 10F);

        // ENTITY | BUNNY2
        ConfigMetric.BUNNY2_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.bunny2.max-level", 200F);
        ConfigMetric.BUNNY2_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.bunny2.max-health", 30F);
        ConfigMetric.BUNNY2_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.bunny2.attack-damage", 5F);
        ConfigMetric.BUNNY2_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny2.attack-speed", 1.2F);
        ConfigMetric.BUNNY2_DEFENCE = CONSTRUCTOR.getOrDefault("entity.bunny2.defence", 6F);
        ConfigMetric.BUNNY2_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.bunny2.movement-speed", 0.4F);
        ConfigMetric.BUNNY2_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny2.base-defence-range", 15F);
        ConfigMetric.BUNNY2_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.bunny2.base-defence-warp-range", 10F);

        // ENTITY | DRAGON
        ConfigMetric.DRAGON_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.dragon.max-level", 200F);
        ConfigMetric.DRAGON_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.dragon.max-health", 30F);
        ConfigMetric.DRAGON_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.dragon.attack-damage", 7F);
        ConfigMetric.DRAGON_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.dragon.attack-speed", 1.2F);
        ConfigMetric.DRAGON_DEFENCE = CONSTRUCTOR.getOrDefault("entity.dragon.defence", 7F);
        ConfigMetric.DRAGON_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.dragon.movement-speed", 0.4F);
        ConfigMetric.DRAGON_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.dragon.base-defence-range", 15F);
        ConfigMetric.DRAGON_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.dragon.base-defence-warp-range", 10F);

        // ENTITY | HONEY
        ConfigMetric.HONEY_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.honey.max-level", 200F);
        ConfigMetric.HONEY_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.honey.max-health", 30F);
        ConfigMetric.HONEY_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.honey.attack-damage", 6F);
        ConfigMetric.HONEY_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.honey.attack-speed", 1.2F);
        ConfigMetric.HONEY_DEFENCE = CONSTRUCTOR.getOrDefault("entity.honey.defence", 5F);
        ConfigMetric.HONEY_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.honey.movement-speed", 0.4F);
        ConfigMetric.HONEY_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.honey.base-defence-range", 15F);
        ConfigMetric.HONEY_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.honey.base-defence-warp-range", 10F);

        // ENTITY | KITSUNE
        ConfigMetric.KITSUNE_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.kitsune.max-level", 200F);
        ConfigMetric.KITSUNE_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.kitsune.max-health", 30F);
        ConfigMetric.KITSUNE_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.kitsune.attack-damage", 5F);
        ConfigMetric.KITSUNE_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.kitsune.attack-speed", 1.2F);
        ConfigMetric.KITSUNE_DEFENCE = CONSTRUCTOR.getOrDefault("entity.kitsune.defence", 5F);
        ConfigMetric.KITSUNE_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.kitsune.movement-speed", 0.4F);
        ConfigMetric.KITSUNE_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.kitsune.base-defence-range", 15F);
        ConfigMetric.KITSUNE_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.kitsune.base-defence-warp-range", 10F);

        // ENTITY | NEKO
        ConfigMetric.NEKO_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.neko.max-level", 200F);
        ConfigMetric.NEKO_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.neko.max-health", 30F);
        ConfigMetric.NEKO_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.neko.attack-damage", 5F);
        ConfigMetric.NEKO_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.neko.attack-speed", 1.2F);
        ConfigMetric.NEKO_DEFENCE = CONSTRUCTOR.getOrDefault("entity.neko.defence", 5F);
        ConfigMetric.NEKO_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.neko.movement-speed", 0.4F);
        ConfigMetric.NEKO_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.neko.base-defence-range", 15F);
        ConfigMetric.NEKO_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.neko.base-defence-warp-range", 10F);

        // ENTITY | VANILLA
        ConfigMetric.VANILLA_MAX_LEVEL = CONSTRUCTOR.getOrDefault("entity.vanilla.max-level", 200F);
        ConfigMetric.VANILLA_MAX_HEALTH = CONSTRUCTOR.getOrDefault("entity.vanilla.max-health", 30F);
        ConfigMetric.VANILLA_ATTACK_DAMAGE = CONSTRUCTOR.getOrDefault("entity.vanilla.attack-damage", 5F);
        ConfigMetric.VANILLA_ATTACK_SPEED = CONSTRUCTOR.getOrDefault("entity.vanilla.attack-speed", 1.2F);
        ConfigMetric.VANILLA_DEFENCE = CONSTRUCTOR.getOrDefault("entity.vanilla.defence", 6F);
        ConfigMetric.VANILLA_MOVEMENT_SPEED = CONSTRUCTOR.getOrDefault("entity.vanilla.movement-speed", 0.4F);
        ConfigMetric.VANILLA_BASE_DEFENCE_RANGE = CONSTRUCTOR.getOrDefault("entity.vanilla.base-defence-range", 15F);
        ConfigMetric.VANILLA_BASE_DEFENCE_WARP_RANGE = CONSTRUCTOR.getOrDefault("entity.vanilla.base-defence-warp-range", 10F);

        // MESSAGE
        System.out.println("[" + CONFIG.getConfigsList().size() + "] Config settings loaded!");
    } // assignConfigs ()

} // Class ModConfigs