package net.heriazone.llovelyr.recipes;

import com.mojang.serialization.MapCodec;
import net.heriazone.lovelylib.hzlib.api.recipes.serializers.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
public class LegacySpawnRecipeSerializer implements RecipeSerializer<LegacySpawnRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<LegacySpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LegacySpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public LegacySpawnRecipeSerializer(Item robotCoreItem) {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapedRecipeCodec(
                (group, category, pattern, result) ->
                        new LegacySpawnRecipe(group, category, pattern, result, robotCoreItem)
        );

        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapedRecipeNetworkCodec(
                (group, category, pattern, result) ->
                        new LegacySpawnRecipe(group, category, pattern, result, robotCoreItem),
                recipe -> recipe.getGroup(),
                recipe -> recipe.category(),
                recipe -> recipe.pattern(),
                recipe -> recipe.getResultItem(null)
        );

        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: LegacySpawnRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<LegacySpawnRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, LegacySpawnRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: LegacySpawnRecipeSerializer