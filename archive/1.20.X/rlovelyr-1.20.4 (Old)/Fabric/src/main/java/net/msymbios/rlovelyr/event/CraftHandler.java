package net.msymbios.rlovelyr.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.msymbios.rlovelyr.config.LovelyRobotID;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.Objects;

public class CraftHandler implements ItemCraftCallback {

    // -- Method --

    /**
     * This method is called when a player crafts an item.
     * It checks for specific ingredients in the crafting matrix and modifies the crafted item accordingly.
     *
     * @param player the player who crafted the item
     * @param crafted the item that was crafted
     * @param matrix the crafting matrix containing the ingredients
     * @return the result of the crafting action
     */
    @Override
    public ActionResult onCraft(PlayerEntity player, ItemStack crafted, RecipeInputInventory matrix) {

        // Check if the crafted item is a SpawnItem
        if (crafted.getItem() instanceof SpawnItem) {
            ItemStack spawn = null;
            ItemStack dye = null;

            // Iterate through the crafting matrix to find ingredients
            for (int i = 0; i < matrix.size(); i++) {
                ItemStack ingredient = matrix.getStack(i);

                // Pass NBT data from RobotCoreItem
                if (ingredient.getItem() instanceof RobotCoreItem) {
                    if (ingredient.hasNbt()) crafted.setNbt(ingredient.getNbt().copy());
                    break;
                }

                // Check for SpawnItem and DyeItem ingredients
                if (ingredient.getItem() instanceof SpawnItem) spawn = ingredient;
                if (ingredient.getItem() instanceof DyeItem) dye = ingredient;
            }

            // Modify the crafted item based on the SpawnItem and DyeItem ingredients
            if(spawn != null && dye != null) {
                NbtCompound data = spawn.getNbt();
                if(dye.getItem() == Items.WHITE_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.WHITE.getId());
                if(dye.getItem() == Items.ORANGE_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.ORANGE.getId());
                if(dye.getItem() == Items.MAGENTA_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.MAGENTA.getId());
                if(dye.getItem() == Items.LIGHT_BLUE_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.LIGHT_BLUE.getId());
                if(dye.getItem() == Items.YELLOW_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.YELLOW.getId());
                if(dye.getItem() == Items.LIME_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.LIME.getId());
                if(dye.getItem() == Items.PINK_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.PINK.getId());
                if(dye.getItem() == Items.GRAY_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.GRAY.getId());
                if(dye.getItem() == Items.LIGHT_GRAY_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.LIGHT_GRAY.getId());
                if(dye.getItem() == Items.CYAN_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.CYAN.getId());
                if(dye.getItem() == Items.PURPLE_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.PURPLE.getId());
                if(dye.getItem() == Items.BLUE_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.BLUE.getId());
                if(dye.getItem() == Items.BROWN_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.BROWN.getId());
                if(dye.getItem() == Items.GREEN_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.GREEN.getId());
                if(dye.getItem() == Items.RED_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.RED.getId());
                if(dye.getItem() == Items.BLACK_DYE) Objects.requireNonNull(data).putInt(LovelyRobotID.STAT_COLOR, EntityTexture.BLACK.getId());
                crafted.setNbt(data);
            }
        }
        return ActionResult.PASS;
    } // Class onCraft

} // Class CraftHandler