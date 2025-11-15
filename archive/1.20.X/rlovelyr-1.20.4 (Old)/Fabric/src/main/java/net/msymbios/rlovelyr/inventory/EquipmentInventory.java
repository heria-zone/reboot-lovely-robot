package net.msymbios.rlovelyr.inventory;


import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.msymbios.rlovelyr.entity.attribute.BlocklingAttributes;
import net.msymbios.rlovelyr.entity.enums.BlocklingHand;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.message.EquipmentInventoryMessage;
import net.msymbios.rlovelyr.util.ToolUtil;
import net.msymbios.rlovelyr.util.enums.ToolType;
import net.msymbios.rlovelyr.util.internal.ToolContext;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Set;
import java.util.function.Predicate;


/**
 * The blockling's equipment inventory.
 */
public class EquipmentInventory extends AbstractInventory {

    // -- Variables --

    /**
     * The index of the slot containing the blockling's main hand tool.
     */
    public static final int TOOL_MAIN_HAND = 0;

    /**
     * The index of the slot containing the blockling's off hand tool.
     */
    public static final int TOOL_OFF_HAND = 1;

    // -- Constructor --

    /**
     * @param blockling the blockling the inventory is attached to.
     */
    public EquipmentInventory(InterfaceEntity blockling) {
        super(blockling, 26);
    } // Constructor EquipmentInventory ()

    // --  Methods --

    /**
     * @return which hand(s) the blockling currently has a tool in.
     */
    public BlocklingHand findHandToolEquipped(ToolType toolType) {
        boolean hasInMain = hasToolEquipped(Hand.MAIN_HAND);
        boolean hasInOff = hasToolEquipped(Hand.OFF_HAND);

        if (hasInMain && hasInOff) {
            return BlocklingHand.BOTH;
        } else if (hasInMain) {
            return BlocklingHand.MAIN;
        } else if (hasInOff) {
            return BlocklingHand.OFF;
        } else {
            return BlocklingHand.NONE;
        }
    } // findHandToolEquipped ()

    /**
     * @return true if the blockling has a tool equipped in the given hand.
     */
    public boolean hasToolEquipped(Hand hand) {
        return ToolUtil.isTool(getHandStack(hand));
    } // hasToolEquipped ()

    /**
     * @return true if the blockling has at least one tool equipped of the given type.
     */
    public boolean hasToolEquipped(ToolType toolType) {
        return hasToolEquipped(Hand.MAIN_HAND, toolType) || hasToolEquipped(Hand.OFF_HAND, toolType);
    } // hasToolEquipped ()

    /**
     * @return true if the blockling has a tool equipped of the given type in the given hand.
     */
    public boolean hasToolEquipped(Hand hand, ToolType toolType) {
        return toolType.is(getHandStack(hand));
    } // hasToolEquipped ()

    /**
     * @return the stack in the given hand.
     */
    public ItemStack getHandStack(Hand hand) {
        return hand == Hand.MAIN_HAND ? getStack(TOOL_MAIN_HAND) : getStack(TOOL_OFF_HAND);
    } // getHandStack ()

    /**
     * Sets the stack in the given hand.
     */
    public void setHandStack(Hand hand, ItemStack stack) {
        if (hand == Hand.MAIN_HAND) {
            setStack(TOOL_MAIN_HAND, stack);
        } else if (hand == Hand.OFF_HAND) {
            setStack(TOOL_OFF_HAND, stack);
        }
    } // setHandStack ()

    /**
     * @return true if the blockling is attacking with the given hand.
     */
    public boolean isAttackingWith(BlocklingHand hand) {
        BlocklingHand attackingHand = findAttackingHand();

        if (hand == BlocklingHand.BOTH && attackingHand == BlocklingHand.BOTH) {
            return true;
        } else if (hand == BlocklingHand.MAIN && (attackingHand == BlocklingHand.BOTH || attackingHand == BlocklingHand.MAIN)) {
            return true;
        } else if (hand == BlocklingHand.OFF && (attackingHand == BlocklingHand.BOTH || attackingHand == BlocklingHand.OFF)) {
            return true;
        } else if (hand == BlocklingHand.NONE && attackingHand == BlocklingHand.NONE) {
            return true;
        }

        return false;
    } // isAttackingWith ()

    /**
     * @return the hand the blockling is attacking with.
     */
    public BlocklingHand findAttackingHand() {
        if (hasUseableWeapon(Hand.MAIN_HAND) && hasUseableWeapon(Hand.OFF_HAND)) {
            return BlocklingHand.BOTH;
        } else if (hasUseableWeapon(Hand.MAIN_HAND) && !hasUseableWeapon(Hand.OFF_HAND)) {
            return BlocklingHand.MAIN;
        } else if (!hasUseableWeapon(Hand.MAIN_HAND) && hasUseableWeapon(Hand.OFF_HAND)) {
            return BlocklingHand.OFF;
        } else if (hasUseableTool(Hand.MAIN_HAND) && !hasUseableTool(Hand.OFF_HAND)) {
            return BlocklingHand.MAIN;
        } else if (!hasUseableTool(Hand.MAIN_HAND) && hasUseableTool(Hand.OFF_HAND)) {
            return BlocklingHand.OFF;
        }
        return BlocklingHand.BOTH;
    } // findAttackingHand ()

    /**
     * @return true if the given hand has a weapon equipped that can currently be used.
     */
    public boolean hasUseableWeapon(Hand hand) {
        return hasToolEquipped(hand, ToolType.WEAPON) && ToolUtil.isUseableTool(getHandStack(hand));
    } // hasUseableWeapon ()

    /**
     * @return true if the given hand has a tool equipped that can currently be used.
     */
    public boolean hasUseableTool(Hand hand) {
        return ToolUtil.isUseableTool(getHandStack(hand));
    } // hasUseableTool ()

    /**
     * @return true if the blockling can harvest the given block with their current tools.
     */
    public boolean canHarvestBlockWithEquippedTools(BlockState blockState) {
        return canHarvestBlockWithEquippedTool(Hand.MAIN_HAND, blockState) || canHarvestBlockWithEquippedTool(Hand.OFF_HAND, blockState);
    } // canHarvestBlockWithEquippedTools ()

    /**
     * @return true if the blockling can harvest the given block with the current tool in the given hand.
     */
    public boolean canHarvestBlockWithEquippedTool(Hand hand, BlockState blockState) {
        return ToolUtil.canToolHarvest(getHandStack(hand), blockState);
    } // canHarvestBlockWithEquippedTool ()

    /**
     * @return true if any slots were switched.
     */
    public boolean trySwitchToBestTool(BlocklingHand hand, ToolContext context) {
        Pair<SwitchedTools, SwitchedTools> bestToolSlots = findBestToolSlotsToSwitchTo(hand, context);

        int mainHandSlot = bestToolSlots.getKey().originalSlot;
        int mainBestSlot = bestToolSlots.getKey().bestSlot;
        int offHandSlot = bestToolSlots.getValue().originalSlot;
        int offBestSlot = bestToolSlots.getValue().bestSlot;

        boolean result = false;

        if (mainHandSlot != mainBestSlot) {
            swapItems(mainHandSlot, mainBestSlot);

            result = true;
        }

        if (offHandSlot != offBestSlot) {
            swapItems(offHandSlot, offBestSlot);

            result = true;
        }

        return result;
    } // trySwitchToBestTool ()

    /**
     * @return the best tools for the given hand slots.
     */
    public Pair<ItemStack, ItemStack> findBestToolsToSwitchTo(BlocklingHand hand, ToolContext context) {
        Pair<SwitchedTools, SwitchedTools> bestTools = findBestToolSlotsToSwitchTo(hand, context);
        return new MutablePair<>(getStack(bestTools.getKey().bestSlot), getStack(bestTools.getValue().bestSlot));
    } // findBestToolsToSwitchTo ()

    /**
     * @return the current hand slots and the best hand slots to switch to.
     */
    public Pair<SwitchedTools, SwitchedTools> findBestToolSlotsToSwitchTo(BlocklingHand hand, ToolContext context) {
        if (hand == BlocklingHand.MAIN) {
            return new MutablePair<>(findBestToolSlotToSwitchTo(BlocklingHand.MAIN, context), new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND));
        } else if (hand == BlocklingHand.OFF) {
            return new MutablePair<>(new SwitchedTools(TOOL_MAIN_HAND, TOOL_MAIN_HAND), findBestToolSlotToSwitchTo(BlocklingHand.OFF, context));
        } else if (hand == BlocklingHand.BOTH) {
            SwitchedTools toolSlotsMain = findBestToolSlotToSwitchTo(BlocklingHand.MAIN, context);

            if (toolSlotsMain.bestSlot != TOOL_MAIN_HAND) {
                swapItems(toolSlotsMain.originalSlot, toolSlotsMain.bestSlot);
            }

            SwitchedTools toolSlotsOff = findBestToolSlotToSwitchTo(BlocklingHand.OFF, context);

            if (toolSlotsMain.bestSlot != TOOL_MAIN_HAND) {
                swapItems(toolSlotsMain.originalSlot, toolSlotsMain.bestSlot);
            }

            return new MutablePair<>(toolSlotsMain, toolSlotsOff);
        }

        return new MutablePair<>(new SwitchedTools(TOOL_MAIN_HAND, TOOL_MAIN_HAND), new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND));
    } // findBestToolSlotsToSwitchTo ()

    /**
     * @param hand    the hand to find the tool for, should be either MAIN or OFF.
     * @param context the context to use when finding the best tool.
     * @return a pair containing the slots to swap with the best items, hand slot first then the other slot to swap with.
     */
    public SwitchedTools findBestToolSlotToSwitchTo(BlocklingHand hand, ToolContext context) {
        if (!(hand == BlocklingHand.MAIN || hand == BlocklingHand.OFF)) {
            //Log.warn("Tried to find the best tool to switch to with a hand that wasn't MAIN or OFF!");

            return new SwitchedTools(TOOL_OFF_HAND, TOOL_OFF_HAND);
        }

        int bestSlot = hand == BlocklingHand.MAIN ? TOOL_MAIN_HAND : TOOL_OFF_HAND;
        int handSlot = bestSlot;
        ItemStack handStack = getStack(handSlot);

        if (context.toolType == ToolType.WEAPON) {
            float bestAttackPower = ToolUtil.getToolAttackSpeed(handStack, context.entity) * ToolUtil.getToolBaseDamage(handStack, context.entity);

            for (int i = TOOL_OFF_HAND + 1; i < size(); i++) {
                ItemStack stack = stacks[i];

                float attackPower = ToolUtil.getToolAttackSpeed(stack, context.entity) * ToolUtil.getToolBaseDamage(stack, context.entity);

                if (attackPower > bestAttackPower) {
                    bestSlot = i;
                    bestAttackPower = attackPower;
                }
            }
        } else {
            float bestSpeed = context.toolType.is(handStack) ? ToolUtil.getToolHarvestSpeedWithEnchantments(handStack, context.blockState) : 0.0f;

            for (int i = TOOL_OFF_HAND + 1; i < size(); i++) {
                ItemStack stack = stacks[i];

                if (context.toolType.is(stack) && ToolUtil.canToolHarvest(stack, context.blockState)) {
                    float speed = ToolUtil.getToolHarvestSpeedWithEnchantments(stack, context.blockState);

                    if (speed > bestSpeed) {
                        bestSlot = i;
                        bestSpeed = speed;
                    }
                }
            }
        }

        return new SwitchedTools(handSlot, bestSlot);
    } // findBestToolSlotToSwitchTo ()

    @Override
    public ItemStack addItem(ItemStack stackToAdd, int slot, boolean simulate) {
        // Only allow tools to be added to the tool slots.
        return (isToolSlot(slot) && !ToolUtil.isTool(stackToAdd)) ? stackToAdd : super.addItem(stackToAdd, slot, simulate);
    } // addItem ()

    @Override
    public void setStack(int slot, ItemStack stack) {
        super.setStack(slot, stack);
        if (isToolSlot(slot))
            updateToolAttributes();
    } // setItem ()

    @Override
    public int getMaxCountPerStack() {
        return super.getMaxCountPerStack();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return false;
    }

    @Override
    public void onOpen(PlayerEntity player) {
        super.onOpen(player);
    }

    @Override
    public void onClose(PlayerEntity player) {
        super.onClose(player);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return super.isValid(slot, stack);
    }

    @Override
    public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
        return super.canTransferTo(hopperInventory, slot, stack);
    }

    @Override
    public int count(Item item) {
        return super.count(item);
    }

    @Override
    public boolean containsAny(Set<Item> items) {
        return super.containsAny(items);
    }

    @Override
    public boolean containsAny(Predicate<ItemStack> predicate) {
        return super.containsAny(predicate);
    }

    @Override
    public NbtCompound writeToNBT() {
        return super.writeToNBT();
    }

    /**
     * Updates the values of the blockling's tool attributes.
     */
    public void updateToolAttributes() {
        BlocklingAttributes stats = blockling.getStats();

        stats.mainHandAttackDamageToolModifier.setValue(0.0f, false);
        stats.offHandAttackDamageToolModifier.setValue(0.0f, false);
        stats.attackSpeedMainHandModifier.setValue(0.0f, false);
        stats.attackSpeedOffHandModifier.setValue(0.0f, false);

        stats.miningSpeedMainHandModifier.setValue(0.0f, false);
        stats.miningSpeedOffHandModifier.setValue(0.0f, false);
        stats.woodcuttingSpeedMainHandModifier.setValue(0.0f, false);
        stats.woodcuttingSpeedOffHandModifier.setValue(0.0f, false);
        stats.farmingSpeedMainHandModifier.setValue(0.0f, false);
        stats.farmingSpeedOffHandModifier.setValue(0.0f, false);

        if (isAttackingWith(BlocklingHand.MAIN) && hasToolEquipped(Hand.MAIN_HAND)) {
            stats.mainHandAttackDamageToolModifier.setValue(ToolUtil.getDefaultToolBaseDamage(blockling.getMainHandStack()), false);
            stats.attackSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolAttackSpeed(blockling.getMainHandStack()), false);
        }

        if (isAttackingWith(BlocklingHand.OFF) && hasToolEquipped(Hand.OFF_HAND)) {
            stats.offHandAttackDamageToolModifier.setValue(ToolUtil.getDefaultToolBaseDamage(blockling.getOffHandStack()), false);
            stats.attackSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolAttackSpeed(blockling.getOffHandStack()), false);
        }

        if (hasToolEquipped(Hand.MAIN_HAND, ToolType.PICKAXE)) {
            stats.miningSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolMiningSpeed(blockling.getMainHandStack()), false);
        } else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.AXE)) {
            stats.woodcuttingSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolWoodcuttingSpeed(blockling.getMainHandStack()), false);
        } else if (hasToolEquipped(Hand.MAIN_HAND, ToolType.HOE)) {
            stats.farmingSpeedMainHandModifier.setValue(ToolUtil.getDefaultToolFarmingSpeed(blockling.getMainHandStack()), false);
        }

        if (hasToolEquipped(Hand.OFF_HAND, ToolType.PICKAXE)) {
            stats.miningSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolMiningSpeed(blockling.getOffHandStack()), false);
        } else if (hasToolEquipped(Hand.OFF_HAND, ToolType.AXE)) {
            stats.woodcuttingSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolWoodcuttingSpeed(blockling.getOffHandStack()), false);
        } else if (hasToolEquipped(Hand.OFF_HAND, ToolType.HOE)) {
            stats.farmingSpeedOffHandModifier.setValue(ToolUtil.getDefaultToolFarmingSpeed(blockling.getOffHandStack()), false);
        }
    } // updateToolAttributes ()

    /**
     * Detects any changed slots and syncs them to the client.
     */
    public void detectAndSendChanges() {
        if (!world.isClient) {
            for (int i = 0; i < invSize; i++) {
                ItemStack oldStack = stacksCopy[i];
                ItemStack newStack = stacks[i];

                if (!ItemStack.areItemsEqual(oldStack, newStack)) {
                    if (newStack.isEmpty() && oldStack.isEmpty()) {
                        stacks[i] = ItemStack.EMPTY;
                        stacksCopy[i] = ItemStack.EMPTY;
                    } else {
                        new EquipmentInventoryMessage(blockling, i, newStack).sync();
                        stacksCopy[i] = newStack.copy();
                    }
                }
            }
        }
    } // detectAndSendChanges ()

    /**
     * @param slot the slot to check.
     * @return whether the slot is a tool slot.
     */
    public boolean isToolSlot(int slot) {
        return slot >= TOOL_MAIN_HAND && slot <= TOOL_OFF_HAND;
    } // isToolSlot ()

    // -- Classes --

    /**
     * Represents a pair of slots to switch.
     */
    public static class SwitchedTools {

        // -- Variables --

        /**
         * The index of the slot that was being switched to.
         */
        public final int originalSlot;

        /**
         * The index of the slot that contained the best tool.
         */
        public final int bestSlot;

        // -- Constructors --

        /**
         * @param originalSlot the index of the slot that was being switched to.
         * @param bestSlot     the index of the slot that contained the best tool.
         */
        public SwitchedTools(int originalSlot, int bestSlot) {
            this.originalSlot = originalSlot;
            this.bestSlot = bestSlot;
        } // Constructor SwitchedTools ()

    } // Class SwitchedTools

} // Class EquipmentInventory