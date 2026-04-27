package net.heriazone.hzlib.api.entity.dynamic;

import net.minecraft.network.chat.MutableComponent;

public interface IInternalVariant {

    // -- Methods --

    public int getId();

    public String getKey();

    public MutableComponent getName();

} // Interface: IInternalVariant