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
 * Mixin accessor exposing private ShapedRecipe static methods.
 * <p>
 * <b>Architecture:</b> Provides access to private static recipe parsing methods
 * needed for custom recipe serializers that extend ShapedRecipe behavior.
 * <p>
 * <b>Usage Pattern:</b> Call static invoker methods directly to parse recipe JSON
 * using vanilla logic.
 */
@Mixin(ShapedRecipe.class)
public interface IShapedRecipeAccessor {

    // -- Methods --

    /**
     * Invokes private static readSymbols method to parse ingredient key map.
     *
     * @param json key JSON object mapping symbols to ingredients
     * @return map of symbols to ingredients
     */
    @Invoker("readSymbols")
    static Map<String, Ingredient> invokeReadSymbols(JsonObject json) {
        throw new AssertionError();
    }

    /**
     * Invokes private static getPattern method to parse pattern array.
     *
     * @param json pattern JSON array
     * @return pattern strings
     */
    @Invoker("getPattern")
    static String[] invokeGetPattern(JsonArray json) {
        throw new AssertionError();
    }

    /**
     * Invokes private static removePadding method to trim pattern whitespace.
     *
     * @param pattern raw pattern strings
     * @return trimmed pattern strings
     */
    @Invoker("removePadding")
    static String[] invokeRemovePadding(String... pattern) {
        throw new AssertionError();
    }

    /**
     * Invokes private static createPatternMatrix method to build ingredient list.
     *
     * @param pattern pattern strings
     * @param key symbol to ingredient map
     * @param width pattern width
     * @param height pattern height
     * @return ingredient list matching pattern
     */
    @Invoker("createPatternMatrix")
    static DefaultedList<Ingredient> invokeCreatePatternMatrix(String[] pattern, Map<String, Ingredient> key, int width, int height) {
        throw new AssertionError();
    }

} // Interface: IShapedRecipeAccessor
