package net.msymbios.rlovelyr.entity.utils;

import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.resources.ResourceLocation;
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


    // -- Properties --
    public static HashMap<String, ResourceLocation> ANIMATIONS = new HashMap<>() {{
        put(RobotAnimation.Locomotion.getName(), new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    public static HashMap<RobotVariant, HashMap<RobotTexture, ResourceLocation>> ALL_TEXTURES = new HashMap<>(){{
        put(RobotVariant.Bunny, setTexture(RobotVariant.Bunny));
        put(RobotVariant.Bunny2, setTexture(RobotVariant.Bunny2));
        put(RobotVariant.Honey, setTexture(RobotVariant.Honey));
        put(RobotVariant.Vanilla, setTexture(RobotVariant.Vanilla));
    }};

    public static HashMap<RobotVariant, HashMap<RobotModel, ResourceLocation>> ALL_MODELS = new HashMap<>(){{
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
    public static ResourceLocation getTexture(RobotVariant variant, RobotTexture texture) {
        return ALL_TEXTURES.get(variant).get(texture);
    } // getTexture ()

    public static int getTextureCount(RobotVariant variant) {
        return ALL_TEXTURES.get(variant).size();
    } // getTextureCount ()

    private static HashMap<RobotTexture, ResourceLocation> setTexture(RobotVariant variant){
        var path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
            put(RobotTexture.WHITE,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_00.png")); // White
            put(RobotTexture.ORANGE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_01.png")); // Orange
            put(RobotTexture.MAGENTA,       new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_02.png")); // Magenta
            put(RobotTexture.LIGHT_BLUE,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_03.png")); // Light Blue
            put(RobotTexture.YELLOW,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_04.png")); // Yellow
            put(RobotTexture.LIME,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_05.png")); // Lime
            put(RobotTexture.PINK,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_06.png")); // Pink
            put(RobotTexture.GRAY,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_07.png")); // Gray
            put(RobotTexture.LIGHT_GRAY,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_08.png")); // Light Gray
            put(RobotTexture.CYAN,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_09.png")); // Cyan
            put(RobotTexture.PURPLE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_10.png")); // Purple
            put(RobotTexture.BLUE,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_11.png")); // Blue
            put(RobotTexture.BROWN,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_12.png")); // Brown
            put(RobotTexture.GREEN,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_13.png")); // Green
            put(RobotTexture.RED,           new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_14.png")); // Red
            put(RobotTexture.BLACK,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_15.png")); // Black
        }};
    } // setTexture ()


    public static ResourceLocation getModel(RobotVariant variant, RobotModel model) {
        return ALL_MODELS.get(variant).get(model);
    } // getAttributeValue ()

    private static HashMap<RobotModel, ResourceLocation> setModel(RobotVariant variant){
        var path = variant.getName();
        return new HashMap<>() {{
            put(RobotModel.Unarmed, new ResourceLocation(LovelyRobotMod.MODID, "geo/" + path + ".geo.json"));
            put(RobotModel.Armed, new ResourceLocation(LovelyRobotMod.MODID, "geo/" + path + ".attack.geo.json"));
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