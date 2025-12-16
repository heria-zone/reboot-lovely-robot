package net.heriazone.lovelylib.hzlib.api.recipes.serializers;

import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

import java.util.function.Function;

/**
 * Helper class for network serialization of recipe data.
 * <p>
 * <b>Architecture:</b> Provides common network serialization patterns for
 * recipe data that can be used across different loader implementations.
 * Separates serialization logic from loader-specific network buffer types.
 * <p>
 * <b>Design Decision:</b> Generic approach allows reuse across different
 * network buffer implementations while maintaining consistent serialization
 * format across all loaders.
 */
public class NetworkSerializationHelper {

    // -- Network Data Structure --

    /**
     * Network-serializable recipe data container.
     */
    public static class NetworkRecipeData {
        private final String group;
        private final CraftingBookCategory category;
        private final ShapedRecipePattern pattern;
        private final ItemStack result;
        private final Item robotCoreItem;

        public NetworkRecipeData(String group, CraftingBookCategory category,
                                 ShapedRecipePattern pattern, ItemStack result, Item robotCoreItem) {
            this.group = group;
            this.category = category;
            this.pattern = pattern;
            this.result = result;
            this.robotCoreItem = robotCoreItem;
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

        public Item getRobotCoreItem() {
            return robotCoreItem;
        }
    } // Class: NetworkRecipeData

    // -- Serialization Logic --

    /**
     * Extracts network data from recipe instance.
     * <p>
     * <b>Architecture:</b> Converts recipe instance to network-serializable
     * format that can be transmitted across different loader network systems.
     *
     * @param recipe recipe instance to serialize
     * @param robotCoreItem robot core item reference
     * @return network-serializable data
     */
    public static NetworkRecipeData extractNetworkData(Object recipe, Item robotCoreItem) {
        try {
            String group = (String) recipe.getClass().getMethod("getGroup").invoke(recipe);
            CraftingBookCategory category = (CraftingBookCategory) recipe.getClass().getMethod("category").invoke(recipe);
            ShapedRecipePattern pattern = (ShapedRecipePattern) recipe.getClass().getMethod("pattern").invoke(recipe);
            ItemStack result = (ItemStack) recipe.getClass().getMethod("getResultItem",
                    net.minecraft.core.HolderLookup.Provider.class).invoke(recipe, (Object) null);

            return new NetworkRecipeData(group, category, pattern, result, robotCoreItem);
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract network data from recipe", e);
        }
    } // extractNetworkData()

    // -- Serialization Patterns --

    /**
     * Defines the standard serialization order for recipe network data.
     * <p>
     * <b>Order:</b>
     * 1. Group (String)
     * 2. Category (Enum)
     * 3. Pattern (ShapedRecipePattern)
     * 4. Result (ItemStack)
     * <p>
     * <i>Note:</i> Robot core item is not serialized as it's provided by the serializer.
     *
     * @return array of field names in serialization order
     */
    public static String[] getSerializationOrder() {
        return new String[]{"group", "category", "pattern", "result"};
    } // getSerializationOrder()

    /**
     * Validates network recipe data for completeness.
     *
     * @param data network recipe data to validate
     * @throws IllegalArgumentException if data is invalid
     */
    public static void validateNetworkData(NetworkRecipeData data) {
        if (data.getGroup() == null) {
            throw new IllegalArgumentException("Recipe group cannot be null");
        }

        if (data.getCategory() == null) {
            throw new IllegalArgumentException("Recipe category cannot be null");
        }

        if (data.getPattern() == null) {
            throw new IllegalArgumentException("Recipe pattern cannot be null");
        }

        if (data.getResult() == null || data.getResult().isEmpty()) {
            throw new IllegalArgumentException("Recipe result cannot be null or empty");
        }

        if (data.getRobotCoreItem() == null) {
            throw new IllegalArgumentException("Robot core item cannot be null");
        }
    } // validateNetworkData()

    // -- StreamCodec Factory Methods --

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
    public static <R> StreamCodec<RegistryFriendlyByteBuf, R> createShapedRecipeNetworkCodec(
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
    } // createShapedRecipeNetworkCodec()

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
    public static <R> StreamCodec<RegistryFriendlyByteBuf, R> createShapelessRecipeNetworkCodec(
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
    } // createShapelessRecipeNetworkCodec()

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

} // Class: NetworkSerializationHelper