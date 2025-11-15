package net.msymbios.rlovelyr.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class Message {

    // -- Methods --

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);

    /**
     * @return the uuid of the client player.
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    protected UUID getClientPlayerId()
    {
        return Minecraft.getInstance().player.getUUID();
    }

    /**
     * @return the client player.
     */
    @OnlyIn(Dist.CLIENT)
    @Nonnull
    protected Player getClientPlayer()
    {
        return Minecraft.getInstance().player;
    }

} // Class Message