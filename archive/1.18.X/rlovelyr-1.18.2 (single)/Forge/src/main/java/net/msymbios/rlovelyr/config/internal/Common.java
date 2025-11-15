package net.msymbios.rlovelyr.config.internal;

import com.electronwill.nightconfig.core.Config;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Config options shared by both the client and server.
 */
public class Common {

    // -- Variables --

    public final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // -- GENERAL --

    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> ownerMaxRobotNum;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> movementMeleeAttack;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> movementFollowOwner;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> movementWanderAround;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> followDistanceMax;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> followDistanceMin;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> lookRange;

    // -- RENDERER --

    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> width;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> height;

    // -- LEVEL | EXPERIENCE ---
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> experienceBase;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> experienceMultiplier;

    // -- COMBAT --
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> attackChance;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> healInterval;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> waryTime;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Boolean> globalAutoHeal;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Boolean> lootEnchantment;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> lootEnchantmentLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> maxLootEnchantment;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> baseDefenceRange;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> baseDefenceWarpRange;

    // -- PROTECTION --
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> protectionLimitFire;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> protectionLimitFall;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> protectionLimitBlast;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> protectionLimitProjectile;

    // -- ENTITY --

    // BUNNY
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> bunnyAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunnyAttributeArmorToughness;

    // BUNNY2
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> bunny2AttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> bunny2AttributeArmorToughness;

    // DRAGON
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> dragonAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> dragonAttributeArmorToughness;

    // HONEY
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> honeyAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> honeyAttributeArmorToughness;

    // KITSUNE
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> kitsuneAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> kitsuneAttributeArmorToughness;

    // NEKO
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> nekoAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> nekoAttributeArmorToughness;

    // VANILLA
    @Nonnull public final ForgeConfigSpec.ConfigValue<Integer> vanillaAttributeMaxLevel;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeMaxHealth;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeAttackDamage;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeAttackSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeMovementSpeed;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeArmor;
    @Nonnull public final ForgeConfigSpec.ConfigValue<Double> vanillaAttributeArmorToughness;

    // -- Constructor --

    public Common() {
        Config.setInsertionOrderPreserved(true);

        BUILDER.push("General");
        ownerMaxRobotNum = BUILDER
                .comment("Maximum number of robots owned by an owner.", "Note: [-1] means unlimited!", "Example: [30]")
                .worldRestart()
                .define("owner-max-robot", 30);

        movementMeleeAttack = BUILDER
                .comment("Movement speed when it is melee attacking.", "Example: [0.8]")
                .worldRestart()
                .define("movement-melee-attack", 0.8);

        movementFollowOwner = BUILDER
                .comment("Movement speed when following player.", "Example: [0.7]")
                .worldRestart()
                .define("movement-follow-owner", 0.7);

        movementWanderAround = BUILDER
                .comment("Movement speed while it is wandering around.", "Example: [0.6]")
                .worldRestart()
                .define("movement-wander-around", 0.6);

        followDistanceMax = BUILDER
                .comment("Maximum distance allowed while following.", "Example: [10]")
                .worldRestart()
                .define("follow-distance-max", 10.0);

        followDistanceMin = BUILDER
                .comment("Minimum distance allowed while following.", "Example: [2]")
                .worldRestart()
                .define("follow-distance-min", 0.2);

        lookRange = BUILDER
                .comment("How much should the head rotate while looking.", "Example: [8]")
                .worldRestart()
                .define("look-range", 8.0);
        BUILDER.pop();

        BUILDER.push("Renderer");
        width = BUILDER
                .comment("Entity hit-box width.", "Example: [0.4]")
                .worldRestart()
                .define("width", 0.4);

        height = BUILDER
                .comment("Entity hit-box height.", "Example: [1.9]")
                .worldRestart()
                .define("height", 1.9);
        BUILDER.pop();

        BUILDER.push("Level & Experience");
        experienceBase = BUILDER
                .comment("Basic experience required to level up.", "Example: [50]")
                .worldRestart()
                .define("experience-base", 50);

        experienceMultiplier = BUILDER
                .comment("Increase level experience multiplier.", "Example: [2]")
                .worldRestart()
                .define("experience-multiplier", 2);
        BUILDER.pop();

        BUILDER.push("Combat");
        attackChance = BUILDER
                .comment("Probability of attacking when attacked.", "Example: [5]")
                .worldRestart()
                .define("attack-chance", 5);

        healInterval = BUILDER
                .comment("Automatic recovery interval.", "Example: [50]")
                .worldRestart()
                .define("heal-interval", 50);

        waryTime = BUILDER
                .comment("Time while being in combat mode.", "Example: [50]")
                .worldRestart()
                .define("wary-time", 50);

        globalAutoHeal = BUILDER
                .comment("Enable/disable global robots healing.", "Example: [true/false]")
                .worldRestart()
                .define("global-heal", true);

        lootEnchantment = BUILDER
                .comment("Enable looting enchantments.", "Example: [true/false]")
                .worldRestart()
                .define("loot-enchantment", true);

        lootEnchantmentLevel = BUILDER
                .comment("Levels required for looting enchantments.", "Example: [10]")
                .worldRestart()
                .define("loot-enchantment-level", 10);

        maxLootEnchantment = BUILDER
                .comment("Maximum level of looting enchantments.", "Example: [3]")
                .worldRestart()
                .define("max-loot-enchantment", 3);

        baseDefenceRange = BUILDER
                .comment("Base range to defend.", "Example: [15F]")
                .worldRestart()
                .define("base-defence-range", 15.0);

        baseDefenceWarpRange = BUILDER
                .comment("Range till teleport back to base.", "Example: [10F]")
                .worldRestart()
                .define("base-defence-warp-range", 10.0);
        BUILDER.pop();

        BUILDER.push("Protection");
        protectionLimitFire = BUILDER
                .comment("Fire protection upper limit.", "Example: [80]")
                .worldRestart()
                .define("limit-fire", 80);

        protectionLimitFall = BUILDER
                .comment("Fall protection upper limit.", "Example: [80]")
                .worldRestart()
                .define("limit-fall", 80);

        protectionLimitBlast = BUILDER
                .comment("Blast protection upper limit.", "Example: [80]")
                .worldRestart()
                .define("limit-blast", 80);

        protectionLimitProjectile = BUILDER
                .comment("Projectile protection upper limit.", "Example: [80]")
                .worldRestart()
                .define("limit-projectile", 80);
        BUILDER.pop();

        BUILDER.push("Entity");

        BUILDER.push("Bunny");
        bunnyAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        bunnyAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        bunnyAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        bunnyAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        bunnyAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.6);

        bunnyAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        bunnyAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Bunny2");
        bunny2AttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        bunny2AttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        bunny2AttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        bunny2AttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        bunny2AttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.6);

        bunny2AttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        bunny2AttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Dragon");
        dragonAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        dragonAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        dragonAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        dragonAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        dragonAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.4);

        dragonAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        dragonAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Honey");
        honeyAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        honeyAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        honeyAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        honeyAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        honeyAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.4);

        honeyAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        honeyAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Kitsune");
        kitsuneAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        kitsuneAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        kitsuneAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        kitsuneAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        kitsuneAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.4);

        kitsuneAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        kitsuneAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Neko");
        nekoAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200]")
                .worldRestart()
                .define("max-level", 200);

        nekoAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        nekoAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        nekoAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        nekoAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.4]")
                .worldRestart()
                .define("movement-speed", 0.4);

        nekoAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        nekoAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.push("Vanilla");
        vanillaAttributeMaxLevel = BUILDER
                .comment("Maximum Level", "Example: [200.0]")
                .worldRestart()
                .define("max-level", 200);

        vanillaAttributeMaxHealth = BUILDER
                .comment("Maximum Health", "Example: [30.0]")
                .worldRestart()
                .define("max-health", 30.0);

        vanillaAttributeAttackDamage = BUILDER
                .comment("Attack Damage", "Example: [5.0]")
                .worldRestart()
                .define("attack-damage", 5.0);

        vanillaAttributeAttackSpeed = BUILDER
                .comment("Attack Speed", "Example: [ 1.2]")
                .worldRestart()
                .define("attack-speed",  1.2);

        vanillaAttributeMovementSpeed = BUILDER
                .comment("Movement Speed", "Example: [0.6]")
                .worldRestart()
                .define("movement-speed", 0.6);

        vanillaAttributeArmor = BUILDER
                .comment("Armor", "Example: [0.0]")
                .worldRestart()
                .define("armor", 5.0);

        vanillaAttributeArmorToughness = BUILDER
                .comment("Armor Toughness", "Example: [0.0]")
                .worldRestart()
                .define("armor-toughness",0.0);
        BUILDER.pop();

        BUILDER.pop();
    } // Constructor Common ()

} // Class Common