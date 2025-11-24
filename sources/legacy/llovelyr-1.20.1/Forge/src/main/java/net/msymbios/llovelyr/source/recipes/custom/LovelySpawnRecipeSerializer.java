package net.msymbios.llovelyr.source.recipes.custom;

import com.google.gson.JsonObject;
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
 * Serializer for spawn egg crafting recipes with NBT transfer.
 * <p>
 * <b>Architecture:</b> Handles JSON and packet serialization for shaped
 * crafting recipes, delegating to ShapedRecipe patterns while wrapping in
 * custom recipe class.
 * <p>
 * <b>Forge Adaptation:</b> Uses Forge's recipe serialization API with
 * FriendlyByteBuf instead of PacketByteBuf.
 */
public class LovelySpawnRecipeSerializer implements RecipeSerializer<LovelySpawnRecipe> {

    // -- Inherited Methods --

    @Override
    public LovelySpawnRecipe fromJson(ResourceLocation id, JsonObject json) {
        String group = GsonHelper.getAsString(json, "group", "");
        CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
            GsonHelper.getAsString(json, "category", null), CraftingBookCategory.MISC);
        
        // Delegate to ShapedRecipe for pattern parsing
        ShapedRecipe baseRecipe = RecipeSerializer.SHAPED_RECIPE.fromJson(id, json);
        
        return new LovelySpawnRecipe(
            id,
            group,
            category,
            baseRecipe.getWidth(),
            baseRecipe.getHeight(),
            baseRecipe.getIngredients(),
            baseRecipe.getResultItem(null)
        );
    } // fromJson()

    @Override
    public LovelySpawnRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        String group = buf.readUtf();
        CraftingBookCategory category = buf.readEnum(CraftingBookCategory.class);
        int width = buf.readVarInt();
        int height = buf.readVarInt();
        NonNullList<Ingredient> ingredients = NonNullList.withSize(width * height, Ingredient.EMPTY);
        
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.fromNetwork(buf));
        }
        
        ItemStack result = buf.readItem();
        return new LovelySpawnRecipe(id, group, category, width, height, ingredients, result);
    } // fromNetwork()

    @Override
    public void toNetwork(FriendlyByteBuf buf, LovelySpawnRecipe recipe) {
        buf.writeUtf(recipe.getGroup());
        buf.writeEnum(recipe.category());
        buf.writeVarInt(recipe.getWidth());
        buf.writeVarInt(recipe.getHeight());
        
        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.toNetwork(buf);
        }
        
        buf.writeItem(recipe.getResultItem(null));
    } // toNetwork()

} // Class: LovelySpawnRecipeSerializer
