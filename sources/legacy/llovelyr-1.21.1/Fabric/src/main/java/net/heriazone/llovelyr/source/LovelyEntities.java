package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.llovelyr.entity.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.common.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/**
 * Registry for LovelyRobotEntity Legacy entity types (Fabric).
 * <p>
 * <b>Architecture:</b> Manages entity type registration, attribute creation,
 * and renderer binding for all robot variants in the Legacy mod.
 * <p>
 * <b>Registered Entities:</b>
 * - Vanilla: Basic robot type
 * - Bunny2: Bunny-themed robot variant
 */
public class LovelyEntities {

    // -- Entity Type Definitions --

    public static final EntityType<RobotEntity> BUNNY = registerRobot(LegacyIdentifier.VARIANT_BUNNY, LovelyRobotType.BUNNY, () -> LovelyItems.BUNNY_SPAWN);
    public static final EntityType<RobotEntity> BUNNY2 = registerRobot(LegacyIdentifier.VARIANT_BUNNY2, LovelyRobotType.BUNNY2, () -> LovelyItems.BUNNY2_SPAWN);
    public static final EntityType<RobotEntity> DRAGON = registerRobot(LegacyIdentifier.VARIANT_DRAGON, LovelyRobotType.DRAGON, () -> LovelyItems.DRAGON_SPAWN);
    public static final EntityType<RobotEntity> HONEY = registerRobot(LegacyIdentifier.VARIANT_HONEY, LovelyRobotType.HONEY, () -> LovelyItems.HONEY_SPAWN);
    public static final EntityType<RobotEntity> KITSUNE = registerRobot(LegacyIdentifier.VARIANT_KITSUNE, LovelyRobotType.KITSUNE, () -> LovelyItems.KITSUNE_SPAWN);
    public static final EntityType<RobotEntity> NEKO = registerRobot(LegacyIdentifier.VARIANT_NEKO, LovelyRobotType.NEKO, () -> LovelyItems.NEKO_SPAWN);
    public static final EntityType<RobotEntity> VANILLA = registerRobot(LegacyIdentifier.VARIANT_VANILLA, LovelyRobotType.VANILLA, () -> LovelyItems.VANILLA_SPAWN);

    // -- Registration Methods --

    /**
     * Registers a robot entity type with RobotEntity implementation.
     * <p>
     * Type-safe registration that ensures RobotEntity is used consistently
     * across all robot variants.
     *
     * @param name entity variant name
     * @param robotType robot configuration data
     * @param spawnItemSupplier supplier for the spawn item (lazy evaluation)
     * @return registered entity type
     */
    private static EntityType<RobotEntity> registerRobot(String name, NativeEntityType robotType, java.util.function.Supplier<Item> spawnItemSupplier) {
        return Registry.register(
                BuiltInRegistries.ENTITY_TYPE,
                LegacyIdentifier.getId(name),
                FabricEntityTypeBuilder.create(
                                MobCategory.CREATURE,
                                (EntityType<RobotEntity> type, Level world) -> new RobotEntity(type, world, robotType, spawnItemSupplier.get())
                        )
                        .dimensions(EntityDimensions.fixed(SharedConfigs.EntityDimensions.DEFAULT_WIDTH, SharedConfigs.EntityDimensions.DEFAULT_HEIGHT))
                        .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY, LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2, LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY2));
        FabricDefaultAttributeRegistry.register(DRAGON, LovelyRobotEntity.createAttributes(LovelyRobotType.DRAGON));
        FabricDefaultAttributeRegistry.register(HONEY, LovelyRobotEntity.createAttributes(LovelyRobotType.HONEY));
        FabricDefaultAttributeRegistry.register(KITSUNE, LovelyRobotEntity.createAttributes(LovelyRobotType.KITSUNE));
        FabricDefaultAttributeRegistry.register(NEKO, LovelyRobotEntity.createAttributes(LovelyRobotType.NEKO));
        FabricDefaultAttributeRegistry.register(VANILLA, LovelyRobotEntity.createAttributes(LovelyRobotType.VANILLA));
    } // register ()

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(BUNNY, BunnyRenderer::new);
        EntityRendererRegistry.register(BUNNY2, RobotRenderer::new);
        EntityRendererRegistry.register(DRAGON, RobotRenderer::new);
        EntityRendererRegistry.register(HONEY, RobotRenderer::new);
        EntityRendererRegistry.register(KITSUNE, KitsuneRenderer::new);
        EntityRendererRegistry.register(NEKO, RobotRenderer::new);
        EntityRendererRegistry.register(VANILLA, RobotRenderer::new);
    } // registerRender ()

} // Class: LovelyEntities