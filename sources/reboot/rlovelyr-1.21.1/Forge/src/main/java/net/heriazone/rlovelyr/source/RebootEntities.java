package net.heriazone.rlovelyr.source;

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
import net.heriazone.lovelylib.source.reboot.RebootRobotFamilies;
import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Registry for Reboot entity types (Forge).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Mirrors LegacyEntities Forge — only modid, identifier, families source, and
 * ModTarget differ. Adding a new Reboot entity requires no change here.
 * <p>
 * <i>Note:</i> Reboot uses hzlib PickupFeature (not lovelylib api) — preserved
 * from the original to maintain behavioral parity.
 */
public class RebootEntities {

    // -- State --

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Reboot.MODID);

    private static final Map<RobotVariant, RegistryObject<EntityType<NativeRobotEntity>>> entityTypes =
        new HashMap<>();

    // -- Access --

    /**
     * Returns the RegistryObject for a Reboot variant's entity type.
     * Call .get() only after RegisterEvent has fired.
     *
     * @throws NullPointerException if the variant was never registered for REBOOT
     */
    public static RegistryObject<EntityType<NativeRobotEntity>> getEntityType(RobotVariant variant) {
        return Objects.requireNonNull(entityTypes.get(variant),
            "No entity type registered for Reboot variant: " + variant);
    } // getEntityType()

    // -- Registration --

    public static void register(IEventBus eventBus) {
        registerAll();
        ENTITY_TYPES.register(eventBus);
        Reboot.LOGGER.info("Registering Entities: " + Reboot.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RobotFamily family = RebootRobotFamilies.get(def.getVariant());
            RegistryObject<EntityType<NativeRobotEntity>> obj = ENTITY_TYPES.register(
                def.getVariantKey(),
                () -> EntityType.Builder.of(
                        (EntityType<NativeRobotEntity> t, Level l) -> new NativeRobotEntity(t, l, family),
                        MobCategory.CREATURE)
                    .sized(SharedConfigs.EntityDimensions.DEFAULT_WIDTH,
                           SharedConfigs.EntityDimensions.DEFAULT_HEIGHT)
                    .build(RebootIdentifier.getId(def.getVariantKey()).toString()));
            entityTypes.put(def.getVariant(), obj);
        }
    } // registerAll()

    public static void registerAttribute(EntityAttributeCreationEvent event) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            event.put(getEntityType(def.getVariant()).get(),
                NativeEntityFamily.createAttributes(RebootRobotFamilies.get(def.getVariant())));
        }
    } // registerAttribute()

    @SuppressWarnings("unchecked")
    public static void registerRender(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
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
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RebootRobotFamilies.get(def.getVariant())
                .withFeature(PickupFeature.class,
                    new PickupFeature(RebootItems.getSpawnItem(def.getVariant()).get()))
                .withFeature(DropFeature.class,
                    new DropFeature(RebootItems.ROBOT_CORE.get()));
        }
    } // registerNativeRobotFeature()

} // Class: RebootEntities
