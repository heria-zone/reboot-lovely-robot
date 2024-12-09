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
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;

public class LovelyRobotEntities {

    // -- Variables --

    public static final EntityType<BunnyEntity> BUNNY = register(LovelyRobotID.VARIANT_BUNNY, BunnyEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<Bunny2Entity> BUNNY2 = register(LovelyRobotID.VARIANT_BUNNY2, Bunny2Entity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<DragonEntity> DRAGON = register(LovelyRobotID.VARIANT_DRAGON, DragonEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<HoneyEntity> HONEY = register(LovelyRobotID.VARIANT_HONEY, HoneyEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<KitsuneEntity> KITSUNE = register(LovelyRobotID.VARIANT_KITSUNE, KitsuneEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<NekoEntity> NEKO = register(LovelyRobotID.VARIANT_NEKO, NekoEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<VanillaEntity> VANILLA = register(LovelyRobotID.VARIANT_VANILLA, VanillaEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);

    public static final EntityType<PrimeEntity> PRIME = register(LovelyRobotID.VARIANT_PRIME, PrimeEntity::new, SpawnGroup.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<HyperionEntity> HYPERION = register(LovelyRobotID.VARIANT_HYPERION, HyperionEntity::new, SpawnGroup.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);
    public static final EntityType<EmpyriumEntity> EMPYRIUM = register(LovelyRobotID.VARIANT_EMPYRIUM, EmpyriumEntity::new, SpawnGroup.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);

    public static final EntityType<StingerEntity> STINGER = register(LovelyRobotID.VARIANT_STINGER, StingerEntity::new, SpawnGroup.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height);

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
        FabricDefaultAttributeRegistry.register(BUNNY, BunnyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BUNNY2, Bunny2Entity.createAttributes());
        FabricDefaultAttributeRegistry.register(DRAGON, DragonEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(HONEY, HoneyEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(KITSUNE, KitsuneEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(NEKO, NekoEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(VANILLA, VanillaEntity.createAttributes());

        FabricDefaultAttributeRegistry.register(PRIME, PrimeEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(HYPERION, HyperionEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(EMPYRIUM, EmpyriumEntity.createAttributes());

        FabricDefaultAttributeRegistry.register(STINGER,  StingerEntity.createAttributes());
    } // registerAttribute ()

    /**
     * Registers entity renderers for various entities.
     * */
    public static void registerRender(){
        // Register entity renderers for different entities
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, Bunny2Renderer::new);
        EntityRendererRegistry.register(DRAGON, DragonRenderer::new);
        EntityRendererRegistry.register(HONEY, HoneyRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, NekoRenderer::new);
        EntityRendererRegistry.register(VANILLA, VanillaRenderer::new);

        EntityRendererRegistry.register(PRIME, PrimeRenderer::new);
        EntityRendererRegistry.register(HYPERION, HyperionRenderer::new);
        EntityRendererRegistry.register(EMPYRIUM, EmpyriumRenderer::new);

        EntityRendererRegistry.register(STINGER, StingerRenderer::new);
    } // registerRender ()

} // Class LovelyRobotEntities