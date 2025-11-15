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
    public static final EntityType<BunnyEntity> BUNNY = register(LovelyRobotID.BUNNY, BunnyEntity::new);
    public static final EntityType<Bunny2Entity> BUNNY2 = register(LovelyRobotID.BUNNY2, Bunny2Entity::new);
    public static final EntityType<DragonEntity> DRAGON = register(LovelyRobotID.DRAGON, DragonEntity::new);
    public static final EntityType<HoneyEntity> HONEY = register(LovelyRobotID.HONEY, HoneyEntity::new);
    public static final EntityType<KitsuneEntity> KITSUNE = register(LovelyRobotID.KITSUNE, KitsuneEntity::new);
    public static final EntityType<NekoEntity> NEKO = register(LovelyRobotID.NEKO, NekoEntity::new);
    public static final EntityType<VanillaEntity> VANILLA = register(LovelyRobotID.VANILLA, VanillaEntity::new);

    // -- Methods --
    private static <T extends Entity> EntityType<T> register (String name, EntityType.EntityFactory<T> factory) {
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobot.MODID, name),
                FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, factory).dimensions(EntityDimensions.fixed(InternalMetric.WIDTH, InternalMetric.HEIGHT)).build());
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

    public static void registerRender(){
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, Bunny2Renderer::new);
        EntityRendererRegistry.register(DRAGON, DragonRenderer::new);
        EntityRendererRegistry.register(HONEY, HoneyRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, NekoRenderer::new);
        EntityRendererRegistry.register(VANILLA, VanillaRenderer::new);
    } // registerRender ()

} // Class LovelyRobotEntities
