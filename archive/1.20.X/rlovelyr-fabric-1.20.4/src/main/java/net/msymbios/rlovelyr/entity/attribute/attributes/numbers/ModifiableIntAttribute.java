package net.msymbios.rlovelyr.entity.attribute.attributes.numbers;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.msymbios.rlovelyr.entity.attribute.ModifiableAttribute;
import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.util.internal.Version;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A modifiable int attribute.
 */
public class ModifiableIntAttribute extends ModifiableNumberAttribute<Integer> {
    /**
     * @param id                         the id of the attribute.
     * @param key                        the key used to identify the attribute (for things like translation text components).
     * @param blockling                  the blockling.
     * @param initialBaseValue           the initial base value.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier  the supplier used to provide the string representation of display name.
     * @param isEnabled                  whether the attribute is currently enabled.
     * @param modifiers                  the initial list of modifiers associated with the attribute.
     */
    public ModifiableIntAttribute(String id, String key, InterfaceEntity blockling, int initialBaseValue, Function<Integer, String> displayStringValueFunction, Supplier<String> displayStringNameSupplier, boolean isEnabled, IModifier<Integer>... modifiers) {
        super(id, key, blockling, initialBaseValue, displayStringValueFunction, displayStringNameSupplier, isEnabled, modifiers);
    }

    @Override
    public NbtCompound writeToNBT( NbtCompound attributeTag) {
        attributeTag.putInt("base_value", baseValue);

        return super.writeToNBT(attributeTag);
    }

    @Override
    public void readFromNBT( NbtCompound attributeTag, Version tagVersion) {
        super.readFromNBT(attributeTag, tagVersion);

        baseValue = attributeTag.getInt("base_value");
    }

    @Override
    public void encode(PacketByteBuf buf) {
        super.encode(buf);

        buf.writeInt(value);
    }

    @Override
    public void decode( PacketByteBuf buf) {
        super.decode(buf);

        value = buf.readInt();
    }

    @Override
    public void calculate() {
        value = 0;
        int tempBase = baseValue;
        boolean end = false;

        for (IModifier<Integer> modifier : getEnabledModifiers()) {
            if (modifier.getOperation() == Operation.ADD) {
                value += modifier.getValue();
            } else if (modifier.getOperation() == Operation.MULTIPLY_BASE) {
                tempBase *= modifier.getValue();
            } else if (modifier.getOperation() == Operation.MULTIPLY_TOTAL) {
                if (!end) {
                    value += tempBase;
                    end = true;
                }

                value *= modifier.getValue();
            }
        }

        if (!end) {
            value += tempBase;
        }

        onValueChanged();
    }

    @Override
    protected void setValue(Integer value, boolean sync) {
        // Let calculate handle setting the value on client/sever
    }

    @Override
    public void incrementBaseValue(Integer amount, boolean sync) {
        setBaseValue(baseValue + amount, sync);
    }

    @Override
    public void setBaseValue(Integer baseValue) {
        setBaseValue(baseValue, true);
    }

    @Override
    public void setBaseValue(Integer baseValue, boolean sync) {
        this.baseValue = baseValue;

        calculate();

        if (sync) {
            new BaseValueMessage(blockling, blockling.getStats().attributes.indexOf(this), baseValue).sync();
        }
    }

    /**
     * The message used to sync the base value of the attribute to the client/server.
     */
    public static class BaseValueMessage extends ModifiableAttribute.BaseValueMessage<Integer, BaseValueMessage> {
        /**
         * Empty constructor used ONLY for decoding.
         */
        public BaseValueMessage() {
            super();
        }

        /**
         * @param blockling the blockling.
         * @param index     the index of the attribute.
         * @param value     the base value of the attribute.
         */
        public BaseValueMessage( InterfaceEntity blockling, int index, int value) {
            super(blockling, index, value);
        }

        @Override
        protected void encodeValue( PacketByteBuf buf) {
            buf.writeInt(value);
        }

        @Override
        protected void decodeValue( PacketByteBuf buf) {
            value = buf.readInt();
        }
    }
}
