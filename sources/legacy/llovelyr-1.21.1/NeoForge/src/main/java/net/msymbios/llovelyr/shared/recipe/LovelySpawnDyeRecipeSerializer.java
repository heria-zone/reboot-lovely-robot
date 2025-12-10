package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.msymbios.llovelyr.lib.recipes.serializers.BaseRecipeSerializer;
import net.msymbios.llovelyr.lib.recipes.serializers.RecipeCodecHelper;
import net.msymbios.llovelyr.lib.recipes.serializers.NetworkSerializationHelper;

/**
 * NeoForge wrapper for spawn egg dye recipe serialization.
 * <p>
 * <b>Architecture:</b> Thin wrapper around Common BaseRecipeSerializer that handles
 * NeoForge-specific codec and stream codec interfaces. Delegates business logic
 * to Common module while keeping loader-specific dependencies in NeoForge.
 * <p>
 * <b>Design Decision:</b> Wrapper pattern maintains loader isolation while
 * extracting reusable recipe serialization logic to Common module.
 */
public class LovelySpawnDyeRecipeSerializer implements RecipeSerializer<LovelySpawnDyeRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
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
        
        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
        );
    } // Constructor: LovelySpawnDyeRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public MapCodec<LovelySpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LovelySpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()



} // Class: LovelySpawnDyeRecipeSerializer
