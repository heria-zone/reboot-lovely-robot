package net.msymbios.llovelyr.lib.entity.features;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.HashSet;
import java.util.Set;

/**
 * <p>Manages food items and tempting items for entity feeding and interaction.</p>
 * <p>
 * <b>Architecture:</b> Provides composable food management that can be attached to any entity type
 * through the feature system. Separates food items (for healing/feeding) from tempting items
 * (for breeding/following behavior).
 * <p>
 * <b>Design Decision:</b> Maintains separate sets for foods and tempting items to support
 * distinct behavioral patterns. An item can be both food and tempting, or only one.
 * <p>
 * <b>Use Case:</b> Enables entities to have configurable food preferences without hardcoding
 * item lists in entity classes.
 */
public class FoodFeature {

    // -- Fields --
    
    private final Set<Item> foods;
    private final Set<Item> temptingItems;

    // -- Constructors --
    
    /**
     * Creates empty food feature with no configured foods or tempting items.
     */
    public FoodFeature() {
        this.foods = new HashSet<>();
        this.temptingItems = new HashSet<>();
    }

    // -- Food Management --
    
    /**
     * Adds items to food set.
     * <p>
     * <b>State Impact:</b> Items added to foods set can be used for healing/feeding behavior.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param items items to add as food
     * @return this feature instance for chaining
     */
    public FoodFeature withFoods(Item... items) {
        for (Item item : items) {
            if (item != null) {
                this.foods.add(item);
            }
        }
        return this;
    }

    /**
     * Adds items to tempting items set.
     * <p>
     * <b>State Impact:</b> Items added to tempting set can be used for breeding/following behavior.
     * <p>
     * <b>Fluent API:</b> Returns this instance for method chaining.
     * 
     * @param items items to add as tempting
     * @return this feature instance for chaining
     */
    public FoodFeature withTemptingItems(Item... items) {
        for (Item item : items) {
            if (item != null) {
                this.temptingItems.add(item);
            }
        }
        return this;
    }

    // -- Food Queries --
    
    /**
     * Checks if specified item is registered as food.
     * 
     * @param item item to check
     * @return true if item is in foods set, false otherwise
     */
    public boolean isFood(Item item) {
        return this.foods.contains(item);
    }

    /**
     * Checks if item in stack is registered as food.
     * <p>
     * <i>Note:</i> Convenience method that extracts item from stack.
     * 
     * @param stack item stack to check
     * @return true if stack's item is in foods set, false otherwise
     */
    public boolean isFood(ItemStack stack) {
        return stack != null && isFood(stack.getItem());
    }

    /**
     * Creates ingredient matching all registered food items.
     * <p>
     * <b>Use Case:</b> Useful for recipe integration or AI goal targeting.
     * 
     * @return ingredient matching all foods, or empty ingredient if no foods registered
     */
    public Ingredient getFoodIngredient() {
        if (this.foods.isEmpty()) {
            return Ingredient.EMPTY;
        }
        return Ingredient.of(this.foods.toArray(new Item[0]));
    }

    /**
     * Creates ingredient matching all registered tempting items.
     * <p>
     * <b>Use Case:</b> Useful for breeding/following AI goals.
     * 
     * @return ingredient matching all tempting items, or empty ingredient if none registered
     */
    public Ingredient getTemptingIngredient() {
        if (this.temptingItems.isEmpty()) {
            return Ingredient.EMPTY;
        }
        return Ingredient.of(this.temptingItems.toArray(new Item[0]));
    }

    // -- Accessors --
    
    /**
     * Returns unmodifiable view of registered food items.
     * <p>
     * <i>Note:</i> Returns defensive copy to prevent external modification.
     * 
     * @return set of food items
     */
    public Set<Item> getFoods() {
        return new HashSet<>(this.foods);
    }

    /**
     * Returns unmodifiable view of registered tempting items.
     * <p>
     * <i>Note:</i> Returns defensive copy to prevent external modification.
     * 
     * @return set of tempting items
     */
    public Set<Item> getTemptingItems() {
        return new HashSet<>(this.temptingItems);
    }

} // Class: FoodFeature
