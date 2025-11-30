package net.msymbios.llovelyr.source.entity.custom.vanilla;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.source.entity.common.LovelyRobot;
import net.msymbios.llovelyr.common.entity.type.RobotEntityType;
import net.msymbios.llovelyr.source.entity.type.NativeRobotType;
import net.msymbios.llovelyr.source.LovelyItems;

/**
 * Vanilla robot entity - basic robot type (Forge).
 * <p>
 * <b>Variant:</b> Standard robot with balanced stats.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class VanillaEntity extends LovelyRobot {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, Level level, RobotEntityType robotEntityType) {
        super(entityType, level, robotEntityType);
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute supplier for Vanilla robot using new RobotEntityType system.
     * <p>
     * <b>Architecture:</b> Retrieves stats from CombatData in RobotEntityType,
     * which is populated from config values during initialization.
     *
     * @return attribute supplier with Vanilla stats from config
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeRobotType.VANILLA.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeRobotType.VANILLA.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeRobotType.VANILLA.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeRobotType.VANILLA.getData().getMoveSpeed())
                .add(Attributes.ARMOR, NativeRobotType.VANILLA.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS, NativeRobotType.VANILLA.getData().getArmorToughness())
                .build();
    } // createAttributes

} // Class: VanillaEntity
