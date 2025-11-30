package net.msymbios.llovelyr.source.entity.custom.vanilla;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import net.msymbios.llovelyr.common.entity.type.RobotEntityType;
import net.msymbios.llovelyr.source.entity.type.NativeRobotType;
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

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, World world, RobotEntityType robotEntityType) {
        super(entityType, world, robotEntityType);
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute container for Vanilla robot using new RobotEntityType system.
     * <p>
     * <b>Architecture:</b> Retrieves stats from CombatData in RobotEntityType,
     * which is populated from config values during initialization.
     *
     * @return attribute container with Vanilla stats from config
     */
    public static DefaultAttributeContainer createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeRobotType.VANILLA.getData().getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeRobotType.VANILLA.getData().getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeRobotType.VANILLA.getData().getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeRobotType.VANILLA.getData().getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeRobotType.VANILLA.getData().getArmor())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeRobotType.VANILLA.getData().getArmorToughness())
                .build();
    } // createAttributes

} // Class: VanillaEntity
