package net.msymbios.rlovelyr.network.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.LovelyRobotMessage;

public class EquipmentInventoryMessage extends LovelyRobotMessage<EquipmentInventoryMessage> {

    // -- Variables --

    /**
     * The index of the stack in the inventory.
     */
    private int index;

    /**
     * The stack.
     */
    private ItemStack stack;

    /**
     * Empty constructor used ONLY for decoding.
     */
    public EquipmentInventoryMessage() {
        super(null);
    }

    /**
     * @param blockling the blockling.
     * @param index     the index of the stack in the inventory.
     * @param stack     the stack.
     */
    public EquipmentInventoryMessage(InterfaceEntity blockling, int index, ItemStack stack) {
        super(blockling);
        this.index = index;
        this.stack = stack;
    } // constructor EquipmentInventoryMessage ()

    // -- Inherited Methods --

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);
        buf.writeInt(index);
        buf.writeItemStack(stack);
    } // encode ()

    @Override
    public void decode(PacketByteBuf buf) {
        super.decode(buf);
        index = buf.readInt();
        stack = buf.readItemStack();
    } // decode ()

    @Override
    protected void handle(PlayerEntity player, InterfaceEntity blockling) {
        // TODO: Uncomment when blockling equipment is implemented
        //blockling.getEquipment().setItem(index, stack);
    } // handle

} // class EquipmentInventoryMessage