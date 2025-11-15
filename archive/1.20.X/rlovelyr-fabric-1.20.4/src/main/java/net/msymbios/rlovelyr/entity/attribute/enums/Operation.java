package net.msymbios.rlovelyr.entity.attribute.enums;

import net.minecraft.entity.attribute.EntityAttributeModifier;

/**
 * The operation performed by an attribute modifier.
 */
public enum Operation {

    ADD,
    MULTIPLY_BASE,
    MULTIPLY_TOTAL;

    /**
     * @return the vanilla equivalent of the operation.
     */
    public static EntityAttributeModifier.Operation vanillaOperation(Operation operation) {
        switch (operation) {
            case MULTIPLY_BASE:
                return EntityAttributeModifier.Operation.MULTIPLY_BASE;
            case MULTIPLY_TOTAL:
                return EntityAttributeModifier.Operation.MULTIPLY_TOTAL;
            default:
                return EntityAttributeModifier.Operation.ADDITION;
        }
    }

} // Enum Operation