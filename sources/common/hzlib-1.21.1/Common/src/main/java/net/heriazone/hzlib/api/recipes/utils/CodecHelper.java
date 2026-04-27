package net.heriazone.hzlib.api.recipes.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.heriazone.hzlib.api.recipes.base.InternalShapedRecipe;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * Provides codec utilities for recipe serialization in Minecraft 1.21.1.
 * <p>
 * <b>Architecture:</b> Centralizes codec creation patterns that were duplicated
 * across loader-specific recipe serializers. Eliminates 100% identical codec
 * implementations across Fabric, Forge, and NeoForge.
 * <p>
 * <b>Migration Support:</b> Minecraft 1.21.1 replaced manual NBT serialization
 * with typed codecs for better type safety and performance. This helper provides
 * common codec patterns for both shaped and shapeless recipe types.
 * <p>
 * <b>Design Decision:</b> Extracted from identical implementations in all three
 * loaders to provide single source of truth for recipe serialization logic.
 */
public class CodecHelper {

    // -- Shaped Recipe Codecs --

    /**
     * Creates a MapCodec for shaped recipes with robot core ingredient.
     * <p>
     * <b>Usage:</b> Used by shaped recipe serializers to encode/decode recipe data
     * including pattern, result, and robot core item for data transfer.
     * <p>
     * <b>Codec Structure:</b> Matches vanilla ShapedRecipe format exactly:
     * group, category, pattern/key, result. Robot core item is injected during construction.
     * <p>
     * <b>Extraction Value:</b> Eliminates 100% identical codec implementations across
     * Fabric, Forge, and NeoForge serializers.
     *
     * @param recipeFactory factory function to create recipe instances
     * @param robotCoreItem the robot core item for data transfer
     * @param <T> recipe type extending BaseLovelySpawnRecipe
     * @return MapCodec for shaped recipe serialization
     */
    public static <T> MapCodec<T> createShapedRecipeCodec(ShapedRecipeFactory<T> recipeFactory, Item robotCoreItem) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> {
                            if (recipe instanceof ShapedRecipe shapedRecipe) {
                                return shapedRecipe.getGroup();
                            }
                            return "";
                        }),
                        CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(recipe -> {
                            if (recipe instanceof ShapedRecipe shapedRecipe) {
                                return shapedRecipe.category();
                            }
                            return CraftingBookCategory.MISC;
                        }),
                        ShapedRecipePattern.MAP_CODEC.forGetter(recipe -> {
                            if (recipe instanceof ShapedRecipe shapedRecipe) {
                                // Access pattern through reflection or cast to our base class
                                if (shapedRecipe instanceof InternalShapedRecipe baseRecipe) {
                                    return baseRecipe.pattern();
                                }
                            }
                            throw new IllegalStateException("Recipe must extend BaseLovelySpawnRecipe");
                        }),
                        ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(recipe -> {
                            if (recipe instanceof Recipe<?> recipeInstance) {
                                return recipeInstance.getResultItem(null);
                            }
                            throw new IllegalStateException("Recipe must implement Recipe interface");
                        })
                ).apply(instance, (group, category, pattern, result) ->
                        recipeFactory.create(group, category, pattern, result, robotCoreItem))
        );
    } // createShapedRecipeCodec()

    /**
     * Creates a StreamCodec for shaped recipes for network serialization.
     * <p>
     * <b>Network Serialization:</b> Handles efficient binary serialization for
     * client-server communication during recipe synchronization.
     * <p>
     * <b>Extraction Value:</b> Eliminates 100% identical network serialization
     * implementations across all three loaders.
     *
     * @param recipeFactory factory function to create recipe instances from network data
     * @param robotCoreItem the robot core item for data transfer
     * @param <T> recipe type extending BaseLovelySpawnRecipe
     * @return StreamCodec for network serialization
     */
    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> createShapedRecipeStreamCodec(
            ShapedRecipeFactory<T> recipeFactory, Item robotCoreItem) {
        return StreamCodec.of(
                (buf, recipe) -> {
                    // Encode recipe to buffer - identical across all loaders
                    if (recipe instanceof ShapedRecipe shapedRecipe) {
                        buf.writeUtf(shapedRecipe.getGroup());
                        buf.writeEnum(shapedRecipe.category());
                        if (shapedRecipe instanceof InternalShapedRecipe baseRecipe) {
                            ShapedRecipePattern.STREAM_CODEC.encode(buf, baseRecipe.pattern());
                        }
                        ItemStack.STREAM_CODEC.encode(buf, shapedRecipe.getResultItem(null));
                    }
                },
                (buf) -> {
                    // Decode recipe from buffer - identical across all loaders
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    ShapedRecipePattern pattern = ShapedRecipePattern.STREAM_CODEC.decode(buf);
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    return recipeFactory.create(group, category, pattern, result, robotCoreItem);
                }
        );
    } // createShapedRecipeStreamCodec()

    // -- Shapeless Recipe Codecs --

    /**
     * Creates a MapCodec for shapeless recipes with dye modification.
     * <p>
     * <b>Usage:</b> Used by shapeless recipe serializers to encode/decode recipe data
     * including ingredients, result, and dye modification logic.
     * <p>
     * <b>Delegation Strategy:</b> Uses vanilla ShapelessRecipe codec for ingredient
     * parsing and validation, then wraps result in custom recipe class. This approach
     * matches the pattern used across all three loaders.
     * <p>
     * <b>Extraction Value:</b> Eliminates 100% identical codec implementations across
     * Fabric, Forge, and NeoForge serializers.
     *
     * @param recipeFactory factory function to create recipe instances
     * @param <T> recipe type extending BaseLovelySpawnDyeRecipe
     * @return MapCodec for shapeless recipe serialization
     */
    public static <T> MapCodec<T> createShapelessRecipeCodec(ShapelessRecipeFactory<T> recipeFactory) {
        return RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(
                        // Convert ShapelessRecipe to custom recipe type
                        shapelessRecipe -> recipeFactory.create(
                                shapelessRecipe.getGroup(),
                                shapelessRecipe.category(),
                                shapelessRecipe.getResultItem(null),
                                shapelessRecipe.getIngredients()
                        ),
                        // Convert custom recipe back to ShapelessRecipe for encoding
                        customRecipe -> {
                            if (customRecipe instanceof ShapelessRecipe shapelessRecipe) {
                                return new ShapelessRecipe(
                                        shapelessRecipe.getGroup(),
                                        shapelessRecipe.category(),
                                        shapelessRecipe.getResultItem(null),
                                        shapelessRecipe.getIngredients()
                                );
                            }
                            throw new IllegalStateException("Recipe must extend ShapelessRecipe");
                        }
                );
    } // createShapelessRecipeCodec()

    /**
     * Creates a StreamCodec for shapeless recipes for network serialization.
     * <p>
     * <b>Network Serialization:</b> Handles efficient binary serialization for
     * client-server communication during recipe synchronization.
     * <p>
     * <b>Extraction Value:</b> Eliminates 100% identical network serialization
     * implementations across all three loaders.
     *
     * @param recipeFactory factory function to create recipe instances from network data
     * @param <T> recipe type extending BaseLovelySpawnDyeRecipe
     * @return StreamCodec for network serialization
     */
    public static <T> StreamCodec<RegistryFriendlyByteBuf, T> createShapelessRecipeStreamCodec(
            ShapelessRecipeFactory<T> recipeFactory) {
        return StreamCodec.of(
                (buf, recipe) -> {
                    // Encode recipe to buffer - identical across all loaders
                    if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                        buf.writeUtf(shapelessRecipe.getGroup());
                        buf.writeEnum(shapelessRecipe.category());
                        buf.writeVarInt(shapelessRecipe.getIngredients().size());

                        for (Ingredient ingredient : shapelessRecipe.getIngredients()) {
                            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                        }

                        ItemStack.STREAM_CODEC.encode(buf, shapelessRecipe.getResultItem(null));
                    }
                },
                (buf) -> {
                    // Decode recipe from buffer - identical across all loaders
                    String group = buf.readUtf();
                    CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
                    int ingredientCount = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
                    ingredients.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
                    return recipeFactory.create(group, category, result, ingredients);
                }
        );
    } // createShapelessRecipeStreamCodec()

    // -- Utility Methods --

    /**
     * Creates a complete recipe serializer for shaped recipes.
     * <p>
     * <b>Usage:</b> Provides a complete serializer implementation that can be used
     * directly by loader-specific recipe registration. Combines both MapCodec and
     * StreamCodec creation into a single method.
     * <p>
     * <b>Extraction Value:</b> Eliminates the need for loader-specific serializer
     * classes entirely, reducing code duplication to zero.
     *
     * @param recipeFactory factory function to create recipe instances
     * @param robotCoreItem the robot core item for data transfer
     * @param <T> recipe type extending BaseLovelySpawnRecipe
     * @return complete RecipeSerializer implementation
     */
    public static <T extends Recipe<CraftingInput>> RecipeSerializer<T> createShapedRecipeSerializer(
            ShapedRecipeFactory<T> recipeFactory, Item robotCoreItem) {

        MapCodec<T> mapCodec = createShapedRecipeCodec(recipeFactory, robotCoreItem);
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = createShapedRecipeStreamCodec(recipeFactory, robotCoreItem);

        return new RecipeSerializer<T>() {
            @Override
            public MapCodec<T> codec() {
                return mapCodec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    } // createShapedRecipeSerializer()

    /**
     * Creates a complete recipe serializer for shapeless recipes.
     * <p>
     * <b>Usage:</b> Provides a complete serializer implementation that can be used
     * directly by loader-specific recipe registration. Combines both MapCodec and
     * StreamCodec creation into a single method.
     * <p>
     * <b>Extraction Value:</b> Eliminates the need for loader-specific serializer
     * classes entirely, reducing code duplication to zero.
     *
     * @param recipeFactory factory function to create recipe instances
     * @param <T> recipe type extending BaseLovelySpawnDyeRecipe
     * @return complete RecipeSerializer implementation
     */
    public static <T extends Recipe<CraftingInput>> RecipeSerializer<T> createShapelessRecipeSerializer(
            ShapelessRecipeFactory<T> recipeFactory) {

        MapCodec<T> mapCodec = createShapelessRecipeCodec(recipeFactory);
        StreamCodec<RegistryFriendlyByteBuf, T> streamCodec = createShapelessRecipeStreamCodec(recipeFactory);

        return new RecipeSerializer<T>() {
            @Override
            public MapCodec<T> codec() {
                return mapCodec;
            }

            @Override
            public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() {
                return streamCodec;
            }
        };
    } // createShapelessRecipeSerializer()

    // -- Factory Interfaces --

    /**
     * Factory interface for creating shaped recipes.
     * <p>
     * <b>Usage:</b> Provides type-safe factory method for codec-based recipe creation.
     * Used by both MapCodec and StreamCodec implementations.
     */
    @FunctionalInterface
    public interface ShapedRecipeFactory<T> {
        T create(String group, CraftingBookCategory category, ShapedRecipePattern pattern,
                 ItemStack result, Item robotCoreItem);
    } // Interface: ShapedRecipeFactory

    /**
     * Factory interface for creating shapeless recipes.
     * <p>
     * <b>Usage:</b> Provides type-safe factory method for codec-based recipe creation.
     * Used by both MapCodec and StreamCodec implementations.
     */
    @FunctionalInterface
    public interface ShapelessRecipeFactory<T> {
        T create(String group, CraftingBookCategory category, ItemStack result,
                 NonNullList<Ingredient> ingredients);
    } // Interface: ShapelessRecipeFactory

    // -- Validation Helpers --

    /**
     * Creates error message for codec validation failures.
     * <p>
     * <b>Usage:</b> Provides consistent error messaging for codec-related
     * validation failures during recipe deserialization.
     *
     * @param recipeType type of recipe being validated
     * @param issue specific validation issue
     * @return formatted error message
     */
    public static String createValidationErrorMessage(String recipeType, String issue) {
        return String.format("Recipe codec validation failed for %s: %s", recipeType, issue);
    } // createValidationErrorMessage()

} // Class: CodecHelper