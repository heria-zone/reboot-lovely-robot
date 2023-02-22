package net.msymbios.rlovelyr.entity.utils;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.HashMap;

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
    public static HashMap<RobotVariant, HashMap<RobotStat, RobotAttribute>> ALL_ATTRIBUTES = new HashMap<>(){{
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
            put(RobotTexture.ORANGE,     new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_01.png")); // Orange
            put(RobotTexture.MAGENTA,    new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_02.png")); // Magenta
            put(RobotTexture.YELLOW,     new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_04.png")); // Yellow
            put(RobotTexture.LIME,       new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_05.png")); // Lime
            put(RobotTexture.PINK,       new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_06.png")); // Pink
            put(RobotTexture.LIGHT_BLUE, new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_08.png")); // Light Blue
            put(RobotTexture.PURPLE,     new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_10.png")); // Purple
            put(RobotTexture.BLUE,       new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_11.png")); // Blue
            put(RobotTexture.RED,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_14.png")); // Red
            put(RobotTexture.BLACK,      new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_15.png")); // Black
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


    public static float getAttributeValue(RobotVariant variant, RobotStat attribute) {
        RobotAttribute robotAttribute = ALL_ATTRIBUTES.get(variant).get(attribute);
        return robotAttribute == null ? 0F : robotAttribute.value;
    } // getAttributeValue ()

    private static HashMap<RobotStat, RobotAttribute> setAttribute(float maxLevel, float maxHealth, float attackDamage, float attackSpeed, float movementSpeed, float defense){
        return new HashMap<>(){{
            put(RobotStat.MAX_LEVEL, new RobotAttribute(RobotStat.MAX_LEVEL, maxLevel));                // Max Level
            put(RobotStat.MAX_HEALTH, new RobotAttribute(RobotStat.MAX_HEALTH, maxHealth));             // Max Health
            put(RobotStat.ATTACK_DAMAGE, new RobotAttribute(RobotStat.ATTACK_DAMAGE, attackDamage));    // Attack Damage
            put(RobotStat.ATTACK_SPEED, new RobotAttribute(RobotStat.ATTACK_SPEED, attackSpeed));       // Attack Speed
            put(RobotStat.MOVEMENT_SPEED, new RobotAttribute(RobotStat.MOVEMENT_SPEED, movementSpeed)); // Movement Speed
            put(RobotStat.DEFENSE, new RobotAttribute(RobotStat.DEFENSE, defense));                     // Defense
            put(RobotStat.ARMOR, new RobotAttribute(RobotStat.ARMOR, 0F));
            put(RobotStat.ARMOR_TOUGHNESS, new RobotAttribute(RobotStat.ARMOR_TOUGHNESS, 0F));
        }};
    } // setAttribute ()

} // Class ModMetrics