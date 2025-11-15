package net.msymbios.rlovelyr.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.util.interfaces.IReadWriteNBT;
import net.msymbios.rlovelyr.util.internal.Version;

public abstract class AbstractInventory implements Inventory, IReadWriteNBT {

    // -- Variables --
    public final int invSize;

    protected InterfaceEntity blockling;
    protected World world;

    protected ItemStack[] stacks;
    protected ItemStack[] stacksCopy;

    private boolean dirty = false;

    // -- Constructors --

    public AbstractInventory(InterfaceEntity blockling, int invSize) {
        this.blockling = blockling;
        this.world = blockling.getWorld();
        this.invSize = invSize;

        stacks = new ItemStack[invSize];
        stacksCopy = new ItemStack[invSize];

        clear();

        for (int i = 0; i < size(); i++)
            stacksCopy[i] = ItemStack.EMPTY;
    } // Constructor AbstractInventory ()

    // -- Inherited Methods --

    @Override
    public NbtCompound writeToNBT(NbtCompound equipmentInvTag) {
        NbtList list = new NbtList();

        for (int i = 0; i < size(); i++) {
            ItemStack stack = stacks[i];

            if (stack.isEmpty()) continue;

            NbtCompound stackTag = new NbtCompound();

            stackTag.putInt("slot", i);
            stack.setNbt(stackTag);

            list.add(stackTag);
        }

        equipmentInvTag.put("slots", list);

        return equipmentInvTag;
    } // writeToNBT ()

    @Override
    public void readFromNBT(NbtCompound invTag, Version tagVersion) {
        NbtList list = (NbtList) invTag.get("slots");

        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                NbtCompound stackTag = list.getCompound(i);

                int slot = stackTag.getInt("slot");
                ItemStack stack = ItemStack.fromNbt(stackTag);

                if (slot < stacks.length) setStack(slot, stack);
            }
        }
    } // readFromNBT ()

    @Override
    public void markDirty() { }

    @Override
    public int size() {
        return invSize;
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < size(); i++) {
            if (!getStack(i).isEmpty()) return false;
        }
        return true;
    } // isEmpty ()

    @Override
    public ItemStack getStack(int index) {
        return stacks[index];
    }

    @Override
    public ItemStack removeStack(int index, int count) {
        ItemStack stack = getStack(index);
        ItemStack copy = stack.copy();
        stack.decrement(count);
        setStack(index, stack);
        copy.setCount(count);
        return copy;
    } // removeItem ()

    @Override
    public ItemStack removeStack(int index) {
        ItemStack stack = getStack(index);
        stacks[index] = ItemStack.EMPTY;
        return stack;
    } // removeItemNoUpdate ()

    @Override
    public void setStack(int index, ItemStack stack) {
        stacks[index] = stack;
    }

    public boolean isValid(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size(); i++)
            removeStack(i);
    } // clearContent ()

    // -- Custom Methods --

    public void encode(PacketByteBuf buf) {
        for (int i = 0; i < size(); i++)
            buf.writeItemStack(stacks[i]);
    } // encode ()

    public void decode(PacketByteBuf buf) {
        for (int i = 0; i < size(); i++)
            setStack(i, buf.readItemStack());
    } // decode ()

    public void swapItems(int slot1, int slot2) {
        ItemStack stack1 = getStack(slot1);
        setStack(slot1, getStack(slot2));
        setStack(slot2, stack1);
    } // swapItems ()

    public int find(Item item) {
        return find(item, 0, size() - 1);
    }

    public int find(Item item, int startIndex, int endIndex) {
        for (int i = startIndex; i < endIndex + 1; i++) {
            if (getStack(i).getItem() == item) {
                return i;
            }
        }

        return -1;
    } // find ()

    public boolean has(ItemStack stack) {
        return has(stack, 0, size() - 1);
    }

    public boolean has(ItemStack stack, int startIndex, int endIndex) {
        int count = 0;

        for (int i = startIndex; i < endIndex + 1; i++) {
            ItemStack slotStack = getStack(i);

            if (ItemStack.areItemsEqual(slotStack, stack)) {
                count += slotStack.getCount();

                if (count >= stack.getCount()) {
                    return true;
                }
            }
        }

        return false;
    } // has ()

    @Deprecated
    public boolean take(ItemStack stack) {
        return take(stack, 0, size() - 1);
    } // take ()

    @Deprecated
    public boolean take(ItemStack stack, int startIndex, int endIndex) {
        if (!has(stack)) return false;

        int remainder = stack.getCount();

        for (int i = startIndex; i < endIndex + 1; i++) {
            ItemStack slotStack = getStack(i);

            if (ItemStack.areItemsEqual(slotStack, stack)) {
                int slotCount = slotStack.getCount();

                if (slotCount >= remainder) {
                    slotStack.decrement(remainder);

                    break;
                } else {
                    remainder -= slotCount;
                    slotStack.decrement(slotCount);
                }
            }
        }

        return true;
    } // take ()

    /**
     * Tries to take the given item stack from the given slot. Doesn't modify the given item stack.
     *
     * @param stack    the item stack to take.
     * @param slot     the slot to take from.
     * @param simulate whether to simulate the take.
     * @return the item stack that was taken.
     */
    public ItemStack takeItem(ItemStack stack, int slot, boolean simulate) {
        ItemStack slotStack = getStack(slot);

        if (ItemStack.areItemsEqual(slotStack, stack)) {
            int count = Math.min(stack.getCount(), slotStack.getCount());

            if (!simulate) {
                slotStack.decrement(count);
            }

            ItemStack taken = stack.copy();
            taken.setCount(count);

            return taken;
        }

        return ItemStack.EMPTY;
    } // takeItem ()

    /**
     * Tries to take the given item stack from the inventory. Doesn't modify the given item stack.
     *
     * @param stack    the item stack to take.
     * @param simulate whether to simulate the take.
     * @return the item stack that was taken.
     */
    public ItemStack takeItem(ItemStack stack, boolean simulate) {
        ItemStack stackCopy = stack.copy();

        for (int i = size() - 1; i >= 0 && !stackCopy.isEmpty(); i--) {
            stackCopy.decrement(takeItem(stackCopy, i, simulate).getCount());
        }

        stackCopy.setCount(stack.getCount() - stackCopy.getCount());

        return stackCopy;
    } // takeItem ()

    /**
     * Counts the number of items in the inventory of the given item stack.
     *
     * @param stack the item stack to count.
     * @return the number of items in the inventory of the given item stack.
     */
    public int count(ItemStack stack) {
        return count(stack, 0, size() - 1);
    } // count ()

    /**
     * Counts the number of items in the inventory of the given item stack.
     *
     * @param stack      the item stack to count.
     * @param startIndex the inclusive slot index to start looking from.
     * @param endIndex   the exclusive slot index to end looking at.
     * @return the number of items in the inventory of the given item stack.
     */
    public int count(ItemStack stack, int startIndex, int endIndex) {
        int count = 0;

        for (int i = startIndex; i < endIndex + 1; i++) {
            ItemStack slotStack = getStack(i);

            if (ItemStack.areItemsEqual(slotStack, stack)) {
                count += slotStack.getCount();
            }
        }

        return count;
    } // count ()

    public boolean couldAddItem(ItemStack stack, int slot) {
        boolean couldAdd = true;

        ItemStack slotStack = getStack(slot);
        if (ItemStack.areItemsEqual(stack, slotStack)) {
            couldAdd = slotStack.getCount() + stack.getCount() <= slotStack.getMaxCount();
        }

        return couldAdd;
    } // couldAddItem ()

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param slot       the slot to add the item stack to.
     * @return the remainder of the item stack that could not be added.
     */
    public ItemStack addItem(ItemStack stackToAdd, int slot) {
        return addItem(stackToAdd, slot, false);
    } // addItem ()

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param slot       the slot to add the item stack to.
     * @param simulate   whether to simulate the addition.
     * @return the remainder of the item stack that could not be added.
     */
    public ItemStack addItem(ItemStack stackToAdd, int slot, boolean simulate) {
        ItemStack slotStack = getStack(slot);
        ItemStack stackToAddCopy = stackToAdd.copy();

        if (ItemStack.areItemsEqual(stackToAddCopy, slotStack)) {
            int amountToAdd = stackToAddCopy.getCount();
            amountToAdd = Math.min(amountToAdd, slotStack.getMaxCount() - slotStack.getCount());

            if (!simulate) {
                slotStack.increment(amountToAdd);
            }

            stackToAddCopy.decrement(amountToAdd);
        } else if (slotStack.isEmpty()) {
            if (!simulate) {
                setStack(slot, stackToAddCopy.copy());
            }

            stackToAddCopy.decrement(stackToAddCopy.getCount());
        }

        return stackToAddCopy;
    } // addItem ()

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @return the remainder of the item stack that could not be added.
     */
    public ItemStack addItem(ItemStack stackToAdd) {
        return addItem(stackToAdd, false);
    } // addItem ()

    /**
     * Adds the given item stack to the inventory.
     *
     * @param stackToAdd the item stack to add.
     * @param simulate   whether to simulate the addition.
     * @return the remainder of the item stack that could not be added.
     */
    public ItemStack addItem(ItemStack stackToAdd, boolean simulate) {
        ItemStack stackToAddCopy = stackToAdd.copy();

        for (int i = 0; i < invSize && !stackToAddCopy.isEmpty(); i++) {
            if (ItemStack.areItemsEqual(stackToAddCopy, getStack(i))) {
                stackToAddCopy = addItem(stackToAddCopy, i, simulate);
            }
        }

        for (int i = 0; i < invSize && !stackToAddCopy.isEmpty(); i++) {
            if (getStack(i).isEmpty()) {
                stackToAddCopy = addItem(stackToAddCopy, i, simulate);
            }
        }

        return stackToAddCopy;
    } // addItem ()

} // Class AbstractInventory