package net.msymbios.rlovelyr.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class LovelyRobotEntities {

    // -- Variables --
    public static final EntityType<BunnyEntity> BUNNY = register(LovelyRobotID.BUNNY, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, BunnyEntity::new);
    public static final EntityType<Bunny2Entity> BUNNY2 = register(LovelyRobotID.BUNNY2, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, Bunny2Entity::new);
    public static final EntityType<DragonEntity> DRAGON = register(LovelyRobotID.DRAGON, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, DragonEntity::new);
    public static final EntityType<HoneyEntity> HONEY = register(LovelyRobotID.HONEY, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, HoneyEntity::new);
    public static final EntityType<KitsuneEntity> KITSUNE = register(LovelyRobotID.KITSUNE, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, KitsuneEntity::new);
    public static final EntityType<NekoEntity> NEKO = register(LovelyRobotID.NEKO, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, NekoEntity::new);
    public static final EntityType<VanillaEntity> VANILLA = register(LovelyRobotID.VANILLA, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT, VanillaEntity::new);

    // -- Methods --
    private static <T extends Entity> EntityType<T> register (String name, SpawnGroup spawnGroup, float width, float height, EntityType.EntityFactory<T> factory) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, name), FabricEntityTypeBuilder.create(spawnGroup, factory).dimensions(EntityDimensions.fixed(width, height)).build());
    } // register ()

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
