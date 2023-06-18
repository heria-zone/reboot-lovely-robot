package net.msymbios.rlovelyr.entity.utils;

import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.HashMap;

public class ModMetrics {

    // -- Logging --
    public static boolean LevelUpLog = true;            // Show log when level up


    // -- Goal Attribute --
    public static float MeleeAttackMovement = 1.0F;                   // Movement speed when it is melee attacking
    public static float FollowOwnerMovement = 1.0F;                   // Movement speed when following player
    public static float WanderAroundMovement = 0.4F;                  // Movement speed while it is wandering around
    public static float FollowBehindDistance = 10.0F;
    public static float FollowCloseDistance= 2.0F;
    public static float LookAtRange = 8.0F;


    // - Level & Experience --
    public static int BaseExp = 50;                     // Basic experience required to level up
    public static int UpExpValue = 2;                   // Increase value for each level


    // -- Auto Healing --
    public static boolean AutoHeal = true;              // Enable automatic recovery
    public static int AutoHealInterval = 50;            // Automatic recovery interval


    // -- Combat --
    public static boolean LootingEnchantment = true;    // Enable looting enchantments
    public static int LootingRequiredLevel = 10;        // Levels required for looting enchantments
    public static int MaxLootingLevel = 3;              // Maximum level of looting enchantments


    // -- Protection --
    public static int FireProtectionLimit = 80;         // Fire Protection upper limit
    public static int FallProtectionLimit = 80;         // Fall Protection upper limit
    public static int BlastProtectionLimit = 80;        // Blast Protection upper limit
    public static int ProjectileProtectionLimit = 80;   // Projectile Protection upper limit


    // Proprieties --
    public static HashMap<String, Identifier> ANIMATIONS = new HashMap<>() {{
        put(RobotAnimation.Locomotion.getName(), new Identifier(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    public static HashMap<RobotVariant, HashMap<RobotTexture, Identifier>> ALL_TEXTURES = new HashMap<>(){{
        put(RobotVariant.Bunny, setTexture(RobotVariant.Bunny));
        put(RobotVariant.Bunny2, setTexture(RobotVariant.Bunny2));
        put(RobotVariant.Honey, setTexture(RobotVariant.Honey));
        put(RobotVariant.Vanilla, setTexture(RobotVariant.Vanilla));
    }};

    public static HashMap<RobotVariant, HashMap<RobotModel, Identifier>> ALL_MODELS = new HashMap<>(){{
        put(RobotVariant.Bunny, setModel(RobotVariant.Bunny));
        put(RobotVariant.Bunny2, setModel(RobotVariant.Bunny2));
        put(RobotVariant.Honey, setModel(RobotVariant.Honey));
        put(RobotVariant.Vanilla, setModel(RobotVariant.Vanilla));
    }};

    public static HashMap<RobotVariant, HashMap<RobotAttribute, RobotAttributeInstance>> ALL_ATTRIBUTES = new HashMap<>(){{
        put(RobotVariant.Bunny, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 5F));
        put(RobotVariant.Bunny2, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
        put(RobotVariant.Honey, setAttribute(200F, 30F, 6F, 1.2F, 0.4F, 5F));
        put(RobotVariant.Vanilla, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
    }};


    // -- Methods --
    public static Identifier getTexture(RobotVariant variant, RobotTexture texture) {
        return ALL_TEXTURES.get(variant).get(texture);
    } // getTexture ()

    public static int getTextureCount(RobotVariant variant) {
        return ALL_TEXTURES.get(variant).size();
    } // getTextureCount ()

    private static HashMap<RobotTexture, Identifier> setTexture(RobotVariant variant){
        var path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
            put(RobotTexture.WHITE,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_00.png")); // White
            put(RobotTexture.ORANGE,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_01.png")); // Orange
            put(RobotTexture.MAGENTA,       new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_02.png")); // Magenta
            put(RobotTexture.LIGHT_BLUE,    new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_03.png")); // Light Blue
            put(RobotTexture.YELLOW,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_04.png")); // Yellow
            put(RobotTexture.LIME,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_05.png")); // Lime
            put(RobotTexture.PINK,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_06.png")); // Pink
            put(RobotTexture.GRAY,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_07.png")); // Gray
            put(RobotTexture.LIGHT_GRAY,    new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_08.png")); // Light Gray
            put(RobotTexture.CYAN,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_09.png")); // Cyan
            put(RobotTexture.PURPLE,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_10.png")); // Purple
            put(RobotTexture.BLUE,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_11.png")); // Blue
            put(RobotTexture.BROWN,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_12.png")); // Brown
            put(RobotTexture.GREEN,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_13.png")); // Green
            put(RobotTexture.RED,           new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_14.png")); // Red
            put(RobotTexture.BLACK,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_15.png")); // Black
        }};
    } // setTexture ()


    public static Identifier getModel(RobotVariant variant, RobotModel model) {
        return ALL_MODELS.get(variant).get(model);
    } // getAttributeValue ()

    private static HashMap<RobotModel, Identifier> setModel(RobotVariant variant){
        var path = variant.getName();
        return new HashMap<>() {{
            put(RobotModel.Unarmed, new Identifier(LovelyRobotMod.MODID, "geo/" + path + ".geo.json"));
            put(RobotModel.Armed, new Identifier(LovelyRobotMod.MODID, "geo/" + path + ".attack.geo.json"));
        }};
    } // setTexture ()


    public static float getAttributeValue(RobotVariant variant, RobotAttribute attribute) {
        RobotAttributeInstance robotAttribute = ALL_ATTRIBUTES.get(variant).get(attribute);
        return robotAttribute == null ? 0F : robotAttribute.value;
    } // getAttributeValue ()

    private static HashMap<RobotAttribute, RobotAttributeInstance> setAttribute(float maxLevel, float maxHealth, float attackDamage, float attackSpeed, float movementSpeed, float defense){
        return new HashMap<>(){{
            put(RobotAttribute.MAX_LEVEL, new RobotAttributeInstance(RobotAttribute.MAX_LEVEL, maxLevel));                // Max Level
            put(RobotAttribute.MAX_HEALTH, new RobotAttributeInstance(RobotAttribute.MAX_HEALTH, maxHealth));             // Max Health
            put(RobotAttribute.ATTACK_DAMAGE, new RobotAttributeInstance(RobotAttribute.ATTACK_DAMAGE, attackDamage));    // Attack Damage
            put(RobotAttribute.ATTACK_SPEED, new RobotAttributeInstance(RobotAttribute.ATTACK_SPEED, attackSpeed));       // Attack Speed
            put(RobotAttribute.MOVEMENT_SPEED, new RobotAttributeInstance(RobotAttribute.MOVEMENT_SPEED, movementSpeed)); // Movement Speed
            put(RobotAttribute.DEFENSE, new RobotAttributeInstance(RobotAttribute.DEFENSE, defense));                     // Defense
            put(RobotAttribute.ARMOR, new RobotAttributeInstance(RobotAttribute.ARMOR, 0F));
            put(RobotAttribute.ARMOR_TOUGHNESS, new RobotAttributeInstance(RobotAttribute.ARMOR_TOUGHNESS, 0F));
            put(RobotAttribute.BASE_DEFENSE_RANGE, new RobotAttributeInstance(RobotAttribute.BASE_DEFENSE_RANGE, 15F));
            put(RobotAttribute.BASE_DEFENSE_WARP_RANGE, new RobotAttributeInstance(RobotAttribute.BASE_DEFENSE_WARP_RANGE, 10F));
        }};
    } // setAttribute ()

} // Class ModMetrics