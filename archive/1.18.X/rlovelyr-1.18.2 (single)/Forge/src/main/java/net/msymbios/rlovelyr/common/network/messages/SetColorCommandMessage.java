package net.msymbios.rlovelyr.common.network.messages;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.msymbios.rlovelyr.common.network.Message;
import net.msymbios.rlovelyr.common.util.PacketBufferUtils;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Supplier;

public class SetColorCommandMessage extends Message {

    // -- Variables --

    /**
     * The type key.
     */
    private String color;

    // -- Constructors --
    /**
     * @param type    the type key.
     */
    public SetColorCommandMessage(@Nonnull String type) {
        this.color = type;
    } // Constructor SetColorCommandMessage ()

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
                if (entity instanceof InternalEntity) ((InternalEntity) entity).setTexture(Objects.requireNonNull(EntityTexture.byName(color)));
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
    public void encode(@Nonnull FriendlyByteBuf buf) {
        PacketBufferUtils.writeString(buf, color);
    } // encode ()

    /**
     * Decodes and returns the message.
     *
     * @param buf the buffer to decode from.
     */
    @Nonnull
    public static SetColorCommandMessage decode(@Nonnull FriendlyByteBuf buf) {
        return new SetColorCommandMessage(PacketBufferUtils.readString(buf));
    } // decode ()

} // SetColorCommandMessage