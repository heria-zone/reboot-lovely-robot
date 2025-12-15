package net.msymbios.llovelyr.source;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.source.entity.custom.RobotEntity;
import net.msymbios.llovelyr.source.entity.custom.RobotRenderer;
import net.msymbios.llovelyr.source.entity.custom.bunny.BunnyRenderer;
import net.msymbios.llovelyr.source.entity.custom.kitsune.KitsuneRenderer;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotType;

/**
 * Registry for LovelyRobotEntity Legacy entity types (Forge).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Legacy mod.
 * <p>
 * <b>Registered Entities:</b>
 * - Vanilla: Basic robot type
 * - Bunny2: Bunny-themed robot variant
 */
public class LovelyEntities {

    // -- Entity Type Registry --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, LovelyLegacy.MODID);

    // -- Entity Type Definitions --

    public static final RegistryObject<EntityType<RobotEntity>> BUNNY = registerRobot(LovelyIdentifier.VARIANT_BUNNY, LovelyRobotType.BUNNY, LovelyItems.BUNNY_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> BUNNY2 = registerRobot(LovelyIdentifier.VARIANT_BUNNY2, LovelyRobotType.BUNNY2, LovelyItems.BUNNY2_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> DRAGON = registerRobot(LovelyIdentifier.VARIANT_DRAGON, LovelyRobotType.DRAGON, LovelyItems.DRAGON_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> HONEY = registerRobot(LovelyIdentifier.VARIANT_HONEY, LovelyRobotType.HONEY, LovelyItems.HONEY_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> KITSUNE = registerRobot(LovelyIdentifier.VARIANT_KITSUNE, LovelyRobotType.KITSUNE, LovelyItems.KITSUNE_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> NEKO = registerRobot(LovelyIdentifier.VARIANT_NEKO, LovelyRobotType.NEKO, LovelyItems.NEKO_SPAWN);
    public static final RegistryObject<EntityType<RobotEntity>> VANILLA = registerRobot(LovelyIdentifier.VARIANT_VANILLA, LovelyRobotType.VANILLA, LovelyItems.VANILLA_SPAWN);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with RobotEntity implementation.
     * <p>
     * Type-safe registration that ensures RobotEntity is used consistently
     * across all robot variants.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @param spawnItem registry object for the spawn item
     * @return registered entity type
     */
    private static RegistryObject<EntityType<RobotEntity>> registerRobot(String name, NativeEntityType robotType, RegistryObject<net.minecraft.world.item.Item> spawnItem) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(
                (EntityType<RobotEntity> type, net.minecraft.world.level.Level level) ->
                        new RobotEntity(type, level, robotType, spawnItem.get()), MobCategory.CREATURE)
                .sized(LovelyConfigs.EntityDimensions.DEFAULT_WIDTH, LovelyConfigs.EntityDimensions.DEFAULT_HEIGHT)
                .build(LovelyIdentifier.getId(name).toString()));
    } // registerRobot()

    /**
     * Registers entity types to the event bus.
     *
     * @param eventBus Forge event bus
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register()

    /**
     * Registers entity attributes during entity attribute creation event.
     * <p>
     * <b>Timing:</b> Fires during mod construction. Config values are loaded
     * before this event to populate combat stats.
     *
     * @param event entity attribute creation event
     */
    public static void registerAttribute(EntityAttributeCreationEvent event) {
        event.put(BUNNY.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY));
        event.put(BUNNY2.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY2));
        event.put(DRAGON.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.DRAGON));
        event.put(HONEY.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.HONEY));
        event.put(KITSUNE.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.KITSUNE));
        event.put(NEKO.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.NEKO));
        event.put(VANILLA.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.VANILLA));
    } // registerAttribute()

    /**
     * Registers entity renderers on client side.
     *
     * @param event entity renderers registration event
     */
    public static void registerRender(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUNNY.get(), BunnyRenderer::new);
        event.registerEntityRenderer(BUNNY2.get(), RobotRenderer::new);
        event.registerEntityRenderer(DRAGON.get(), RobotRenderer::new);
        event.registerEntityRenderer(HONEY.get(), RobotRenderer::new);
        event.registerEntityRenderer(KITSUNE.get(), KitsuneRenderer::new);
        event.registerEntityRenderer(NEKO.get(), RobotRenderer::new);
        event.registerEntityRenderer(VANILLA.get(), RobotRenderer::new);
    } // registerRender()

} // Class: LovelyEntities
