package net.heriazone.rlovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.hzlib.api.recipes.serializers.RecipeCodecHelper;
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
public class RebootSpawnDyeRecipeSerializer implements RecipeSerializer<RebootSpawnDyeRecipe> {

    // -- Fields --

    private final MapCodec<RebootSpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, RebootSpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public RebootSpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(RebootSpawnDyeRecipe::new);

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
                RebootSpawnDyeRecipe::new,
                ShapelessRecipe::getGroup,
                ShapelessRecipe::category,
                recipe -> recipe.getResultItem(null),
                ShapelessRecipe::getIngredients
        );
    } // Constructor: RebootSpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<RebootSpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, RebootSpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: RebootSpawnDyeRecipeSerializer