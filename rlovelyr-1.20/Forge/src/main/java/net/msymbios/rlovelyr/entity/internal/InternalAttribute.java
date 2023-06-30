package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.EntityAttribute;

public class InternalAttribute {

    // -- Variables --
    public EntityAttribute attribute;
    public float value;

    // -- Construct --
    public InternalAttribute(EntityAttribute attribute, float value) {
        this.attribute = attribute;
        this.value = value;
    } // Construct RobotAttributeInstance

} // Class InternalAttribute