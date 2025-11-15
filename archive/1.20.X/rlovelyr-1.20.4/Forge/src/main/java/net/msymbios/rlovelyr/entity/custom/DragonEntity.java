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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.DRAGON_SPAWN;

public class DragonEntity extends RobotEntity  {
    
    // -- Constructor --

    public DragonEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.DRAGON;
    } // Constructor DragonEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(DRAGON_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.DRAGON.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.DRAGON.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.DRAGON.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.DRAGON.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.DRAGON.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.DRAGON.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class DragonEntity