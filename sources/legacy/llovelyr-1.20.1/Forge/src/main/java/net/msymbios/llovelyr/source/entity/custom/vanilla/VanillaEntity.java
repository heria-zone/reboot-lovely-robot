package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.NativeEntityType;
import net.msymbios.llovelyr.source.LovelyItems;

/**
 * Vanilla robot entity - basic robot type.
 * <p>
 * <b>Variant:</b> Standard robot with balanced stats.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class VanillaEntity extends LovelyRobot {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, Level level, NativeEntityType nativeEntityType) {
        super(entityType, level, nativeEntityType);
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute supplier for Vanilla robot.
     *
     * @return attribute supplier with Vanilla stats
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
    } // createAttributes

} // Class: VanillaEntity
