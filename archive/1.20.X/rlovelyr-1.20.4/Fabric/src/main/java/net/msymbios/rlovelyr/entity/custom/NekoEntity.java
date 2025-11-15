package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

public class NekoEntity extends RobotEntity  {

    // -- Constructor --

    public NekoEntity(EntityType<? extends RobotEntity> entityType, World level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.NEKO;
    } // Constructor NekoEntity ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.NEKO.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.NEKO.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.NEKO.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.NEKO.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.NEKO.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.NEKO.getArmourToughness());
    } // createAttributes ()

} // Class NekoEntity