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

    public static final RegistryObject<EntityType<RobotEntity>> BUNNY2 = registerRobot(LovelyIdentifier.VARIANT_BUNNY2, LovelyRobotType.BUNNY2);
    public static final RegistryObject<EntityType<RobotEntity>> VANILLA = registerRobot(LovelyIdentifier.VARIANT_VANILLA, LovelyRobotType.VANILLA);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with RobotEntity implementation.
     * <p>
     * Type-safe registration that ensures RobotEntity is used consistently
     * across all robot variants.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @return registered entity type
     */
    private static RegistryObject<EntityType<RobotEntity>> registerRobot(String name, NativeEntityType robotType) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(
                (EntityType<RobotEntity> type, net.minecraft.world.level.Level level) ->
                        new RobotEntity(type, level, robotType), MobCategory.CREATURE)
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
        event.put(BUNNY2.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY2));
        event.put(VANILLA.get(), LovelyRobotEntity.createAttributes(LovelyRobotType.VANILLA));
    } // registerAttribute()

    /**
     * Registers entity renderers on client side.
     *
     * @param event entity renderers registration event
     */
    public static void registerRender(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(BUNNY2.get(), RobotRenderer::new);
        event.registerEntityRenderer(VANILLA.get(), RobotRenderer::new);
    } // registerRender()

} // Class: LovelyEntities
