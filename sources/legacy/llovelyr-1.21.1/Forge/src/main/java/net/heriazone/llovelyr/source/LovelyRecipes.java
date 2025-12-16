package net.heriazone.llovelyr.source;

import net.heriazone.llovelyr.LegacyIdentifier;
import net.heriazone.llovelyr.recipes.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registry for custom recipe serializers.
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for NBT transfer recipes
 * including spawn egg crafting and dyeing with preview support.
 * <p>
 * <b>Forge Pattern:</b> Uses DeferredRegister for recipe serializer registration,
 * ensuring proper initialization timing during mod loading.
 * <p>
 * <b>Migration Note:</b> Recipes that extend vanilla types (ShapedRecipe, ShapelessRecipe)
 * use RecipeType.CRAFTING and only need custom serializers, not custom types.
 * <p>
 * <b>Design Decision:</b> Uses supplier pattern to break cyclical dependency between
 * recipe classes and registry, allowing recipe implementations to exist in Common module.
 */
public class LovelyRecipes {

    // -- Registry --

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LegacyIdentifier.MODID());

    // -- Recipe Serializers --

    public static final RegistryObject<LovelySpawnRecipeSerializer> SPAWN_EGG_CRAFTING = RECIPE_SERIALIZERS.register("lovely_spawn", () -> new LovelySpawnRecipeSerializer(LovelyItems.ROBOT_CORE.get()));
    public static final RegistryObject<LovelySpawnDyeRecipeSerializer> SPAWN_EGG_DYE = RECIPE_SERIALIZERS.register("lovely_spawn_dye", LovelySpawnDyeRecipeSerializer::new);

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
    } // register()

} // Class: LovelyRecipes