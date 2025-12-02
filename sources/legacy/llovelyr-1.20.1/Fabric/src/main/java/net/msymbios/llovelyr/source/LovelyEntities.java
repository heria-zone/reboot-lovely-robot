package net.msymbios.llovelyr.source;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotType;
import net.msymbios.llovelyr.source.entity.custom.RobotEntity;
import net.msymbios.llovelyr.source.entity.custom.RobotRenderer;

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

    public static final EntityType<RobotEntity> BUNNY2 = registerRobot(LovelyIdentifier.VARIANT_BUNNY2, LovelyRobotType.BUNNY2);
    public static final EntityType<RobotEntity> VANILLA = registerRobot(LovelyIdentifier.VARIANT_VANILLA, LovelyRobotType.VANILLA);

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
    private static EntityType<RobotEntity> registerRobot(String name, NativeEntityType robotType) {
        return Registry.register(
            Registries.ENTITY_TYPE, 
            LovelyIdentifier.getId(name),
            FabricEntityTypeBuilder.create(
                SpawnGroup.CREATURE,
                (EntityType<RobotEntity> type, World world) -> new RobotEntity(type, world, robotType)
            )
            .dimensions(EntityDimensions.fixed(LovelyConfigs.EntityDimensions.DEFAULT_WIDTH, LovelyConfigs.EntityDimensions.DEFAULT_HEIGHT))
            .build()
        );
    } // registerRobot()

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(BUNNY2, LovelyRobotEntity.createAttributes(LovelyRobotType.BUNNY2));
        FabricDefaultAttributeRegistry.register(VANILLA, LovelyRobotEntity.createAttributes(LovelyRobotType.VANILLA));
    } // register ()

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(BUNNY2, RobotRenderer::new);
        EntityRendererRegistry.register(VANILLA, RobotRenderer::new);
    } // registerRender ()

} // Class: LovelyEntities
