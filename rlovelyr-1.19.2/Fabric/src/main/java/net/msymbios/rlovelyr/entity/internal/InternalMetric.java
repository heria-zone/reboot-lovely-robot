package net.msymbios.rlovelyr.entity.internal;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.config.InternalMetrics;
import net.msymbios.rlovelyr.entity.enums.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

public class InternalMetric {

    // -- Entity Renderer --
    public static float ShadowRadius = 0.4F;
    public static float Width = 0.4F;
    public static float Height = 2F;

    // -- Goal Attribute --
    public static Predicate<LivingEntity> AvoidAttackingEntities = entity -> entity instanceof Monster && !(entity instanceof CreeperEntity);

    // Proprieties --
    public static HashMap<EntityVariant, EntityAnimator> ENTITY_ANIMATOR = new HashMap<>(){{
        put(EntityVariant.Bunny, EntityAnimator.Default);
        put(EntityVariant.Bunny2, EntityAnimator.Default);
        put(EntityVariant.Dragon, EntityAnimator.Default);
        put(EntityVariant.Honey, EntityAnimator.Default);
        put(EntityVariant.Kitsune, EntityAnimator.Default);
        put(EntityVariant.Neko, EntityAnimator.Default);
        put(EntityVariant.Vanilla, EntityAnimator.Default);
    }};

    public static HashMap<EntityVariant, HashMap<EntityModel, Identifier>> ENTITY_MODEL = new HashMap<>(){{
        put(EntityVariant.Bunny, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/bunny.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/bunny.attack.geo.json"));
        }});

        put(EntityVariant.Bunny2, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/bunny2.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/bunny2.attack.geo.json"));
        }});

        put(EntityVariant.Dragon, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/dragon.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/dragon.attack.geo.json"));
        }});

        put(EntityVariant.Honey, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/honey.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/honey.attack.geo.json"));
        }});

        put(EntityVariant.Kitsune, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/kitsune.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/kitsune.attack.geo.json"));
        }});

        put(EntityVariant.Neko, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/neko.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/neko.attack.geo.json"));
        }});

        put(EntityVariant.Vanilla, new HashMap<>() {{
            put(EntityModel.Default,    new Identifier(LovelyRobotMod.MODID, "geo/vanilla.geo.json"));
            put(EntityModel.Armed,      new Identifier(LovelyRobotMod.MODID, "geo/vanilla.attack.geo.json"));
        }});
    }};

    public static HashMap<EntityTexture, List<EntityVariant>> ENTITY_TEXTURE = new HashMap<>(){{
        List<EntityVariant> list = new ArrayList<>() {{
            add(EntityVariant.Bunny);
            add(EntityVariant.Bunny2);
            add(EntityVariant.Dragon);
            add(EntityVariant.Honey);
            add(EntityVariant.Kitsune);
            add(EntityVariant.Neko);
            add(EntityVariant.Vanilla);
        }};
        put(EntityTexture.BLUE, list);
        put(EntityTexture.BLACK, list);
        put(EntityTexture.CYAN, list);
        put(EntityTexture.BROWN, list);
        put(EntityTexture.LIME, list);
        put(EntityTexture.GRAY, list);
        put(EntityTexture.GREEN, list);
        put(EntityTexture.LIGHT_BLUE, list);
        put(EntityTexture.LIGHT_GRAY, list);
        put(EntityTexture.MAGENTA, list);
        put(EntityTexture.ORANGE, list);
        put(EntityTexture.PINK, list);
        put(EntityTexture.PURPLE, list);
        put(EntityTexture.RED, list);
        put(EntityTexture.WHITE, list);
        put(EntityTexture.YELLOW, list);
    }};

    public static HashMap<EntityAnimator, Identifier> ANIMATOR = new HashMap<>() {{
        put(EntityAnimator.Default,    new Identifier(LovelyRobotMod.MODID, "animations/default.animation.json"));
    }};

    public static HashMap<EntityVariant, HashMap<EntityTexture, Identifier>> TEXTURE = new HashMap<>(){{
        put(EntityVariant.Bunny,    setTexture(EntityVariant.Bunny));
        put(EntityVariant.Bunny2,   setTexture(EntityVariant.Bunny2));
        put(EntityVariant.Dragon,   setTexture(EntityVariant.Dragon));
        put(EntityVariant.Honey,    setTexture(EntityVariant.Honey));
        put(EntityVariant.Kitsune,  setTexture(EntityVariant.Kitsune));
        put(EntityVariant.Neko,     setTexture(EntityVariant.Neko));
        put(EntityVariant.Vanilla,  setTexture(EntityVariant.Vanilla));
    }};

    public static HashMap<EntityVariant, HashMap<EntityAttribute, Float>> ATTRIBUTES = new HashMap<>(){{
        put(EntityVariant.Bunny,        setAttribute(InternalMetrics.BUNNY_MAX_LEVEL, InternalMetrics.BUNNY_MAX_HEALTH, InternalMetrics.BUNNY_ATTACK_DAMAGE, InternalMetrics.BUNNY_ATTACK_SPEED, InternalMetrics.BUNNY_MOVEMENT_SPEED, InternalMetrics.BUNNY_DEFENCE));
        put(EntityVariant.Bunny2,       setAttribute(InternalMetrics.BUNNY2_MAX_LEVEL, InternalMetrics.BUNNY2_MAX_HEALTH, InternalMetrics.BUNNY2_ATTACK_DAMAGE, InternalMetrics.BUNNY2_ATTACK_SPEED, InternalMetrics.BUNNY2_MOVEMENT_SPEED, InternalMetrics.BUNNY2_DEFENCE));
        put(EntityVariant.Dragon,       setAttribute(InternalMetrics.DRAGON_MAX_LEVEL, InternalMetrics.DRAGON_MAX_HEALTH, InternalMetrics.DRAGON_ATTACK_DAMAGE, InternalMetrics.DRAGON_ATTACK_SPEED, InternalMetrics.DRAGON_MOVEMENT_SPEED, InternalMetrics.DRAGON_DEFENCE));
        put(EntityVariant.Honey,        setAttribute(InternalMetrics.HONEY_MAX_LEVEL, InternalMetrics.HONEY_MAX_HEALTH, InternalMetrics.HONEY_ATTACK_DAMAGE, InternalMetrics.HONEY_ATTACK_SPEED, InternalMetrics.HONEY_MOVEMENT_SPEED, InternalMetrics.HONEY_DEFENCE));
        put(EntityVariant.Kitsune,      setAttribute(InternalMetrics.KITSUNE_MAX_LEVEL, InternalMetrics.KITSUNE_MAX_HEALTH, InternalMetrics.KITSUNE_ATTACK_DAMAGE, InternalMetrics.KITSUNE_ATTACK_SPEED, InternalMetrics.KITSUNE_MOVEMENT_SPEED, InternalMetrics.KITSUNE_DEFENCE));
        put(EntityVariant.Neko,         setAttribute(InternalMetrics.NEKO_MAX_LEVEL, InternalMetrics.NEKO_MAX_HEALTH, InternalMetrics.NEKO_ATTACK_DAMAGE, InternalMetrics.NEKO_ATTACK_SPEED, InternalMetrics.NEKO_MOVEMENT_SPEED, InternalMetrics.NEKO_DEFENCE));
        put(EntityVariant.Vanilla,      setAttribute(InternalMetrics.VANILLA_MAX_LEVEL, InternalMetrics.VANILLA_MAX_HEALTH, InternalMetrics.VANILLA_ATTACK_DAMAGE, InternalMetrics.VANILLA_ATTACK_SPEED, InternalMetrics.VANILLA_MOVEMENT_SPEED, InternalMetrics.VANILLA_DEFENCE));
    }};

    // -- Methods --

    // ANIMATOR
    public static Identifier getAnimator(EntityVariant variant) {
        EntityAnimator selectedAnimator = ENTITY_ANIMATOR.get(variant);
        if (selectedAnimator != null && ANIMATOR.containsKey(selectedAnimator)) return ANIMATOR.get(selectedAnimator);
        return null; // Animator not found
    } // getAnimator ()

    public static Identifier getAnimator(EntityVariant variant, EntityAnimator animator) {
        EntityAnimator selectedAnimator = ENTITY_ANIMATOR.get(variant);
        if (selectedAnimator != null && selectedAnimator == animator && ANIMATOR.containsKey(animator)) return ANIMATOR.get(animator);
        return null; // Animator not found for the specified parameters
    } // getAnimator ()

    // MODEL
    public static Identifier getModel(EntityVariant variant) {
        if (ENTITY_MODEL.containsKey(variant)) {
            EntityModel defaultModel = EntityModel.Default; // Use the default model key, or adjust as needed
            if (ENTITY_MODEL.get(variant).containsKey(defaultModel)) return ENTITY_MODEL.get(variant).get(defaultModel);
        }
        return null; // Return a default or error identifier if the combination is not found
    } // getModel ()

    public static Identifier getModel(EntityVariant variant, EntityModel model) {
        if (ENTITY_MODEL.containsKey(variant) && ENTITY_MODEL.get(variant).containsKey(model)) return ENTITY_MODEL.get(variant).get(model);
        return null;  // Return a default or error identifier if the combination is not found
    } // getModel ()

    // TEXTURE
    public static Identifier getTexture(EntityVariant variant) {
        EntityTexture randomTexture = EntityTexture.byId(getRandomTextureID(variant));
        if (ENTITY_TEXTURE.containsKey(randomTexture) && ENTITY_TEXTURE.get(randomTexture).contains(variant)) {
            if (TEXTURE.containsKey(variant) && TEXTURE.get(variant).containsKey(randomTexture)) return TEXTURE.get(variant).get(randomTexture);
        }

        return null; // Return a default or error identifier if the combination is not found
    } // getTexture ()

    public static Identifier getTexture(EntityVariant variant, EntityTexture texture) {
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
            return randomTexture.getId();
        }

        // Return a default or error ID if no valid variant is found
        return -1;
    } // getRandomTextureID ()

    private static HashMap<EntityTexture, Identifier> setTexture(EntityVariant variant){
        String path = variant.getName() + "/" + variant.getName();
        return new HashMap<>() {{
            put(EntityTexture.WHITE,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_00.png")); // White
            put(EntityTexture.ORANGE,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_01.png")); // Orange
            put(EntityTexture.MAGENTA,       new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_02.png")); // Magenta
            put(EntityTexture.LIGHT_BLUE,    new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_03.png")); // Light Blue
            put(EntityTexture.YELLOW,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_04.png")); // Yellow
            put(EntityTexture.LIME,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_05.png")); // Lime
            put(EntityTexture.PINK,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_06.png")); // Pink
            put(EntityTexture.GRAY,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_07.png")); // Gray
            put(EntityTexture.LIGHT_GRAY,    new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_08.png")); // Light Gray
            put(EntityTexture.CYAN,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_09.png")); // Cyan
            put(EntityTexture.PURPLE,        new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_10.png")); // Purple
            put(EntityTexture.BLUE,          new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_11.png")); // Blue
            put(EntityTexture.BROWN,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_12.png")); // Brown
            put(EntityTexture.GREEN,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_13.png")); // Green
            put(EntityTexture.RED,           new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_14.png")); // Red
            put(EntityTexture.BLACK,         new Identifier(LovelyRobotMod.MODID, "textures/entity/" + path + "_15.png")); // Black
        }};
    } // setTexture ()

    // ATTRIBUTE
    public static float getAttributeValue(EntityVariant variant, EntityAttribute attribute) {
        Float robotAttribute = ATTRIBUTES.get(variant).get(attribute);
        return robotAttribute == null ? 0F : robotAttribute;
    } // getAttributeValue ()

    private static HashMap<EntityAttribute, Float> setAttribute(float maxLevel, float maxHealth, float attackDamage, float attackSpeed, float movementSpeed, float defense){
        return new HashMap<>(){{
            put(EntityAttribute.MAX_LEVEL, maxLevel);               // Max Level
            put(EntityAttribute.MAX_HEALTH, maxHealth);             // Max Health
            put(EntityAttribute.ATTACK_DAMAGE, attackDamage);       // Attack Damage
            put(EntityAttribute.ATTACK_SPEED, attackSpeed);         // Attack Speed
            put(EntityAttribute.MOVEMENT_SPEED, movementSpeed);     // Movement Speed
            put(EntityAttribute.DEFENSE, defense);                  // Defense
            put(EntityAttribute.ARMOR, InternalMetrics.GENERAL_ARMOR);
            put(EntityAttribute.ARMOR_TOUGHNESS, InternalMetrics.GENERAL_ARMOR_TOUGHNESS);
        }};
    } // setAttribute ()

} // Class InternalMetrics