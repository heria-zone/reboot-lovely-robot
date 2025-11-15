package net.msymbios.rlovelyr.config.internal;

import eu.midnightdust.lib.config.MidnightConfig;

/**
 * Config options shared by both the client and server.
 */
public class Common extends MidnightConfig {

    // -- Variables --

    // -- GENERAL --

    @Comment private static final String commentOwnerMaxRobotNum = "Maximum number of robots owned by an owner.";
    @Entry(category = "General", name = "Owner Max Robot Number")
    public final int ownerMaxRobotNum = 30;

    @Comment private static final String commentMovementMeleeAttack = "Movement speed when it is melee attacking.";
    @Entry(category = "General", name = "Movement Melee Attack")
    public final double movementMeleeAttack = 0.8D;

    @Comment private static final String commentMovementFollowOwner = "Movement speed when following player.";
    @Entry(category = "General", name = "Movement Follow Owner")
    public final double movementFollowOwner = 0.7D;

    @Comment private static final String commentMovementWanderAround = "Movement speed while it is wandering around.";
    @Entry(category = "General", name = "Movement Wander Around")
    public final double movementWanderAround = 0.6D;

    @Comment private static final String commentFollowDistanceMax = "Maximum distance allowed while following.";
    @Entry(category = "General", name = "Follow Distance Max")
    public final double followDistanceMax = 10D;

    @Comment private static final String commentFollowDistanceMin = "Minimum distance allowed while following.";
    @Entry(category = "General", name = "Follow Distance Min")
    public final double followDistanceMin = 0.2D;

    @Comment private static final String commentLookRange = "How much should the head rotate while looking.";
    @Entry(category = "General", name = "Look Range")
    public final double lookRange = 8D;

// -- RENDERER --

    @Comment private static final String commentWidth = "Entity hit-box width.";
    @Entry(category = "Renderer", name = "Width")
    public final double width = 0.4;

    @Comment private static final String commentHeight = "Entity hit-box height.";
    @Entry(category = "Renderer", name = "Height")
    public final double height = 1.9;

// -- LEVEL | EXPERIENCE --

    @Comment private static final String commentExperienceBase = "Basic experience required to level up.";
    @Entry(category = "Level & Experience", name = "Experience Base")
    public final int experienceBase = 50;

    @Comment private static final String commentExperienceMultiplier = "Increase level experience multiplier.";
    @Entry(category = "Level & Experience", name = "Experience Multiplier")
    public final int experienceMultiplier = 2;

// -- COMBAT --

    @Comment private static final String commentAttackChance = "Probability of attacking when attacked.";
    @Entry(category = "Combat", name = "Attack Chance")
    public final int attackChance = 5;

    @Comment private static final String commentHealInterval = "Automatic recovery interval.";
    @Entry(category = "Combat", name = "Heal Interval")
    public final int healInterval = 50;

    @Comment private static final String commentWaryTime = "Time while being in combat mode.";
    @Entry(category = "Combat", name = "Wary Time")
    public final int waryTime = 50;

    @Comment private static final String commentGlobalAutoHeal = "Enable/disable global robots healing.";
    @Entry(category = "Combat", name = "Global Auto Heal")
    public final boolean globalAutoHeal = true;

    @Comment private static final String commentLootEnchantment = "Enable looting enchantments.";
    @Entry(category = "Combat", name = "Loot Enchantment")
    public final boolean lootEnchantment = true;

    @Comment private static final String commentLootEnchantmentLevel = "Levels required for looting enchantments.";
    @Entry(category = "Combat", name = "Loot Enchantment Level")
    public final int lootEnchantmentLevel = 10;

    @Comment private static final String commentMaxLootEnchantment = "Maximum level of looting enchantments.";
    @Entry(category = "Combat", name = "Max Loot Enchantment")
    public final int maxLootEnchantment = 3;

    @Comment private static final String commentBaseDefenceRange = "Base range to defend.";
    @Entry(category = "Combat", name = "Base Defence Range")
    public final double baseDefenceRange = 15.0;

    @Comment private static final String commentBaseDefenceWarpRange = "Range till teleport back to base.";
    @Entry(category = "Combat", name = "Base Defence Warp Range")
    public final double baseDefenceWarpRange = 10.0;

// -- PROTECTION --

    @Comment private static final String commentProtectionLimitFire = "Fire protection upper limit.";
    @Entry(category = "Protection", name = "Protection Limit Fire")
    public final int protectionLimitFire = 80;

    @Comment private static final String commentProtectionLimitFall = "Fall protection upper limit.";
    @Entry(category = "Protection", name = "Protection Limit Fall")
    public final int protectionLimitFall = 80;

    @Comment private static final String commentProtectionLimitBlast = "Blast protection upper limit.";
    @Entry(category = "Protection", name = "Protection Limit Blast")
    public final int protectionLimitBlast = 80;

    @Comment private static final String commentProtectionLimitProjectile = "Projectile protection upper limit.";
    @Entry(category = "Protection", name = "Protection Limit Projectile")
    public final int protectionLimitProjectile = 80;

// -- ENTITY --

    // BUNNY
    @Comment private static final String commentBunnyAttributeMaxLevel = "Maximum Level";
    @Entry(category = "Entity - Bunny", name = "Max Level")
    public final int bunnyAttributeMaxLevel = 200;

    @Comment private static final String commentBunnyAttributeMaxHealth = "Maximum Health";
    @Entry(category = "Entity - Bunny", name = "Max Health")
    public final double bunnyAttributeMaxHealth = 30.0;

    @Comment private static final String commentBunnyAttributeAttackDamage = "Attack Damage";
    @Entry(category = "Entity - Bunny", name = "Attack Damage")
    public final double bunnyAttributeAttackDamage = 5.0;

    @Comment private static final String commentBunnyAttributeAttackSpeed = "Attack Speed";
    @Entry(category = "Entity - Bunny", name = "Attack Speed")
    public final double bunnyAttributeAttackSpeed = 1.2;

    @Comment private static final String commentBunnyAttributeMovementSpeed = "Movement Speed";
    @Entry(category = "Entity - Bunny", name = "Movement Speed")
    public final double bunnyAttributeMovementSpeed = 0.6;

    @Comment private static final String commentBunnyAttributeArmor = "Armor";
    @Entry(category = "Entity - Bunny", name = "Armor")
    public final double bunnyAttributeArmor = 5.0;

    @Comment private static final String commentBunnyAttributeArmorToughness = "Armor Toughness";
    @Entry(category = "Entity - Bunny", name = "Armor Toughness")
    public final double bunnyAttributeArmorToughness = 0.0;

    // BUNNY2
    @Comment private static final String commentBunny2AttributeMaxLevel = "Maximum Level";
    @Entry(category = "Entity - Bunny2", name = "Max Level")
    public final int bunny2AttributeMaxLevel = 200;

    @Comment private static final String commentBunny2AttributeMaxHealth = "Maximum Health";
    @Entry(category = "Entity - Bunny2", name = "Max Health")
    public final double bunny2AttributeMaxHealth = 30.0;

    @Comment private static final String commentBunny2AttributeAttackDamage = "Attack Damage";
    @Entry(category = "Entity - Bunny2", name = "Attack Damage")
    public final double bunny2AttributeAttackDamage = 5.0;

    @Comment private static final String commentBunny2AttributeAttackSpeed = "Attack Speed";
    @Entry(category = "Entity - Bunny2", name = "Attack Speed")
    public final double bunny2AttributeAttackSpeed = 1.2;

    @Comment private static final String commentBunny2AttributeMovementSpeed = "Movement Speed";
    @Entry(category = "Entity - Bunny2", name = "Movement Speed")
    public final double bunny2AttributeMovementSpeed = 0.6;

    @Comment private static final String commentBunny2AttributeArmor = "Armor";
    @Entry(category = "Entity - Bunny2", name = "Armor")
    public final double bunny2AttributeArmor = 5.0;

    @Comment private static final String commentBunny2AttributeArmorToughness = "Armor Toughness";
    @Entry(category = "Entity - Bunny2", name = "Armor Toughness")
    public final double bunny2AttributeArmorToughness = 0.0;

    // DRAGON
    @Comment private static final String commentDragonAttributeMaxLevel = "Maximum Level";
    @Entry(category = "Entity - Dragon", name = "Max Level")
    public final int dragonAttributeMaxLevel = 200;

    @Comment private static final String commentDragonAttributeMaxHealth = "Maximum Health";
    @Entry(category = "Entity - Dragon", name = "Max Health")
    public final double dragonAttributeMaxHealth = 30.0;

    @Comment private static final String commentDragonAttributeAttackDamage = "Attack Damage";
    @Entry(category = "Entity - Dragon", name = "Attack Damage")
    public final double dragonAttributeAttackDamage = 5.0;

    @Comment private static final String commentDragonAttributeAttackSpeed = "Attack Speed";
    @Entry(category = "Entity - Dragon", name = "Attack Speed")
    public final double dragonAttributeAttackSpeed = 1.2;

    @Comment private static final String commentDragonAttributeMovementSpeed = "Movement Speed";
    @Entry(category = "Entity - Dragon", name = "Movement Speed")
    public final double dragonAttributeMovementSpeed = 0.4;

    @Comment private static final String commentDragonAttributeArmor = "Armor";
    @Entry(category = "Entity - Dragon", name = "Armor")
    public final double dragonAttributeArmor = 5.0;

    @Comment private static final String commentDragonAttributeArmorToughness = "Armor Toughness";
    @Entry(category = "Entity - Dragon", name = "Armor Toughness")
    public final double dragonAttributeArmorToughness = 0.0;

    // HONEY
    @Comment private static final String commentHoneyAttributeMaxLevel = "Maximum Level";
    @Entry(category = "Entity - Honey", name = "Max Level")
    public final int honeyAttributeMaxLevel = 200;

    @Comment private static final String commentHoneyAttributeMaxHealth = "Maximum Health";
    @Entry(category = "Entity - Honey", name = "Max Health")
    public final double honeyAttributeMaxHealth = 30.0;

    @Comment private static final String commentHoneyAttributeAttackDamage = "Attack Damage";
    @Entry(category = "Entity - Honey", name = "Attack Damage")
    public final double honeyAttributeAttackDamage = 5.0;

    @Comment private static final String commentHoneyAttributeAttackSpeed = "Attack Speed";
    @Entry(category = "Entity - Honey", name = "Attack Speed")
    public final double honeyAttributeAttackSpeed = 1.2;

    @Comment private static final String commentHoneyAttributeMovementSpeed = "Movement Speed";
    @Entry(category = "Entity - Honey", name = "Movement Speed")
    public final double honeyAttributeMovementSpeed = 0.4;

    @Comment private static final String commentHoneyAttributeArmor = "Armor";
    @Entry(category = "Entity - Honey", name = "Armor")
    public final double honeyAttributeArmor = 5.0;

    @Comment private static final String commentHoneyAttributeArmorToughness = "Armor Toughness";
    @Entry(category = "Entity - Honey", name = "Armor Toughness")
    public final double honeyAttributeArmorToughness = 0.0;

    // KITSUNE
    @Comment private static final String commentKitsuneAttributeMaxLevel = "Maximum Level";
    @Entry(category = "Entity - Kitsune", name = "Max Level")
    public final int kitsuneAttributeMaxLevel = 200;

    @Comment private static final String commentKitsuneAttributeMaxHealth = "Maximum Health";
    @Entry(category = "Entity - Kitsune", name = "Max Health")
    public final double kitsuneAttributeMaxHealth = 30.0;

    @Comment private static final String commentKitsuneAttributeAttackDamage = "Attack Damage";
    @Entry(category = "Entity - Kitsune", name = "Attack Damage")
    public final double kitsuneAttributeAttackDamage = 5.0;

    @Comment private static final String commentKitsuneAttributeAttackSpeed = "Attack Speed";
    @Entry(category = "Entity - Kitsune", name = "Attack Speed")
    public final double kitsuneAttributeAttackSpeed = 1.2;

    @Comment private static final String commentKitsuneAttributeMovementSpeed = "Movement Speed";
    @Entry(category = "Entity - Kitsune", name = "Movement Speed")
    public final double kitsuneAttributeMovementSpeed = 0.6;

    @Comment private static final String commentKitsuneAttributeArmor = "Armor";
    @Entry(category = "Entity - Kitsune", name = "Armor")
    public final double kitsuneAttributeArmor = 5.0;

    @Comment private static final String commentKitsuneAttributeArmorToughness = "Armor Toughness";
    @Entry(category = "Entity - Kitsune", name = "Armor Toughness")
    public final double kitsuneAttributeArmorToughness = 0.0;

    // -- NEKO --

    @Comment private static final String commentNekoAttributeMaxLevel = "Maximum Level for Neko.";
    @Entry(category = "Neko", name = "Max Level")
    public final int nekoAttributeMaxLevel = 200;

    @Comment private static final String commentNekoAttributeMaxHealth = "Maximum Health for Neko.";
    @Entry(category = "Neko", name = "Max Health")
    public final double nekoAttributeMaxHealth = 30.0;

    @Comment private static final String commentNekoAttributeAttackDamage = "Attack Damage for Neko.";
    @Entry(category = "Neko", name = "Attack Damage")
    public final double nekoAttributeAttackDamage = 5.0;

    @Comment private static final String commentNekoAttributeAttackSpeed = "Attack Speed for Neko.";
    @Entry(category = "Neko", name = "Attack Speed")
    public final double nekoAttributeAttackSpeed = 1.2;

    @Comment private static final String commentNekoAttributeMovementSpeed = "Movement Speed for Neko.";
    @Entry(category = "Neko", name = "Movement Speed")
    public final double nekoAttributeMovementSpeed = 0.4;

    @Comment private static final String commentNekoAttributeArmor = "Armor for Neko.";
    @Entry(category = "Neko", name = "Armor")
    public final double nekoAttributeArmor = 5.0;

    @Comment private static final String commentNekoAttributeArmorToughness = "Armor Toughness for Neko.";
    @Entry(category = "Neko", name = "Armor Toughness")
    public final double nekoAttributeArmorToughness = 0.0;

    // -- VANILLA --

    @Comment private static final String commentVanillaAttributeMaxLevel = "Maximum Level for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Max Level")
    public final int vanillaAttributeMaxLevel = 200;

    @Comment private static final String commentVanillaAttributeMaxHealth = "Maximum Health for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Max Health")
    public final double vanillaAttributeMaxHealth = 30.0;

    @Comment private static final String commentVanillaAttributeAttackDamage = "Attack Damage for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Attack Damage")
    public final double vanillaAttributeAttackDamage = 5.0;

    @Comment private static final String commentVanillaAttributeAttackSpeed = "Attack Speed for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Attack Speed")
    public final double vanillaAttributeAttackSpeed = 1.2;

    @Comment private static final String commentVanillaAttributeMovementSpeed = "Movement Speed for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Movement Speed")
    public final double vanillaAttributeMovementSpeed = 0.6;

    @Comment private static final String commentVanillaAttributeArmor = "Armor for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Armor")
    public final double vanillaAttributeArmor = 5.0;

    @Comment private static final String commentVanillaAttributeArmorToughness = "Armor Toughness for Vanilla.";
    @Entry(category = "Entity/Vanilla", name = "Armor Toughness")
    public final double vanillaAttributeArmorToughness = 0.0;

} // Class Common