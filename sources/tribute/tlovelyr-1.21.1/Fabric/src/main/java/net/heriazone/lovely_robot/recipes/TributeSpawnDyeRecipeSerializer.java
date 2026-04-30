package net.heriazone.lovely_robot.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.hzlib.api.recipes.serializers.BaseRecipeSerializer;
import net.heriazone.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.hzlib.api.recipes.serializers.RecipeCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Fabric wrapper for spawn egg dye recipe serialization.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common BaseRecipeSerializer that handles
 * Fabric-specific codec and stream codec interfaces. Delegates business logic
 * to Common module while keeping loader-specific dependencies in Fabric.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains loader isolation while
 * extracting reusable recipe serialization logic to Common module.
 */
public class TributeSpawnDyeRecipeSerializer implements RecipeSerializer<TributeSpawnDyeRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<TributeSpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, TributeSpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public TributeSpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(
                (group, category, result, ingredients) ->
                        new TributeSpawnDyeRecipe(group, category, result, ingredients)
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
                (group, category, result, ingredients) ->
                        new TributeSpawnDyeRecipe(group, category, result, ingredients),
                recipe -> recipe.getGroup(),
                recipe -> recipe.category(),
                recipe -> recipe.getResultItem(null),
                recipe -> recipe.getIngredients()
        );

        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: TributeSpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public MapCodec<TributeSpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TributeSpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: TributeSpawnDyeRecipeSerializer