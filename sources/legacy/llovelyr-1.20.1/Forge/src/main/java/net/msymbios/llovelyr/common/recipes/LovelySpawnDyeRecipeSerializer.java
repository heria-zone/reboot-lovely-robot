package net.msymbios.llovelyr.source.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Serializer for spawn egg dye recipes with NBT transfer.
 * <p>
 * <b>Architecture:</b> Handles JSON and packet serialization for shapeless
 * dye recipes, parsing ingredients directly without mixin dependency.
 * <p>
 * <b>Forge Adaptation:</b> Uses Forge's recipe serialization API with
 * FriendlyByteBuf instead of PacketByteBuf.
 */
public class LovelySpawnDyeRecipeSerializer implements RecipeSerializer<LovelySpawnDyeRecipe> {

    // -- Inherited Methods --

    @Override
    public LovelySpawnDyeRecipe fromJson(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
            GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
        
        // Parse ingredients array
        JsonArray ingredientsJson = GsonHelper.getAsJsonArray(json, "ingredients");
        if (ingredientsJson.size() == 0) {
            throw new JsonSyntaxException("No ingredients for shapeless recipe");
        }
        if (ingredientsJson.size() > 9) {
            throw new JsonSyntaxException("Too many ingredients for shapeless recipe");
        }
        
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientsJson.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredientsJson.size(); i++) {
            ingredients.set(i, Ingredient.fromJson(ingredientsJson.get(i)));
        }
        
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        
        return new LovelySpawnDyeRecipe(id, group, category, result, ingredients);
    } // fromJson()

    @Override
    public LovelySpawnDyeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
        int ingredientCount = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(ingredientCount, Ingredient.EMPTY);
        
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.fromNetwork(buf));
        }
        
        ItemStack result = buf.readItem();
        return new LovelySpawnDyeRecipe(id, group, category, result, ingredients);
    } // fromNetwork()

    @Override
    public void toNetwork(FriendlyByteBuf buf, LovelySpawnDyeRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.category());
        buf.writeVarInt(recipe.getIngredients().size());
        
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buf);
        }
        
        buf.writeItem(recipe.getResultItem(null));
    } // toNetwork()

} // Class: LovelySpawnDyeRecipeSerializer
