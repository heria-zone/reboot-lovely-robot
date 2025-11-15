package net.msymbios.rlovelyr.common.network.messages;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.msymbios.rlovelyr.common.network.Message;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class SetLevelCommandMessage extends Message {

    // -- Variables --

    /**
     * The value to set the level to.
     */
    private int value;

    // -- Constructors --

    /**
     * @param value the value to set the level to.
     */
    public SetLevelCommandMessage(int value) {
        this.value = value;
    } // Constructor SetLevelCommandMessage ()

    // -- Inherited Methods --

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() -> {
            boolean isClient = context.getDirection() == NetworkDirection.PLAY_TO_CLIENT;

            Player player = isClient ? getClientPlayer() : context.getSender();
            Objects.requireNonNull(player, "No player entity found when handling message.");

            if (isClient) {
                Entity entity = Minecraft.getInstance().crosshairPickEntity;
                if (entity instanceof InternalEntity) ((InternalEntity) entity).setCurrentLevel(value);
            }
        });
        context.setPacketHandled(true);
    } // handle ()

    // -- Custom Methods --

    /**
     * Encodes the message.
     *
     * @param buf the buffer to encode to.
     */
    public void encode(@Nonnull ByteBuf buf) {
        buf.writeInt(value);
    } // encode ()

    /**
     * Decodes and returns the message.
     *
     * @param buf the buffer to decode from.
     */
    @Nonnull
    public static SetLevelCommandMessage decode(@Nonnull ByteBuf buf) {
        return new SetLevelCommandMessage(buf.readInt());
    } // decode ()

} // Class SetLevelCommandMessage