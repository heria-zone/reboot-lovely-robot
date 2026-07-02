package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.lovelylib.common.entity.definition.ModTarget;
import net.heriazone.lovelylib.common.entity.definition.RobotDefinitionRegistry;
import net.heriazone.lovelylib.common.entity.definition.RobotEntityDefinition;
import net.heriazone.lovelylib.common.entity.definition.RobotVariant;
import net.heriazone.lovelylib.common.items.LovelyCoreItem;
import net.heriazone.lovelylib.common.items.LovelySpawnItem;
import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.hzlib.api.items.InternalItems;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Centralizes item registration for Reboot variant robots (Forge).
 * <p>
 * <b>Architecture:</b> Registry-driven map replaces 8 named static fields.
 * Mirrors LegacyItems Forge — only modid and entity source differ.
 */
public class RebootItems extends InternalItems {

    // -- State --

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, Reboot.MODID);

    private static final Map<RobotVariant, RegistryObject<Item>> spawnItems = new HashMap<>();

    // -- Static Items --

    public static final RegistryObject<Item> ROBOT_CORE =
        ITEMS.register(LovelyConstant.ROBOT_CORE,
            () -> new LovelyCoreItem(new Item.Properties().rarity(Rarity.UNCOMMON).fireResistant().stacksTo(1)));

    // -- Registration --

    public static void register(IEventBus eventBus) {
        registerAll();
        ITEMS.register(eventBus);
        Reboot.LOGGER.info("Registering Items: " + Reboot.MODID);
    } // register()

    private static void registerAll() {
        for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
            RobotVariant variant = def.getVariant();
            RegistryObject<Item> item = registerSpawnItem(
                def.getSpawnItemKey(),
                () -> RebootEntities.getEntityType(variant).get(),
                Rarity.RARE, 1);
            spawnItems.put(variant, item);
        }
    } // registerAll()

    // -- Access --

    public static RegistryObject<Item> getSpawnItem(RobotVariant variant) {
        return Objects.requireNonNull(spawnItems.get(variant),
            "No spawn item registered for Reboot variant: " + variant);
    } // getSpawnItem()

    // -- Client --

    public static void registerModel(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            for (RobotEntityDefinition def : RobotDefinitionRegistry.getForMod(ModTarget.REBOOT)) {
                registerModel(getSpawnItem(def.getVariant()).get(),
                    LovelyConstant.ITEM_TAG_VARIANT, LovelyConstant.STAT_COLOR);
            }
        });
    } // registerModel()

    // -- Helpers --

    private static RegistryObject<Item> registerSpawnItem(String name,
            Supplier<EntityType<? extends Mob>> entity, Rarity rarity, int stack) {
        return ITEMS.register(name,
            () -> new LovelySpawnItem(entity,
                new Item.Properties().rarity(rarity).fireResistant().stacksTo(stack)));
    } // registerSpawnItem()

} // Class: RebootItems
