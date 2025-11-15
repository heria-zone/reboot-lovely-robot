package net.msymbios.rlovelyr.entity.attribute;

import net.minecraft.entity.player.PlayerEntity;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifiable;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import net.msymbios.rlovelyr.network.LovelyRobotMessage;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * An attribute that can have modifiers applied to it.
 *
 * @param <T> the type of the value of the attribute.
 */
public abstract class ModifiableAttribute<T> extends Attribute<T> implements IModifiable<T> {

    // -- Variables --

    /**
     * The list of modifiers applied to the attribute.
     */
    protected final List<IModifier<T>> modifiers = new ArrayList<>();

    /**
     * The base value of the attribute.
     */
    protected T baseValue;

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
    public ModifiableAttribute(String id, String key, InterfaceEntity blockling, T initialBaseValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled, IModifier<T>... modifiers) {
        super(id, key, blockling, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.baseValue = initialBaseValue;
        this.value = initialBaseValue;
        this.modifiers.addAll(Arrays.asList(modifiers));
        this.modifiers.forEach(modifier -> modifier.getAttributes().add(this));
    }

    @Override
    public T getBaseValue() {
        return baseValue;
    }

    @Override
    public List<IModifier<T>> getModifiers() {
        return new ArrayList<>(modifiers);
    }

    @Override
    public List<IModifier<T>> getEnabledModifiers() {
        return modifiers.stream().filter(IModifier::isEnabled).collect(Collectors.toList());
    }

    @Override
    public IModifier<T> getModifier(int index) {
        return modifiers.get(index);
    }

    @Override
    public int indexOf(IModifier<T> modifier) {
        return modifiers.indexOf(modifier);
    }

    @Override
    public void addModifier(IModifier<T> modifier) {
        // Don't add if modifier is already applied.
        if (modifiers.contains(modifier)) {
            LovelyRobot.LOGGER.warn("Tried to add modifier \"" + modifier.getDisplayStringNameSupplier().get() + "\" that is already applied to attribute \"" + getDisplayStringNameSupplier().get() + "\".");

            return;
        }

        // Add this attribute to the list of attributes the modifier is associated with.
        modifier.getAttributes().add(this);

        // Add total multiplications last.
        if (modifier.getOperation() != Operation.MULTIPLY_TOTAL) {
            boolean inserted = false;

            for (IModifier<T> existingModifier : modifiers) {
                if (existingModifier.getOperation() == Operation.MULTIPLY_TOTAL) {
                    modifiers.add(modifiers.indexOf(existingModifier), modifier);
                    inserted = true;

                    break;
                }
            }

            if (!inserted) {
                modifiers.add(modifier);
            }
        } else {
            modifiers.add(modifier);
        }

        // Calculate new value.
        calculate();
    }

    @Override
    public void addModifiers(IModifier<T>... modifiers) {
        Arrays.stream(modifiers).forEach(this::addModifier);
    }

    @Override
    public void removeModifier(IModifier<T> modifier) {
        modifiers.remove(modifier);
        modifier.getAttributes().remove(this);

        // Recalculate value.
        calculate();
    }

    @Override
    public Function<T, String> getDisplayStringValueFunction() {
        return displayStringValueFunction;
    }

    @Override
    public Supplier<String> getDisplayStringNameSupplier() {
        return displayStringNameSupplier;
    }

    /**
     * The message used to sync the attribute value to the client/server.
     */
    public static abstract class BaseValueMessage<T, M extends LovelyRobotMessage<M>> extends ValueMessage<T, M> {

        // -- Constructors --

        /**
         * Empty constructor used ONLY for decoding.
         */
        public BaseValueMessage() {
            super();
        } // Constructor BaseValueMessage ()

        /**
         * @param blockling the blockling.
         * @param index     the index of the attribute.
         * @param baseValue the base value of the attribute.
         */
        public BaseValueMessage(InterfaceEntity blockling, int index, T baseValue) {
            super(blockling, index, baseValue);
        } // Constructor BaseValueMessage ()

        // -- Inherited Methods --

        @Override
        protected void handle(PlayerEntity player, InterfaceEntity entity) {
            ((ModifiableAttribute<T>) entity.getStats().attributes.get(index)).setBaseValue(value, false);
        } // handle ()

    } // Class BaseValueMessage

} // Class ModifiableAttribute