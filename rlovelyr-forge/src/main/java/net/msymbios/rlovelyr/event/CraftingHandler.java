package net.msymbios.rlovelyr.event;

import net.minecraft.item.DyeItem;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.ItemStack;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.enums.EntityTexture;
import net.msymbios.rlovelyr.item.custom.RobotCoreItem;
import net.msymbios.rlovelyr.item.custom.SpawnItem;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = LovelyRobot.MODID)
public class CraftingHandler {

    @SubscribeEvent
    public void onCraftingTableChange(CraftingTableChangeEvent event) {
        RecipeManager recipeManager = event.getWorld().getRecipeManager();
        IRecipe<?> recipe = recipeManager.getRecipeFor(IRecipeType.CRAFTING, event.getInventory(), event.getWorld()).orElse(null);

        if (recipe != null) {
            ItemStack craftedItem = recipe.getResultItem();

            if (craftedItem.getItem() instanceof SpawnItem) {
                // Fire your custom event here
                ItemStack dyeItem = null;
                ItemStack spawnItem = null;

                for (int i = 0; i < recipe.getIngredients().size(); i++) {
                    Ingredient ingredient = recipe.getIngredients().get(i);
                    ItemStack core = ingredient.getItems()[0];

                    // PASS NBT DATA
                    if (core.getItem() instanceof RobotCoreItem) {
                        if (core.hasTag()) craftedItem.setTag(core.getTag().copy());
                        break;
                    }

                    if (core.getItem() instanceof SpawnItem) spawnItem = core;
                    if (core.getItem() instanceof DyeItem) dyeItem = core;
                }

                if(spawnItem != null && dyeItem != null) {
                    CompoundNBT data = spawnItem.getTag();
                    if(dyeItem.getItem() == Items.WHITE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.WHITE.getId());
                    if(dyeItem.getItem() == Items.ORANGE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.ORANGE.getId());
                    if(dyeItem.getItem() == Items.MAGENTA_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.MAGENTA.getId());
                    if(dyeItem.getItem() == Items.LIGHT_BLUE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIGHT_BLUE.getId());
                    if(dyeItem.getItem() == Items.YELLOW_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.YELLOW.getId());
                    if(dyeItem.getItem() == Items.LIME_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIME.getId());
                    if(dyeItem.getItem() == Items.PINK_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.PINK.getId());
                    if(dyeItem.getItem() == Items.GRAY_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.GRAY.getId());
                    if(dyeItem.getItem() == Items.LIGHT_GRAY_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.LIGHT_GRAY.getId());
                    if(dyeItem.getItem() == Items.CYAN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.CYAN.getId());
                    if(dyeItem.getItem() == Items.PURPLE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.PURPLE.getId());
                    if(dyeItem.getItem() == Items.BLUE_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BLUE.getId());
                    if(dyeItem.getItem() == Items.BROWN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BROWN.getId());
                    if(dyeItem.getItem() == Items.GREEN_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.GREEN.getId());
                    if(dyeItem.getItem() == Items.RED_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.RED.getId());
                    if(dyeItem.getItem() == Items.BLACK_DYE) Objects.requireNonNull(data).putInt("color", EntityTexture.BLACK.getId());
                    craftedItem.setTag(data);
                }
            }
        }

        System.out.println("Item crafted!");
    } // onCraftingTableChange ()

} // Class CraftingHandler