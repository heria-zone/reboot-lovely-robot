package net.msymbios.rlovelyr.entity.attribute.attributes.numbers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.msymbios.rlovelyr.entity.attribute.Attribute;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.util.internal.Version;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple int attribute.
 */
public class IntAttribute extends NumberAttribute<Integer> {
    /**
     * @param id                         the id of the attribute.
     * @param key                        the key used to identify the attribute (for things like translation text components).
     * @param blockling                  the blockling.
     * @param initialValue               the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier  the supplier used to provide the string representation of display name.
     * @param isEnabled                  whether the attribute is currently enabled.
     */
    public IntAttribute(String id, String key, InterfaceEntity blockling, int initialValue, Function<Integer, String> displayStringValueFunction, Supplier<String> displayStringNameSupplier, boolean isEnabled) {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled);
    }

    @Override
    public NbtCompound writeToNBT(NbtCompound attributeTag) {
        attributeTag.putInt("value", value);

        return super.writeToNBT(attributeTag);
    }

    @Override
    public void readFromNBT(NbtCompound attributeTag, Version tagVersion) {
        super.readFromNBT(attributeTag, tagVersion);

        value = attributeTag.getInt("value");
    }

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);

        buf.writeInt(value);
    }

    @Override
    public void decode(PacketByteBuf buf) {
        super.decode(buf);

        value = buf.readInt();
    }

    @Override
    public Integer getValue() {
        return value;
    }

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server.
     *
     * @param amount the amount to increment the value by.
     */
    public void incrementValue(int amount) {
        incrementValue(amount, true);
    }

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server if sync is true.
     *
     * @param amount the amount to increment the value by.
     * @param sync   whether to sync to the client/server.
     */
    public void incrementValue(int amount, boolean sync) {
        setValue(value + amount, sync);
    }

    @Override
    public void setValue(Integer value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Integer value, boolean sync) {
        this.value = value;

        onValueChanged();

        if (sync) {
            new ValueMessage(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
        }
    }

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static class ValueMessage extends Attribute.ValueMessage<Integer, ValueMessage> {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public ValueMessage() {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param index     the index of the attribute.
         * @param value     the value of the attribute.
         */
        public ValueMessage(InterfaceEntity blockling, int index, int value) {
            super(blockling, index, value);
        }

        @Override
        protected void encodeValue(PacketByteBuf buf) {
            buf.writeInt(value);
        }

        @Override
        protected void decodeValue(PacketByteBuf buf) {
            value = buf.readInt();
        }
    }
} // IntAttribute