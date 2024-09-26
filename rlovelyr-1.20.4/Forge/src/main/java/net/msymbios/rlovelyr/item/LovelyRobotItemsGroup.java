package net.msymbios.rlovelyr.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.config.LovelyRobotID;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.ROBOT_CORE;
import static net.msymbios.rlovelyr.item.LovelyRobotItems.ROBOT_CORE_ALDARIAN;

public class LovelyRobotItemsGroup {

    // -- Variables --

    // Create a Deferred Register to hold CreativeModeTabs which will all be registered
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LovelyRobot.MODID);

    // -- TABS
    public static final RegistryObject<CreativeModeTab> DEFAULT_TAB = CREATIVE_TABS.register(LovelyRobotID.DEFAULT_TAB, () -> registerCreativeTab(LovelyRobotID.DEFAULT_TAB, ROBOT_CORE));
    public static final RegistryObject<CreativeModeTab> ALDARIAN_TECH_TAB = CREATIVE_TABS.register(LovelyRobotID.ALDARIAN_TECH_TAB, () -> registerCreativeTab(LovelyRobotID.ALDARIAN_TECH_TAB, ROBOT_CORE_ALDARIAN));

    // -- KEYS
    public static final ResourceKey<CreativeModeTab> DEFAULT_KEY = createKey(LovelyRobotID.getId(LovelyRobotID.DEFAULT_TAB));
    public static final ResourceKey<CreativeModeTab> ALDARIAN_TECH_KEY = createKey(LovelyRobotID.getId(LovelyRobotID.ALDARIAN_TECH_TAB));

    // -- Methods --

    protected static CreativeModeTab registerCreativeTab(String title, RegistryObject<Item> icon) {
        return CreativeModeTab.builder()
                .title(LovelyRobotID.getTabTranslation(title))
                .icon(() -> icon.get().getDefaultInstance()).build();
    } // registerCreativeTab()

    @SafeVarargs
    protected static CreativeModeTab registerCreativeTab(String title, RegistryObject<Item> icon, ResourceKey<CreativeModeTab> tabBefore, RegistryObject<Item>... items) {
        return CreativeModeTab.builder()
                .title(LovelyRobotID.getTabTranslation(title))
                .withTabsBefore(tabBefore)
                .icon(() -> icon.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    for (RegistryObject<Item> item : items)
                        output.accept(item.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                }).build();
    } // registerCreativeTab()

    private static ResourceKey<CreativeModeTab> createKey(ResourceLocation location) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, location);
    } // createKey()


    public static void register(IEventBus event) {
        LovelyRobot.LOGGER.info("Registering Creative Tabs for: " + LovelyRobot.MODID);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_TABS.register(event);
    } // register ()

} // Class LovelyRobotItemsGroup