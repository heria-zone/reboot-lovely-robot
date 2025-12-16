package net.heriazone.lovelylib.common.items;

import net.heriazone.lovelylib.api.items.base.BaseSpawnItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

/**
 * Forge spawn egg for summoning robots with component-stored customization.
 * <p>
 * <b>Architecture:</b> Extends ForgeSpawnEggItem to leverage Minecraft's spawn
 * egg mechanics while delegating behavior to BaseSpawnItem for consistency
 * across loaders.
 * <p>
 * <b>Composition Pattern:</b> Uses composition with BaseSpawnItem to share
 * common behavior while maintaining Forge-specific inheritance requirements.
 * Provides thin wrapper around base functionality.
 * <p>
 * <b>Migration Note:</b> Minecraft 1.21.1 replaced NBT tags with typed data components.
 * Custom data is now stored in DataComponents.CUSTOM_DATA as a CustomData object.
 */
public class LovelySpawnItem extends ForgeSpawnEggItem {

    // -- Fields --

    private final BaseSpawnItemDelegate delegate;

    // -- Constructor --

    public LovelySpawnItem(Supplier<? extends EntityType<? extends Mob>> type, Item.Properties properties) {
        super(type, 0xFFFFFF, 0xFFFFFF, properties);
        this.delegate = new BaseSpawnItemDelegate();
    } // Constructor: LovelySpawnItem()

    // -- Inherited Methods --

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);

        // Delegate to base spawn item behavior
        delegate.appendHoverText(stack, context, tooltip, flag);
    } // appendHoverText()

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // Delegate to base spawn item behavior
        return delegate.use(level, player, hand);
    } // use()

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Delegate to base spawn item behavior
        return delegate.useOn(context);
    } // useOn()

    // -- Inner Delegate Class --

    /**
     * Delegate class that extends BaseSpawnItem to provide Forge-specific implementations.
     * <p>
     * <b>Delegation Pattern:</b> Allows composition with BaseSpawnItem while maintaining
     * access to the outer class's methods for loader-specific operations.
     */
    private class BaseSpawnItemDelegate extends BaseSpawnItem {

        @Override
        protected EntityType<?> getEntityType(ItemStack itemStack) {
            return LovelySpawnItem.this.getType(itemStack);
        } // getEntityType()

        @Override
        protected net.minecraft.world.phys.BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid fluidMode) {
            return LovelySpawnItem.this.getPlayerPOVHitResult(level, player, fluidMode);
        } // getPlayerPOVHitResult()

    } // Class: BaseSpawnItemDelegate

} // Class: LovelySpawnItem