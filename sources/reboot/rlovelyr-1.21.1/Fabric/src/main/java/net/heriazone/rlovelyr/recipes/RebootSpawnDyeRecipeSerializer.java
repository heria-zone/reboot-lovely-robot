package net.heriazone.rlovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.BaseRecipeSerializer;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.RecipeCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

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
public class RebootSpawnDyeRecipeSerializer implements RecipeSerializer<RebootSpawnDyeRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<RebootSpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, RebootSpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public RebootSpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(
                RebootSpawnDyeRecipe::new
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
                RebootSpawnDyeRecipe::new,
                ShapelessRecipe::getGroup,
                ShapelessRecipe::category,
                recipe -> recipe.getResultItem(null),
                ShapelessRecipe::getIngredients
        );

        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: RebootSpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public MapCodec<RebootSpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RebootSpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: RebootSpawnDyeRecipeSerializer