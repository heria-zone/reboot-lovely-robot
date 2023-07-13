package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.ResourceLocation;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.HashMap;
import java.util.function.Predicate;

public class InternalMetric {

    // -- Logging --
    public static boolean LevelUpLog = true;            // Show log when level up

    // -- Goal Attribute --
    public static float MeleeAttackMovement = 1.0F;                   // Movement speed when it is melee attacking
    public static float FollowOwnerMovement = 1.0F;                   // Movement speed when following player
    public static float WanderAroundMovement = 0.6F;                  // Movement speed while it is wandering around
    public static float FollowBehindDistance = 10.0F;
    public static float FollowCloseDistance= 2.0F;
    public static float LookAtRange = 8.0F;
    public static Predicate<LivingEntity> AvoidAttackingEntities = entity -> entity instanceof MonsterEntity && !(entity instanceof CreeperEntity); //&& !(entity instanceof Bunny2Entity) && !(entity instanceof BunnyEntity) && !(entity instanceof HoneyEntity);

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

    // -- Base --
    public static float BaseDefenseRange = 15;
    public static float BaseDefenseWarpRange = 10;

    // -- Properties --
    public static HashMap<EntityAnimation, ResourceLocation> ANIMATIONS = new HashMap() {{
        put(EntityAnimation.Locomotion, new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    public static HashMap<EntityVariant, HashMap<EntityTexture, ResourceLocation>> ALL_TEXTURES = new HashMap(){{
        put(EntityVariant.Bunny, setTexture(EntityVariant.Bunny));
        put(EntityVariant.Bunny2, setTexture(EntityVariant.Bunny2));
        put(EntityVariant.Honey, setTexture(EntityVariant.Honey));
        put(EntityVariant.Vanilla, setTexture(EntityVariant.Vanilla));
    }};

    public static HashMap<EntityVariant, HashMap<EntityModel, ResourceLocation>> ALL_MODELS = new HashMap(){{
        put(EntityVariant.Bunny, setModel(EntityVariant.Bunny));
        put(EntityVariant.Bunny2, setModel(EntityVariant.Bunny2));
        put(EntityVariant.Honey, setModel(EntityVariant.Honey));
        put(EntityVariant.Vanilla, setModel(EntityVariant.Vanilla));
    }};

    public static HashMap<EntityVariant, HashMap<EntityAttribute, InternalAttribute>> ALL_ATTRIBUTES = new HashMap(){{
        put(EntityVariant.Bunny, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Bunny2, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
        put(EntityVariant.Honey, setAttribute(200F, 30F, 6F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Vanilla, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
    }};

    // -- Methods --
    public static ResourceLocation getTexture(EntityVariant variant, EntityTexture texture) {
        return ALL_TEXTURES.get(variant).get(texture);
    } // getTexture ()

    public static int getTextureCount(EntityVariant variant) {
        return ALL_TEXTURES.get(variant).size();
    } // getTextureCount ()

    private static HashMap<EntityTexture, ResourceLocation> setTexture(EntityVariant variant){
        String path = variant.getName() + "/" + variant.getName();
        return new HashMap() {{
            put(EntityTexture.WHITE,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_00.png")); // White
            put(EntityTexture.ORANGE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_01.png")); // Orange
            put(EntityTexture.MAGENTA,       new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_02.png")); // Magenta
            put(EntityTexture.LIGHT_BLUE,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_03.png")); // Light Blue
            put(EntityTexture.YELLOW,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_04.png")); // Yellow
            put(EntityTexture.LIME,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_05.png")); // Lime
            put(EntityTexture.PINK,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_06.png")); // Pink
            put(EntityTexture.GRAY,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_07.png")); // Gray
            put(EntityTexture.LIGHT_GRAY,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_08.png")); // Light Gray
            put(EntityTexture.CYAN,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_09.png")); // Cyan
            put(EntityTexture.PURPLE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_10.png")); // Purple
            put(EntityTexture.BLUE,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_11.png")); // Blue
            put(EntityTexture.BROWN,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_12.png")); // Brown
            put(EntityTexture.GREEN,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_13.png")); // Green
            put(EntityTexture.RED,           new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_14.png")); // Red
            put(EntityTexture.BLACK,         new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/" + path + "_15.png")); // Black
        }};
    } // setTexture ()


    public static ResourceLocation getModel(EntityVariant variant, EntityModel model) {
        return ALL_MODELS.get(variant).get(model);
    } // getAttributeValue ()

    private static HashMap<EntityModel, ResourceLocation> setModel(EntityVariant variant){
        String path = variant.getName();
        return new HashMap() {{
            put(EntityModel.Unarmed, new ResourceLocation(LovelyRobotMod.MODID, "geo/" + path + ".geo.json"));
            put(EntityModel.Armed, new ResourceLocation(LovelyRobotMod.MODID, "geo/" + path + ".attack.geo.json"));
        }};
    } // setTexture ()


    public static float getAttributeValue(EntityVariant variant, EntityAttribute attribute) {
        InternalAttribute robotAttribute = ALL_ATTRIBUTES.get(variant).get(attribute);
        return robotAttribute == null ? 0F : robotAttribute.value;
    } // getAttributeValue ()

    private static HashMap<EntityAttribute, InternalAttribute> setAttribute(float maxLevel, float maxHealth, float attackDamage, float attackSpeed, float movementSpeed, float defense){
        return new HashMap(){{
            put(EntityAttribute.MAX_LEVEL, new InternalAttribute(EntityAttribute.MAX_LEVEL, maxLevel));                // Max Level
            put(EntityAttribute.MAX_HEALTH, new InternalAttribute(EntityAttribute.MAX_HEALTH, maxHealth));             // Max Health
            put(EntityAttribute.ATTACK_DAMAGE, new InternalAttribute(EntityAttribute.ATTACK_DAMAGE, attackDamage));    // Attack Damage
            put(EntityAttribute.ATTACK_SPEED, new InternalAttribute(EntityAttribute.ATTACK_SPEED, attackSpeed));       // Attack Speed
            put(EntityAttribute.MOVEMENT_SPEED, new InternalAttribute(EntityAttribute.MOVEMENT_SPEED, movementSpeed)); // Movement Speed
            put(EntityAttribute.DEFENSE, new InternalAttribute(EntityAttribute.DEFENSE, defense));                     // Defense
            put(EntityAttribute.ARMOR, new InternalAttribute(EntityAttribute.ARMOR, 0F));
            put(EntityAttribute.ARMOR_TOUGHNESS, new InternalAttribute(EntityAttribute.ARMOR_TOUGHNESS, 0F));
            put(EntityAttribute.BASE_DEFENSE_RANGE, new InternalAttribute(EntityAttribute.BASE_DEFENSE_RANGE, 15F));
            put(EntityAttribute.BASE_DEFENSE_WARP_RANGE, new InternalAttribute(EntityAttribute.BASE_DEFENSE_WARP_RANGE, 10F));
        }};
    } // setAttribute ()

} // Class ModMetrics