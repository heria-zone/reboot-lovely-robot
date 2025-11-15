package net.msymbios.rlovelyr.entity.attribute.attributes.numbers;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.entity.attribute.Attribute;
import net.msymbios.rlovelyr.entity.attribute.enums.Operation;
import net.msymbios.rlovelyr.entity.attribute.interfaces.IModifier;
import net.msymbios.rlovelyr.entity.internal.InterfaceEntity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A simple number attribute.
 */
public abstract class NumberAttribute<T extends Number> extends Attribute<T> {
    
    /**
     * The vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    protected EntityAttribute vanillaAttribute;

    /**
     * @param id                         the id of the attribute.
     * @param key                        the key used to identify the attribute (for things like translation text components).
     * @param blockling                  the blockling.
     * @param initialValue               the initial value of the attribute.
     * @param displayStringValueFunction the function used to provide the string representation of the value.
     * @param displayStringNameSupplier  the supplier used to provide the string representation of display name.
     * @param isEnabled                  whether the attribute is currently enabled.
     */
    public NumberAttribute(String id, String key, InterfaceEntity blockling, T initialValue, @Nullable Function<T, String> displayStringValueFunction, @Nullable Supplier<String> displayStringNameSupplier, boolean isEnabled) {
        super(id, key, blockling, displayStringValueFunction, displayStringNameSupplier, isEnabled);
        this.value = initialValue;
    }

    /**
     * @return the vanilla attribute to update when the number attribute changes.
     */
    @Nullable
    public EntityAttribute getVanillaAttribute() {
        return vanillaAttribute;
    }

    /**
     * Sets the vanilla attribute to update when the number attribute changes.
     */
    public void setVanillaAttribute(@Nullable EntityAttribute vanillaAttribute) {
        removeFromVanillaAttribute();
        this.vanillaAttribute = vanillaAttribute;
        updateVanillaAttribute();
    } // setVanillaAttribute ()

    /**
     * Removes the number attribute from the vanilla attribute.
     */
    protected void removeFromVanillaAttribute() {
        if (vanillaAttribute != null) {
            EntityAttributeInstance vanillaAttributeInstance = blockling.getAttributeInstance(vanillaAttribute);
            if (this instanceof IModifier) {
                vanillaAttributeInstance.removeModifier(id);
            } else {
                // Leave the base value alone, as it could be the case something else has set it.
            }
        }
    } // removeFromVanillaAttribute ()

    /**
     * Updates the vanilla attribute inline with the number attribute.
     */
    protected void updateVanillaAttribute() {
        if (vanillaAttribute == null) return;

        EntityAttributeInstance vanillaAttributeInstance = blockling.getAttributeInstance(vanillaAttribute);

        // If this attribute is a modifier then add it to the vanilla attribute as a transient modifier.
        if (this instanceof IModifier) {
            IModifier<T> modifier = (IModifier<T>) this;

            if (modifier.getAttributes().stream().anyMatch(modifiable -> modifiable instanceof IModifier)) {
                // Because vanilla attributes only support 1 layer of modifiers, prevent any nested modifiers from being added.
                // Otherwise, the same modifier could be applied twice.
                LovelyRobot.LOGGER.warn("Tried to add a modifier to a vanilla attribute that is applied to other modifiers.");

                return;
            }

            // Remove the modifier it exists as you can't just set the value again.
            vanillaAttributeInstance.removeModifier(id);

            // Do not apply the modifier if it is not enabled.
            if (isEnabled()) {
                // Add the attribute modifier with the current value.
                vanillaAttributeInstance.addPersistentModifier(new EntityAttributeModifier(id, getDisplayStringNameSupplier().get(), getValue().doubleValue(), Operation.vanillaOperation(modifier.getOperation())));
            }
        }
        // If this is just a regular attribute, use it to set the base value for the vanilla attribute.
        else {
            vanillaAttributeInstance.setBaseValue(value.doubleValue());
        }
    } // updateVanillaAttribute ()

    @Override
    public void onValueChanged() {
        super.onValueChanged();
        updateVanillaAttribute();
    } // onValueChanged ()

} // Class NumberAttribute