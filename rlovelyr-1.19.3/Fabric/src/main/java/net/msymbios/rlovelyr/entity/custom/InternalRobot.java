package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.world.World;

public abstract class InternalRobot extends TameableEntity {

    // -- Variables --
    protected static final TrackedData<Boolean> AUTO_ATTACK = DataTracker.registerData(VanillaEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    // -- Construct --
    protected InternalRobot(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Construct InternalRobot

    // -- Methods --

    // -- AUTO ATTACK --
    public boolean getAutoAttack() {
        boolean value = false;
        try {value = this.dataTracker.get(AUTO_ATTACK);}
        catch (Exception ignored) {}
        return value;
    } // getAutoAttack ()

    public void setAutoAttack(boolean value) {
        this.dataTracker.set(AUTO_ATTACK, value);
    } // setAutoAttack ()


} // Class InternalRobot