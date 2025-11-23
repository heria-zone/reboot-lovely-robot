package net.msymbios.llovelyr.source.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

/**
 * Mixin accessor exposing private ShapedRecipe.Serializer methods.
 * <p>
 * <b>Architecture:</b> Provides access to private recipe parsing methods in
 * ShapedRecipe.Serializer needed for custom recipe serializers.
 * <p>
 * <b>Usage Pattern:</b> Cast ShapedRecipe.Serializer instance to this accessor
 * interface to call private parsing methods.
 */
@Mixin(ShapedRecipe.Serializer.class)
public interface IShapedRecipeSerializerAccessor {

    // -- Methods --

    /**
     * Invokes private readSymbols method to parse ingredient key map.
     *
     * @param json key JSON object mapping symbols to ingredients
     * @return map of symbols to ingredients
     */
    @Invoker("readSymbols")
    Map<String, Ingredient> invokeReadSymbols(JsonObject json);

    /**
     * Invokes private getPattern method to parse pattern array.
     *
     * @param json pattern JSON array
     * @return pattern strings
     */
    @Invoker("getPattern")
    String[] invokeGetPattern(JsonArray json);

    /**
     * Invokes private removePadding method to trim pattern whitespace.
     *
     * @param pattern raw pattern strings
     * @return trimmed pattern strings
     */
    @Invoker("removePadding")
    String[] invokeRemovePadding(String... pattern);

    /**
     * Invokes private createPatternMatrix method to build ingredient list.
     *
     * @param pattern pattern strings
     * @param key symbol to ingredient map
     * @param width pattern width
     * @param height pattern height
     * @return ingredient list matching pattern
     */
    @Invoker("createPatternMatrix")
    DefaultedList<Ingredient> invokeCreatePatternMatrix(String[] pattern, Map<String, Ingredient> key, int width, int height);

} // Interface: IShapedRecipeSerializerAccessor
