package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityVariant;
import net.msymbios.llovelyr.source.items.LovelyItems;

/**
 * Vanilla robot entity - basic robot type.
 * <p>
 * <b>Variant:</b> Standard robot with balanced stats.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class VanillaEntity extends LovelyRobot {

    // -- Constructor --

    public VanillaEntity(EntityType<? extends LovelyRobot> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = EntityVariant.VANILLA;
    } // VanillaEntity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    @Override
    protected void displayExtra() {
        // Vanilla robots use default display behavior from parent
    } // displayExtra

    // -- Attribute Creation --

    /**
     * Creates attribute supplier for Vanilla robot.
     *
     * @return attribute supplier with Vanilla stats
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, EntityVariant.VANILLA.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, EntityVariant.VANILLA.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, EntityVariant.VANILLA.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, EntityVariant.VANILLA.getMoveSpeed())
                .add(Attributes.ARMOR, EntityVariant.VANILLA.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, EntityVariant.VANILLA.getArmourToughness())
                .build();
    } // createAttributes

} // Class: VanillaEntity
