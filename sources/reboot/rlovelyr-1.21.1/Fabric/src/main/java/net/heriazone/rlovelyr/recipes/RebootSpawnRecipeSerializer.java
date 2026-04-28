package net.heriazone.rlovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnRecipe;
import net.heriazone.hzlib.api.recipes.serializers.BaseRecipeSerializer;
import net.heriazone.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.hzlib.api.recipes.serializers.RecipeCodecHelper;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import org.jetbrains.annotations.NotNull;

/**
 * Fabric wrapper for spawn egg crafting recipe serialization.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common BaseRecipeSerializer that handles
 * Fabric-specific codec and stream codec interfaces. Delegates business logic
 * to Common module while keeping loader-specific dependencies in Fabric.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains loader isolation while
 * extracting reusable recipe serialization logic to Common module.
 */
public class RebootSpawnRecipeSerializer implements RecipeSerializer<RebootSpawnRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<RebootSpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, RebootSpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public RebootSpawnRecipeSerializer(Item robotCoreItem) {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapedRecipeCodec(
                (group, category, pattern, result) ->
                        new RebootSpawnRecipe(group, category, pattern, result, robotCoreItem)
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapedRecipeNetworkCodec(
                (group, category, pattern, result) ->
                        new RebootSpawnRecipe(group, category, pattern, result, robotCoreItem),
                ShapedRecipe::getGroup,
                ShapedRecipe::category,
                BaseLovelySpawnRecipe::pattern,
                recipe -> recipe.getResultItem(null)
        );

        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: RebootSpawnRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<RebootSpawnRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, RebootSpawnRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: RebootSpawnRecipeSerializer