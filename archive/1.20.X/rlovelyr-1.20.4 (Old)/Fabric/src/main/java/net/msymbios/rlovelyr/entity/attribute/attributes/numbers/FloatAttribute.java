package net.msymbios.rlovelyr.entity.attribute.attributes.numbers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.msymbios.rlovelyr.entity.attribute.Attribute;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.util.internal.Version;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple float attribute.
 */
public class FloatAttribute extends NumberAttribute<Float> {

    // -- Constructors --

    /**
     * @param id                         the id of the attribute.
     * @param key                        the key used to identify the attribute (for things like translation text components).
     * @param blockling                  the blockling.
     * @param initialValue               the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier  the supplier used to provide the string representation of display name.
     * @param isEnabled                  whether the attribute is currently enabled.
     */
    public FloatAttribute(String id, String key, InterfaceEntity blockling, float initialValue, @Nullable Function<Float, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled) {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled);
    } // Constructor FloatAttribute ()

    // -- Inherited Methods --

    @Override
    public NbtCompound writeToNBT(NbtCompound attributeTag) {
        attributeTag.putFloat("value", value);
        return super.writeToNBT(attributeTag);
    } // writeToNBT ()

    @Override
    public void readFromNBT(NbtCompound attributeTag, Version tagVersion) {
        super.readFromNBT(attributeTag, tagVersion);
        value = attributeTag.getFloat("value");
    } // readFromNBT ()

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);
        buf.writeFloat(value);
    } // encode ()

    @Override
    public void decode(PacketByteBuf buf) {
        super.decode(buf);
        value = buf.readFloat();
    } // decode ()

    @Override
    public Float getValue() {
        return value;
    } // getValue ()

    @Override
    public void setValue(Float value) {
        setValue(value, true);
    }

    @Override
    public void setValue(Float value, boolean sync) {
        this.value = value;
        onValueChanged();
        if (sync) new ValueMessage(blockling, blockling.getStats().attributes.indexOf(this), value).sync();
    } // setValue ()

    // -- Custom Methods --

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server.
     *
     * @param amount the amount to increment the value by.
     */
    public void incrementValue(float amount) {
        incrementValue(amount, true);
    } // incrementValue ()

    /**
     * Increments the value by the given amount.
     * Syncs to the client/server if sync is true.
     *
     * @param amount the amount to increment the value by.
     * @param sync   whether to sync to the client/server.
     */
    public void incrementValue(float amount, boolean sync) {
        setValue(value + amount, sync);
    } // incrementValue ()

    // --  Classes --

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static class ValueMessage extends Attribute.ValueMessage<Float, ValueMessage> {

        // -- Constructors --

        /**
         * Empty constructor used ONLY for decoding.
         */
        public ValueMessage() {
            super();
        } // Constructor ValueMessage ()

        /**
         * @param blockling the blockling.
         * @param index     the index of the attribute.
         * @param value     the value of the attribute.
         */
        public ValueMessage(InterfaceEntity blockling, int index, float value) {
            super(blockling, index, value);
        } // Constructor ValueMessage ()

        @Override
        protected void encodeValue(PacketByteBuf buf) {
            buf.writeFloat(value);
        } // encodeValue ()

        @Override
        protected void decodeValue(PacketByteBuf buf) {
            value = buf.readFloat();
        } // decodeValue ()

    } // Class ValueMessage

} // Class FloatAttribute