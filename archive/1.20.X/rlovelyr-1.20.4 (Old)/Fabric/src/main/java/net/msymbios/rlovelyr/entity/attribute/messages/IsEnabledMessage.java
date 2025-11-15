package net.msymbios.rlovelyr.entity.attribute.messages;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.LovelyRobotMessage;

/**
 * The message used to sync whether an attribute is enabled.
 */
public class IsEnabledMessage extends LovelyRobotMessage<IsEnabledMessage> {

    // -- Variables --

    /**
     * The index of the attribute.
     */
    private int index;

    /**
     * Whether the attribute is enabled.
     */
    private boolean isEnabled;

    // -- Constructors --

    /**
     * Empty constructor used ONLY for decoding.
     */
    public IsEnabledMessage() {
        super(null);
    } // Constructor IsEnabledMessage ()

    /**
     * @param blockling the blockling.
     * @param index     the index of the attribute.
     * @param isEnabled whether the attribute is enabled.
     */
    public IsEnabledMessage(InterfaceEntity blockling, int index, boolean isEnabled) {
        super(blockling);
        this.index = index;
        this.isEnabled = isEnabled;
    } // Constructor IsEnabledMessage ()

    // -- Inherited Methods --

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);
        buf.writeInt(index);
        buf.writeBoolean(isEnabled);
    } // encode ()

    @Override
    public void decode(PacketByteBuf buf) {
        super.decode(buf);
        index = buf.readInt();
        isEnabled = buf.readBoolean();
    } // decode ()

    @Override
    protected void handle(PlayerEntity player, InterfaceEntity entity) {
        entity.getStats().attributes.get(index).setIsEnabled(isEnabled, false);
    } // handle ()

} // Class IsEnabledMessage