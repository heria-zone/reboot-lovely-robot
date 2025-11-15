package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.HONEY_SPAWN;

public class HoneyEntity extends RobotEntity  {
    
    // -- Constructor --

    public HoneyEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.HONEY;
    } // Constructor HoneyEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(HONEY_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.HONEY.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.HONEY.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.HONEY.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.HONEY.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.HONEY.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.HONEY.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class HoneyEntity