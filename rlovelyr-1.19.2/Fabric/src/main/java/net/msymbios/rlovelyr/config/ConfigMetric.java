package net.msymbios.rlovelyr.config;

public class ConfigMetric {

    // -- Variables --

    // GENERAL
    public static float MOVEMENT_MELEE_ATTACK;      // Movement speed when it is melee attacking
    public static float MOVEMENT_FOLLOW_OWNER;      // Movement speed when following player
    public static float MOVEMENT_WANDER_AROUND;     // Movement speed while it is wandering around
    public static float FOLLOW_DISTANCE_MAX;        // Maximum distance allowed while following
    public static float FOLLOW_DISTANCE_MIN;        // Minimum distance allowed while following
    public static float LOOK_RANGE;                 // How much should the head rotate while looking

    // LEVEL & EXPERIENCE
    public static int EXPERIENCE_BASE;              // Basic experience required to level up
    public static int EXPERIENCE_MULTIPLIER;        // Increase value for each level

    // COMBAT
    public static int ATTACK_CHANCE;                // Probability of attacking when attacked
    public static int HEAL_INTERVAL;                // Automatic recovery interval
    public static int WARY_TIME;                    // Time while being in combat mode
    public static boolean LOOT_ENCHANTMENT;         // Enable looting enchantments
    public static int LOOT_ENCHANTMENT_LEVEL;       // Levels required for looting enchantments
    public static int MAX_LOOT_ENCHANTMENT;         // Maximum level of looting enchantments

    // PROTECTION
    public static int PROTECTION_LIMIT_FIRE;        // Fire protection upper limit
    public static int PROTECTION_LIMIT_FALL;        // Fall protection upper limit
    public static int PROTECTION_LIMIT_BLAST;       // Blast protection upper limit
    public static int PROTECTION_LIMIT_PROJECTILE;  // Projectile protection upper limit

    // ENTITY | BUNNY
    public static float BUNNY_MAX_LEVEL;
    public static float BUNNY_MAX_HEALTH;
    public static float BUNNY_ATTACK_DAMAGE;
    public static float BUNNY_ATTACK_SPEED;
    public static float BUNNY_DEFENCE;
    public static float BUNNY_MOVEMENT_SPEED;
    public static float BUNNY_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float BUNNY_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | BUNNY2
    public static float BUNNY2_MAX_LEVEL;
    public static float BUNNY2_MAX_HEALTH;
    public static float BUNNY2_ATTACK_DAMAGE;
    public static float BUNNY2_ATTACK_SPEED;
    public static float BUNNY2_DEFENCE;
    public static float BUNNY2_MOVEMENT_SPEED;
    public static float BUNNY2_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float BUNNY2_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | DRAGON
    public static float DRAGON_MAX_LEVEL;
    public static float DRAGON_MAX_HEALTH;
    public static float DRAGON_ATTACK_DAMAGE;
    public static float DRAGON_ATTACK_SPEED;
    public static float DRAGON_DEFENCE;
    public static float DRAGON_MOVEMENT_SPEED;
    public static float DRAGON_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float DRAGON_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | HONEY
    public static float HONEY_MAX_LEVEL;
    public static float HONEY_MAX_HEALTH;
    public static float HONEY_ATTACK_DAMAGE;
    public static float HONEY_ATTACK_SPEED;
    public static float HONEY_DEFENCE;
    public static float HONEY_MOVEMENT_SPEED;
    public static float HONEY_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float HONEY_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | KITSUNE
    public static float KITSUNE_MAX_LEVEL;
    public static float KITSUNE_MAX_HEALTH;
    public static float KITSUNE_ATTACK_DAMAGE;
    public static float KITSUNE_ATTACK_SPEED;
    public static float KITSUNE_DEFENCE;
    public static float KITSUNE_MOVEMENT_SPEED;
    public static float KITSUNE_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float KITSUNE_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | NEKO
    public static float NEKO_MAX_LEVEL;
    public static float NEKO_MAX_HEALTH;
    public static float NEKO_ATTACK_DAMAGE;
    public static float NEKO_ATTACK_SPEED;
    public static float NEKO_DEFENCE;
    public static float NEKO_MOVEMENT_SPEED;
    public static float NEKO_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float NEKO_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

    // ENTITY | VANILLA
    public static float VANILLA_MAX_LEVEL;
    public static float VANILLA_MAX_HEALTH;
    public static float VANILLA_ATTACK_DAMAGE;
    public static float VANILLA_ATTACK_SPEED;
    public static float VANILLA_DEFENCE;
    public static float VANILLA_MOVEMENT_SPEED;
    public static float VANILLA_BASE_DEFENCE_RANGE;         // Total area to be defended
    public static float VANILLA_BASE_DEFENCE_WARP_RANGE;    // Distance from base allowed until teleported

} // Class ConfigMetric