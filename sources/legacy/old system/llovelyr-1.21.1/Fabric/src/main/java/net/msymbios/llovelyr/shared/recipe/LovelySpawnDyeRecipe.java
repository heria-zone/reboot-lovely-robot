package net.msymbios.llovelyr.shared.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.msymbios.llovelyr.lib.recipes.base.BaseLovelySpawnDyeRecipe;
import net.msymbios.llovelyr.source.LovelyRecipes;

/**
 * Fabric-specific implementation of spawn egg dyeing recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnDyeRecipe to inherit common recipe logic
 * while providing Fabric-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Fabric's direct serializer reference pattern
 * without DeferredRegister wrapper.
 */
public class LovelySpawnDyeRecipe extends BaseLovelySpawnDyeRecipe {

    // -- Constructor --

    public LovelySpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    } // Constructor: LovelySpawnDyeRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE;
    } // getSerializer()

} // Class: LovelySpawnDyeRecipe
