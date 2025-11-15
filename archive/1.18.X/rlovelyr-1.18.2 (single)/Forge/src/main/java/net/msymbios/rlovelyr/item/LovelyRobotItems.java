package net.msymbios.rlovelyr.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.function.Supplier;

public class LovelyRobotItems {

    // -- Variables --
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, LovelyRobot.MODID);
    public static final RegistryObject<Item> ROBOT_CORE = ITEMS.register(LovelyRobotID.ROBOT_CORE, () -> new RobotCoreItem(new Item.Properties().tab(LovelyRobotItemsGroup.LOVELY_ROBOT).rarity(Rarity.UNCOMMON).fireResistant()));
    public static final RegistryObject<Item> BUNNY_SPAWN = register(LovelyRobotID.BUNNY_SPAWN, LovelyRobotEntities.BUNNY, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> BUNNY2_SPAWN = register(LovelyRobotID.BUNNY2_SPAWN, LovelyRobotEntities.BUNNY2, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> DRAGON_SPAWN = register(LovelyRobotID.DRAGON_SPAWN, LovelyRobotEntities.DRAGON, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> HONEY_SPAWN = register(LovelyRobotID.HONEY_SPAWN, LovelyRobotEntities.HONEY, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> KITSUNE_SPAWN = register(LovelyRobotID.KITSUNE_SPAWN, LovelyRobotEntities.KITSUNE, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> NEKO_SPAWN = register(LovelyRobotID.NEKO_SPAWN, LovelyRobotEntities.NEKO, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);
    public static final RegistryObject<Item> VANILLA_SPAWN = register(LovelyRobotID.VANILLA_SPAWN, LovelyRobotEntities.VANILLA, LovelyRobotItemsGroup.LOVELY_ROBOT, Rarity.RARE, 1);

    // -- Methods --

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    } // register ()

    public static RegistryObject<Item> register (String name, Supplier<? extends EntityType<? extends Mob>> mob, CreativeModeTab tab, Rarity rarity, int stack) {
        return ITEMS.register(name, () -> new SpawnItem(mob, new Item.Properties().tab(tab).rarity(rarity).stacksTo(stack).fireResistant()));
    } // register ()

    public static void registerModel(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            registerModel(LovelyRobotItems.BUNNY_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.BUNNY2_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.DRAGON_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.HONEY_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.KITSUNE_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.NEKO_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
            registerModel(LovelyRobotItems.VANILLA_SPAWN, LovelyRobotID.ITEM_TAG_VARIANT, LovelyRobotID.STAT_COLOR);
        });
    } // registerModel ()

    private static void registerModel(RegistryObject<Item> item, String name, String key) {
        ItemProperties.register(item.get(), LovelyRobotID.getId(name), (stack, world, entity, id) -> stack.getTag() != null ? stack.getTag().getInt(key) : 16);
    } // registerModel ()

} // Class LovelyRobotItems