package net.msymbios.llovelyr.source;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.msymbios.llovelyr.common.shared.LovelyIdentifier;
import net.msymbios.llovelyr.common.recipes.LovelySpawnDyeRecipe;
import net.msymbios.llovelyr.common.recipes.LovelySpawnDyeRecipeSerializer;
import net.msymbios.llovelyr.common.recipes.LovelySpawnRecipe;
import net.msymbios.llovelyr.common.recipes.LovelySpawnRecipeSerializer;

/**
 * Registry for custom recipe types and serializers.
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for NBT transfer recipes
 * including spawn egg crafting and dyeing with preview support.
 */
public class LovelyRecipes {

    // -- Recipe Serializers --

    public static final RecipeSerializer<LovelySpawnRecipe> SPAWN_EGG_CRAFTING = Registry.register(Registries.RECIPE_SERIALIZER, LovelyIdentifier.getId("lovely_spawn"), new LovelySpawnRecipeSerializer());
    public static final RecipeSerializer<LovelySpawnDyeRecipe> SPAWN_EGG_DYE = Registry.register(Registries.RECIPE_SERIALIZER, LovelyIdentifier.getId("lovely_spawn_dye"), new LovelySpawnDyeRecipeSerializer());

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
