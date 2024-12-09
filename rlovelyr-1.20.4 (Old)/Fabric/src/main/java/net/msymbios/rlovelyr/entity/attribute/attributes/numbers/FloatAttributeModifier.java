package net.msymbios.rlovelyr.entity.attribute.attributes.numbers;

import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifiable;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple float attribute modifier.
 */
public class FloatAttributeModifier extends FloatAttribute implements IModifier<Float> {
    /**
     * The attributes the modifier is associated with.
     */
    public final List<IModifiable<Float>> attributes = new ArrayList<>();

    /**
     * The operation to be performed on the associated attribute and the modifier.
     */
    public final Operation operation;

    /**
     * @param id                         the id of the attribute.
     * @param key                        the key used to identify the attribute (for things like translation text components).
     * @param blockling                  the blockling.
     * @param initialValue               the initial value of the attribute.
     * @param operation                  the operation to be performed on the associated attribute and the modifier.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier  the supplier used to provide the string representation of display name.
     * @param isEnabled                  whether the attribute is currently enabled.
     */
    public FloatAttributeModifier(String id, String key, InterfaceEntity blockling, float initialValue, Operation operation, @Nullable Function<Float, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled) {
        super(id, key, blockling, initialValue, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.operation = operation;
    }

    @Override
    public void setValue(Float value, boolean sync) {
        super.setValue(value, sync);

        attributes.forEach(IModifiable::calculate);
    }

    @Override
    public List<IModifiable<Float>> getAttributes() {
        return attributes;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

    @Override
    public boolean isEffective() {
        return !((getOperation() == Operation.ADD && getValue() == 0.0f) || ((getOperation() == Operation.MULTIPLY_BASE || getOperation() == Operation.MULTIPLY_TOTAL) && getValue() == 1.0f));
    }

    @Override
    public void setIsEnabled(boolean isEnabled, boolean sync) {
        super.setIsEnabled(isEnabled, sync);

        attributes.forEach(IModifiable::calculate);
    }

} // Class FloatAttributeModifier