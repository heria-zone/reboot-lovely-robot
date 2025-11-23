package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.NativeEntityType;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityVariant;
import net.msymbios.llovelyr.source.items.LovelyItems;

/**
 * Bunny2 robot entity - bunny-themed robot variant (Fabric).
 * <p>
 * <b>Variant:</b> Bunny-themed robot with unique appearance.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class Bunny2Entity extends LovelyRobot {

    // -- Constructor --

    public Bunny2Entity(EntityType<? extends LovelyRobot> entityType, World world) {
        super(entityType, world, NativeEntityType.BUNNY2);
    } // Bunny2Entity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute container for Bunny2 robot.
     *
     * @return attribute container with Bunny2 stats
     */
    public static DefaultAttributeContainer createAttributes() {
        return AnimalEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.BUNNY2.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.BUNNY2.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.BUNNY2.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.BUNNY2.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.BUNNY2.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.BUNNY2.getArmourToughness())
                .build();
    } // createAttributes

} // Class: Bunny2Entity
