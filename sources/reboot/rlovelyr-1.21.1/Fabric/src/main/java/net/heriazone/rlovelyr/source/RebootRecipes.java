package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.rlovelyr.recipes.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Registry for custom recipe types and serializers.
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for NBT transfer recipes
 * including spawn egg crafting and dyeing with preview support.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 with Mojang mappings no longer requires
 * mixins to access ShapedRecipe methods - they are now public. Uses supplier pattern
 * to break cyclical dependency with RebootItems registry.
 */
public class RebootRecipes {

    // -- Recipe Serializers --

    public static final RecipeSerializer<RebootSpawnRecipe> SPAWN_EGG_CRAFTING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            RebootIdentifier.getId("lovely_spawn"),
            new RebootSpawnRecipeSerializer(RebootItems.ROBOT_CORE)
    );

    public static final RecipeSerializer<RebootSpawnDyeRecipe> SPAWN_EGG_DYE = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            RebootIdentifier.getId("lovely_spawn_dye"),
            new RebootSpawnDyeRecipeSerializer()
    );

    // -- Methods --

    /**
     * Registers all custom recipes.
     * <p>
     * <b>Timing:</b> Must be called during mod initialization.
     * <p>
     * <b>Design Note:</b> Registration happens via static initializers, but this
     * method ensures the class is loaded and initializers execute.
     */
    public static void register() {
        // Registration happens via static initializer
    } // register()

} // Class: RebootRecipes