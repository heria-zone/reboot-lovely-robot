package net.msymbios.llovelyr.source.recipes.custom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;

/**
 * Serializer for spawn egg dye recipes with NBT transfer.
 * <p>
 * <b>Architecture:</b> Handles JSON and packet serialization for shapeless
 * dye recipes, parsing ingredients directly without mixin dependency.
 */
public class LovelySpawnDyeRecipeSerializer implements RecipeSerializer<LovelySpawnDyeRecipe> {

    // -- Inherited Methods --

    @Override
    public LovelySpawnDyeRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        CraftingRecipeCategory category = CraftingRecipeCategory.CODEC.byId(
            JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC);
        
        // Parse ingredients array
        JsonArray ingredientsJson = JsonHelper.getArray(json, "ingredients");
        if (ingredientsJson.size() == 0) {
            throw new JsonSyntaxException("No ingredients for shapeless recipe");
        }
        if (ingredientsJson.size() > 9) {
            throw new JsonSyntaxException("Too many ingredients for shapeless recipe");
        }
        
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientsJson.size(), Ingredient.EMPTY);
        for (int i = 0; i < ingredientsJson.size(); i++) {
            ingredients.set(i, Ingredient.fromJson(ingredientsJson.get(i)));
        }
        
        ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
        
        return new LovelySpawnDyeRecipe(id, group, category, result, ingredients);
    } // read

    @Override
    public LovelySpawnDyeRecipe read(Identifier id, PacketByteBuf buf) {
        String group = buf.readString();
        CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
        int ingredientCount = buf.readVarInt();
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(ingredientCount, Ingredient.EMPTY);
        
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.fromPacket(buf));
        }
        
        ItemStack result = buf.readItemStack();
        return new LovelySpawnDyeRecipe(id, group, category, result, ingredients);
    } // read

    @Override
    public void write(PacketByteBuf buf, LovelySpawnDyeRecipe recipe) {
        buf.writeString(recipe.getGroup());
        buf.writeEnumConstant(recipe.getCategory());
        buf.writeVarInt(recipe.getIngredients().size());
        
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }
        
        buf.writeItemStack(recipe.getOutput(null));
    } // write

} // Class: LovelySpawnDyeRecipeSerializer
