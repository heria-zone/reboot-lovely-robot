package net.heriazone.rlovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.api.recipes.BaseLovelySpawnRecipe;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.NetworkSerializationHelper;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.RecipeCodecHelper;
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
public class RebootSpawnRecipeSerializer implements RecipeSerializer<RebootSpawnRecipe> {

    // -- Fields --

    private final Item robotCoreItem;
    private final MapCodec<RebootSpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, RebootSpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public RebootSpawnRecipeSerializer(Item robotCoreItem) {
        this.robotCoreItem = robotCoreItem;

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