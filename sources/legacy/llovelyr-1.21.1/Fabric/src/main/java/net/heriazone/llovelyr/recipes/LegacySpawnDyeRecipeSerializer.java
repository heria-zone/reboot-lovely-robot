package net.heriazone.llovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.hzlib.api.recipes.serializers.*;
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
public class LegacySpawnDyeRecipeSerializer implements RecipeSerializer<LegacySpawnDyeRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<LegacySpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LegacySpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public LegacySpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(
                (group, category, result, ingredients) ->
                        new LegacySpawnDyeRecipe(group, category, result, ingredients)
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
                (group, category, result, ingredients) ->
                        new LegacySpawnDyeRecipe(group, category, result, ingredients),
                recipe -> recipe.getGroup(),
                recipe -> recipe.category(),
                recipe -> recipe.getResultItem(null),
                recipe -> recipe.getIngredients()
        );

        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: LegacySpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public MapCodec<LegacySpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LegacySpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: LegacySpawnDyeRecipeSerializer