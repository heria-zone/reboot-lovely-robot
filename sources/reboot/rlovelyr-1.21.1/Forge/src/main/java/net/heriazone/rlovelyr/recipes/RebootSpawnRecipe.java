package net.heriazone.rlovelyr.recipes;

import net.heriazone.rlovelyr.source.RebootRecipes;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * Forge-specific implementation of spawn egg crafting recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnRecipe to inherit common recipe logic
 * while providing Forge-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Forge's DeferredRegister pattern with .get()
 * method to access registered serializer.
 */
public class RebootSpawnRecipe extends BaseLovelySpawnRecipe {

    // -- Constructor --

    public RebootSpawnRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, Item robotCoreItem) {
        super(group, category, pattern, result, robotCoreItem);
    } // Constructor: RebootSpawnRecipe()

    public RebootSpawnRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, Item robotCoreItem) {
        super(group, category, pattern, result, showNotification, robotCoreItem);
    } // Constructor: RebootSpawnRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RebootRecipes.SPAWN_EGG_CRAFTING.get();
    } // getSerializer()

} // Class: RebootSpawnRecipe