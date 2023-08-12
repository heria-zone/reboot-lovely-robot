package net.msymbios.rlovelyr.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.msymbios.rlovelyr.entity.client.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class ModEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, LovelyRobotMod.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = ENTITY_TYPES.register("bunny",
            () -> EntityType.Builder.of(BunnyEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny").toString()));

    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = ENTITY_TYPES.register("bunny2",
            () -> EntityType.Builder.of(Bunny2Entity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "bunny2").toString()));

    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = ENTITY_TYPES.register("dragon",
            () -> EntityType.Builder.of(DragonEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "dragon").toString()));

    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = ENTITY_TYPES.register("honey",
            () -> EntityType.Builder.of(HoneyEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "honey").toString()));

    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = ENTITY_TYPES.register("kitsune",
            () -> EntityType.Builder.of(KitsuneEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "kitsune").toString()));

    public static final RegistryObject<EntityType<NekoEntity>> NEKO = ENTITY_TYPES.register("neko",
            () -> EntityType.Builder.of(NekoEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "neko").toString()));

    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = ENTITY_TYPES.register("vanilla",
            () -> EntityType.Builder.of(VanillaEntity::new, EntityClassification.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, "vanilla").toString()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BUNNY.get(), BunnyEntity.setAttributes());
        event.put(ModEntities.BUNNY2.get(), Bunny2Entity.setAttributes());
        event.put(ModEntities.DRAGON.get(), DragonEntity.setAttributes());
        event.put(ModEntities.HONEY.get(), HoneyEntity.setAttributes());
        event.put(ModEntities.KITSUNE.get(), KitsuneEntity.setAttributes());
        event.put(ModEntities.NEKO.get(), NekoEntity.setAttributes());
        event.put(ModEntities.VANILLA.get(), VanillaEntity.setAttributes());
    } // registerAttribute ()

    public static void registerRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUNNY.get(), BunnyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BUNNY2.get(), Bunny2Renderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.DRAGON.get(), DragonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HONEY.get(), HoneyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.KITSUNE.get(), KitsuneRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.NEKO.get(), NekoRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VANILLA.get(), VanillaRenderer::new);
    } // registerRenderer ()

} // Class ModEntities