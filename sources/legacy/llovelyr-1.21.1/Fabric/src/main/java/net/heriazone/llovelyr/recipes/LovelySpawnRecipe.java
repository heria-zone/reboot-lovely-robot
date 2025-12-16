package net.heriazone.llovelyr.recipes;

import net.heriazone.llovelyr.source.LovelyRecipes;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * Fabric-specific implementation of spawn egg crafting recipe.
 * <p>
 * <b>Architecture:</b> Extends BaseLovelySpawnRecipe to inherit common recipe logic
 * while providing Fabric-specific serializer registration.
 * <p>
 * <b>Loader Integration:</b> Uses Fabric's direct serializer reference pattern
 * without DeferredRegister wrapper.
 */
public class LovelySpawnRecipe extends BaseLovelySpawnRecipe {

    // -- Constructor --

    public LovelySpawnRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, Item robotCoreItem) {
        super(group, category, pattern, result, robotCoreItem);
    } // Constructor: LovelySpawnRecipe()

    public LovelySpawnRecipe(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result, boolean showNotification, Item robotCoreItem) {
        super(group, category, pattern, result, showNotification, robotCoreItem);
    } // Constructor: LovelySpawnRecipe()

    // -- Inherited Methods --

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    } // getSerializer()

} // Class: LovelySpawnRecipe
