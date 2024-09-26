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
import net.msymbios.rlovelyr.common.entity.InternalEntity;

public class LovelyRobotEntities {

    // -- Variables --
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyRobot.MODID);

    public static final RegistryObject<EntityType<BunnyEntity>> BUNNY = register(LovelyRobotID.VARIANT_BUNNY, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, BunnyEntity::new);
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = register(LovelyRobotID.VARIANT_BUNNY2, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, Bunny2Entity::new);
    public static final RegistryObject<EntityType<DragonEntity>> DRAGON = register(LovelyRobotID.VARIANT_DRAGON, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, DragonEntity::new);
    public static final RegistryObject<EntityType<HoneyEntity>> HONEY = register(LovelyRobotID.VARIANT_HONEY, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, HoneyEntity::new);
    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE = register(LovelyRobotID.VARIANT_KITSUNE, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, KitsuneEntity::new);
    public static final RegistryObject<EntityType<NekoEntity>> NEKO = register(LovelyRobotID.VARIANT_NEKO, MobCategory.CREATURE, LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, NekoEntity::new);
    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = register(LovelyRobotID.VARIANT_VANILLA, MobCategory.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, VanillaEntity::new);

    public static final RegistryObject<EntityType<PrimeEntity>> PRIME = register(LovelyRobotID.VARIANT_PRIME, MobCategory.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, PrimeEntity::new);
    public static final RegistryObject<EntityType<HyperionEntity>> HYPERION = register(LovelyRobotID.VARIANT_HYPERION, MobCategory.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, HyperionEntity::new);
    public static final RegistryObject<EntityType<EmpyriumEntity>> EMPYRIUM = register(LovelyRobotID.VARIANT_EMPYRIUM, MobCategory.CREATURE,  LovelyRobotConfig.Common.Width, LovelyRobotConfig.Common.Height, EmpyriumEntity::new);

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
        event.put(BUNNY.get(), BunnyEntity.createAttributes());
        event.put(BUNNY2.get(), Bunny2Entity.createAttributes());
        event.put(DRAGON.get(), DragonEntity.createAttributes());
        event.put(HONEY.get(), HoneyEntity.createAttributes());
        event.put(KITSUNE.get(), KitsuneEntity.createAttributes());
        event.put(NEKO.get(), NekoEntity.createAttributes());
        event.put(VANILLA.get(), VanillaEntity.createAttributes());

        event.put(PRIME.get(), PrimeEntity.createAttributes());
        event.put(HYPERION.get(), HyperionEntity.createAttributes());
        event.put(EMPYRIUM.get(), EmpyriumEntity.createAttributes());
    } // registerAttribute ()

    /**
     * Registers entity renderers for various entities.
     * */
    public static void registerRender() {
        EntityRenderers.register(BUNNY.get(), BunnyRenderer::new);
        EntityRenderers.register(BUNNY2.get(), Bunny2Renderer::new);
        EntityRenderers.register(DRAGON.get(), DragonRenderer::new);
        EntityRenderers.register(HONEY.get(), HoneyRenderer::new);
        EntityRenderers.register(KITSUNE.get(), KitsuneRenderer::new);
        EntityRenderers.register(NEKO.get(), NekoRenderer::new);
        EntityRenderers.register(VANILLA.get(), VanillaRenderer::new);

        EntityRenderers.register(PRIME.get(), PrimeRenderer::new);
        EntityRenderers.register(HYPERION.get(), HyperionRenderer::new);
        EntityRenderers.register(EMPYRIUM.get(), EmpyriumRenderer::new);
    } // registerRender (

} // Class LovelyRobotEntities