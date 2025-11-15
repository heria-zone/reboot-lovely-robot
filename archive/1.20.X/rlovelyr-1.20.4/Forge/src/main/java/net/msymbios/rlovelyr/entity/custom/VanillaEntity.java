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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.VANILLA_SPAWN;

public class VanillaEntity extends RobotEntity  {
    
    // -- Constructor --

    public VanillaEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.VANILLA;
    } // Constructor VanillaEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(VANILLA_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.VANILLA.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.VANILLA.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.VANILLA.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.VANILLA.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.VANILLA.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.VANILLA.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class VanillaEntity