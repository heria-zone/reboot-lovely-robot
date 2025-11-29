package net.msymbios.llovelyr.source;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.msymbios.llovelyr.LovelyLegacy;
import net.msymbios.llovelyr.common.recipes.LovelySpawnDyeRecipe;
import net.msymbios.llovelyr.common.recipes.LovelySpawnDyeRecipeSerializer;
import net.msymbios.llovelyr.common.recipes.LovelySpawnRecipe;
import net.msymbios.llovelyr.common.recipes.LovelySpawnRecipeSerializer;

/**
 * Registry for custom recipe types and serializers.
 * <p>
 * <b>Architecture:</b> Centralizes recipe registration for NBT transfer recipes
 * including spawn egg crafting and dyeing with preview support.
 * <p>
 * <b>Forge Pattern:</b> Uses DeferredRegister for recipe serializer registration,
 * ensuring proper initialization timing during mod loading.
 */
public class LovelyRecipes {

    // -- Registry --

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = 
        DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, LovelyLegacy.MODID);

    // -- Recipe Serializers --

    public static final RegistryObject<RecipeSerializer<LovelySpawnRecipe>> SPAWN_EGG_CRAFTING = 
        RECIPE_SERIALIZERS.register("lovely_spawn", LovelySpawnRecipeSerializer::new);

    public static final RegistryObject<RecipeSerializer<LovelySpawnDyeRecipe>> SPAWN_EGG_DYE = 
        RECIPE_SERIALIZERS.register("lovely_spawn_dye", LovelySpawnDyeRecipeSerializer::new);

    // -- Methods --

    /**
     * Registers all custom recipes.
     * <p>
     * <b>Timing:</b> Called during mod initialization to register DeferredRegister
     * with the mod event bus.
     * <p>
     * <i>Note:</i> Actual registration happens automatically via DeferredRegister.
     */
    public static void register() {
        // Registration happens via DeferredRegister
    } // register()

} // Class: LovelyRecipes
