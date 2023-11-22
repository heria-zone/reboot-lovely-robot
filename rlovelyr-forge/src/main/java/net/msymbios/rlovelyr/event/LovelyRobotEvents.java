package net.msymbios.rlovelyr.event;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.msymbios.rlovelyr.entity.LovelyRobotEntities;
import net.msymbios.rlovelyr.LovelyRobot;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.Objects;

public class LovelyRobotEvents {

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID)
    public static class ForgeEvents  {  } // Class ForgeEvents

    @Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class LovelyRobotEventBusEvents {

        @SubscribeEvent
        public static void onRegisterEntityAttribute(EntityAttributeCreationEvent event) {
            LovelyRobotEntities.registerAttribute(event);
        } // onRegisterEntityAttribute ()

        /*@SubscribeEvent
        public static ActionResultType onCrafting(PlayerEvent.ItemCraftedEvent event) {
            //PlayerEntity player = event.getPlayer();
            ItemStack crafted = event.getCrafting();
            IInventory matrix = event.getInventory();

            if (crafted.getItem() instanceof SpawnItem) {
                ItemStack spawn = null;
                ItemStack dye = null;

                for (int i = 0; i < matrix.getContainerSize(); i++) {
                    ItemStack ingredient = matrix.getItem(i);

                    // PASS NBT DATA
                    if (ingredient.getItem() instanceof RobotCoreItem) {
                        if (ingredient.hasTag()) crafted.setTag(ingredient.getTag().copy());
                        break;
                    }

                    if (ingredient.getItem() instanceof SpawnItem) spawn = ingredient;
                    if (ingredient.getItem() instanceof DyeItem) dye = ingredient;
                }

                if(spawn != null && dye != null) {
                    CompoundNBT data = spawn.getTag();
                    if(dye.getItem() == Items.WHITE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.WHITE.getId());
                    if(dye.getItem() == Items.ORANGE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.ORANGE.getId());
                    if(dye.getItem() == Items.MAGENTA_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.MAGENTA.getId());
                    if(dye.getItem() == Items.LIGHT_BLUE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIGHT_BLUE.getId());
                    if(dye.getItem() == Items.YELLOW_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.YELLOW.getId());
                    if(dye.getItem() == Items.LIME_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIME.getId());
                    if(dye.getItem() == Items.PINK_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.PINK.getId());
                    if(dye.getItem() == Items.GRAY_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.GRAY.getId());
                    if(dye.getItem() == Items.LIGHT_GRAY_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIGHT_GRAY.getId());
                    if(dye.getItem() == Items.CYAN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.CYAN.getId());
                    if(dye.getItem() == Items.PURPLE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.PURPLE.getId());
                    if(dye.getItem() == Items.BLUE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BLUE.getId());
                    if(dye.getItem() == Items.BROWN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BROWN.getId());
                    if(dye.getItem() == Items.GREEN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.GREEN.getId());
                    if(dye.getItem() == Items.RED_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.RED.getId());
                    if(dye.getItem() == Items.BLACK_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BLACK.getId());
                    crafted.setTag(data);
                }
                System.out.println("Robot Girl Crafting!");
            }
            return ActionResultType.PASS;
        } // onCrafting ()*/

    } // Class LovelyRobotEventBusEvents

    /*@Mod.EventBusSubscriber(modid = LovelyRobot.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CraftingEventHandler {
        @SubscribeEvent
        public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
            // Your code here
            System.out.println("Item crafted!");
        } // onItemCrafted ()

    } /// Class CraftingEventHandler*/

} // Class LovelyRobotEvents