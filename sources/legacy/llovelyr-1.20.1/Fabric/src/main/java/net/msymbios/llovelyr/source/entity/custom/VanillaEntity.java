package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityVariant;
import net.msymbios.llovelyr.source.items.LovelyItems;

/**
 * Vanilla robot entity - basic robot type (Fabric).
 * <p>
 * <b>Variant:</b> Standard robot with balanced stats.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class VanillaEntity extends LovelyRobot {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, World world) {
        super(entityType, world);
        this.nativeEntity = EntityVariant.VANILLA;
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // setDropItem

    @Override
    protected void displayExtra() {
        // Vanilla robots use default display behavior from parent
    } // displayExtra

    // -- Attribute Creation --

    /**
     * Creates attribute container for Vanilla robot.
     *
     * @return attribute container with Vanilla stats
     */
    public static DefaultAttributeContainer createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, EntityVariant.VANILLA.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, EntityVariant.VANILLA.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, EntityVariant.VANILLA.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, EntityVariant.VANILLA.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, EntityVariant.VANILLA.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, EntityVariant.VANILLA.getArmourToughness())
                .build();
    } // createAttributes

} // Class: VanillaEntity
