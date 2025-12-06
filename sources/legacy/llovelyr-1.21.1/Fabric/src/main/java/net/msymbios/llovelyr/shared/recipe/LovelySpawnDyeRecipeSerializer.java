package net.msymbios.llovelyr.shared.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;

/**
 * Serializer for spawn egg dye recipes with data transfer.
 * <p>
 * <b>Architecture:</b> Delegates to vanilla ShapelessRecipe codec for ingredient parsing,
 * then wraps result in custom recipe class for data transfer logic.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 uses codec-based serialization. This
 * serializer leverages vanilla ShapelessRecipe.Serializer codec to handle ingredient
 * parsing and validation, avoiding duplication of complex validation logic.
 * <p>
 * <b>Design Decision:</b> Delegation approach matches 1.20.1 pattern and ensures
 * compatibility with vanilla recipe format while adding custom behavior for
 * preserving spawn egg data during dyeing. Uses supplier pattern to break cyclical
 * dependency with registry class.
 */
public class LovelySpawnDyeRecipeSerializer implements RecipeSerializer<LovelySpawnDyeRecipe> {

    // -- Variables --

    private final MapCodec<LovelySpawnDyeRecipe> codecInstance;
    private final StreamCodec<RegistryFriendlyByteBuf, LovelySpawnDyeRecipe> streamCodecInstance;

    // -- Constructor --

    public LovelySpawnDyeRecipeSerializer() {
        this.codecInstance = RecipeSerializer.SHAPELESS_RECIPE.codec()
                .xmap(
                        // Convert ShapelessRecipe to LovelySpawnDyeRecipe
                        shapelessRecipe -> new LovelySpawnDyeRecipe(
                                shapelessRecipe.getGroup(),
                                shapelessRecipe.category(),
                                shapelessRecipe.getResultItem(null),
                                shapelessRecipe.getIngredients()
                        ),
                        // Convert LovelySpawnDyeRecipe back to ShapelessRecipe for encoding
                        lovelyRecipe -> new ShapelessRecipe(
                                lovelyRecipe.getGroup(),
                                lovelyRecipe.category(),
                                lovelyRecipe.getResultItem(null),
                                lovelyRecipe.getIngredients()
                        )
                );
        
        this.streamCodecInstance = StreamCodec.of(this::toNetwork, this::fromNetwork);
    } // Constructor: LovelySpawnDyeRecipeSerializer ()

    // -- Inherited Methods --

    @Override
    public MapCodec<LovelySpawnDyeRecipe> codec() {
        return codecInstance;
    } // codec()

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, LovelySpawnDyeRecipe> streamCodec() {
        return streamCodecInstance;
    } // streamCodec()

    // -- Network Serialization --

    private LovelySpawnDyeRecipe fromNetwork(RegistryFriendlyByteBuf buf) {
        String group = buf.readUtf();
        CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
        int ingredientCount = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
        ingredients.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
        ItemStack result = ItemStack.STREAM_CODEC.decode(buf);
        return new LovelySpawnDyeRecipe(group, category, result, ingredients);
    } // fromNetwork()

    private void toNetwork(RegistryFriendlyByteBuf buf, LovelySpawnDyeRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.category());
        buf.writeVarInt(recipe.getIngredients().size());

        for (Ingredient ingredient : recipe.getIngredients()) {
            Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
        }

        ItemStack.STREAM_CODEC.encode(buf, recipe.getResultItem(null));
    } // toNetwork()

} // Class: LovelySpawnDyeRecipeSerializer
