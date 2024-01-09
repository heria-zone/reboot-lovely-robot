package net.msymbios.rlovelyr.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.msymbios.rlovelyr.entity.enums.EntityAttribute;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

import java.util.HashMap;
public class LovelyRobotCommonConfig {

    // -- Variables --
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> DRAGON_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> HONEY_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> NEKO_BASE_DEFENSE_WARP_RANGE;

    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MAX_LEVEL;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MAX_HEALTH;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_DAMAGE;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MOVEMENT_SPEED;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_DEFENSE;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR_TOUGHNESS;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_BASE_DEFENSE_RANGE;
    public static final ForgeConfigSpec.ConfigValue<Float> VANILLA_BASE_DEFENSE_WARP_RANGE;

    static {
        BUILDER.push("Reboot LovelyRobot");

        // GENERAL
        InternalMetric.MOVEMENT_MELEE_ATTACK = BUILDER.comment("Movement speed when it is melee attacking").define("general.movement-melee-attack", 0.8F);
        InternalMetric.MOVEMENT_FOLLOW_OWNER = BUILDER.comment("Movement speed when following player").define("general.movement-follow-owner", 0.7F);
        InternalMetric.MOVEMENT_WANDER_AROUND = BUILDER.comment("Movement speed while it is wandering around").define("general.movement-wander-around", 0.6F);
        InternalMetric.FOLLOW_DISTANCE_MAX = BUILDER.comment("Maximum distance allowed while following").define("general.follow-distance-max", 10F);
        InternalMetric.FOLLOW_DISTANCE_MIN = BUILDER.comment("Minimum distance allowed while following").define("general.follow-distance-min", 2F);
        InternalMetric.LOOK_RANGE = BUILDER.comment("How much should the head rotate while looking").define("general.look-range", 8F);

        // RENDERER
        InternalMetric.SHADOW_RADIUS = BUILDER.comment("Entity shadow cast size on the ground").define("renderer.shadow-radius", 0.4F);
        InternalMetric.WIDTH = BUILDER.comment("Entity hit-box width").define("renderer.width", 0.4F);
        InternalMetric.HEIGHT = BUILDER.comment("Entity hit-box height").define("renderer.height", 2F);

        // LEVEL & EXPERIENCE
        InternalMetric.EXPERIENCE_BASE = BUILDER.comment("Basic experience required to level up").define("level.experience-base", 50);
        InternalMetric.EXPERIENCE_MULTIPLIER = BUILDER.comment("Increase value for each level").define("level.experience-mult", 2);

        // COMBAT
        InternalMetric.ATTACK_CHANCE = BUILDER.comment("Probability of attacking when attacked").define("combat.attack-chance", 5);
        InternalMetric.GLOBAL_AUTO_HEAL = BUILDER.comment("Enable/Disable Auto Heal for every robot (WIP)").define("combat.global-auto-heal", true);
        InternalMetric.HEAL_INTERVAL = BUILDER.comment("Automatic recovery interval").define("combat.heal-interval", 50);
        InternalMetric.WARY_TIME = BUILDER.comment("Time while being in combat mode").define("combat.wary-time", 50);
        InternalMetric.LOOT_ENCHANTMENT = BUILDER.comment("Enable looting enchantments").define("combat.loot-enchantment", true);
        InternalMetric.LOOT_ENCHANTMENT_LEVEL = BUILDER.comment("Levels required for looting enchantments").define("combat.loot-enchantment-level", 10);
        InternalMetric.MAX_LOOT_ENCHANTMENT = BUILDER.comment("Maximum level of looting enchantments").define("combat.max-loot-enchantment", 3);
        InternalMetric.BASE_DEFENCE_RANGE = BUILDER.comment("Base range to defend").define("combat.base-defence-range", 15);
        InternalMetric.BASE_DEFENCE_WARP_RANGE = BUILDER.comment("Range till teleport back to base").define("combat.base-defence-warp-range", 10);

        // PROTECTION
        InternalMetric.PROTECTION_LIMIT_FIRE = BUILDER.comment("Fire protection upper limit").define("protection.limit-fire", 80);
        InternalMetric.PROTECTION_LIMIT_FALL = BUILDER.comment("Fall protection upper limit").define("protection.limit-fall", 80);
        InternalMetric.PROTECTION_LIMIT_BLAST = BUILDER.comment("Blast protection upper limit").define("protection.limit-blast", 80);
        InternalMetric.PROTECTION_LIMIT_PROJECTILE = BUILDER.comment("Projectile protection upper limit").define("protection.limit-projectile", 80);

        // ENTITY | BUNNY
        BUNNY_MAX_LEVEL = BUILDER.define("entity.bunny.max-level", 200F);
        BUNNY_MAX_HEALTH = BUILDER.define("entity.bunny.max-health", 30F);
        BUNNY_ATTACK_DAMAGE = BUILDER.define("entity.bunny.attack-damage", 5F);
        BUNNY_ATTACK_SPEED = BUILDER.define("entity.bunny.attack-speed", 1.2F);
        BUNNY_MOVEMENT_SPEED = BUILDER.define("entity.bunny.movement-speed", 0.4F);
        BUNNY_DEFENSE = BUILDER.define("entity.bunny.defence", 5F);
        BUNNY_ARMOR = BUILDER.define("entity.bunny.armor", 0F);
        BUNNY_ARMOR_TOUGHNESS = BUILDER.define("entity.bunny.armor-toughness", 0F);
        BUNNY_BASE_DEFENSE_RANGE = BUILDER.define("entity.bunny.base-defence-range", 15F);
        BUNNY_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.bunny.base-defence-warp-range", 10F);

        // ENTITY | BUNNY2
        BUNNY2_MAX_LEVEL = BUILDER.define("entity.bunny2.max-level", 200F);
        BUNNY2_MAX_HEALTH = BUILDER.define("entity.bunny2.max-health", 30F);
        BUNNY2_ATTACK_DAMAGE = BUILDER.define("entity.bunny2.attack-damage", 5F);
        BUNNY2_ATTACK_SPEED = BUILDER.define("entity.bunny2.attack-speed", 1.2F);
        BUNNY2_MOVEMENT_SPEED = BUILDER.define("entity.bunny2.movement-speed", 0.4F);
        BUNNY2_DEFENSE = BUILDER.define("entity.bunny2.defence", 6F);
        BUNNY2_ARMOR = BUILDER.define("entity.bunny2.armor", 0F);
        BUNNY2_ARMOR_TOUGHNESS = BUILDER.define("entity.bunny2.armor-toughness", 0F);
        BUNNY2_BASE_DEFENSE_RANGE = BUILDER.define("entity.bunny2.base-defence-range", 15F);
        BUNNY2_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.bunny2.base-defence-warp-range", 10F);

        // ENTITY | DRAGON
        DRAGON_MAX_LEVEL = BUILDER.define("entity.dragon.max-level", 200F);
        DRAGON_MAX_HEALTH = BUILDER.define("entity.dragon.max-health", 30F);
        DRAGON_ATTACK_DAMAGE = BUILDER.define("entity.dragon.attack-damage", 7F);
        DRAGON_ATTACK_SPEED = BUILDER.define("entity.dragon.attack-speed", 1.2F);
        DRAGON_MOVEMENT_SPEED = BUILDER.define("entity.dragon.movement-speed", 0.4F);
        DRAGON_DEFENSE = BUILDER.define("entity.dragon.defence", 7F);
        DRAGON_ARMOR = BUILDER.define("entity.dragon.armor", 0F);
        DRAGON_ARMOR_TOUGHNESS = BUILDER.define("entity.dragon.armor-toughness", 0F);
        DRAGON_BASE_DEFENSE_RANGE = BUILDER.define("entity.dragon.base-defence-range", 15F);
        DRAGON_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.dragon.base-defence-warp-range", 10F);

        // ENTITY | HONEY
        HONEY_MAX_LEVEL = BUILDER.define("entity.honey.max-level", 200F);
        HONEY_MAX_HEALTH = BUILDER.define("entity.honey.max-health", 30F);
        HONEY_ATTACK_DAMAGE = BUILDER.define("entity.honey.attack-damage", 6F);
        HONEY_ATTACK_SPEED = BUILDER.define("entity.honey.attack-speed", 1.2F);
        HONEY_MOVEMENT_SPEED = BUILDER.define("entity.honey.movement-speed", 0.4F);
        HONEY_DEFENSE = BUILDER.define("entity.honey.defence", 5F);
        HONEY_ARMOR = BUILDER.define("entity.honey.armor", 0F);
        HONEY_ARMOR_TOUGHNESS = BUILDER.define("entity.honey.armor-toughness", 0F);
        HONEY_BASE_DEFENSE_RANGE = BUILDER.define("entity.honey.base-defence-range", 15F);
        HONEY_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.honey.base-defence-warp-range", 10F);

        // ENTITY | KITSUNE
        KITSUNE_MAX_LEVEL = BUILDER.define("entity.kitsune.max-level", 200F);
        KITSUNE_MAX_HEALTH = BUILDER.define("entity.kitsune.max-health", 30F);
        KITSUNE_ATTACK_DAMAGE = BUILDER.define("entity.kitsune.attack-damage", 5F);
        KITSUNE_ATTACK_SPEED = BUILDER.define("entity.kitsune.attack-speed", 1.2F);
        KITSUNE_MOVEMENT_SPEED = BUILDER.define("entity.kitsune.movement-speed", 0.4F);
        KITSUNE_DEFENSE = BUILDER.define("entity.kitsune.defence", 5F);
        KITSUNE_ARMOR = BUILDER.define("entity.kitsune.armor", 0F);
        KITSUNE_ARMOR_TOUGHNESS = BUILDER.define("entity.kitsune.armor-toughness", 0F);
        KITSUNE_BASE_DEFENSE_RANGE = BUILDER.define("entity.kitsune.base-defence-range", 15F);
        KITSUNE_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.kitsune.base-defence-warp-range", 10F);

        // ENTITY | NEKO
        NEKO_MAX_LEVEL = BUILDER.define("entity.neko.max-level", 200F);
        NEKO_MAX_HEALTH = BUILDER.define("entity.neko.max-health", 30F);
        NEKO_ATTACK_DAMAGE = BUILDER.define("entity.neko.attack-damage", 6F);
        NEKO_ATTACK_SPEED = BUILDER.define("entity.neko.attack-speed", 1.2F);
        NEKO_MOVEMENT_SPEED = BUILDER.define("entity.neko.movement-speed", 0.4F);
        NEKO_DEFENSE = BUILDER.define("entity.neko.defence", 5F);
        NEKO_ARMOR = BUILDER.define("entity.neko.armor", 0F);
        NEKO_ARMOR_TOUGHNESS = BUILDER.define("entity.neko.armor-toughness", 0F);
        NEKO_BASE_DEFENSE_RANGE = BUILDER.define("entity.neko.base-defence-range", 15F);
        NEKO_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.neko.base-defence-warp-range", 10F);

        // ENTITY | VANILLA
        VANILLA_MAX_LEVEL = BUILDER.define("entity.vanilla.max-level", 200F);
        VANILLA_MAX_HEALTH = BUILDER.define("entity.vanilla.max-health", 30F);
        VANILLA_ATTACK_DAMAGE = BUILDER.define("entity.vanilla.attack-damage", 6F);
        VANILLA_ATTACK_SPEED = BUILDER.define("entity.vanilla.attack-speed", 1.2F);
        VANILLA_MOVEMENT_SPEED = BUILDER.define("entity.vanilla.movement-speed", 0.4F);
        VANILLA_DEFENSE = BUILDER.define("entity.vanilla.defence", 6F);
        VANILLA_ARMOR = BUILDER.define("entity.vanilla.armor", 0F);
        VANILLA_ARMOR_TOUGHNESS = BUILDER.define("entity.vanilla.armor-toughness", 0F);
        VANILLA_BASE_DEFENSE_RANGE = BUILDER.define("entity.vanilla.base-defence-range", 15F);
        VANILLA_BASE_DEFENSE_WARP_RANGE = BUILDER.define("entity.vanilla.base-defence-warp-range", 10F);

        BUILDER.pop();
        SPEC = BUILDER.build();

        InternalMetric.ATTRIBUTES.put(EntityVariant.Bunny, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, BUNNY_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, BUNNY_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, BUNNY_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, BUNNY_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, BUNNY_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, BUNNY_DEFENSE.get());
            put(EntityAttribute.ARMOR, BUNNY_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, BUNNY_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, BUNNY_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, BUNNY_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Bunny2, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, BUNNY2_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, BUNNY2_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, BUNNY2_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, BUNNY2_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, BUNNY2_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, BUNNY2_DEFENSE.get());
            put(EntityAttribute.ARMOR, BUNNY2_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, BUNNY2_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, BUNNY2_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, BUNNY2_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Dragon, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, DRAGON_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, DRAGON_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, DRAGON_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, DRAGON_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, DRAGON_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, DRAGON_DEFENSE.get());
            put(EntityAttribute.ARMOR, DRAGON_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, DRAGON_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, DRAGON_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, DRAGON_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Honey, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, HONEY_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, HONEY_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, HONEY_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, HONEY_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, HONEY_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, HONEY_DEFENSE.get());
            put(EntityAttribute.ARMOR, HONEY_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, HONEY_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, HONEY_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, HONEY_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Kitsune, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, KITSUNE_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, KITSUNE_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, KITSUNE_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, KITSUNE_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, KITSUNE_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, KITSUNE_DEFENSE.get());
            put(EntityAttribute.ARMOR, KITSUNE_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, KITSUNE_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, KITSUNE_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, KITSUNE_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Neko, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, NEKO_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, NEKO_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, NEKO_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, NEKO_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, NEKO_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, NEKO_DEFENSE.get());
            put(EntityAttribute.ARMOR, NEKO_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, NEKO_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, NEKO_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, NEKO_BASE_DEFENSE_WARP_RANGE.get());
        }});

        InternalMetric.ATTRIBUTES.put(EntityVariant.Vanilla, new HashMap<>() {{
            put(EntityAttribute.MAX_LEVEL, VANILLA_MAX_LEVEL.get());
            put(EntityAttribute.MAX_HEALTH, VANILLA_MAX_HEALTH.get());
            put(EntityAttribute.ATTACK_DAMAGE, VANILLA_ATTACK_DAMAGE.get());
            put(EntityAttribute.ATTACK_SPEED, VANILLA_ATTACK_SPEED.get());
            put(EntityAttribute.MOVEMENT_SPEED, VANILLA_MOVEMENT_SPEED.get());
            put(EntityAttribute.DEFENSE, VANILLA_DEFENSE.get());
            put(EntityAttribute.ARMOR, VANILLA_ARMOR.get());
            put(EntityAttribute.ARMOR_TOUGHNESS, VANILLA_ARMOR_TOUGHNESS.get());
            put(EntityAttribute.BASE_DEFENSE_RANGE, VANILLA_BASE_DEFENSE_RANGE.get());
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, VANILLA_BASE_DEFENSE_WARP_RANGE.get());
        }});
    }

} // Class LovelyRobotCommonConfig