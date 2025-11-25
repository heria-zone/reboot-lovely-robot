package net.msymbios.llovelyr.source.configs;

public class LovelyConfigs {

    // -- Variables --


    // -- Methods --

    public static void register () {

    } // register ()

    // -- Classes --

    /**
     * Config options only available to each client.
     */
    public static class Client {

        // -- Variables --

        // -- RENDERER --

        public static float ShadowRadius = 0.4F;

    } // Class Client

    /**
     * Config options shared by both the client and server.
     */
    public static class Common {

        // -- Variables --

        // -- GENERAL --
        public static int OwnerMaxRobotNum = 30;
        public static double MovementMeleeAttack = 0.8F;
        public static float MovementFollowOwner = 0.7F;
        public static double MovementWanderAround = 0.6F;
        public static float FollowDistanceMax = 10F;
        public static float FollowDistanceMin = 0.1F;
        public static float LookRange = 8F;

        // -- RENDERER --
        public static float Width = 0.4F;
        public static float Height = 1.9F;

        // -- LEVEL | EXPERIENCE ---
        public static int ExperienceBase = 50;
        public static int ExperienceMultiplier = 2;

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

        // -- PROTECTION --
        public static int ProtectionLimitFire = 80;
        public static int ProtectionLimitFall = 80;
        public static int ProtectionLimitBlast = 80;
        public static int ProtectionLimitProjectile = 80;

        // -- SMART CORE RETRIEVAL --
        public static boolean EnableSmartCoreRetrieval = true;
        public static double SmartCoreRetrievalDistance = 16.0;

        // -- ENTITY --

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2MaxHealth = 24F;
        public static float Bunny2AttackDamage = 4F;
        public static float Bunny2AttackSpeed = 1.8F;
        public static float Bunny2MovementSpeed = 0.37F;
        public static float Bunny2Armor = 6F;
        public static float Bunny2ArmorToughness = 1F;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaMaxHealth = 16F;
        public static float VanillaAttackDamage = 2F;
        public static float VanillaAttackSpeed = 1.0F;
        public static float VanillaMovementSpeed = 0.37F; // 0.25F is too slow
        public static float VanillaArmor = 2F;
        public static float VanillaArmorToughness = 0F;

    } // Class Common

} // Class: LovelyConfigs