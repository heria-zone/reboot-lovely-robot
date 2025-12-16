package net.heriazone.lovelylib.hzlib.api.recipes.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * Helper class for building recipe codecs with common patterns.
 * <p>
 * <b>Architecture:</b> Centralizes codec building logic that was duplicated
 * across recipe serializers. Provides reusable codec patterns for different
 * recipe types while maintaining compatibility with vanilla formats.
 * <p>
 * <b>Design Decision:</b> Static utility methods allow easy reuse without
 * inheritance complexity. Codec patterns are standardized across all recipe
 * types for consistency.
 */
public class RecipeCodecHelper {

    // -- Codec Building --

    /**
     * Creates a standard shaped recipe codec with data transfer support.
     * <p>
     * <b>Architecture:</b> Uses codec-based serialization matching vanilla ShapedRecipe
     * format while allowing custom recipe class instantiation. Ensures full
     * compatibility with vanilla recipe format and proper pattern validation.
     *
     * @param recipeFactory function that creates recipe instance from data and robot core
     * @param robotCoreItem robot core item for recipe validation
     * @param <T> recipe type
     * @return configured map codec for the recipe type
     */
    public static <T> MapCodec<T> createShapedRecipeCodec(
            BiFunction<RecipeData, Item, T> recipeFactory,
            Item robotCoreItem) {

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(recipe ->
                                getRecipeGroup(recipe)),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe ->
                                getRecipeCategory(recipe)),
                        ShapedRecipePattern.MAP_CODEC.forGetter(recipe ->
                                getRecipePattern(recipe)),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe ->
                                getRecipeResult(recipe))
                ).apply(instance, (group, category, pattern, result) -> {
                    RecipeData data = new RecipeData(group, category, pattern, result);
                    return recipeFactory.apply(data, robotCoreItem);
                })
        );
    } // createShapedRecipeCodec()

    /**
     * Creates a standard shaped recipe codec with direct factory function.
     * <p>
     * <b>Architecture:</b> Simplified version that takes a direct factory function
     * for creating recipes from parsed components.
     *
     * @param recipeFactory function that creates recipe from components
     * @param <T> recipe type
     * @return configured map codec for the recipe type
     */
    public static <T> MapCodec<T> createShapedRecipeCodec(
            ShapedRecipeFactory<T> recipeFactory) {

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(recipe ->
                                getRecipeGroup(recipe)),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe ->
                                getRecipeCategory(recipe)),
                        ShapedRecipePattern.MAP_CODEC.forGetter(recipe ->
                                getRecipePattern(recipe)),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe ->
                                getRecipeResult(recipe))
                ).apply(instance, recipeFactory::create)
        );
    } // createShapedRecipeCodec()

    /**
     * Creates a standard shapeless recipe codec by delegating to vanilla codec.
     * <p>
     * <b>Architecture:</b> Leverages vanilla ShapelessRecipe codec for compatibility
     * while allowing custom recipe class instantiation.
     *
     * @param recipeFactory function that creates recipe from components
     * @param <T> recipe type
     * @return configured map codec for the recipe type
     */
    public static <T> MapCodec<T> createShapelessRecipeCodec(
            ShapelessRecipeFactory<T> recipeFactory) {

        return net.minecraft.world.item.crafting.RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(
                        // Convert ShapelessRecipe to custom recipe
                        shapelessRecipe -> recipeFactory.create(
                                shapelessRecipe.getGroup(),
                                shapelessRecipe.category(),
                                shapelessRecipe.getResultItem(null),
                                shapelessRecipe.getIngredients()
                        ),
                        // Convert custom recipe back to ShapelessRecipe for encoding
                        customRecipe -> new net.minecraft.world.item.crafting.ShapelessRecipe(
                                getRecipeGroup(customRecipe),
                                getRecipeCategory(customRecipe),
                                getRecipeResult(customRecipe),
                                getShapelessIngredients(customRecipe)
                        )
                );
    } // createShapelessRecipeCodec()

    // -- Recipe Data Extraction --

    /**
     * Extracts group from recipe instance.
     * <p>
     * <i>Note:</i> Uses reflection-like approach to extract data from recipe.
     * Subclasses should override if they have specific group extraction logic.
     *
     * @param recipe recipe instance
     * @return recipe group
     */
    private static String getRecipeGroup(Object recipe) {
        // Default implementation - recipes should implement a getGroup() method
        try {
            return (String) recipe.getClass().getMethod("getGroup").invoke(recipe);
        } catch (Exception e) {
            return "";
        }
    } // getRecipeGroup()

    /**
     * Extracts category from recipe instance.
     *
     * @param recipe recipe instance
     * @return recipe category
     */
    private static CraftingBookCategory getRecipeCategory(Object recipe) {
        try {
            return (CraftingBookCategory) recipe.getClass().getMethod("category").invoke(recipe);
        } catch (Exception e) {
            return CraftingBookCategory.MISC;
        }
    } // getRecipeCategory()

    /**
     * Extracts pattern from recipe instance.
     *
     * @param recipe recipe instance
     * @return recipe pattern
     */
    private static ShapedRecipePattern getRecipePattern(Object recipe) {
        try {
            return (ShapedRecipePattern) recipe.getClass().getMethod("pattern").invoke(recipe);
        } catch (Exception e) {
            throw new RuntimeException("Recipe must have pattern() method", e);
        }
    } // getRecipePattern()

    /**
     * Extracts result from recipe instance.
     *
     * @param recipe recipe instance
     * @return recipe result
     */
    private static ItemStack getRecipeResult(Object recipe) {
        try {
            // Try getResultItem(null) first (common pattern)
            return (ItemStack) recipe.getClass().getMethod("getResultItem",
                    net.minecraft.core.HolderLookup.Provider.class).invoke(recipe, (Object) null);
        } catch (Exception e) {
            throw new RuntimeException("Recipe must have getResultItem(HolderLookup.Provider) method", e);
        }
    } // getRecipeResult()

    /**
     * Extracts ingredients from shapeless recipe instance.
     *
     * @param recipe recipe instance
     * @return recipe ingredients
     */
    private static NonNullList<Ingredient> getShapelessIngredients(Object recipe) {
        try {
            return (NonNullList<Ingredient>) recipe.getClass().getMethod("getIngredients").invoke(recipe);
        } catch (Exception e) {
            return NonNullList.create();
        }
    } // getShapelessIngredients()

    // -- Functional Interfaces --

    /**
     * Functional interface for recipe factory with two parameters.
     *
     * @param <T> first parameter type
     * @param <U> second parameter type
     * @param <R> result type
     */
    @FunctionalInterface
    public interface BiFunction<T, U, R> {
        R apply(T t, U u);
    } // Interface: BiFunction

    /**
     * Factory interface for creating shaped recipes from parsed components.
     *
     * @param <T> recipe type to create
     */
    @FunctionalInterface
    public interface ShapedRecipeFactory<T> {
        /**
         * Creates recipe instance from parsed JSON components.
         *
         * @param group recipe group (optional)
         * @param category crafting book category
         * @param pattern shaped recipe pattern with ingredients
         * @param result result item stack
         * @return created recipe instance
         */
        T create(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result);
    } // ShapedRecipeFactory

    /**
     * Factory interface for creating shapeless recipes from parsed components.
     *
     * @param <T> recipe type to create
     */
    @FunctionalInterface
    public interface ShapelessRecipeFactory<T> {
        /**
         * Creates recipe instance from parsed JSON components.
         *
         * @param group recipe group (optional)
         * @param category crafting book category
         * @param result result item stack
         * @param ingredients list of recipe ingredients
         * @return created recipe instance
         */
        T create(String group, CraftingBookCategory category, ItemStack result, NonNullList<Ingredient> ingredients);
    } // ShapelessRecipeFactory

    // -- Recipe Data Structure --

    /**
     * Common recipe data structure for serialization.
     */
    public static class RecipeData {
        private final String group;
        private final CraftingBookCategory category;
        private final ShapedRecipePattern pattern;
        private final ItemStack result;

        public RecipeData(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
            this.group = group;
            this.category = category;
            this.pattern = pattern;
            this.result = result;
        }

        public String getGroup() {
            return group;
        }

        public CraftingBookCategory getCategory() {
            return category;
        }

        public ShapedRecipePattern getPattern() {
            return pattern;
        }

        public ItemStack getResult() {
            return result;
        }
    } // Class: RecipeData

} // Class: RecipeCodecHelper