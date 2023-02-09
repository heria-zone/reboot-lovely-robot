package net.msymbios.rlovelyr.entity.utils;

public class ModMetrics {

    // -- Logging --
    public static boolean LevelUpLog = true;            // Show log when level up


    // - Level & Experience --
    public static int MaxLevel = 200;                   // Maximum level
    public static int BaseExp = 50;                     // Basic experience required to level up
    public static int UpExpValue = 2;                   // Increase value for each level


    // -- Auto Healing --
    public static boolean AutoHeal = true;              // Enable automatic recovery
    public static int AutoHealInterval = 50;            // Automatic recovery interval


    // -- Combat --
    public static float AttackMoveSpeed = 1.2f;         // Movement speed when attacking
    public static boolean LootingEnchantment = true;    // Enable looting enchantments
    public static int LootingRequiredLevel = 10;        // Levels required for looting enchantments
    public static int MaxLootingLevel = 3;              // Maximum level of looting enchantments


    // -- Protection --
    public static int FireProtectionLimit = 80;         // Fire Protection upper limit
    public static int FallProtectionLimit = 80;         // Fall Protection upper limit
    public static int BlastProtectionLimit = 80;        // Blast Protection upper limit
    public static int ProjectileProtectionLimit = 80;   // Projectile Protection upper limit


    // -- Defense --
    public static float BaseDefenseRange = 15f;         // Base defense range
    public static float BaseDefenseWarpRange = 10f;     // Base defense warp range


    // -- Entity Stats --
    public static double BunnyBaseHp = 30.0D;           // Bunny's basic Hp
    public static float BunnyBaseAttack = 5f;           // Bunny's basic Attack
    public static float BunnyBaseDefense = 5f;          // Bunny's basic Defense
    public static float BunnyMovementSpeed = 0.4f;      // Bunny's Movement Speed

    public static double Bunny2BaseHp = 30.0D;          // Bunny2's basic Hp
    public static float Bunny2BaseAttack = 5;           // Bunny2's basic Attack
    public static float Bunny2BaseDefense = 6;          // Bunny2's basic Defense
    public static float Bunny2MovementSpeed = 0.4f;     // Bunny's Movement Speed

    public static double HoneyBaseHp = 30.0D;           // Honey's basic Hp
    public static float HoneyBaseAttack = 6;            // Honey's basic Attack
    public static float HoneyBaseDefense = 5;           // Honey's basic Defense
    public static float HoneyMovementSpeed = 0.4f;      // Bunny's Movement Speed

    public static double VanillaBaseHp = 30.0D;         // Vanilla's basic Hp
    public static float VanillaBaseAttack = 5;          // Vanilla's basic Attack
    public static float VanillaBaseDefense = 6;         // Vanilla's basic Defense
    public static float VanillaMovementSpeed = 0.4f;    // Bunny's Movement Speed


} // Class ModMetrics