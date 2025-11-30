package net.msymbios.llovelyr.source.entity.custom.bunny2;

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
 * Bunny2 robot entity - bunny-themed robot variant (Fabric).
 * <p>
 * <b>Variant:</b> Bunny-themed robot with unique appearance.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class Bunny2Entity extends LovelyRobot {

    // -- Constructor --

    public Bunny2Entity(EntityType<? extends LovelyRobot> entityType, World world, RobotEntityType robotEntityType) {
        super(entityType, world, robotEntityType);
    } // Bunny2Entity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute container for Bunny2 robot using new RobotEntityType system.
     * <p>
     * <b>Architecture:</b> Retrieves stats from CombatData in RobotEntityType,
     * which is populated from config values during initialization.
     *
     * @return attribute container with Bunny2 stats from config
     */
    public static DefaultAttributeContainer createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeRobotType.BUNNY2.getData().getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeRobotType.BUNNY2.getData().getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeRobotType.BUNNY2.getData().getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeRobotType.BUNNY2.getData().getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeRobotType.BUNNY2.getData().getArmor())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeRobotType.BUNNY2.getData().getArmorToughness())
                .build();
    } // createAttributes

} // Class: Bunny2Entity
