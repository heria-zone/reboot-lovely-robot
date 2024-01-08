package net.msymbios.rlovelyr.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class LovelyRobotEntities {

    // -- Variables --
    public static final EntityType<BunnyEntity> BUNNY = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.BUNNY), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BunnyEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<Bunny2Entity> BUNNY2 = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.BUNNY2), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Bunny2Entity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<DragonEntity> DRAGON = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.DRAGON), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DragonEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<HoneyEntity> HONEY = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.HONEY), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoneyEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<KitsuneEntity> KITSUNE = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.KITSUNE), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, KitsuneEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<NekoEntity> NEKO = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.NEKO), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NekoEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
    public static final EntityType<VanillaEntity> VANILLA = Registry.register(Registry.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, LovelyRobotID.VANILLA), FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, VanillaEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());

    // -- Methods --
    public static void registerAttribute(){
        FabricDefaultAttributeRegistry.register(BUNNY, BunnyEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(BUNNY2, Bunny2Entity.setAttributes());
        FabricDefaultAttributeRegistry.register(DRAGON, DragonEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(HONEY, HoneyEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(KITSUNE, KitsuneEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(NEKO, NekoEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(VANILLA, VanillaEntity.setAttributes());
    } // registerAttribute ()

    public static void registerRenderer(){
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, Bunny2Renderer::new);
        EntityRendererRegistry.register(DRAGON, DragonRenderer::new);
        EntityRendererRegistry.register(HONEY, HoneyRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, NekoRenderer::new);
        EntityRendererRegistry.register(VANILLA, VanillaRenderer::new);
    } // registerRenderer ()

} // Class LovelyRobotEntities
