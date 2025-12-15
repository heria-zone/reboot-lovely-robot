package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.msymbios.llovelyr.lib.recipes.serializers.RecipeCodecHelper;
import net.msymbios.llovelyr.lib.recipes.serializers.NetworkSerializationHelper;
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
public class LovelySpawnRecipeSerializer implements RecipeSerializer<LovelySpawnRecipe> {

    // -- Fields --

    private final Item robotCoreItem;
    private final MapCodec<LovelySpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LovelySpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public LovelySpawnRecipeSerializer(Item robotCoreItem) {
        this.robotCoreItem = robotCoreItem;
        
        // Delegate codec creation to Common helper
        this.codecInstance = RecipeCodecHelper.createShapedRecipeCodec(
            (group, category, pattern, result) -> 
                new LovelySpawnRecipe(group, category, pattern, result, robotCoreItem)
        );
        
        // Delegate network serialization to Common helper
        this.streamCodecInstance = NetworkSerializationHelper.createShapedRecipeNetworkCodec(
            (group, category, pattern, result) -> 
                new LovelySpawnRecipe(group, category, pattern, result, robotCoreItem),
            recipe -> recipe.getGroup(),
            recipe -> recipe.category(),
            recipe -> recipe.pattern(),
            recipe -> recipe.getResultItem(null)
        );
    } // Constructor: LovelySpawnRecipeSerializer()

    // -- RecipeSerializer Implementation --

    @Override
    public @NotNull MapCodec<LovelySpawnRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public @NotNull StreamCodec<RegistryFriendlyByteBuf, LovelySpawnRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

} // Class: LovelySpawnRecipeSerializer
