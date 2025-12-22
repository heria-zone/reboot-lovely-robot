package net.heriazone.rlovelyr.source;

import net.heriazone.rlovelyr.Reboot;
import net.heriazone.rlovelyr.RebootIdentifier;
import net.heriazone.rlovelyr.recipes.RebootSpawnDyeRecipeSerializer;
import net.heriazone.rlovelyr.recipes.RebootSpawnRecipeSerializer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
public class RebootRecipes {

    // -- Registry --

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, RebootIdentifier.MODID());

    // -- Recipe Serializers --

    public static final DeferredHolder<RecipeSerializer<?>, RebootSpawnRecipeSerializer> SPAWN_EGG_CRAFTING = RECIPE_SERIALIZERS.register("lovely_spawn", () -> new RebootSpawnRecipeSerializer(RebootItems.ROBOT_CORE.get()));
    public static final DeferredHolder<RecipeSerializer<?>, RebootSpawnDyeRecipeSerializer> SPAWN_EGG_DYE = RECIPE_SERIALIZERS.register("lovely_spawn_dye", RebootSpawnDyeRecipeSerializer::new);

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
        Reboot.LOGGER.info("Registering Recipes: " + Reboot.MODID);
    } // register()

} // Class: RebootRecipes