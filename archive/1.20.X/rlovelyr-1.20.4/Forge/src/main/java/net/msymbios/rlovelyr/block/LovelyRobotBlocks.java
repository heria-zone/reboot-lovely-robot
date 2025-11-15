package net.msymbios.rlovelyr.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.rlovelyr.LovelyRobot;

public class LovelyRobotBlocks {

    // -- Variables --

    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, LovelyRobot.MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)));

    // -- Methods --

    public static void register (IEventBus event) {
        LovelyRobot.LOGGER.info("Registering Blocks: " + LovelyRobot.MODID);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(event);
    } // register ()

} // Class LovelyRobotBlocks