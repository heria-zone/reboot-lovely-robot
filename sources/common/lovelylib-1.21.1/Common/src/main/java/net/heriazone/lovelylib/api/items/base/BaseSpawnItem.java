package net.heriazone.lovelylib.api.items.base;

import net.heriazone.lovelylib.api.items.utils.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Abstract base class for spawn items providing common interaction behavior.
 * <p>
 * <b>Architecture:</b> Implements the Template Method pattern to provide consistent
 * spawn item behavior across all loaders while allowing loader-specific customization
 * of entity type access and hit result calculation.
 * <p>
 * <b>Delegation Strategy:</b> Delegates core interaction logic to ItemInteractionHelper
 * while providing abstract methods for loader-specific operations. This maintains
 * consistency while respecting loader inheritance requirements.
 * <p>
 * <b>Design Decision:</b> Uses composition over inheritance for core logic to avoid
 * multiple inheritance issues while still providing a clean base class interface.
 */
public abstract class BaseSpawnItem {

    // -- Abstract Methods --

    /**
     * Gets the entity type from the given ItemStack.
     * <p>
     * <b>Loader Abstraction:</b> Each loader has different mechanisms for storing
     * and retrieving entity types from spawn eggs (direct reference, supplier, etc.).
     *
     * @param itemStack the ItemStack to get entity type from
     * @return the EntityType for spawning
     */
    protected abstract EntityType<?> getEntityType(ItemStack itemStack);

    /**
     * Performs player point-of-view hit result calculation.
     * <p>
     * <b>Access Abstraction:</b> Provides access to the protected getPlayerPOVHitResult
     * method from the concrete item implementation.
     *
     * @param level the level to perform raycast in
     * @param player the player performing the raycast
     * @param fluidMode the fluid handling mode for raycast
     * @return BlockHitResult from player's point of view
     */
    protected abstract net.minecraft.world.phys.BlockHitResult getPlayerPOVHitResult(
            Level level, Player player, net.minecraft.world.level.ClipContext.Fluid fluidMode);

    // -- Common Behavior Implementation --

    /**
     * Handles tooltip generation for spawn items.
     * <p>
     * <b>Consistent Behavior:</b> Provides uniform tooltip display across all loaders
     * by delegating to ItemInteractionHelper for data extraction and formatting.
     *
     * @param stack the ItemStack to generate tooltip for
     * @param context tooltip context (nullable)
     * @param tooltip mutable list to append tooltip lines to
     * @param flag tooltip flag indicating detail level
     */
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        // Delegate to common helper for consistent tooltip behavior
        ItemInteractionHelper.appendHoverText(stack, context, tooltip, flag);
    } // appendHoverText()

    /**
     * Handles right-click interaction for liquid spawning.
     * <p>
     * <b>Consistent Behavior:</b> Provides uniform liquid spawning logic across all
     * loaders by delegating to ItemInteractionHelper with loader-specific providers.
     *
     * @param level the level where interaction occurs
     * @param player the player performing the interaction
     * @param hand the hand used for interaction
     * @return interaction result indicating success, failure, or pass
     */
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        // Delegate to common helper with loader-specific providers
        return ItemInteractionHelper.handleLiquidSpawn(level, player, hand, itemStack,
                this::getEntityType,
                this::getPlayerPOVHitResult);
    } // use()

    /**
     * Handles right-click on block interaction for spawning and spawner configuration.
     * <p>
     * <b>Consistent Behavior:</b> Provides uniform block placement logic across all
     * loaders by delegating to ItemInteractionHelper with loader-specific entity type provider.
     *
     * @param context use context containing level, position, player, and item information
     * @return interaction result indicating success, failure, or consumption
     */
    public InteractionResult useOn(UseOnContext context) {
        // Delegate to common helper with loader-specific entity type provider
        return ItemInteractionHelper.handleBlockPlacement(context, this::getEntityType);
    } // useOn()

    // -- Utility Methods --

    /**
     * Validates spawn item data integrity.
     * <p>
     * <b>Data Validation:</b> Ensures spawn item contains valid data for entity spawning,
     * providing a consistent validation interface across all loaders.
     *
     * @param itemStack the ItemStack to validate
     * @return true if item is valid or was successfully corrected
     */
    public boolean validateSpawnItem(ItemStack itemStack) {
        return ItemSpawnHelper.validateSpawnItem(itemStack);
    } // validateSpawnItem()

    /**
     * Checks if ItemStack represents a valid spawn item.
     * <p>
     * <b>Completeness Check:</b> Verifies item has required data for spawning,
     * providing consistent validation across all loaders.
     *
     * @param itemStack the ItemStack to check
     * @return true if item is valid for spawning
     */
    public boolean isValidSpawnItem(ItemStack itemStack) {
        return ItemSpawnHelper.isValidSpawnItem(itemStack);
    } // isValidSpawnItem()

} // Class: BaseSpawnItem