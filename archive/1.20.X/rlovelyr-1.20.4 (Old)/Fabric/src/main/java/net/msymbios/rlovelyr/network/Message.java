package net.msymbios.rlovelyr.network;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public abstract class Message<T extends Message> {

    // -- Methods --

    /**
     * @return the uuid of the client player.
     */
    protected UUID getClientPlayerId()
    {
        return MinecraftClient.getInstance().player.getUuid();
    }

    /**
     * @return the client player.
     */
    protected PlayerEntity getClientPlayer()
    {
        return MinecraftClient.getInstance().player;
    }

} // Class Message