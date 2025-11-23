package net.msymbios.llovelyr.source.recipes.custom;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.msymbios.llovelyr.source.items.LovelyItems;
import net.msymbios.llovelyr.source.recipes.LovelyRecipes;
import net.msymbios.llovelyr.source.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.source.recipes.internal.strategies.FullNbtCopyStrategy;

/**
 * Shaped recipe for crafting spawn eggs from robot cores with full NBT transfer.
 * <p>
 * <b>Architecture:</b> Extends ShapedRecipe to preserve vanilla crafting behavior
 * while adding NBT transfer logic using FullNbtCopyStrategy.
 * <p>
 * <b>NBT Behavior:</b> Copies ALL NBT data from robot core to spawn egg,
 * transferring name, owner, level, color, protections, and all other data.
 * <p>
 * <b>Preview Support:</b> Overrides craft() to show NBT data in crafting result
 * slot before item is taken, matching final crafted item appearance.
 */
public class LovelySpawnRecipe extends ShapedRecipe {

    // -- Fields --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public LovelySpawnRecipe(Identifier id, String group, CraftingRecipeCategory category,
                             int width, int height, DefaultedList<Ingredient> ingredients,
                             ItemStack result) {
        super(id, group, category, width, height, ingredients, result);
        
        this.strategy = new FullNbtCopyStrategy(LovelyItems.ROBOT_CORE);
    } // LovelySpawnRecipe

    // -- Inherited Methods --

    /**
     * Crafts result with NBT data transferred from robot core ingredient.
     * <p>
     * <b>Preview Support:</b> Called for both preview (ghost item) and final craft,
     * ensuring consistent appearance in result slot.
     * <p>
     * <b>NBT Transfer:</b> Uses full copy strategy to transfer all NBT from robot
     * core to spawn egg. Preserves owner, name, level, color, and protections.
     *
     * @param inventory crafting grid inventory
     * @param registryManager dynamic registry manager
     * @return spawn egg with transferred NBT data
     */
    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        return strategy.transferNbt(inventory, result);
    } // craft

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    } // getSerializer

} // Class: LovelySpawnRecipe
