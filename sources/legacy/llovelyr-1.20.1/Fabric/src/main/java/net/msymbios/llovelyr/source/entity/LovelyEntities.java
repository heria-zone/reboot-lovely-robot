package net.msymbios.llovelyr.source.entity;

import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.client.renderer.Bunny2Renderer;
import net.msymbios.llovelyr.source.entity.client.renderer.VanillaRenderer;
import net.msymbios.llovelyr.source.entity.custom.Bunny2Entity;
import net.msymbios.llovelyr.source.entity.custom.VanillaEntity;

/**
 * Registry for LovelyRobot Legacy entity types (Fabric).
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

    public static final EntityType<VanillaEntity> VANILLA = 
        register(LovelyIdentifier.VARIANT_VANILLA, (type, world) -> new VanillaEntity(type, world), SpawnGroup.CREATURE, 
            LovelyConfigs.Common.Width, LovelyConfigs.Common.Height);

    public static final EntityType<Bunny2Entity> BUNNY2 = 
        register(LovelyIdentifier.VARIANT_BUNNY2, (type, world) -> new Bunny2Entity(type, world), SpawnGroup.CREATURE, 
            LovelyConfigs.Common.Width, LovelyConfigs.Common.Height);

    // -- Registration Methods --

    /**
     * Registers an entity type with specified parameters.
     *
     * @param name entity variant name
     * @param factory entity factory
     * @param spawnGroup spawn group
     * @param width entity width
     * @param height entity height
     * @return registered entity type
     */
    private static <T extends Entity> EntityType<T> register(
            String name, EntityType.EntityFactory<T> factory, SpawnGroup spawnGroup, 
            float width, float height) {
        return Registry.register(Registries.ENTITY_TYPE, LovelyIdentifier.getId(name),
                FabricEntityTypeBuilder.create(spawnGroup, factory)
                    .dimensions(EntityDimensions.fixed(width, height))
                    .build());
    } // register

    /**
     * Registers entity attributes.
     */
    public static void register() {
        FabricDefaultAttributeRegistry.register(VANILLA, VanillaEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(BUNNY2, Bunny2Entity.createAttributes());
    } // register

    /**
     * Registers entity renderers on client side.
     */
    public static void registerRender() {
        EntityRendererRegistry.register(VANILLA, VanillaRenderer::new);
        EntityRendererRegistry.register(BUNNY2, Bunny2Renderer::new);
    } // registerRender

} // Class: LovelyEntities
