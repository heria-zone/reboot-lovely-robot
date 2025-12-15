package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.msymbios.llovelyr.lib.recipes.serializers.BaseRecipeSerializer;
import net.msymbios.llovelyr.lib.recipes.serializers.RecipeCodecHelper;
import net.msymbios.llovelyr.lib.recipes.serializers.NetworkSerializationHelper;
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
public class LovelySpawnRecipeSerializer implements RecipeSerializer<LovelySpawnRecipe> {

    // -- Fields --

    private final BaseRecipeSerializer commonSerializer;
    private final MapCodec<LovelySpawnRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LovelySpawnRecipe> streamCodecInstance;

    // -- Constructor --

    public LovelySpawnRecipeSerializer(Item robotCoreItem) {
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
        
        this.commonSerializer = new BaseRecipeSerializer<>(
                () -> this.codecInstance,
                this.streamCodecInstance
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
