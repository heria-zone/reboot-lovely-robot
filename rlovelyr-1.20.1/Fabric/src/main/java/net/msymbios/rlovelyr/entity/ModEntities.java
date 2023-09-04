package net.msymbios.rlovelyr.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.msymbios.rlovelyr.entity.client.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class ModEntities {

    // -- Variables --
    public static final EntityType<BunnyEntity> BUNNY = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Bunny.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, BunnyEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<Bunny2Entity> BUNNY2 = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Bunny2.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, Bunny2Entity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<DragonEntity> DRAGON = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Dragon.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, DragonEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<HoneyEntity> HONEY = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Honey.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, HoneyEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<KitsuneEntity> KITSUNE = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Kitsune.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, KitsuneEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<NekoEntity> NEKO = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Neko.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, NekoEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

    public static final EntityType<VanillaEntity> VANILLA = Registry.register(Registries.ENTITY_TYPE, new Identifier(LovelyRobotMod.MODID, EntityVariant.Vanilla.getName()),
            FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, VanillaEntity::new).dimensions(EntityDimensions.fixed(InternalMetric.Width, InternalMetric.Height)).build());

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

} // Class ModEntities
