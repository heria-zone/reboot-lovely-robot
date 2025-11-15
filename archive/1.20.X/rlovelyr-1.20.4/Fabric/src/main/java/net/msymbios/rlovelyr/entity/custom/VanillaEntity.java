package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

public class VanillaEntity extends RobotEntity  {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends RobotEntity> entityType, World level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.VANILLA;
    } // Constructor VanillaEntity ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.VANILLA.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.VANILLA.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.VANILLA.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.VANILLA.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.VANILLA.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.VANILLA.getArmourToughness());
    } // createAttributes ()

} // Class VanillaEntity