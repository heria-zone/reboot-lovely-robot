package net.msymbios.rlovelyr.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.Objects;

public class CraftHandler implements ItemCraftCallback {

    @Override
    public ActionResult onCraft(PlayerEntity player, ItemStack crafted, CraftingInventory matrix) {
        if (crafted.getItem() instanceof SpawnItem) {
            ItemStack spawn = null;
            ItemStack dye = null;

            for (int i = 0; i < matrix.size(); i++) {
                ItemStack ingredient = matrix.getStack(i);

                // PASS NBT DATA
                if (ingredient.getItem() instanceof RobotCoreItem) {
                    if (ingredient.hasNbt()) crafted.setNbt(ingredient.getNbt().copy());
                    break;
                }

                if (ingredient.getItem() instanceof SpawnItem) spawn = ingredient;
                if (ingredient.getItem() instanceof DyeItem) dye = ingredient;
            }

            if(spawn != null && dye != null) {
                NbtCompound data = spawn.getNbt();
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
                crafted.setNbt(data);
            }
        }
        return ActionResult.PASS;
    } // Class onCraft

} // Class CraftHandler