package net.msymbios.llovelyr.source.recipes;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.msymbios.llovelyr.source.configs.LovelyIdentifier;

/**
 * Registry for custom recipe types and serializers.
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for spawn egg crafting
 * with NBT transfer support.
 */
public class LovelyRecipes {

    // -- Recipe Serializers --

    public static final RecipeSerializer<SpawnEggCraftingRecipe> SPAWN_EGG_CRAFTING = Registry.register(Registries.RECIPE_SERIALIZER, LovelyIdentifier.getId("spawn_egg_crafting"), new SpawnEggCraftingRecipeSerializer());

    // -- Methods --

    /**
     * Registers all custom recipes.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     */
    public static void register() {
        // Registration happens via static initializer
    } // register

} // Class: LovelyRecipes
