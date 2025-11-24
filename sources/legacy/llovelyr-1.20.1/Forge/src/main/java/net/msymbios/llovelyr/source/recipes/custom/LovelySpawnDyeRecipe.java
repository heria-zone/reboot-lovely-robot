package net.msymbios.llovelyr.source.recipes.custom;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;
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
 * <b>Preview Support:</b> Overrides assemble() to show color change in crafting
 * result slot before item is taken.
 */
public class LovelySpawnDyeRecipe extends ShapelessRecipe {

    // -- Fields --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public LovelySpawnDyeRecipe(ResourceLocation id, String group, CraftingBookCategory category, 
                                ItemStack result, net.minecraft.core.NonNullList<Ingredient> ingredients) {
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
     * @param registryAccess registry access
     * @return spawn egg with preserved NBT and updated color
     */
    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(inventory, registryAccess);
        return strategy.transferNbt(inventory, result);
    } // assemble

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE.get();
    } // getSerializer

} // Class: LovelySpawnDyeRecipe
