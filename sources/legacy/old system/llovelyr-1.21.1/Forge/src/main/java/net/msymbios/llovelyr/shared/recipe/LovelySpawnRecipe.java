package net.msymbios.llovelyr.shared.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.msymbios.llovelyr.lib.recipes.base.BaseLovelySpawnRecipe;
import net.msymbios.llovelyr.source.LovelyRecipes;

/**
 * Forge-specific implementation of spawn egg crafting recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnRecipe to inherit common recipe logic
 * while providing Forge-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Forge's DeferredRegister pattern with .get()
 * method to access registered serializer.
 */
public class LovelySpawnRecipe extends BaseLovelySpawnRecipe {

    // -- Constructor --

    public LovelySpawnRecipe(String group, CraftingBookCategory category,
                             ShapedRecipePattern pattern, ItemStack result,
                             Item robotCoreItem) {
        super(group, category, pattern, result, robotCoreItem);
    } // Constructor: LovelySpawnRecipe()
    
    public LovelySpawnRecipe(String group, CraftingBookCategory category,
                             ShapedRecipePattern pattern, ItemStack result, boolean showNotification,
                             Item robotCoreItem) {
        super(group, category, pattern, result, showNotification, robotCoreItem);
    } // Constructor: LovelySpawnRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING.get();
    } // getSerializer()

} // Class: LovelySpawnRecipe
