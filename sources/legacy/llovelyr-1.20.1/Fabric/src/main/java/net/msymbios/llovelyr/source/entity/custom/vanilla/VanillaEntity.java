package net.msymbios.llovelyr.source.entity.custom.vanilla;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import net.msymbios.llovelyr.source.entity.common.NativeEntityType;
import net.msymbios.llovelyr.source.LovelyItems;

/**
 * Vanilla robot entity - basic robot type (Fabric).
 * <p>
 * <b>Variant:</b> Standard robot with balanced stats.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class VanillaEntity extends LovelyRobot {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, World world, NativeEntityType nativeEntityType) {
        super(entityType, world, nativeEntityType);
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute container for Vanilla robot.
     *
     * @return attribute container with Vanilla stats
     */
    public static DefaultAttributeContainer createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.VANILLA.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.VANILLA.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.VANILLA.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.VANILLA.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.VANILLA.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.VANILLA.getArmourToughness())
                .build();
    } // createAttributes

} // Class: VanillaEntity
