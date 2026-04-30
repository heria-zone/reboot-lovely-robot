package net.heriazone.lovely_robot.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnRecipe;
import net.heriazone.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.hzlib.api.recipes.serializers.RecipeCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Forge wrapper for spawn egg crafting recipe serializer.
 * <p>
 * <b>Architecture:</b> Thin wrapper that delegates business logic to Common module
 * while maintaining Forge-specific RecipeSerializer interface compliance.
 * <p>
 * <b>Design Decision:</b> Preserves Forge registration patterns while extracting
 * codec and network serialization logic to shared Common implementation.
 */
public class TributeSpawnRecipeSerializer implements RecipeSerializer<TributeSpawnRecipe> {

    // -- Fields --

    private final Item robotCoreItem;
    private final MapCodec<TributeSpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, TributeSpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public TributeSpawnRecipeSerializer(Item robotCoreItem) {
        this.robotCoreItem = robotCoreItem;

        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapedRecipeCodec(
                (group, category, pattern, result) ->
                        new TributeSpawnRecipe(group, category, pattern, result, robotCoreItem)
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapedRecipeNetworkCodec(
                (group, category, pattern, result) ->
                        new TributeSpawnRecipe(group, category, pattern, result, robotCoreItem),
                ShapedRecipe::getGroup,
                ShapedRecipe::category,
                BaseLovelySpawnRecipe::pattern,
                recipe -> recipe.getResultItem(null)
        );
    } // Constructor: TributeSpawnRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<TributeSpawnRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, TributeSpawnRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: TributeSpawnRecipeSerializer