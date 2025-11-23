package net.msymbios.llovelyr.source.recipes;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.msymbios.llovelyr.source.mixin.IShapedRecipeAccessor;

import java.util.Map;

/**
 * Serializer for spawn egg crafting recipes with NBT transfer.
 * <p>
 * <b>Architecture:</b> Handles JSON and packet serialization for custom recipe type,
 * delegating to ShapedRecipe patterns while wrapping in custom class.
 */
public class SpawnEggCraftingRecipeSerializer implements RecipeSerializer<SpawnEggCraftingRecipe> {

    // -- Inherited Methods --

    @Override
    @SuppressWarnings("UnreachableCode")
    public SpawnEggCraftingRecipe read(Identifier id, JsonObject json) {
        String group = JsonHelper.getString(json, "group", "");
        CraftingRecipeCategory category = CraftingRecipeCategory.CODEC.byId(
            JsonHelper.getString(json, "category", null), CraftingRecipeCategory.MISC);
        
        Map<String, Ingredient> key = IShapedRecipeAccessor.invokeReadSymbols(JsonHelper.getObject(json, "key"));
        String[] pattern = IShapedRecipeAccessor.invokeRemovePadding(
            IShapedRecipeAccessor.invokeGetPattern(JsonHelper.getArray(json, "pattern")));
        int width = pattern[0].length();
        int height = pattern.length;
        DefaultedList<Ingredient> ingredients = IShapedRecipeAccessor.invokeCreatePatternMatrix(pattern, key, width, height);
        ItemStack result = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
        
        return new SpawnEggCraftingRecipe(id, group, category, width, height, ingredients, result);
    } // read

    @Override
    public SpawnEggCraftingRecipe read(Identifier id, PacketByteBuf buf) {
        String group = buf.readString();
        CraftingRecipeCategory category = buf.readEnumConstant(CraftingRecipeCategory.class);
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(width * height, Ingredient.EMPTY);
        
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.fromPacket(buf));
        }
        
        ItemStack result = buf.readItemStack();
        return new SpawnEggCraftingRecipe(id, group, category, width, height, ingredients, result);
    } // read

    @Override
    public void write(PacketByteBuf buf, SpawnEggCraftingRecipe recipe) {
        buf.writeString(recipe.getGroup());
        buf.writeEnumConstant(recipe.getCategory());
        buf.writeVarInt(recipe.getWidth());
        buf.writeVarInt(recipe.getHeight());
        
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.write(buf);
        }
        
        buf.writeItemStack(recipe.getOutput(null));
    } // write

} // Class: SpawnEggCraftingRecipeSerializer
