package net.heriazone.lovelylib.api.recipes.utils;

import net.heriazone.lovelylib.utils.ValidationUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

/**
 * Provides validation utilities for recipe processing and ingredient checking.
 * <p>
 * <b>Architecture:</b> Centralizes recipe validation logic that was duplicated
 * across loader implementations. Ensures consistent validation behavior and
 * prevents invalid recipe states.
 * <p>
 * <b>Design Philosophy:</b> Fail-safe validation that provides detailed feedback
 * about validation failures while maintaining performance for valid cases.
 */
public class RecipeValidationUtils {

    // -- Ingredient Validation --

    /**
     * Validates that crafting input contains required ingredient.
     * <p>
     * <b>Usage:</b> Used by recipe strategies to ensure required items are present
     * before attempting data transfer operations.
     *
     * @param input crafting grid input
     * @param requiredItem item that must be present
     * @return true if ingredient is found in crafting grid
     */
    public static boolean hasRequiredIngredient(CraftingInput input, Item requiredItem) {
        if (input == null || requiredItem == null) return false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(requiredItem)) {
                return true;
            }
        }
        return false;
    } // hasRequiredIngredient()

    /**
     * Validates that crafting input contains any of the specified ingredients.
     * <p>
     * <b>Usage:</b> Used for recipes that accept multiple valid ingredients,
     * such as dye recipes that accept any dye color.
     *
     * @param input crafting grid input
     * @param validItems list of acceptable items
     * @return true if any valid ingredient is found
     */
    public static boolean hasAnyRequiredIngredient(CraftingInput input, List<Item> validItems) {
        if (input == null || validItems == null || validItems.isEmpty()) return false;

        for (Item item : validItems) {
            if (hasRequiredIngredient(input, item)) {
                return true;
            }
        }
        return false;
    } // hasAnyRequiredIngredient()

    /**
     * Finds first ItemStack in crafting input that matches specified item.
     * <p>
     * <b>Usage:</b> Used by recipe strategies to locate source items for
     * data transfer operations.
     *
     * @param input crafting grid input
     * @param targetItem item to search for
     * @return first matching ItemStack, or ItemStack.EMPTY if not found
     */
    public static ItemStack findIngredient(CraftingInput input, Item targetItem) {
        if (input == null || targetItem == null) return ItemStack.EMPTY;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty() && stack.is(targetItem)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    } // findIngredient()

    /**
     * Finds first ItemStack in crafting input that matches any of the specified items.
     * <p>
     * <b>Usage:</b> Used for recipes with multiple valid source items.
     *
     * @param input crafting grid input
     * @param validItems list of acceptable items
     * @return first matching ItemStack, or ItemStack.EMPTY if none found
     */
    public static ItemStack findAnyIngredient(CraftingInput input, List<Item> validItems) {
        if (input == null || validItems == null || validItems.isEmpty()) return ItemStack.EMPTY;

        for (Item item : validItems) {
            ItemStack found = findIngredient(input, item);
            if (!found.isEmpty()) {
                return found;
            }
        }
        return ItemStack.EMPTY;
    } // findAnyIngredient()

    // -- Recipe Result Validation --

    /**
     * Validates recipe result ItemStack for robot operations.
     * <p>
     * <b>Validation:</b> Ensures result is valid for robot data transfer and
     * can safely receive custom data components.
     *
     * @param result recipe result ItemStack
     * @return true if result is valid for robot operations
     */
    public static boolean isValidRecipeResult(ItemStack result) {
        return ValidationUtils.isValidRobotItemStack(result);
    } // isValidRecipeResult()

    // -- Ingredient Pattern Validation --

    /**
     * Validates that ingredient pattern matches expected structure.
     * <p>
     * <b>Usage:</b> Used during recipe registration to ensure ingredient
     * patterns are correctly configured.
     *
     * @param ingredients ingredient array to validate
     * @param expectedCount expected number of ingredients
     * @return validation result with details
     */
    public static ValidationUtils.ValidationResult validateIngredientPattern(
            net.minecraft.core.NonNullList<Ingredient> ingredients, int expectedCount) {

        if (ingredients == null) {
            return new ValidationUtils.ValidationResult(false, "Ingredients list is null");
        }

        if (ingredients.size() != expectedCount) {
            return new ValidationUtils.ValidationResult(false,
                    String.format("Expected %d ingredients, found %d", expectedCount, ingredients.size()));
        }

        // Check for empty ingredients
        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ingredient = ingredients.get(i);
            if (ingredient == null || ingredient.isEmpty()) {
                return new ValidationUtils.ValidationResult(false,
                        String.format("Ingredient at index %d is null or empty", i));
            }
        }

        return new ValidationUtils.ValidationResult(true, "");
    } // validateIngredientPattern()

    // -- Crafting Grid Validation --

    /**
     * Validates crafting input structure and contents.
     * <p>
     * <b>Comprehensive Check:</b> Validates grid size, item presence, and
     * data integrity for recipe processing.
     *
     * @param input crafting grid input
     * @param expectedItems expected number of non-empty items
     * @return validation result with details
     */
    public static ValidationUtils.ValidationResult validateCraftingInput(CraftingInput input, int expectedItems) {
        if (input == null) {
            return new ValidationUtils.ValidationResult(false, "Crafting input is null");
        }

        int nonEmptyCount = 0;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                nonEmptyCount++;

                // Validate each non-empty stack
                if (!ValidationUtils.isValidRobotItemStack(stack)) {
                    return new ValidationUtils.ValidationResult(false,
                            String.format("Invalid item stack at position %d", i));
                }
            }
        }

        if (nonEmptyCount != expectedItems) {
            return new ValidationUtils.ValidationResult(false,
                    String.format("Expected %d items, found %d", expectedItems, nonEmptyCount));
        }

        return new ValidationUtils.ValidationResult(true, "");
    } // validateCraftingInput()

    // -- Item Compatibility Validation --

    /**
     * Validates that two items are compatible for data transfer operations.
     * <p>
     * <b>Usage:</b> Ensures source and target items can safely participate
     * in NBT/data component transfer without type conflicts.
     *
     * @param sourceItem source item for data transfer
     * @param targetItem target item for data transfer
     * @return true if items are compatible for data transfer
     */
    public static boolean areItemsCompatibleForTransfer(Item sourceItem, Item targetItem) {
        if (sourceItem == null || targetItem == null) return false;

        // Both items should be robot-related items that can hold custom data
        // This is a basic check - more specific validation could be added
        return true; // For now, allow all transfers
    } // areItemsCompatibleForTransfer()

    /**
     * Validates that ItemStack can be used as recipe ingredient.
     * <p>
     * <b>Checks:</b> Non-null, non-empty, valid for robot operations.
     *
     * @param stack ItemStack to validate
     * @return true if stack is valid ingredient
     */
    public static boolean isValidIngredient(ItemStack stack) {
        return stack != null && !stack.isEmpty() && ValidationUtils.isValidRobotItemStack(stack);
    } // isValidIngredient()

    // -- Recipe Configuration Validation --

    /**
     * Validates recipe configuration parameters.
     * <p>
     * <b>Usage:</b> Used during recipe registration to ensure all parameters
     * are correctly configured and compatible.
     *
     * @param group recipe group identifier
     * @param category crafting book category
     * @param result recipe result item
     * @return validation result with details
     */
    public static ValidationUtils.ValidationResult validateRecipeConfiguration(
            String group, Object category, ItemStack result) {

        StringBuilder issues = new StringBuilder();
        boolean isValid = true;

        // Validate group
        if (group == null || group.trim().isEmpty()) {
            issues.append("Recipe group is null or empty; ");
            isValid = false;
        }

        // Validate category
        if (category == null) {
            issues.append("Crafting category is null; ");
            isValid = false;
        }

        // Validate result
        if (!isValidRecipeResult(result)) {
            issues.append("Recipe result is invalid; ");
            isValid = false;
        }

        return new ValidationUtils.ValidationResult(isValid, issues.toString());
    } // validateRecipeConfiguration()

    /**
     * Validates codec-decoded recipe data for consistency.
     * <p>
     * <b>Usage:</b> Called after codec deserialization to ensure recipe data
     * is valid and can be safely used for crafting operations.
     * <p>
     * <b>Integration:</b> Uses existing RecipeValidationUtils for consistency
     * with other recipe validation throughout the system.
     *
     * @param group recipe group identifier
     * @param result recipe result ItemStack
     * @param ingredients recipe ingredients (can be null for shaped recipes)
     * @return true if recipe data is valid
     */
    public static boolean validateRecipeData(String group, ItemStack result,
                                             NonNullList<Ingredient> ingredients) {
        // Validate group
        if (group == null) return false;

        // Validate result
        if (!RecipeValidationUtils.isValidRecipeResult(result)) return false;

        // Validate ingredients if provided
        if (ingredients != null) {
            var validationResult = RecipeValidationUtils.validateIngredientPattern(ingredients, ingredients.size());
            if (!validationResult.isValid()) return false;
        }

        return true;
    } // validateRecipeData()

} // Class: RecipeValidationUtils