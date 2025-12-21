package net.heriazone.lovely_robot.recipes;

import net.heriazone.lovely_robot.source.TributeRecipes;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnDyeRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Forge-specific implementation of spawn egg dyeing recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnDyeRecipe to inherit common recipe logic
 * while providing Forge-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Forge's DeferredRegister pattern with .get()
 * method to access registered serializer.
 */
public class TributeSpawnDyeRecipe extends BaseLovelySpawnDyeRecipe {

    // -- Constructor --

    public TributeSpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    } // Constructor: TributeSpawnDyeRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return TributeRecipes.SPAWN_EGG_DYE.get();
    } // getSerializer()

} // Class: TributeSpawnDyeRecipe
