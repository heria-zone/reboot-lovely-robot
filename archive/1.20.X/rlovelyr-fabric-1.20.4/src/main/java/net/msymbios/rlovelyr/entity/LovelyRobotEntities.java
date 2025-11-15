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
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class LovelyRobotEntities {

    // -- Variables --
    public static final EntityType<BunnyEntity> BUNNY_ENTITY = register(LovelyRobotID.BUNNY, BunnyEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<Bunny2Entity> BUNNY2_ENTITY = register(LovelyRobotID.BUNNY2, Bunny2Entity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<DragonEntity> DRAGON_ENTITY = register(LovelyRobotID.DRAGON, DragonEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<HoneyEntity> HONEY_ENTITY = register(LovelyRobotID.HONEY, HoneyEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<KitsuneEntity> KITSUNE_ENTITY = register(LovelyRobotID.KITSUNE, KitsuneEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<NekoEntity> NEKO_ENTITY = register(LovelyRobotID.NEKO, NekoEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);
    public static final EntityType<VanillaEntity> VANILLA_ENTITY = register(LovelyRobotID.VANILLA, VanillaEntity::new, SpawnGroup.CREATURE, InternalMetric.WIDTH, InternalMetric.HEIGHT);

    // -- Methods --

    /**
     * Registers a custom entity type with the specified name, factory, spawn group, width, and height.
     *
     * @param <T> The type of entity
     * @param name The name of the entity type
     * @param factory The factory to create instances of the entity type
     * @param spawnGroup The spawn group of the entity
     * @param width The width of the entity
     * @param height The height of the entity
     * @return The registered entity type
     * */
    private static <T extends Entity> EntityType<T> register (String name, EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup, float width, float height) {
        // Register the entity type with the provided name, factory, spawn group, width, and height
        return Registry.register(Registries.ENTITY_TYPE, LovelyRobotID.getId(name),
                FabricEntityTypeBuilder.create(spawnGroup, factory).dimensions(EntityDimensions.fixed(width, height)).build());
    } // register ()

    /**
     * Registers default attributes for various entities.
     * */
    public static void registerAttribute(){
        // Register default attributes for different entities
        FabricDefaultAttributeRegistry.register(BUNNY_ENTITY, BunnyEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(BUNNY2_ENTITY, Bunny2Entity.setAttributes());
        FabricDefaultAttributeRegistry.register(DRAGON_ENTITY, DragonEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(HONEY_ENTITY, HoneyEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(KITSUNE_ENTITY, KitsuneEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(NEKO_ENTITY, NekoEntity.setAttributes());
        FabricDefaultAttributeRegistry.register(VANILLA_ENTITY, VanillaEntity.setAttributes());
    } // registerAttribute ()

    /**
     * Registers entity renderers for various entities.
     * */
    public static void registerRender(){
        // Register entity renderers for different entities
        EntityRendererRegistry.register(BUNNY_ENTITY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2_ENTITY, Bunny2Renderer::new);
        EntityRendererRegistry.register(DRAGON_ENTITY, DragonRenderer::new);
        EntityRendererRegistry.register(HONEY_ENTITY, HoneyRenderer::new);
        EntityRendererRegistry.register(KITSUNE_ENTITY, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO_ENTITY, NekoRenderer::new);
        EntityRendererRegistry.register(VANILLA_ENTITY, VanillaRenderer::new);
    } // registerRender ()

} // Class LovelyRobotEntities
