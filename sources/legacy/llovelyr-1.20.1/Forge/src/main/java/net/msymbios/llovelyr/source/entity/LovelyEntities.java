package net.msymbios.llovelyr.source.entity;

import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.entity.internal.InternalEntity;
import net.msymbios.llovelyr.source.configs.LovelyConfigs;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.entity.client.renderer.Bunny2Renderer;
import net.msymbios.llovelyr.source.entity.client.renderer.VanillaRenderer;
import net.msymbios.llovelyr.source.entity.custom.Bunny2Entity;
import net.msymbios.llovelyr.source.entity.custom.VanillaEntity;

/**
 * Registry for LovelyRobot Legacy entity types.
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

    public static final RegistryObject<EntityType<VanillaEntity>> VANILLA = register(LovelyIdentifier.VARIANT_VANILLA, MobCategory.CREATURE, 0.4F, 1.9F/*LovelyConfigs.Width, LovelyConfigs.Height*/, (type, level) -> new VanillaEntity(type, level));
    public static final RegistryObject<EntityType<Bunny2Entity>> BUNNY2 = register(LovelyIdentifier.VARIANT_BUNNY2, MobCategory.CREATURE, 0.4F, 1.9F/*LovelyConfigs.Width, LovelyConfigs.Height**/, (type, level) -> new Bunny2Entity(type, level));

    // -- Registration Methods --
    /**/
    /**
     * Registers an entity type with specified parameters.
     *
     * @param name entity variant name
     * @param category mob category
     * @param width entity width
     * @param height entity height
     * @param factory entity factory
     * @return registered entity type
     */
    private static <T extends InternalEntity> RegistryObject<EntityType<T>> register(
            String name, MobCategory category, float width, float height, 
            EntityType.EntityFactory<T> factory) {
        return ENTITY_TYPES.register(name, () -> EntityType.Builder.of(factory, category)
            .sized(width, height)
            .build(LovelyIdentifier.getId(name).toString()));
    } // register

    /**
     * Registers entity types to the event bus.
     *
     * @param eventBus Forge event bus
     */
    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    } // register

    /**
     * Registers entity attributes during entity attribute creation event.
     * <p>
     * <b>Timing:</b> This event fires during mod construction. To ensure config
     * values are loaded, we call NativeEntityType.register() first to populate
     * combat stats from config before creating attribute suppliers.
     *
     * @param event entity attribute creation event
     */
    public static void registerAttribute(EntityAttributeCreationEvent event) {
        event.put(VANILLA.get(), VanillaEntity.createAttributes());
        event.put(BUNNY2.get(), Bunny2Entity.createAttributes());
    } // registerAttribute ()

    /**
     * Registers entity renderers on client side.
     * <p>
     * <b>Architecture:</b> Uses EntityRenderersEvent.RegisterRenderers for proper
     * renderer registration in Forge 1.20.1+. This ensures renderers are registered
     * at the correct time and on the correct thread.
     *
     * @param event the entity renderers registration event
    */
    public static void registerRender(net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(VANILLA.get(), VanillaRenderer::new);
        event.registerEntityRenderer(BUNNY2.get(), Bunny2Renderer::new);
    } // registerRender ()

} // Class: LovelyEntities
