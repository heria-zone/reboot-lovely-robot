package net.msymbios.llovelyr.source.recipes;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.msymbios.llovelyr.source.LovelyItems;
import net.msymbios.llovelyr.source.LovelyRecipes;
import net.msymbios.llovelyr.lib.recipes.interfaces.INbtTransferStrategy;
import net.msymbios.llovelyr.lib.recipes.strategies.FullNbtCopyStrategy;

/**
 * Shaped recipe for crafting spawn eggs from robot cores with full NBT transfer.
 * <p>
 * <b>Architecture:</b> Extends ShapedRecipe to preserve vanilla crafting behavior
 * while adding NBT transfer logic using FullNbtCopyStrategy.
 * <p>
 * <b>NBT Behavior:</b> Copies ALL NBT data from robot core to spawn egg,
 * transferring name, owner, level, color, protections, and all other data.
 * <p>
 * <b>Preview Support:</b> Overrides assemble() to show NBT data in crafting result
 * slot before item is taken, matching final crafted item appearance.
 */
public class LovelySpawnRecipe extends ShapedRecipe {

    // -- Fields --

    private final INbtTransferStrategy strategy;

    // -- Constructor --

    public LovelySpawnRecipe(ResourceLocation id, String group, CraftingBookCategory category,
                             int width, int height, net.minecraft.core.NonNullList<Ingredient> ingredients,
                             ItemStack result) {
        super(id, group, category, width, height, ingredients, result);
        
        this.strategy = new FullNbtCopyStrategy(LovelyItems.ROBOT_CORE.get());
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
     * @param registryAccess registry access
     * @return spawn egg with transferred NBT data
     */
    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(inventory, registryAccess);
        return strategy.transferNbt(inventory, result);
    } // assemble

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LovelyRecipes.SPAWN_EGG_CRAFTING.get();
    } // getSerializer

} // Class: LovelySpawnRecipe
