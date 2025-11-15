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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.NEKO_SPAWN;

public class NekoEntity extends RobotEntity  {
    
    // -- Constructor --

    public NekoEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.NEKO;
    } // Constructor NekoEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(NEKO_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.NEKO.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.NEKO.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.NEKO.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.NEKO.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.NEKO.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.NEKO.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class NekoEntity