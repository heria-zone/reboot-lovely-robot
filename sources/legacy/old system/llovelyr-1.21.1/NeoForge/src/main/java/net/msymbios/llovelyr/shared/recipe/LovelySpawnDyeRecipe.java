package net.msymbios.llovelyr.shared.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.msymbios.llovelyr.lib.recipes.base.BaseLovelySpawnDyeRecipe;
import net.msymbios.llovelyr.source.LovelyRecipes;

/**
 * NeoForge-specific implementation of spawn egg dyeing recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnDyeRecipe to inherit common recipe logic
 * while providing NeoForge-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses NeoForge's DeferredRegister pattern with .get()
 * method to access registered serializer.
 */
public class LovelySpawnDyeRecipe extends BaseLovelySpawnDyeRecipe {

    // -- Constructor --

    public LovelySpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result,
                                net.minecraft.core.NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
    } // Constructor: LovelySpawnDyeRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE.get();
    } // getSerializer()

} // Class: LovelySpawnDyeRecipe
