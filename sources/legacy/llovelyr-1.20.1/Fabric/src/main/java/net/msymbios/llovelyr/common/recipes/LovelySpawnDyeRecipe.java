package net.msymbios.llovelyr.source.recipes.custom;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapelessRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.source.recipes.LovelyRecipes;
import net.msymbios.llovelyr.source.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.source.recipes.internal.modifiers.DyeColorModifier;
import net.msymbios.llovelyr.source.recipes.internal.strategies.AdditiveNbtMergeStrategy;

import java.util.List;

/**
 * Shapeless recipe for dyeing spawn eggs while preserving NBT data.
 * <p>
 * <b>Architecture:</b> Extends ShapelessRecipe to preserve vanilla crafting behavior
 * while adding NBT transfer logic using AdditiveNbtMergeStrategy.
 * <p>
 * <b>NBT Behavior:</b> Preserves all existing NBT data from spawn egg and only
 * modifies the color key based on dye ingredient.
 * <p>
 * <b>Preview Support:</b> Overrides craft() to show color change in crafting
 * result slot before item is taken.
 */
public class LovelySpawnDyeRecipe extends ShapelessRecipe {

    // -- Fields --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public LovelySpawnDyeRecipe(Identifier id, String group, CraftingRecipeCategory category, ItemStack result, DefaultedList<Ingredient> ingredients) {
        super(id, group, category, result, ingredients);
        
        this.strategy = new AdditiveNbtMergeStrategy(
            result.getItem(),
            List.of(new DyeColorModifier(LovelyIdentifier.STAT_COLOR))
        );
    } // LovelySpawnDyeRecipe

    // -- Inherited Methods --

    /**
     * Crafts result with NBT data preserved and color modified.
     * <p>
     * <b>Preview Support:</b> Called for both preview (ghost item) and final craft,
     * ensuring consistent appearance in result slot.
     * <p>
     * <b>NBT Transfer:</b> Uses additive merge strategy to preserve all existing
     * NBT while modifying only the color based on dye ingredient.
     *
     * @param inventory crafting grid inventory
     * @param registryManager dynamic registry manager
     * @return spawn egg with preserved NBT and updated color
     */
    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack result = super.craft(inventory, registryManager);
        return strategy.transferNbt(inventory, result);
    } // craft

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE;
    } // getSerializer

} // Class: LovelySpawnDyeRecipe
