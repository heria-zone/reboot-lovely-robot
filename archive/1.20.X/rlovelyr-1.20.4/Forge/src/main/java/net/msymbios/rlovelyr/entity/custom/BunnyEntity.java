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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.BUNNY_SPAWN;

public class BunnyEntity extends RobotEntity  {

    // -- Constructor --

    public BunnyEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.BUNNY;
    } // Constructor BunnyEntity ()

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(BUNNY_SPAWN.get(), 1);
    } // setDropItem ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.BUNNY.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.BUNNY.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.BUNNY.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.BUNNY.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.BUNNY.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.BUNNY.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class BunnyEntity