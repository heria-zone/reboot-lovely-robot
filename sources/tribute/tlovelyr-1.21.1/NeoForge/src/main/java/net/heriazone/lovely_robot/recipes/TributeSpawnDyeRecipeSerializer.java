package net.heriazone.lovely_robot.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.RecipeCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Forge wrapper for spawn egg dye recipe serializer.
 * <p>
 * <b>Architecture:</b> Thin wrapper that delegates business logic to Common module
 * while maintaining Forge-specific RecipeSerializer interface compliance.
 * <p>
 * <b>Design Decision:</b> Preserves Forge registration patterns while extracting
 * codec and network serialization logic to shared Common implementation.
 */
public class TributeSpawnDyeRecipeSerializer implements RecipeSerializer<TributeSpawnDyeRecipe> {

    // -- Fields --

    private final MapCodec<TributeSpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, TributeSpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public TributeSpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(TributeSpawnDyeRecipe::new);

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
                TributeSpawnDyeRecipe::new,
                ShapelessRecipe::getGroup,
                ShapelessRecipe::category,
                recipe -> recipe.getResultItem(null),
                ShapelessRecipe::getIngredients
        );
    } // Constructor: TributeSpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<TributeSpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, TributeSpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: TributeSpawnDyeRecipeSerializer