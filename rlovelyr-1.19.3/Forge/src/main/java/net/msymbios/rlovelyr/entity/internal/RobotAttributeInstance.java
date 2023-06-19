package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.entity.enums.RobotAttribute;

public class RobotAttributeInstance {

    // -- Variables --
    public RobotAttribute attribute;
    public float value;

    // -- Construct --
    public RobotAttributeInstance(RobotAttribute attribute, float value) {
        this.attribute = attribute;
        this.value = value;
    } // Construct RobotAttributeInstance

} // Class RobotAttributeInstance