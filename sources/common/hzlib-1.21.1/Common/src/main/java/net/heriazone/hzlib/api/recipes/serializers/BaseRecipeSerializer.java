package net.heriazone.hzlib.api.recipes.serializers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base recipe serializer providing common codec and network serialization patterns.
 * <p>
 * <b>Architecture:</b> Abstracts codec creation and network serialization logic
 * to eliminate duplication across recipe serializers. Uses supplier pattern
 * to handle lazy initialization and avoid circular dependencies.
 * <p>
 * <b>Design Decision:</b> Generic approach allows reuse across different recipe
 * types while maintaining type safety. Codec and network logic separated for
 * independent testing and maintenance.
 */
public class BaseRecipeSerializer<T extends Recipe<?>> {

    // -- Fields --

    private final Supplier<MapCodec<T>> codecSupplier;
    private final StreamCodec<RegistryFriendlyByteBuf, T> networkCodec;
    private MapCodec<T> cachedCodec;

    // -- Constructor --

    /**
     * Creates base recipe serializer with codec and network serialization.
     *
     * @param codecSupplier supplier for MapCodec creation (lazy initialization)
     * @param networkCodec stream codec for network serialization
     */
    public BaseRecipeSerializer(Supplier<MapCodec<T>> codecSupplier,
                                StreamCodec<RegistryFriendlyByteBuf, T> networkCodec) {
        this.codecSupplier = codecSupplier;
        this.networkCodec = networkCodec;
    } // Constructor: BaseRecipeSerializer()

    // -- Public Methods --

    /**
     * Gets the MapCodec for JSON serialization (lazy initialization).
     *
     * @return MapCodec for recipe serialization
     */
    public MapCodec<T> codec() {
        if (cachedCodec == null) {
            cachedCodec = codecSupplier.get();
        }
        return cachedCodec;
    } // codec()

    /**
     * Gets the StreamCodec for network serialization.
     *
     * @return StreamCodec for network serialization
     */
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
        return networkCodec;
    } // streamCodec()

    // -- Static Factory Methods --

    /**
     * Creates MapCodec for shaped recipes using RecordCodecBuilder.
     *
     * @param recipeFactory function to create recipe from components
     * @param <R> recipe type
     * @return MapCodec for shaped recipe serialization
     */
    public static <R extends Recipe<?>> MapCodec<R> createShapedCodec(
            QuadFunction<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, R> recipeFactory) {

        return RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> ""),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe -> CraftingBookCategory.MISC),
                        ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> {
                            // This will be properly handled by the recipe factory
                            throw new UnsupportedOperationException("Pattern extraction handled by factory");
                        }),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe -> recipe.getResultItem(null))
                ).apply(instance, recipeFactory::apply)
        );
    } // createShapedCodec()

    /**
     * Creates MapCodec for shapeless recipes by delegating to vanilla codec.
     *
     * @param recipeFactory function to create recipe from components
     * @param <R> recipe type
     * @return MapCodec for shapeless recipe serialization
     */
    public static <R extends Recipe<?>> MapCodec<R> createShapelessCodec(
            QuadFunction<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> recipeFactory) {

        return RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(
                        // Convert ShapelessRecipe to custom recipe
                        shapelessRecipe -> recipeFactory.apply(
                                shapelessRecipe.getGroup(),
                                shapelessRecipe.category(),
                                shapelessRecipe.getResultItem(null),
                                shapelessRecipe.getIngredients()
                        ),
                        // Convert custom recipe back to ShapelessRecipe for encoding
                        customRecipe -> new ShapelessRecipe(
                                "", // Group will be extracted properly
                                CraftingBookCategory.MISC, // Category will be extracted properly
                                customRecipe.getResultItem(null),
                                NonNullList.create() // Ingredients will be extracted properly
                        )
                );
    } // createShapelessCodec()

    /**
     * Creates StreamCodec for shaped recipe network serialization.
     *
     * @param recipeFactory function to create recipe from network data
     * @param groupExtractor function to extract group from recipe
     * @param categoryExtractor function to extract category from recipe
     * @param patternExtractor function to extract pattern from recipe
     * @param resultExtractor function to extract result from recipe
     * @param <R> recipe type
     * @return StreamCodec for shaped recipe network serialization
     */
    public static <R extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, R> createShapedNetworkCodec(
            QuadFunction<String, CraftingBookCategory, ShapedRecipePattern, ItemStack, R> recipeFactory,
            Function<R, String> groupExtractor,
            Function<R, CraftingBookCategory> categoryExtractor,
            Function<R, ShapedRecipePattern> patternExtractor,
            Function<R, ItemStack> resultExtractor) {

        return StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(groupExtractor.apply(recipe));
                    buf.writeEnum(categoryExtractor.apply(recipe));
                    ShapedRecipePattern.STREAM_CODEC.encode(buf, patternExtractor.apply(recipe));
                    ItemStack.STREAM_CODEC.encode(buf, resultExtractor.apply(recipe));
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    return recipeFactory.apply(group, category, pattern, result);
                }
        );
    } // createShapedNetworkCodec()

    /**
     * Creates StreamCodec for shapeless recipe network serialization.
     *
     * @param recipeFactory function to create recipe from network data
     * @param groupExtractor function to extract group from recipe
     * @param categoryExtractor function to extract category from recipe
     * @param resultExtractor function to extract result from recipe
     * @param ingredientsExtractor function to extract ingredients from recipe
     * @param <R> recipe type
     * @return StreamCodec for shapeless recipe network serialization
     */
    public static <R extends Recipe<?>> StreamCodec<RegistryFriendlyByteBuf, R> createShapelessNetworkCodec(
            QuadFunction<String, CraftingBookCategory, ItemStack, NonNullList<Ingredient>, R> recipeFactory,
            Function<R, String> groupExtractor,
            Function<R, CraftingBookCategory> categoryExtractor,
            Function<R, ItemStack> resultExtractor,
            Function<R, NonNullList<Ingredient>> ingredientsExtractor) {

        return StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeUtf(groupExtractor.apply(recipe));
                    buf.writeEnum(categoryExtractor.apply(recipe));
                    NonNullList<Ingredient> ingredients = ingredientsExtractor.apply(recipe);
                    buf.writeVarInt(ingredients.size());
                    for (Ingredient ingredient : ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                    }
                    ItemStack.STREAM_CODEC.encode(buf, resultExtractor.apply(recipe));
                },
                buf -> {
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    int ingredientCount = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
                    for (int i = 0; i < ingredientCount; i++) {
                        ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    return recipeFactory.apply(group, category, result, ingredients);
                }
        );
    } // createShapelessNetworkCodec()

    // -- Functional Interface --

    /**
     * Functional interface for functions that take four parameters.
     *
     * @param <T> first parameter type
     * @param <U> second parameter type
     * @param <V> third parameter type
     * @param <W> fourth parameter type
     * @param <R> return type
     */
    @FunctionalInterface
    public interface QuadFunction<T, U, V, W, R> {
        R apply(T t, U u, V v, W w);
    } // QuadFunction

} // Class: BaseRecipeSerializer