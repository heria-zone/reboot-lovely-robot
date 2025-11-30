package net.msymbios.llovelyr.source.entity.custom.bunny2;

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
 * Bunny2 robot entity - bunny-themed robot variant (Forge).
 * <p>
 * <b>Variant:</b> Bunny-themed robot with unique appearance.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class Bunny2Entity extends LovelyRobot {

    // -- Constructor --

    public Bunny2Entity(EntityType<? extends LovelyRobot> entityType, Level level, RobotEntityType robotEntityType) {
        super(entityType, level, robotEntityType);
    } // Bunny2Entity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute supplier for Bunny2 robot using new RobotEntityType system.
     * <p>
     * <b>Architecture:</b> Retrieves stats from CombatData in RobotEntityType,
     * which is populated from config values during initialization.
     *
     * @return attribute supplier with Bunny2 stats from config
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeRobotType.BUNNY2.getData().getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeRobotType.BUNNY2.getData().getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeRobotType.BUNNY2.getData().getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeRobotType.BUNNY2.getData().getMoveSpeed())
                .add(Attributes.ARMOR, NativeRobotType.BUNNY2.getData().getArmor())
                .add(Attributes.ARMOR_TOUGHNESS, NativeRobotType.BUNNY2.getData().getArmorToughness())
                .build();
    } // createAttributes

} // Class: Bunny2Entity
