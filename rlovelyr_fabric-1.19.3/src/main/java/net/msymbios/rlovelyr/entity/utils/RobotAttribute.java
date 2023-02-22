package net.msymbios.rlovelyr.entity.utils;

import net.msymbios.rlovelyr.entity.enums.RobotStat;

public class RobotAttribute {

    // -- Variables --
    public RobotStat attribute;
    public float value;

    // -- Construct --
    public RobotAttribute(RobotStat attribute, float value) {
        this.attribute = attribute;
        this.value = value;
    } // Construct RobotAttribute

} // Class RobotAttribute