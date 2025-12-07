package net.msymbios.llovelyr.shared.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.lib.recipes.modifiers.DyeColorModifier;
import net.msymbios.llovelyr.lib.recipes.strategies.AdditiveNbtMergeStrategy;
import net.msymbios.llovelyr.source.LovelyRecipes;

import java.util.List;

/**
 * Shapeless recipe for dyeing spawn eggs while preserving custom data.
 * <p>
 * <b>Architecture:</b> Extends ShapelessRecipe to preserve vanilla crafting behavior
 * while adding data transfer logic using AdditiveNbtMergeStrategy.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses Data Components instead of NBT.
 * Strategy handles conversion between CustomData component and intermediate CompoundTag.
 * <p>
 * <b>Data Behavior:</b> Preserves all existing custom data from spawn egg and only
 * modifies the color key based on dye ingredient.
 * <p>
 * <b>Preview Support:</b> Overrides assemble() to show color change in crafting
 * result slot before item is taken.
 * <p>
 * <b>Design Decision:</b> Uses supplier pattern for serializer to break cyclical
 * dependency with registry class, allowing recipe to exist in Common module.
 */
public class LovelySpawnDyeRecipe extends ShapelessRecipe {

    // -- Variables --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public LovelySpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result,
                                net.minecraft.core.NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
        this.strategy = new AdditiveNbtMergeStrategy(result.getItem(), List.of(new DyeColorModifier(LovelyIdentifier.STAT_COLOR)));
    } // Constructor: LovelySpawnDyeRecipe ()

    // -- Inherited Methods --

    /**
     * Crafts result with custom data preserved and color modified.
     * <p>
     * <b>Preview Support:</b> Called for both preview (ghost item) and final craft,
     * ensuring consistent appearance in result slot.
     * <p>
     * <b>Data Transfer:</b> Uses additive merge strategy to preserve all existing
     * custom data while modifying only the color based on dye ingredient. Strategy
     * handles Data Component conversion internally.
     *
     * @param inventory crafting grid inventory
     * @param registryAccess registry access
     * @return spawn egg with preserved custom data and updated color
     */
    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
        ItemStack result = super.assemble(inventory, registryAccess);
        return strategy.transferNbt(inventory, result);
    } // assemble ()

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_DYE.get();
    } // getSerializer ()

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    } // getType ()

} // Class: LovelySpawnDyeRecipe
