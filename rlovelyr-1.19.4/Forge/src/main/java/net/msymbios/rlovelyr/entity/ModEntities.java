package net.msymbios.rlovelyr.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.msymbios.rlovelyr.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.LovelyRobotMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.entity.enums.EntityVariant;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class ModEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyRobotMod.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = ENTITY_TYPES.register(EntityVariant.Bunny.getName(),
            () -> EntityType.Builder.of(BunnyEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Bunny.getName()).toString()));

    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = ENTITY_TYPES.register(EntityVariant.Bunny2.getName(),
            () -> EntityType.Builder.of(Bunny2Entity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Bunny2.getName()).toString()));

    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = ENTITY_TYPES.register(EntityVariant.Dragon.getName(),
            () -> EntityType.Builder.of(DragonEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Dragon.getName()).toString()));

    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = ENTITY_TYPES.register(EntityVariant.Honey.getName(),
            () -> EntityType.Builder.of(HoneyEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Honey.getName()).toString()));

    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = ENTITY_TYPES.register(EntityVariant.Kitsune.getName(),
            () -> EntityType.Builder.of(KitsuneEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Kitsune.getName()).toString()));

    public static final RegistryObject<EntityType<NekoEntity>> NEKO = ENTITY_TYPES.register(EntityVariant.Neko.getName(),
            () -> EntityType.Builder.of(NekoEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Neko.getName()).toString()));

    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = ENTITY_TYPES.register(EntityVariant.Vanilla.getName(),
            () -> EntityType.Builder.of(VanillaEntity::new, MobCategory.CREATURE).sized(InternalMetric.Width, InternalMetric.Height)
                    .build(new ResourceLocation(LovelyRobotMod.MODID, EntityVariant.Vanilla.getName()).toString()));

    // -- Methods --
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BUNNY.get(), BunnyEntity.setAttributes());
        event.put(ModEntities.BUNNY2.get(), Bunny2Entity.setAttributes());
        event.put(ModEntities.DRAGON.get(), DragonEntity.setAttributes());
        event.put(ModEntities.HONEY.get(), HoneyEntity.setAttributes());
        event.put(ModEntities.KITSUNE.get(), KitsuneEntity.setAttributes());
        event.put(ModEntities.NEKO.get(), NekoEntity.setAttributes());
        event.put(ModEntities.VANILLA.get(), VanillaEntity.setAttributes());
    } // registerAttributes ()

    public static void registerRenderers() {
        EntityRenderers.register(ModEntities.BUNNY.get(), BunnyRenderer::new);
        EntityRenderers.register(ModEntities.BUNNY2.get(), Bunny2Renderer::new);
        EntityRenderers.register(ModEntities.DRAGON.get(), DragonRenderer::new);
        EntityRenderers.register(ModEntities.HONEY.get(), HoneyRenderer::new);
        EntityRenderers.register(ModEntities.KITSUNE.get(), KitsuneRenderer::new);
        EntityRenderers.register(ModEntities.NEKO.get(), NekoRenderer::new);
        EntityRenderers.register(ModEntities.VANILLA.get(), VanillaRenderer::new);
    } // registerRenderer ()

} // Class ModEntities