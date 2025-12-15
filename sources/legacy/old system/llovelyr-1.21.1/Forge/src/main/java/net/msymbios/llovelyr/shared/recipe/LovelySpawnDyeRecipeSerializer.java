package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.msymbios.llovelyr.lib.recipes.serializers.RecipeCodecHelper;
import net.msymbios.llovelyr.lib.recipes.serializers.NetworkSerializationHelper;
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
public class LovelySpawnDyeRecipeSerializer implements RecipeSerializer<LovelySpawnDyeRecipe> {

    // -- Fields --

    private final MapCodec<LovelySpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LovelySpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public LovelySpawnDyeRecipeSerializer() {
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapelessRecipeCodec(
            (group, category, result, ingredients) -> 
                new LovelySpawnDyeRecipe(group, category, result, ingredients)
        );
        
        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapelessRecipeNetworkCodec(
            (group, category, result, ingredients) -> 
                new LovelySpawnDyeRecipe(group, category, result, ingredients),
            recipe -> recipe.getGroup(),
            recipe -> recipe.category(),
            recipe -> recipe.getResultItem(null),
            recipe -> recipe.getIngredients()
        );
    } // Constructor: LovelySpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<LovelySpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, LovelySpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: LovelySpawnDyeRecipeSerializer
