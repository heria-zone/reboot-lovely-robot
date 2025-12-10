package net.msymbios.llovelyr.lib.recipes.serializers;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * Base class for recipe serializers with common serialization patterns.
 * <p>
 * <b>Architecture:</b> Provides shared serialization logic for recipe types
 * while allowing loader-specific implementations to handle platform APIs.
 * Separates business logic from codec/network serialization details.
 * <p>
 * <b>Design Decision:</b> Abstract base class allows sharing common patterns
 * while maintaining flexibility for different recipe types and loader
 * requirements.
 */
public abstract class BaseRecipeSerializer {

    // -- Fields --

    protected final Item robotCoreItem;

    // -- Constructor --

    /**
     * Creates base recipe serializer with robot core item reference.
     *
     * @param robotCoreItem item used as robot core in recipes
     */
    protected BaseRecipeSerializer(Item robotCoreItem) {
        this.robotCoreItem = robotCoreItem;
    } // Constructor: BaseRecipeSerializer()

    // -- Recipe Data Structures --

    /**
     * Common recipe data structure for serialization.
     */
    public static class RecipeData {
        private final String group;
        private final CraftingBookCategory category;
        private final ShapedRecipePattern pattern;
        private final ItemStack result;

        public RecipeData(String group, CraftingBookCategory category, ShapedRecipePattern pattern, ItemStack result) {
            this.group = group;
            this.category = category;
            this.pattern = pattern;
            this.result = result;
        }

        public String getGroup() {
            return group;
        }

        public CraftingBookCategory getCategory() {
            return category;
        }

        public ShapedRecipePattern getPattern() {
            return pattern;
        }

        public ItemStack getResult() {
            return result;
        }
    } // Class: RecipeData

    // -- Serialization Helpers --

    /**
     * Creates recipe data from components with validation.
     *
     * @param group recipe group (optional)
     * @param category crafting book category
     * @param pattern shaped recipe pattern
     * @param result result item stack
     * @return validated recipe data
     */
    protected RecipeData createRecipeData(String group, CraftingBookCategory category, 
                                        ShapedRecipePattern pattern, ItemStack result) {
        // Validate inputs
        String validGroup = group != null ? group : "";
        CraftingBookCategory validCategory = category != null ? category : CraftingBookCategory.MISC;
        
        if (pattern == null) {
            throw new IllegalArgumentException("Recipe pattern cannot be null");
        }
        
        if (result == null || result.isEmpty()) {
            throw new IllegalArgumentException("Recipe result cannot be null or empty");
        }
        
        return new RecipeData(validGroup, validCategory, pattern, result);
    } // createRecipeData()

    /**
     * Gets the robot core item for recipe validation.
     *
     * @return robot core item
     */
    protected Item getRobotCoreItem() {
        return robotCoreItem;
    } // getRobotCoreItem()

} // Class: BaseRecipeSerializer