package net.heriazone.lovelylib.api.recipes;

import net.heriazone.lovelylib.common.shared.LovelyConstant;
import net.heriazone.lovelylib.common.shared.LovelyIdentifier;
import net.heriazone.lovelylib.hzlib.api.recipes.interfaces.INbtTransferStrategy;
import net.heriazone.lovelylib.api.recipes.modifiers.DyeColorModifier;
import net.heriazone.lovelylib.hzlib.api.recipes.strategies.AdditiveNbtMergeStrategy;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.List;

/**
 * Base class for shapeless recipes that dye spawn eggs while preserving custom data.
 * <p>
 * <b>Architecture:</b> Extends ShapelessRecipe to preserve vanilla crafting behavior
 * while adding data transfer logic using AdditiveNbtMergeStrategy. Provides common
 * implementation that loader-specific recipes can extend.
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
 * <b>Design Decision:</b> Abstract getSerializer() method allows loader-specific
 * implementations to provide their own serializer while sharing common logic.
 */
public abstract class BaseLovelySpawnDyeRecipe extends ShapelessRecipe {

    // -- Variables --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public BaseLovelySpawnDyeRecipe(String group, CraftingBookCategory category, ItemStack result,
                                    net.minecraft.core.NonNullList<Ingredient> ingredients) {
        super(group, category, result, ingredients);
        this.strategy = new AdditiveNbtMergeStrategy(result.getItem(), List.of(new DyeColorModifier(LovelyConstant.STAT_COLOR)));
    } // Constructor: BaseLovelySpawnDyeRecipe()

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
    } // assemble()

    /**
     * Returns the recipe serializer for this recipe type.
     * <p>
     * <b>Loader-Specific:</b> Each loader implementation must provide its own
     * serializer instance to handle registration differences.
     *
     * @return the recipe serializer
     */
    @Override
    public abstract RecipeSerializer<?> getSerializer();

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    } // getType()

} // Class: BaseLovelySpawnDyeRecipe