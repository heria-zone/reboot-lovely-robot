package net.msymbios.rlovelyr.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.InternalMetric;

public class LovelyRobotEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyRobot.MODID);
    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = register(LovelyRobotID.BUNNY, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), BunnyEntity::new);
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = register(LovelyRobotID.BUNNY2, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), Bunny2Entity::new);
    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = register(LovelyRobotID.DRAGON, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), DragonEntity::new);
    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = register(LovelyRobotID.HONEY, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), HoneyEntity::new);
    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = register(LovelyRobotID.KITSUNE, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), KitsuneEntity::new);
    public static final RegistryObject<EntityType<NekoEntity>> NEKO = register(LovelyRobotID.NEKO, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), NekoEntity::new);
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = register(LovelyRobotID.VANILLA, MobCategory.CREATURE, InternalMetric.WIDTH.get(), InternalMetric.HEIGHT.get(), VanillaEntity::new);

    // -- Methods --
    private static <T extends InternalEntity> RegistryObject<EntityType<T>> register(String name, MobCategory category, float width, float height, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(new ResourceLocation(LovelyRobot.MODID, name).toString()));
    } // register ()

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

    public static void registerRenderer() {
        EntityRenderers.register(LovelyRobotEntities.BUNNY.get(), BunnyRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.BUNNY2.get(), Bunny2Renderer::new);
        EntityRenderers.register(LovelyRobotEntities.DRAGON.get(), DragonRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.HONEY.get(), HoneyRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.KITSUNE.get(), KitsuneRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.NEKO.get(), NekoRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.VANILLA.get(), VanillaRenderer::new);
    } // registerRenderer ()

} // Class LovelyRobotEntities