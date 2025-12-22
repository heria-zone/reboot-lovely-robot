package net.heriazone.rlovelyr.recipes;

import net.heriazone.rlovelyr.source.RebootRecipes;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnDyeRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Fabric-specific implementation of spawn egg dyeing recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnDyeRecipe to inherit common recipe logic
 * while providing Fabric-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Fabric's direct serializer reference pattern
 * without DeferredRegister wrapper.
 */
public class RebootSpawnDyeRecipe extends BaseLovelySpawnDyeRecipe {

    // -- Constructor --

    public RebootSpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    } // Constructor: RebootSpawnDyeRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RebootRecipes.SPAWN_EGG_DYE;
    } // getSerializer()

} // Class: RebootSpawnDyeRecipe
