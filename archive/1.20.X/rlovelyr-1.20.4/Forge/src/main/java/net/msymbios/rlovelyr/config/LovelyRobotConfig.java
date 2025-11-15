package net.msymbios.rlovelyr.config;

import com.electronwill.nightconfig.core.Config;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.msymbios.rlovelyr.LovelyRobot;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LovelyRobotConfig {

    // -- Variables --

    //private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    //public static final ForgeConfigSpec SPEC = BUILDER.build();

    //private static final ForgeConfigSpec.IntValue MAGIC_NUMBER = BUILDER.comment("A magic number").defineInRange("magicNumber", 42, 0, Integer.MAX_VALUE);

    // a list of strings that are treated as resource locations for items
    //private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), LovelyRobotConfig::validateItemName);

    //public static String magicNumberIntroduction;
    //public static Set<Item> items;

    // -- Methods --

    public static void register (FMLJavaModLoadingContext context) {
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        //context.registerConfig(ModConfig.Type.COMMON, LovelyRobotConfig.SPEC);

        context.registerConfig(ModConfig.Type.CLIENT, LovelyRobotConfig.Client.CLIENT_SPEC);
        context.registerConfig(ModConfig.Type.COMMON, LovelyRobotConfig.Common.COMMON_SPEC);
    } // register ()

    private static boolean validateItemName(final Object obj) {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    } // validateItemName ()

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        Client.register();
        //Common.register();
        // convert the list of strings into a set of items
        //items = ITEM_STRINGS.get().stream().map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName))).collect(Collectors.toSet());
    } // onLoad ()

    // -- Classes --

    /**
     * Config options only available to each client.
     */
    public static class Client {

        // -- Variables --

        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        public static final ForgeConfigSpec CLIENT_SPEC;

        // -- RENDERER --

        private static final ForgeConfigSpec.ConfigValue<Float> SHADOW_RADIUS;
        public static float ShadowRadius = 0;

        static {
            Config.setInsertionOrderPreserved(true);

            BUILDER.push("Renderer");
            SHADOW_RADIUS = BUILDER
                    .comment("Entity shadow, the cast size on the ground.", "Example: [0.4]")
                    .define("shadow-radius", 0.4F);
            BUILDER.pop();

            CLIENT_SPEC = Client.BUILDER.build();
        }

        // -- Methods --

        public static void register() {
            ShadowRadius = SHADOW_RADIUS.get();
        } // register ()

    } // Class Client

    /**
     * Config options shared by both the client and server.
     */
    public static class Common {

        // -- Variables --

        public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec COMMON_SPEC;

        // -- GENERAL --
        public static int OwnerMaxRobotNum = 30;
        public static double MovementMeleeAttack = 0.8F;
        public static float MovementFollowOwner = 0.7F;
        public static double MovementWanderAround = 0.6F;
        public static float FollowDistanceMax = 10F;
        public static float FollowDistanceMin = 0.1F;
        public static float LookRange = 8F;

        private static final ForgeConfigSpec.ConfigValue<Integer> OWNER_MAX_ROBOT_NUM;
        private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_MELEE_ATTACK;
        private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_FOLLOW_OWNER;
        private static final ForgeConfigSpec.ConfigValue<Float> MOVEMENT_WANDER_AROUND;
        private static final ForgeConfigSpec.ConfigValue<Float> FOLLOW_DISTANCE_MAX;
        private static final ForgeConfigSpec.ConfigValue<Float> FOLLOW_DISTANCE_MIN;
        private static final ForgeConfigSpec.ConfigValue<Float> LOOK_RANGE;

        // -- RENDERER --
        public static float Width = 0.4F;
        public static float Height = 1.9F;

        private static final ForgeConfigSpec.ConfigValue<Float> WIDTH;
        private static final ForgeConfigSpec.ConfigValue<Float> HEIGHT;

        // -- LEVEL | EXPERIENCE ---
        public static int ExperienceBase = 50;
        public static int ExperienceMultiplier = 2;

        private static final ForgeConfigSpec.ConfigValue<Integer> EXPERIENCE_BASE;
        private static final ForgeConfigSpec.ConfigValue<Integer> EXPERIENCE_MULTIPLIER;

        // -- COMBAT --
        public static boolean FriendlyFire = false;
        public static int AttackChance = 5;
        public static int HealInterval = 50;
        public static int WaryTime = 50;
        public static boolean GlobalAutoHeal = true;
        public static boolean LootEnchantment = true;
        public static int LootEnchantmentLevel = 10;
        public static int MaxLootEnchantment = 3;
        public static float BaseDefenceRange = 10;
        public static float BaseDefenceWarpRange = 15;

        private static final ForgeConfigSpec.ConfigValue<Boolean> FRIENDLY_FIRE;
        private static final ForgeConfigSpec.ConfigValue<Integer> ATTACK_CHANCE;
        private static final ForgeConfigSpec.ConfigValue<Integer> HEAL_INTERVAL;
        private static final ForgeConfigSpec.ConfigValue<Integer> WARY_TIME;
        private static final ForgeConfigSpec.ConfigValue<Boolean> GLOBAL_AUTO_HEAL;
        private static final ForgeConfigSpec.ConfigValue<Boolean> LOOT_ENCHANTMENT;
        private static final ForgeConfigSpec.ConfigValue<Integer> LOOT_ENCHANTMENT_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Integer> MAX_LOOT_ENCHANTMENT;
        private static final ForgeConfigSpec.ConfigValue<Float> BASE_DEFENCE_RANGE;
        private static final ForgeConfigSpec.ConfigValue<Float> BASE_DEFENCE_WARP_RANGE;

        // -- PROTECTION --
        public static int ProtectionLimitFire = 80;
        public static int ProtectionLimitFall = 80;
        public static int ProtectionLimitBlast = 80;
        public static int ProtectionLimitProjectile = 80;

        private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FIRE;
        private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_FALL;
        private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_BLAST;
        private static final ForgeConfigSpec.ConfigValue<Integer> PROTECTION_LIMIT_PROJECTILE;

        // -- ENTITY --

        // BUNNY
        public static int BunnyMaxLevel = 200;
        public static float BunnyMaxHealth = 30F;
        public static float BunnyAttackDamage = 5F;
        public static float BunnyAttackSpeed = 1.2F;
        public static float BunnyMovementSpeed = 0.6F;
        public static float BunnyArmor = 5F;
        public static float BunnyArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> BUNNY_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY_ARMOR_TOUGHNESS;

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2MaxHealth = 30F;
        public static float Bunny2AttackDamage = 5F;
        public static float Bunny2AttackSpeed = 1.2F;
        public static float Bunny2MovementSpeed = 0.6F;
        public static float Bunny2Armor = 5F;
        public static float Bunny2ArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> BUNNY2_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> BUNNY2_ARMOR_TOUGHNESS;

        // DRAGON
        public static int DragonMaxLevel = 200;
        public static float DragonMaxHealth = 30F;
        public static float DragonAttackDamage = 5F;
        public static float DragonAttackSpeed = 1.2F;
        public static float DragonMovementSpeed = 0.6F;
        public static float DragonArmor = 5F;
        public static float DragonArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> DRAGON_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> DRAGON_ARMOR_TOUGHNESS;

        // HONEY
        public static int HoneyMaxLevel = 200;
        public static float HoneyMaxHealth = 30F;
        public static float HoneyAttackDamage = 5F;
        public static float HoneyAttackSpeed = 1.2F;
        public static float HoneyMovementSpeed = 0.6F;
        public static float HoneyArmor = 5F;
        public static float HoneyArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> HONEY_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> HONEY_ARMOR_TOUGHNESS;

        // KITSUNE
        public static int KitsuneMaxLevel = 200;
        public static float KitsuneMaxHealth = 30F;
        public static float KitsuneAttackDamage = 5F;
        public static float KitsuneAttackSpeed = 1.2F;
        public static float KitsuneMovementSpeed = 0.6F;
        public static float KitsuneArmor = 5F;
        public static float KitsuneArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> KITSUNE_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> KITSUNE_ARMOR_TOUGHNESS;

        // NEKO
        public static int NekoMaxLevel = 200;
        public static float NekoMaxHealth = 30F;
        public static float NekoAttackDamage = 5F;
        public static float NekoAttackSpeed = 1.2F;
        public static float NekoMovementSpeed = 0.6F;
        public static float NekoArmor = 5F;
        public static float NekoArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> NEKO_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> NEKO_ARMOR_TOUGHNESS;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaMaxHealth = 30F;
        public static float VanillaAttackDamage = 5F;
        public static float VanillaAttackSpeed = 1.2F;
        public static float VanillaMovementSpeed = 0.6F;
        public static float VanillaArmor = 5F;
        public static float VanillaArmorToughness = 0F;

        private static final ForgeConfigSpec.ConfigValue<Integer> VANILLA_MAX_LEVEL;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MAX_HEALTH;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_DAMAGE;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ATTACK_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_MOVEMENT_SPEED;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR;
        private static final ForgeConfigSpec.ConfigValue<Float> VANILLA_ARMOR_TOUGHNESS;

        static {
            Config.setInsertionOrderPreserved(true);

            BUILDER.push("General");
            OWNER_MAX_ROBOT_NUM = BUILDER
                    .comment("Maximum number of robots owned by an owner.", "Note: [-1] means unlimited!", "Example: [30]")
                    .define("owner-max-robot", 30);

            MOVEMENT_MELEE_ATTACK = BUILDER
                    .comment("Movement speed when it is melee attacking.", "Example: [0.8]")
                    .define("movement-melee-attack", 0.8F);

            MOVEMENT_FOLLOW_OWNER = BUILDER
                    .comment("Movement speed when following player.", "Example: [0.7]")
                    .define("movement-follow-owner", 0.7F);

            MOVEMENT_WANDER_AROUND = BUILDER
                    .comment("Movement speed while it is wandering around.", "Example: [0.6]")
                    .define("movement-wander-around", 0.6F);

            FOLLOW_DISTANCE_MAX = BUILDER
                    .comment("Maximum distance allowed while following.", "Example: [10]")
                    .define("follow-distance-max", 10.0F);

            FOLLOW_DISTANCE_MIN = BUILDER
                    .comment("Minimum distance allowed while following.", "Example: [2]")
                    .define("follow-distance-min", 0.2F);

            LOOK_RANGE = BUILDER
                    .comment("How much should the head rotate while looking.", "Example: [8]")
                    .define("look-range", 8.0F);
            BUILDER.pop();

            BUILDER.push("Renderer");
            WIDTH = BUILDER
                    .comment("Entity hit-box width.", "Example: [0.4]")
                    .define("width", 0.4F);

            HEIGHT = BUILDER
                    .comment("Entity hit-box height.", "Example: [1.9]")
                    .define("height", 1.9F);
            BUILDER.pop();

            BUILDER.push("Level & Experience");
            EXPERIENCE_BASE = BUILDER
                    .comment("Basic experience required to level up.", "Example: [50]")
                    .define("experience-base", 50);

            EXPERIENCE_MULTIPLIER = BUILDER
                    .comment("Increase level experience multiplier.", "Example: [2]")
                    .define("experience-multiplier", 2);
            BUILDER.pop();

            BUILDER.push("Combat");
            FRIENDLY_FIRE = BUILDER
                    .comment("Enable/Disable Robots owners attack on their own robots.", "Example: [false]")
                    .define("friendly-fire", false);

            ATTACK_CHANCE = BUILDER
                    .comment("Probability of attacking when attacked.", "Example: [5]")
                    .define("attack-chance", 5);

            HEAL_INTERVAL = BUILDER
                    .comment("Automatic recovery interval.", "Example: [50]")
                    .define("heal-interval", 50);

            WARY_TIME = BUILDER
                    .comment("Time while being in combat mode.", "Example: [50]")
                    .define("wary-time", 50);

            GLOBAL_AUTO_HEAL = BUILDER
                    .comment("Enable/disable global robots healing.", "Example: [true/false]")
                    .define("global-heal", true);

            LOOT_ENCHANTMENT = BUILDER
                    .comment("Enable looting enchantments.", "Example: [true/false]")
                    .define("loot-enchantment", true);

            LOOT_ENCHANTMENT_LEVEL = BUILDER
                    .comment("Levels required for looting enchantments.", "Example: [10]")
                    .define("loot-enchantment-level", 10);

            MAX_LOOT_ENCHANTMENT = BUILDER
                    .comment("Maximum level of looting enchantments.", "Example: [3]")
                    .define("max-loot-enchantment", 3);

            BASE_DEFENCE_RANGE = BUILDER
                    .comment("Base range to defend.", "Example: [15F]")
                    .define("base-defence-range", 15.0F);

            BASE_DEFENCE_WARP_RANGE = BUILDER
                    .comment("Range till teleport back to base.", "Example: [10F]")
                    .define("base-defence-warp-range", 10.0F);
            BUILDER.pop();

            BUILDER.push("Protection");
            PROTECTION_LIMIT_FIRE = BUILDER
                    .comment("Fire protection upper limit.", "Example: [80]")
                    .define("limit-fire", 80);

            PROTECTION_LIMIT_FALL = BUILDER
                    .comment("Fall protection upper limit.", "Example: [80]")
                    .define("limit-fall", 80);

            PROTECTION_LIMIT_BLAST = BUILDER
                    .comment("Blast protection upper limit.", "Example: [80]")
                    .define("limit-blast", 80);

            PROTECTION_LIMIT_PROJECTILE = BUILDER
                    .comment("Projectile protection upper limit.", "Example: [80]")
                    .define("limit-projectile", 80);
            BUILDER.pop();

            BUILDER.push("Entity");

            BUILDER.push("Bunny");
            BUNNY_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            BUNNY_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            BUNNY_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            BUNNY_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            BUNNY_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.6F);

            BUNNY_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            BUNNY_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Bunny2");
            BUNNY2_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            BUNNY2_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            BUNNY2_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            BUNNY2_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            BUNNY2_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.6F);

            BUNNY2_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            BUNNY2_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Dragon");
            DRAGON_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            DRAGON_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            DRAGON_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            DRAGON_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            DRAGON_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.4F);

            DRAGON_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            DRAGON_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Honey");
            HONEY_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            HONEY_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            HONEY_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            HONEY_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            HONEY_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.4F);

            HONEY_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            HONEY_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Kitsune");
            KITSUNE_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            KITSUNE_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            KITSUNE_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            KITSUNE_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            KITSUNE_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.4F);

            KITSUNE_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            KITSUNE_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Neko");
            NEKO_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200]")
                    .define("max-level", 200);

            NEKO_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            NEKO_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            NEKO_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            NEKO_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.4]")
                    .define("movement-speed", 0.4F);

            NEKO_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            NEKO_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.push("Vanilla");
            VANILLA_MAX_LEVEL = BUILDER
                    .comment("Maximum Level", "Example: [200.0]")
                    .define("max-level", 200);

            VANILLA_MAX_HEALTH = BUILDER
                    .comment("Maximum Health", "Example: [30.0]")
                    .define("max-health", 30.0F);

            VANILLA_ATTACK_DAMAGE = BUILDER
                    .comment("Attack Damage", "Example: [5.0]")
                    .define("attack-damage", 5.0F);

            VANILLA_ATTACK_SPEED = BUILDER
                    .comment("Attack Speed", "Example: [ 1.2]")
                    .define("attack-speed",  1.2F);

            VANILLA_MOVEMENT_SPEED = BUILDER
                    .comment("Movement Speed", "Example: [0.6]")
                    .define("movement-speed", 0.6F);

            VANILLA_ARMOR = BUILDER
                    .comment("Armor", "Example: [0.0]")
                    .define("armor", 5.0F);

            VANILLA_ARMOR_TOUGHNESS = BUILDER
                    .comment("Armor Toughness", "Example: [0.0]")
                    .define("armor-toughness",0.0F);
            BUILDER.pop();

            BUILDER.pop();

            COMMON_SPEC = Common.BUILDER.build();
        }

        // -- Methods --

        public static void register() {
            // -- GENERAL --
            OwnerMaxRobotNum = OWNER_MAX_ROBOT_NUM.get();
            MovementMeleeAttack = MOVEMENT_MELEE_ATTACK.get();
            MovementFollowOwner = MOVEMENT_FOLLOW_OWNER.get();
            MovementWanderAround = MOVEMENT_WANDER_AROUND.get();
            FollowDistanceMax = FOLLOW_DISTANCE_MAX.get();
            FollowDistanceMin = FOLLOW_DISTANCE_MIN.get();
            LookRange = LOOK_RANGE.get();

            // -- RENDERER --
            Width = WIDTH.get();
            Height = HEIGHT.get();

            // -- LEVEL | EXPERIENCE ---
            ExperienceBase = EXPERIENCE_BASE.get();
            ExperienceMultiplier = EXPERIENCE_MULTIPLIER.get();

            // -- COMBAT --
            FriendlyFire = FRIENDLY_FIRE.get();
            AttackChance = ATTACK_CHANCE.get();
            HealInterval = HEAL_INTERVAL.get();
            WaryTime = WARY_TIME.get();
            GlobalAutoHeal = GLOBAL_AUTO_HEAL.get();
            LootEnchantment = LOOT_ENCHANTMENT.get();
            LootEnchantmentLevel = LOOT_ENCHANTMENT_LEVEL.get();
            MaxLootEnchantment = MAX_LOOT_ENCHANTMENT.get();
            BaseDefenceRange = BASE_DEFENCE_RANGE.get();
            BaseDefenceWarpRange = BASE_DEFENCE_WARP_RANGE.get();

            // -- PROTECTION --
            ProtectionLimitFire = PROTECTION_LIMIT_FIRE.get();
            ProtectionLimitFall = PROTECTION_LIMIT_FALL.get();
            ProtectionLimitBlast = PROTECTION_LIMIT_BLAST.get();
            ProtectionLimitProjectile = PROTECTION_LIMIT_PROJECTILE.get();

            // -- ENTITY -- BUNNY
            BunnyMaxLevel = BUNNY_MAX_LEVEL.get();
            BunnyMaxHealth = BUNNY_MAX_HEALTH.get();
            BunnyAttackDamage = BUNNY_ATTACK_DAMAGE.get();
            BunnyAttackSpeed = BUNNY_ATTACK_SPEED.get();
            BunnyMovementSpeed = BUNNY_MOVEMENT_SPEED.get();
            BunnyArmor = BUNNY_ARMOR.get();
            BunnyArmorToughness = BUNNY_ARMOR_TOUGHNESS.get();

            // BUNNY2
            Bunny2MaxLevel = BUNNY2_MAX_LEVEL.get();
            Bunny2MaxHealth = BUNNY2_MAX_HEALTH.get();
            Bunny2AttackDamage = BUNNY2_ATTACK_DAMAGE.get();
            Bunny2AttackSpeed = BUNNY2_ATTACK_SPEED.get();
            Bunny2MovementSpeed = BUNNY2_MOVEMENT_SPEED.get();
            Bunny2Armor = BUNNY2_ARMOR.get();
            Bunny2ArmorToughness = BUNNY2_ARMOR_TOUGHNESS.get();

            // DRAGON
            DragonMaxLevel = DRAGON_MAX_LEVEL.get();
            DragonMaxHealth = DRAGON_MAX_HEALTH.get();
            DragonAttackDamage = DRAGON_ATTACK_DAMAGE.get();
            DragonAttackSpeed = DRAGON_ATTACK_SPEED.get();
            DragonMovementSpeed = DRAGON_MOVEMENT_SPEED.get();
            DragonArmor = DRAGON_ARMOR.get();
            DragonArmorToughness = DRAGON_ARMOR_TOUGHNESS.get();

            // HONEY
            HoneyMaxLevel = HONEY_MAX_LEVEL.get();
            HoneyMaxHealth = HONEY_MAX_HEALTH.get();
            HoneyAttackDamage = HONEY_ATTACK_DAMAGE.get();
            HoneyAttackSpeed = HONEY_ATTACK_SPEED.get();
            HoneyMovementSpeed = HONEY_MOVEMENT_SPEED.get();
            HoneyArmor = HONEY_ARMOR.get();
            HoneyArmorToughness = HONEY_ARMOR_TOUGHNESS.get();

            // KITSUNE
            KitsuneMaxLevel = KITSUNE_MAX_LEVEL.get();
            KitsuneMaxHealth = KITSUNE_MAX_HEALTH.get();
            KitsuneAttackDamage = KITSUNE_ATTACK_DAMAGE.get();
            KitsuneAttackSpeed = KITSUNE_ATTACK_SPEED.get();
            KitsuneMovementSpeed = KITSUNE_MOVEMENT_SPEED.get();
            KitsuneArmor = KITSUNE_ARMOR.get();
            KitsuneArmorToughness = KITSUNE_ARMOR_TOUGHNESS.get();

            // NEKO
            NekoMaxLevel = NEKO_MAX_LEVEL.get();
            NekoMaxHealth = NEKO_MAX_HEALTH.get();
            NekoAttackDamage = NEKO_ATTACK_DAMAGE.get();
            NekoAttackSpeed = NEKO_ATTACK_SPEED.get();
            NekoMovementSpeed = NEKO_MOVEMENT_SPEED.get();
            NekoArmor = NEKO_ARMOR.get();
            NekoArmorToughness = NEKO_ARMOR_TOUGHNESS.get();

            // VANILLA
            VanillaMaxLevel = VANILLA_MAX_LEVEL.get();
            VanillaMaxHealth = VANILLA_MAX_HEALTH.get();
            VanillaAttackDamage = VANILLA_ATTACK_DAMAGE.get();
            VanillaAttackSpeed = VANILLA_ATTACK_SPEED.get();
            VanillaMovementSpeed = VANILLA_MOVEMENT_SPEED.get();
            VanillaArmor = VANILLA_ARMOR.get();
            VanillaArmorToughness = VANILLA_ARMOR_TOUGHNESS.get();
        } // register ()

    } // Class Common

} // Class LovelyRobotConfig