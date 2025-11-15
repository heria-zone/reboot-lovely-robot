package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RangedRobotEntity;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

public class StingerEntity extends RangedRobotEntity {

    // -- Constructor --

    public StingerEntity(EntityType<? extends RangedRobotEntity> entityType, World level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.STINGER;
    } // Constructor StingerEntity ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.STINGER.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.STINGER.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.STINGER.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.STINGER.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.STINGER.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.STINGER.getArmourToughness());
    } // createAttributes ()

} // Class StingerEntity