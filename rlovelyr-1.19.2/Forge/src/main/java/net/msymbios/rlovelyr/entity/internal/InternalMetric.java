package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.resources.ResourceLocation;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InternalMetric {

    // -- Entity Renderer --
    public static float ShadowRadius = 0.4F;
    public static float Width = 0.4F;
    public static float Height = 2F;

    // -- Goal Attribute --
    public static float MeleeAttackMovement = 1.0F;                   // Movement speed when it is melee attacking
    public static float FollowOwnerMovement = 1.0F;                   // Movement speed when following player
    public static float WanderAroundMovement = 0.6F;                  // Movement speed while it is wandering around
    public static float FollowBehindDistance = 10.0F;
    public static float FollowCloseDistance= 2.0F;
    public static float LookAtRange = 8.0F;
    public static int AttackChance = 5;
    public static Predicate<LivingEntity> AvoidAttackingEntities = entity -> entity instanceof Enemy && !(entity instanceof Creeper); //&& !(entity instanceof Bunny2Entity) && !(entity instanceof BunnyEntity) && !(entity instanceof HoneyEntity);

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
    public static int WaryTime = 50;            // Time while being in combat mode

    // -- Protection --
    public static int FireProtectionLimit = 80;         // Fire Protection upper limit
    public static int FallProtectionLimit = 80;         // Fall Protection upper limit
    public static int BlastProtectionLimit = 80;        // Blast Protection upper limit
    public static int ProjectileProtectionLimit = 80;   // Projectile Protection upper limit

    // -- Base --
    public static float BaseDefenseRange = 15;
    public static float BaseDefenseWarpRange = 10;

    // -- Properties --
    public static HashMap<EntityVariant, EntityAnimator> ENTITY_ANIMATOR = new HashMap<>(){{
        put(EntityVariant.Bunny, EntityAnimator.Default);
        put(EntityVariant.Bunny2, EntityAnimator.Default);
        put(EntityVariant.Dragon, EntityAnimator.Default);
        put(EntityVariant.Honey, EntityAnimator.Default);
        put(EntityVariant.Kitsune, EntityAnimator.Default);
        put(EntityVariant.Neko, EntityAnimator.Default);
        put(EntityVariant.Vanilla, EntityAnimator.Default);
    }};

    public static HashMap<EntityVariant, HashMap<EntityModel, ResourceLocation>> ENTITY_MODEL = new HashMap<>(){{
        put(EntityVariant.Bunny, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny.attack.geo.json"));
        }});

        put(EntityVariant.Bunny2, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/bunny2.attack.geo.json"));
        }});

        put(EntityVariant.Dragon, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/dragon.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/dragon.attack.geo.json"));
        }});

        put(EntityVariant.Honey, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/honey.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/honey.attack.geo.json"));
        }});

        put(EntityVariant.Kitsune, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/kitsune.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/kitsune.attack.geo.json"));
        }});

        put(EntityVariant.Neko, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/neko.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/neko.attack.geo.json"));
        }});

        put(EntityVariant.Vanilla, new HashMap<>() {{
            put(EntityModel.Default,    new ResourceLocation(LovelyRobotMod.MODID, "geo/vanilla.geo.json"));
            put(EntityModel.Armed,      new ResourceLocation(LovelyRobotMod.MODID, "geo/vanilla.attack.geo.json"));
        }});
    }};

    public static HashMap<EntityTexture, List<EntityVariant>> ENTITY_TEXTURE = new HashMap<>(){{
        List<EntityVariant> list = new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
        }};
        put(EntityTexture.BLUE, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Dragon);
            add(EntityVariant.Neko);
        }});
        put(EntityTexture.BLACK, list);
        put(EntityTexture.CYAN, list);
        put(EntityTexture.BROWN, list);

        put(EntityTexture.LIME, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Dragon);
        }});

        put(EntityTexture.GRAY, list);
        put(EntityTexture.GREEN, list);

        put(EntityTexture.LIGHT_BLUE, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Dragon);
            add(EntityVariant.Kitsune);
            add(EntityVariant.Neko);
        }});

        put(EntityTexture.LIGHT_GRAY, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Kitsune);
        }});

        put(EntityTexture.MAGENTA, list);

        put(EntityTexture.ORANGE, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Kitsune);
            add(EntityVariant.Neko);
        }});

        put(EntityTexture.PINK, list);

        put(EntityTexture.PURPLE, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Neko);
        }});

        put(EntityTexture.RED, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Dragon);
        }});

        put(EntityTexture.WHITE, list);

        put(EntityTexture.YELLOW, new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Honey);
            add(EntityVariant.Vanilla);
            add(EntityVariant.Dragon);
            add(EntityVariant.Kitsune);
        }});
    }};

    public static HashMap<EntityAnimator, ResourceLocation> ANIMATOR = new HashMap<>() {{
        put(EntityAnimator.Default,    new ResourceLocation(LovelyRobotMod.MODID, "animations/lovelyrobot.animation.json"));
    }};

    public static HashMap<EntityVariant, HashMap<EntityTexture, ResourceLocation>> TEXTURE = new HashMap<>(){{
        put(EntityVariant.Bunny,    setTexture(EntityVariant.Bunny));
        put(EntityVariant.Bunny2,    setTexture(EntityVariant.Bunny2));
        put(EntityVariant.Honey,    setTexture(EntityVariant.Honey));
        put(EntityVariant.Vanilla,    setTexture(EntityVariant.Vanilla));

        put(EntityVariant.Dragon,    new HashMap<>() {{
            put(EntityTexture.LIGHT_BLUE,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/dragon/dragon_03.png")); // Light Blue
            put(EntityTexture.YELLOW,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/dragon/dragon_04.png")); // Yellow
            put(EntityTexture.LIME,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/dragon/dragon_05.png")); // Lime
            put(EntityTexture.BLUE,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/dragon/dragon_11.png")); // Blue
            put(EntityTexture.RED,           new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/dragon/dragon_14.png")); // Red
        }});

        put(EntityVariant.Kitsune,    new HashMap<>() {{
            put(EntityTexture.ORANGE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/kitsune/kitsune_01.png")); // Orange
            put(EntityTexture.LIGHT_BLUE,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/kitsune/kitsune_03.png")); // Light Blue
            put(EntityTexture.YELLOW,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/kitsune/kitsune_04.png")); // Yellow
            put(EntityTexture.LIGHT_GRAY,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/kitsune/kitsune_08.png")); // Light Gray
        }});

        put(EntityVariant.Neko,    new HashMap<>() {{
            put(EntityTexture.ORANGE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/neko/neko_01.png")); // Orange
            put(EntityTexture.LIGHT_BLUE,    new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/neko/neko_03.png")); // Light Blue
            put(EntityTexture.PURPLE,        new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/neko/neko_10.png")); // Purple
            put(EntityTexture.BLUE,          new ResourceLocation(LovelyRobotMod.MODID, "textures/entity/neko/neko_11.png")); // Blue
        }});
    }};

    public static HashMap<EntityVariant, HashMap<EntityAttribute, InternalAttribute>> ATTRIBUTES = new HashMap<>(){{
        put(EntityVariant.Bunny, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Bunny2, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
        put(EntityVariant.Dragon, setAttribute(200F, 30F, 7F, 1.2F, 0.4F, 7F));
        put(EntityVariant.Honey, setAttribute(200F, 30F, 6F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Kitsune, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Neko, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 5F));
        put(EntityVariant.Vanilla, setAttribute(200F, 30F, 5F, 1.2F, 0.4F, 6F));
    }};

    // -- Methods --

    // ANIMATOR
    public static ResourceLocation getAnimator(EntityVariant variant) {
        EntityAnimator selectedAnimator = ENTITY_ANIMATOR.get(variant);
        if (selectedAnimator != null && ANIMATOR.containsKey(selectedAnimator)) return ANIMATOR.get(selectedAnimator);
        return null; // Animator not found
    } // getAnimator ()

    public static ResourceLocation getAnimator(EntityVariant variant, EntityAnimator animator) {
        EntityAnimator selectedAnimator = ENTITY_ANIMATOR.get(variant);
        if (selectedAnimator != null && selectedAnimator == animator && ANIMATOR.containsKey(animator)) return ANIMATOR.get(animator);
        return null; // Animator not found for the specified parameters
    } // getAnimator ()

    // MODEL
    public static ResourceLocation getModel(EntityVariant variant) {
        if (ENTITY_MODEL.containsKey(variant)) {
            EntityModel defaultModel = EntityModel.Default; // Use the default model key, or adjust as needed
            if (ENTITY_MODEL.get(variant).containsKey(defaultModel)) return ENTITY_MODEL.get(variant).get(defaultModel);
        }
        return null; // Return a default or error identifier if the combination is not found
    } // getModel ()

    public static ResourceLocation getModel(EntityVariant variant, EntityModel model) {
        if (ENTITY_MODEL.containsKey(variant) && ENTITY_MODEL.get(variant).containsKey(model)) return ENTITY_MODEL.get(variant).get(model);
        return null;  // Return a default or error identifier if the combination is not found
    } // getModel ()

    // TEXTURE
    public static ResourceLocation getTexture(EntityVariant variant) {
        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID(variant));
        if (ENTITY_TEXTURE.containsKey(randomTexture) && ENTITY_TEXTURE.get(randomTexture).contains(variant)) {
            if (TEXTURE.containsKey(variant) && TEXTURE.get(variant).containsKey(randomTexture)) return TEXTURE.get(variant).get(randomTexture);
        }

        return null; // Return a default or error identifier if the combination is not found
    } // getTexture ()

    public static ResourceLocation getTexture(EntityVariant variant, EntityTexture texture) {
        if (ENTITY_TEXTURE.containsKey(texture) && ENTITY_TEXTURE.get(texture).contains(variant)) {
            if (TEXTURE.containsKey(variant) && TEXTURE.get(variant).containsKey(texture)) return TEXTURE.get(variant).get(texture);
        }

        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID(variant));
        // If the texture doesn't exist for the specified variant, return the default texture
        if (TEXTURE.containsKey(variant) && TEXTURE.get(variant).containsKey(randomTexture))
            return TEXTURE.get(variant).get(randomTexture);

        return null; // Return a default or error identifier if the combination is not found
    } // getTexture ()

    public static boolean checkTextureID(EntityVariant variant, EntityTexture texture) {
        if (ENTITY_TEXTURE.containsKey(texture) && ENTITY_TEXTURE.get(texture).contains(variant))
            return TEXTURE.containsKey(variant) && TEXTURE.get(variant).containsKey(texture);
        return false;
    } // checkTextureID ()

    public static int getRandomTextureID(EntityVariant variant) {
        List<EntityTexture> textures = ENTITY_TEXTURE.keySet().stream().filter(entityTexture -> ENTITY_TEXTURE.get(entityTexture).contains(variant)).toList();
        if (!textures.isEmpty()) {
            EntityTexture randomTexture = textures.get(new Random().nextInt(textures.size()));
            return randomTexture.getId(); // Replace with the appropriate method to get the ID
        }

        // Return a default or error ID if no valid variant is found
        return -1;
    } // getRandomTextureID ()

    private static HashMap<EntityTexture, ResourceLocation> setTexture(EntityVariant variant){
        String path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
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

    // ATTRIBUTE
    public static float getAttributeValue(EntityVariant variant, EntityAttribute attribute) {
        InternalAttribute robotAttribute = ATTRIBUTES.get(variant).get(attribute);
        return robotAttribute == null ? 0F : robotAttribute.value;
    } // getAttributeValue ()

    private static HashMap<EntityAttribute, InternalAttribute> setAttribute(float maxLevel, float maxHealth, float attackDamage, float attackSpeed, float movementSpeed, float defense){
        return new HashMap<>(){{
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