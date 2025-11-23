package net.msymbios.llovelyr.source.recipes;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
import net.msymbios.llovelyr.source.items.LovelyItems;

/**
 * Custom crafting recipe for spawn eggs that transfers NBT from robot core.
 * <p>
 * <b>Architecture:</b> Extends ShapedRecipe to preserve vanilla crafting behavior
 * while adding NBT transfer logic in craft() method for preview support.
 * <p>
 * <b>Preview Support:</b> Overrides craft() to show NBT data in crafting result
 * slot before item is taken, matching final crafted item appearance.
 */
public class SpawnEggCraftingRecipe extends ShapedRecipe {

    // -- Constructor --

    public SpawnEggCraftingRecipe(Identifier id, String group, CraftingRecipeCategory category, int width, int height, DefaultedList<Ingredient> ingredients, ItemStack result) {
        super(id, group, category, width, height, ingredients, result);
    } // SpawnEggCraftingRecipe

    // -- Inherited Methods --

    /**
     * Crafts result with NBT data transferred from robot core ingredient.
     * <p>
     * <b>Preview Support:</b> Called for both preview (ghost item) and final craft,
     * ensuring consistent appearance in result slot.
     * <p>
     * <b>NBT Transfer:</b> Searches crafting grid for robot core, copies its NBT
     * to spawn egg result. Preserves owner, name, level, color, and protections.
     *
     * @param inventory crafting grid inventory
     * @param registryManager dynamic registry manager
     * @return spawn egg with transferred NBT data
     */
    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        
        // Find robot core in crafting grid
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.getItem() == LovelyItems.ROBOT_CORE) {
                NbtCompound sourceNbt = stack.getNbt();
                if (sourceNbt != null) {
                    // Transfer NBT to result
                    NbtCompound resultNbt = result.getOrCreateNbt();
                    
                    // Copy all relevant data
                    if (sourceNbt.contains(LovelyIdentifier.STAT_CUSTOM_NAME)) {
                        resultNbt.putString(LovelyIdentifier.STAT_CUSTOM_NAME, 
                            sourceNbt.getString(LovelyIdentifier.STAT_CUSTOM_NAME));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_OWNER)) {
                        resultNbt.putString(LovelyIdentifier.STAT_OWNER, 
                            sourceNbt.getString(LovelyIdentifier.STAT_OWNER));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_TYPE)) {
                        resultNbt.putString(LovelyIdentifier.STAT_TYPE, 
                            sourceNbt.getString(LovelyIdentifier.STAT_TYPE));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_COLOR)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_COLOR, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_COLOR));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_LEVEL)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_LEVEL, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_LEVEL));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_EXP)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_EXP, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_EXP));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_MAX_LEVEL)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_MAX_LEVEL, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_MAX_LEVEL));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_FIRE_PROTECTION)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_FIRE_PROTECTION, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_FIRE_PROTECTION));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_FALL_PROTECTION)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_FALL_PROTECTION, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_FALL_PROTECTION));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_BLAST_PROTECTION)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_BLAST_PROTECTION, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_BLAST_PROTECTION));
                    }
                    if (sourceNbt.contains(LovelyIdentifier.STAT_PROJECTILE_PROTECTION)) {
                        resultNbt.putInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION, 
                            sourceNbt.getInt(LovelyIdentifier.STAT_PROJECTILE_PROTECTION));
                    }
                }
                break;
            }
        }
        
        return result;
    } // craft

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    } // getSerializer

} // Class: SpawnEggCraftingRecipe
