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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.KITSUNE_SPAWN;

public class KitsuneEntity extends RobotEntity  {
    
    // -- Constructor --

    public KitsuneEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.KITSUNE;
    } // Constructor KitsuneEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(KITSUNE_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.KITSUNE.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.KITSUNE.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.KITSUNE.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.KITSUNE.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.KITSUNE.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.KITSUNE.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class KitsuneEntity