package net.heriazone.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.llovelyr.entity.*;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.source.legacy.LegacyRobotType;
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
public class LegacyEntities {

    // -- Entity Type Definitions --

    public static final EntityType<RobotEntity> BUNNY = registerRobot(LovelyConstant.VARIANT_BUNNY, LegacyRobotType.BUNNY, () -> LegacyItems.BUNNY_SPAWN);
    public static final EntityType<RobotEntity> BUNNY2 = registerRobot(LovelyConstant.VARIANT_BUNNY2, LegacyRobotType.BUNNY2, () -> LegacyItems.BUNNY2_SPAWN);
    public static final EntityType<RobotEntity> DRAGON = registerRobot(LovelyConstant.VARIANT_DRAGON, LegacyRobotType.DRAGON, () -> LegacyItems.DRAGON_SPAWN);
    public static final EntityType<RobotEntity> HONEY = registerRobot(LovelyConstant.VARIANT_HONEY, LegacyRobotType.HONEY, () -> LegacyItems.HONEY_SPAWN);
    public static final EntityType<RobotEntity> KITSUNE = registerRobot(LovelyConstant.VARIANT_KITSUNE, LegacyRobotType.KITSUNE, () -> LegacyItems.KITSUNE_SPAWN);
    public static final EntityType<RobotEntity> NEKO = registerRobot(LovelyConstant.VARIANT_NEKO, LegacyRobotType.NEKO, () -> LegacyItems.NEKO_SPAWN);
    public static final EntityType<RobotEntity> VANILLA = registerRobot(LovelyConstant.VARIANT_VANILLA, LegacyRobotType.VANILLA, () -> LegacyItems.VANILLA_SPAWN);

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
                LovelyIdentifier.getId(name),
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
        FabricDefaultAttributeRegistry.register(BUNNY, LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY));
        FabricDefaultAttributeRegistry.register(BUNNY2, LovelyRobotEntity.createAttributes(LegacyRobotType.BUNNY2));
        FabricDefaultAttributeRegistry.register(DRAGON, LovelyRobotEntity.createAttributes(LegacyRobotType.DRAGON));
        FabricDefaultAttributeRegistry.register(HONEY, LovelyRobotEntity.createAttributes(LegacyRobotType.HONEY));
        FabricDefaultAttributeRegistry.register(KITSUNE, LovelyRobotEntity.createAttributes(LegacyRobotType.KITSUNE));
        FabricDefaultAttributeRegistry.register(NEKO, LovelyRobotEntity.createAttributes(LegacyRobotType.NEKO));
        FabricDefaultAttributeRegistry.register(VANILLA, LovelyRobotEntity.createAttributes(LegacyRobotType.VANILLA));
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

} // Class: LegacyEntities