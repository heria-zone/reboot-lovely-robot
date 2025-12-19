package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.Legacy;
import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.llovelyr.recipes.LegacySpawnDyeRecipeSerializer;
import net.heriazone.llovelyr.recipes.LegacySpawnRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Registry for custom recipe serializers (NeoForge).
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for NBT transfer recipes
 * including spawn egg crafting and dyeing with preview support.
 * <p>
 * <b>NeoForge Pattern:</b> Uses DeferredRegister for recipe serializer registration,
 * ensuring proper initialization timing during mod loading.
 * <p>
 * <b>Migration Note:</b> Recipes that extend vanilla types (ShapedRecipe, ShapelessRecipe)
 * use RecipeType.CRAFTING and only need custom serializers, not custom types.
 * <p>
 * <b>Design Decision:</b> Uses supplier pattern to break cyclical dependency between
 * recipe classes and registry, allowing recipe implementations to exist in Common module.
 */
public class LegacyRecipes {

    // -- Registry --

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, LegacyIdentifier.MODID());

    // -- Recipe Serializers --

    public static final DeferredHolder<RecipeSerializer<?>, LegacySpawnRecipeSerializer> SPAWN_EGG_CRAFTING = RECIPE_SERIALIZERS.register("lovely_spawn", () -> new LegacySpawnRecipeSerializer(LegacyItems.ROBOT_CORE.get()));
    public static final DeferredHolder<RecipeSerializer<?>, LegacySpawnDyeRecipeSerializer> SPAWN_EGG_DYE = RECIPE_SERIALIZERS.register("lovely_spawn_dye", LegacySpawnDyeRecipeSerializer::new);

    // -- Methods --

    /**
     * Registers all custom recipe serializers.
     * <p>
     * <b>Timing:</b> Called during mod initialization to register DeferredRegister
     * with the mod event bus.
     * <p>
     * <b>Design Note:</b> Custom serializers allow data transfer logic while using
     * vanilla CRAFTING recipe type for compatibility with crafting tables and recipe book.
     * Supplier pattern breaks cyclical dependency, enabling Common module placement.
     */
    public static void register(IEventBus events) {
        RECIPE_SERIALIZERS.register(events);
        Legacy.LOGGER.info("Registering Recipes: " + Legacy.MODID);
    } // register()

} // Class: LegacyRecipes