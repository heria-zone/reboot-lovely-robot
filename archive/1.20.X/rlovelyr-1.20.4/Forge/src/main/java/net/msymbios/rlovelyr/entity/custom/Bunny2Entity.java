package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.BUNNY2_SPAWN;

public class Bunny2Entity extends RobotEntity  {

    // -- Constructor --

    public Bunny2Entity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.BUNNY2;
    } // Constructor Bunny2Entity ()

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(BUNNY2_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.BUNNY2.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.BUNNY2.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.BUNNY2.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.BUNNY2.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.BUNNY2.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.BUNNY2.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class Bunny2Entity