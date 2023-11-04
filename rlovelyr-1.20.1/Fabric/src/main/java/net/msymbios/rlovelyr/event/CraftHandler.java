package net.msymbios.rlovelyr.event;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

public class CraftHandler implements ItemCraftCallback {

    @Override
    public ActionResult onCraft(PlayerEntity player, ItemStack crafted, RecipeInputInventory matrix) {

        // PASS NBT DATA
        if (crafted.getItem() instanceof SpawnItem) {
            for (int i = 0; i < matrix.size(); i++) {
                ItemStack ingredient = matrix.getStack(i);
                if (ingredient.getItem() instanceof RobotCoreItem) {
                    if (ingredient.hasNbt()) {
                        crafted.setNbt(ingredient.getNbt().copy());
                    }
                    break;
                }
            }
        }

        return ActionResult.PASS;
    } // Class onCraft

} // Class CraftHandler