package net.msymbios.rlovelyr.network.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.LovelyRobotMessage;
import net.msymbios.rlovelyr.util.PacketBufferUtils;
import org.jetbrains.annotations.Nullable;

/**
 * A message used to sync the blockling's name from the client to the server.
 */
public class NameMessage extends LovelyRobotMessage<NameMessage> {

    // -- Variables --

    /**
     * The blockling's name.
     */
    private String name = "";

    // -- Constructors --

    /**
     * Empty constructor used ONLY for decoding.
     */
    public NameMessage() {
        super(null);
    } // Constructor NameMessage ()

    /**
     * @param blockling the blockling.
     * @param name      the blockling's name.
     */
    public NameMessage(InterfaceEntity blockling, @Nullable Text name) {
        super(blockling, false);
        this.name = name == null ? "" : name.getString();
    } // Constructor NameMessage ()

    // -- Inherited Methods --

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);
        PacketBufferUtils.writeString(buf, name);
    } // encode ()

    @Override
    public void decode(PacketByteBuf buf) {
        super.decode(buf);
        name = PacketBufferUtils.readString(buf);
    } // decode ()

    @Override
    protected void handle(PlayerEntity player, InterfaceEntity entity) {
        entity.setCustomName(name.isEmpty() ? null : Text.literal(name));
    } // handle ()

} // Class NameMessage