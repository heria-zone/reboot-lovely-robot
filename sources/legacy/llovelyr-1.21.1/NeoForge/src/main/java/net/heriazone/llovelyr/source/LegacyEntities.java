package net.heriazone.llovelyr.source;

import net.heriazone.hzlib.api.entity.NativeEntityFamily;
import net.heriazone.hzlib.api.entity.features.DropFeature;
import net.heriazone.hzlib.api.entity.features.PickupFeature;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.entity.NativeRobotEntity;
import net.heriazone.lovelylib.common.entity.NativeRobotRenderer;
import net.heriazone.lovelylib.common.entity.RobotFamily;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.source.legacy.LegacyRobotFamilies;
import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for Legacy entity types (NeoForge).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * NeoForge uses DeferredHolder instead of Forge's RegistryObject — otherwise
 * the pattern is identical to the Forge implementation.
 */
public class LegacyEntities {

    // -- State --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, Legacy.MODID);

    private static final Map<RobotVariant, DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>>> entityTypes =
        new HashMap<>();

    // -- Access --

    /**
     * Returns the DeferredHolder for a Legacy variant's entity type.
     * Call .get() only after RegisterEvent has fired.
     *
     * @throws NullPointerException if the variant was never registered for LEGACY
     */
    public static DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> getEntityType(RobotVariant variant) {
        return Objects.requireNonNull(entityTypes.get(variant),
            "No entity type registered for Legacy variant: " + variant);
    } // getEntityType()

    // -- Registration --

    public static void register(IEventBus eventBus) {
        registerAll();
        ENTITY_TYPES.register(eventBus);
        Legacy.LOGGER.info("Registering Entities: " + Legacy.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            RobotFamily family = LegacyRobotFamilies.get(def.getVariant());
            DeferredHolder<EntityType<?>, EntityType<NativeRobotEntity>> obj = ENTITY_TYPES.register(
                def.getVariantKey(),
                () -> EntityType.Builder.of(
                        (EntityType<NativeRobotEntity> t, Level l) -> new NativeRobotEntity(t, l, family),
                        MobCategory.CREATURE)
                    .sized(SharedConfigs.EntityDimensions.DEFAULT_WIDTH,
                           SharedConfigs.EntityDimensions.DEFAULT_HEIGHT)
                    .build(LegacyIdentifier.getId(def.getVariantKey()).toString()));
            entityTypes.put(def.getVariant(), obj);
        }
    } // registerAll()

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            event.put(getEntityType(def.getVariant()).get(),
                NativeEntityFamily.createAttributes(LegacyRobotFamilies.get(def.getVariant())));
        }
    } // registerAttribute()

    @SuppressWarnings("unchecked")
    public static void registerRender(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            EntityType<NativeRobotEntity> type = getEntityType(def.getVariant()).get();
            if (def.usesNativeRenderer()) {
                event.registerEntityRenderer(type, NativeRobotRenderer::new);
            } else {
                event.registerEntityRenderer(type,
                    ctx -> (net.minecraft.client.renderer.entity.EntityRenderer<NativeRobotEntity>)
                        def.getRendererFactory().create(ctx));
            }
        }
    } // registerRender()

    public static void registerNativeRobotFeature() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.LEGACY)) {
            LegacyRobotFamilies.get(def.getVariant())
                .withFeature(PickupFeature.class,
                    new PickupFeature(LegacyItems.getSpawnItem(def.getVariant()).get()))
                .withFeature(DropFeature.class,
                    new DropFeature(LegacyItems.ROBOT_CORE.get()));
        }
    } // registerNativeRobotFeature()

} // Class: LegacyEntities
