package net.msymbios.rlovelyr.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class LovelyRobotEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, LovelyRobot.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = ENTITY_TYPES.register(EntityVariant.Bunny.getName(),
            () -> EntityType.Builder.of(BunnyEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Bunny.getName()).toString()));

    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = ENTITY_TYPES.register(EntityVariant.Bunny2.getName(),
            () -> EntityType.Builder.of(Bunny2Entity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Bunny2.getName()).toString()));

    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = ENTITY_TYPES.register(EntityVariant.Dragon.getName(),
            () -> EntityType.Builder.of(DragonEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Dragon.getName()).toString()));

    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = ENTITY_TYPES.register(EntityVariant.Honey.getName(),
            () -> EntityType.Builder.of(HoneyEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Honey.getName()).toString()));

    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = ENTITY_TYPES.register(EntityVariant.Kitsune.getName(),
            () -> EntityType.Builder.of(KitsuneEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Kitsune.getName()).toString()));

    public static final RegistryObject<EntityType<NekoEntity>> NEKO = ENTITY_TYPES.register(EntityVariant.Neko.getName(),
            () -> EntityType.Builder.of(NekoEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Neko.getName()).toString()));

    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = ENTITY_TYPES.register(EntityVariant.Vanilla.getName(),
            () -> EntityType.Builder.of(VanillaEntity::new, MobCategory.CREATURE).sized(InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get())
                    .build(new ResourceLocation(LovelyRobot.MODID, EntityVariant.Vanilla.getName()).toString()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(LovelyRobotEntities.BUNNY.get(), BunnyEntity.setAttributes());
        event.put(LovelyRobotEntities.BUNNY2.get(), Bunny2Entity.setAttributes());
        event.put(LovelyRobotEntities.DRAGON.get(), DragonEntity.setAttributes());
        event.put(LovelyRobotEntities.HONEY.get(), HoneyEntity.setAttributes());
        event.put(LovelyRobotEntities.KITSUNE.get(), KitsuneEntity.setAttributes());
        event.put(LovelyRobotEntities.NEKO.get(), NekoEntity.setAttributes());
        event.put(LovelyRobotEntities.VANILLA.get(), VanillaEntity.setAttributes());
    } // registerAttributes ()

    public static void registerRenderers() {
         EntityRenderers.register(LovelyRobotEntities.BUNNY.get(), BunnyRenderer::new);
         EntityRenderers.register(LovelyRobotEntities.BUNNY2.get(), Bunny2Renderer::new);
         EntityRenderers.register(LovelyRobotEntities.DRAGON.get(), DragonRenderer::new);
         EntityRenderers.register(LovelyRobotEntities.HONEY.get(), HoneyRenderer::new);
         EntityRenderers.register(LovelyRobotEntities.KITSUNE.get(), KitsuneRenderer::new);
         EntityRenderers.register(LovelyRobotEntities.NEKO.get(), NekoRenderer::new);
         EntityRenderers.register(LovelyRobotEntities.VANILLA.get(), VanillaRenderer::new);
    } // registerRenderer ()

} // Class LovelyRobotEntities