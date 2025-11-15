package net.msymbios.rlovelyr.util;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class PacketBufferUtils {

    // -- Methods --

    public static String readString(ByteBuf buf) {
        return buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
    } // readString ()

    public static void writeString(ByteBuf buf, String string) {
        buf.writeInt(string.getBytes(StandardCharsets.UTF_8).length);
        buf.writeCharSequence(string, StandardCharsets.UTF_8);
    } // writeString ()

} // Class PacketBufferUtils