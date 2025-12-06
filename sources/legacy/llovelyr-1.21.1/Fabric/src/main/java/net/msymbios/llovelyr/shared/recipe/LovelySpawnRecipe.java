package net.msymbios.llovelyr.shared.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.lib.recipes.strategies.FullNbtCopyStrategy;
import net.msymbios.llovelyr.source.LovelyRecipes;

/**
 * Shaped recipe for crafting spawn eggs from robot cores with full data transfer.
 * <p>
 * <b>Architecture:</b> Extends ShapedRecipe to preserve vanilla crafting behavior
 * while adding data transfer logic using FullNbtCopyStrategy.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses Data Components instead of NBT.
 * Strategy handles conversion between CustomData component and intermediate CompoundTag.
 * <p>
 * <b>Data Behavior:</b> Copies ALL custom data from robot core to spawn egg,
 * transferring name, owner, level, color, protections, and all other data.
 * <p>
 * <b>Preview Support:</b> Overrides assemble() to show custom data in crafting result
 * slot before item is taken, matching final crafted item appearance.
 * <p>
 * <b>Design Decision:</b> Uses supplier pattern for serializer to break cyclical
 * dependency with registry class, allowing recipe to exist in Common module.
 */
public class LovelySpawnRecipe extends ShapedRecipe {

    // -- Variables --

    private final INbtTransferStrategy strategy;
    private final ShapedRecipePattern recipePattern;

    // -- Constructor --

    public LovelySpawnRecipe(String group, CraftingBookCategory category,
                             ShapedRecipePattern pattern, ItemStack result,
                             Item robotCoreItem) {
        super(group, category, pattern, result);
        this.recipePattern = pattern;
        this.strategy = new FullNbtCopyStrategy(robotCoreItem);
    } // Constructor: LovelySpawnRecipe ()
    
    public LovelySpawnRecipe(String group, CraftingBookCategory category,
                             ShapedRecipePattern pattern, ItemStack result, boolean showNotification,
                             Item robotCoreItem) {
        super(group, category, pattern, result, showNotification);
        this.recipePattern = pattern;
        this.strategy = new FullNbtCopyStrategy(robotCoreItem);
    } // Constructor: LovelySpawnRecipe ()

    // -- Public Methods --

    /**
     * Provides access to the recipe pattern for serialization.
     * <p>
     * <b>Usage:</b> Required by LovelySpawnRecipeSerializer for codec-based
     * serialization in Minecraft 1.21.1.
     *
     * @return the shaped recipe pattern
     */
    public ShapedRecipePattern pattern() {
        return this.recipePattern;
    } // pattern()

    // -- Inherited Methods --

    /**
     * Crafts result with custom data transferred from robot core ingredient.
     * <p>
     * <b>Preview Support:</b> Called for both preview (ghost item) and final craft,
     * ensuring consistent appearance in result slot.
     * <p>
     * <b>Data Transfer:</b> Uses full copy strategy to transfer all custom data from
     * robot core to spawn egg. Preserves owner, name, level, color, and protections.
     * Strategy handles Data Component conversion internally.
     *
     * @param inventory crafting grid inventory
     * @param registryAccess registry access
     * @return spawn egg with transferred custom data
     */
    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryAccess) {
        ItemStack result = super.assemble(inventory, registryAccess);
        ItemStack transferred = strategy.transferNbt(inventory, result);
        return transferred;
    } // assemble ()

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING;
    } // getSerializer ()

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    } // getType ()

} // Class: LovelySpawnRecipe
