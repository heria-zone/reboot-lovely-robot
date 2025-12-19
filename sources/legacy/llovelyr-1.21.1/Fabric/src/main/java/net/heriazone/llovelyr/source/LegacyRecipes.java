package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.llovelyr.recipes.*;
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
 * to break cyclical dependency with LegacyItems registry.
 */
public class LegacyRecipes {

    // -- Recipe Serializers --

    public static final RecipeSerializer<LegacySpawnRecipe> SPAWN_EGG_CRAFTING = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            LegacyIdentifier.getId("lovely_spawn"),
            new LegacySpawnRecipeSerializer(LegacyItems.ROBOT_CORE)
    );

    public static final RecipeSerializer<LegacySpawnDyeRecipe> SPAWN_EGG_DYE = Registry.register(
            BuiltInRegistries.RECIPE_SERIALIZER,
            LegacyIdentifier.getId("lovely_spawn_dye"),
            new LegacySpawnDyeRecipeSerializer()
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

} // Class: LegacyRecipes