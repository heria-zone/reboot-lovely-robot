package net.msymbios.rlovelyr.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotConfig;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.client.renderer.*;
import net.msymbios.rlovelyr.entity.custom.*;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

public class LovelyRobotEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, LovelyRobot.MODID);
    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = register(LovelyRobotID.VARIANT_BUNNY, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), BunnyEntity::new);
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = register(LovelyRobotID.VARIANT_BUNNY2, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), Bunny2Entity::new);
    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = register(LovelyRobotID.VARIANT_DRAGON, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), DragonEntity::new);
    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = register(LovelyRobotID.VARIANT_HONEY, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), HoneyEntity::new);
    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = register(LovelyRobotID.VARIANT_KITSUNE, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), KitsuneEntity::new);
    public static final RegistryObject<EntityType<NekoEntity>> NEKO = register(LovelyRobotID.VARIANT_NEKO, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), NekoEntity::new);
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = register(LovelyRobotID.VARIANT_VANILLA, MobCategory.CREATURE, LovelyRobotConfig.COMMON.width.get().floatValue(), LovelyRobotConfig.COMMON.height.get().floatValue(), VanillaEntity::new);

    // -- Methods --
    private static <T extends InternalEntity> RegistryObject<EntityType<T>> register(String name, MobCategory category, float width, float height, EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category).sized(width, height).build(LovelyRobotID.getId(name).toString()));
    } // register ()

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register ()

    /**
     * Registers default attributes for various entities.
     * */
    public static void registerAttribute(EntityAttributeCreationEvent event) {
        event.put(LovelyRobotEntities.BUNNY.get(), BunnyEntity.createAttributes());
        event.put(LovelyRobotEntities.BUNNY2.get(), Bunny2Entity.createAttributes());
        event.put(LovelyRobotEntities.DRAGON.get(), DragonEntity.createAttributes());
        event.put(LovelyRobotEntities.HONEY.get(), HoneyEntity.createAttributes());
        event.put(LovelyRobotEntities.KITSUNE.get(), KitsuneEntity.createAttributes());
        event.put(LovelyRobotEntities.NEKO.get(), NekoEntity.createAttributes());
        event.put(LovelyRobotEntities.VANILLA.get(), VanillaEntity.createAttributes());
    } // registerAttributes ()

    /**
     * Registers entity renderers for various entities.
     * */
    public static void registerRender() {
        EntityRenderers.register(LovelyRobotEntities.BUNNY.get(), BunnyRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.BUNNY2.get(), Bunny2Renderer::new);
        EntityRenderers.register(LovelyRobotEntities.DRAGON.get(), DragonRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.HONEY.get(), HoneyRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.KITSUNE.get(), KitsuneRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.NEKO.get(), NekoRenderer::new);
        EntityRenderers.register(LovelyRobotEntities.VANILLA.get(), VanillaRenderer::new);
    } // registerRenderer ()

} // Class LovelyRobotEntities