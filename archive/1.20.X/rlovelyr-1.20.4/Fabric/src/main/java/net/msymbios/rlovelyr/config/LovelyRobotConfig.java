package net.msymbios.rlovelyr.config;

import net.msymbios.rlovelyr.LovelyRobot;

public class LovelyRobotConfig {

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

        public static float ShadowRadius = 0;

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

        // -- ENTITY --

        // BUNNY
        public static int BunnyMaxLevel = 200;
        public static float BunnyMaxHealth = 30F;
        public static float BunnyAttackDamage = 5F;
        public static float BunnyAttackSpeed = 1.2F;
        public static float BunnyMovementSpeed = 0.6F;
        public static float BunnyArmor = 5F;
        public static float BunnyArmorToughness = 0F;

        // BUNNY2
        public static int Bunny2MaxLevel = 200;
        public static float Bunny2MaxHealth = 30F;
        public static float Bunny2AttackDamage = 5F;
        public static float Bunny2AttackSpeed = 1.2F;
        public static float Bunny2MovementSpeed = 0.6F;
        public static float Bunny2Armor = 5F;
        public static float Bunny2ArmorToughness = 0F;

        // DRAGON
        public static int DragonMaxLevel = 200;
        public static float DragonMaxHealth = 30F;
        public static float DragonAttackDamage = 5F;
        public static float DragonAttackSpeed = 1.2F;
        public static float DragonMovementSpeed = 0.6F;
        public static float DragonArmor = 5F;
        public static float DragonArmorToughness = 0F;

        // HONEY
        public static int HoneyMaxLevel = 200;
        public static float HoneyMaxHealth = 30F;
        public static float HoneyAttackDamage = 5F;
        public static float HoneyAttackSpeed = 1.2F;
        public static float HoneyMovementSpeed = 0.6F;
        public static float HoneyArmor = 5F;
        public static float HoneyArmorToughness = 0F;

        // KITSUNE
        public static int KitsuneMaxLevel = 200;
        public static float KitsuneMaxHealth = 30F;
        public static float KitsuneAttackDamage = 5F;
        public static float KitsuneAttackSpeed = 1.2F;
        public static float KitsuneMovementSpeed = 0.6F;
        public static float KitsuneArmor = 5F;
        public static float KitsuneArmorToughness = 0F;

        // NEKO
        public static int NekoMaxLevel = 200;
        public static float NekoMaxHealth = 30F;
        public static float NekoAttackDamage = 5F;
        public static float NekoAttackSpeed = 1.2F;
        public static float NekoMovementSpeed = 0.6F;
        public static float NekoArmor = 5F;
        public static float NekoArmorToughness = 0F;

        // VANILLA
        public static int VanillaMaxLevel = 200;
        public static float VanillaMaxHealth = 30F;
        public static float VanillaAttackDamage = 5F;
        public static float VanillaAttackSpeed = 1.2F;
        public static float VanillaMovementSpeed = 0.6F;
        public static float VanillaArmor = 5F;
        public static float VanillaArmorToughness = 0F;

    } // Class Common

} // Class LovelyRobotConfig