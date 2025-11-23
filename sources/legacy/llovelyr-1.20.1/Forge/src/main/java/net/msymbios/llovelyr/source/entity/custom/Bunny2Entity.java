package net.msymbios.llovelyr.source.entity.custom;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.source.entity.LovelyRobot;
import net.msymbios.llovelyr.source.entity.internal.NativeEntityType;
import net.msymbios.llovelyr.source.entity.internal.enums.EntityVariant;
import net.msymbios.llovelyr.source.items.LovelyItems;

/**
 * Bunny2 robot entity - bunny-themed robot variant.
 * <p>
 * <b>Variant:</b> Bunny-themed robot with unique appearance.
 * <p>
 * <b>Drop Item:</b> Robot core for respawning with saved data.
 */
public class Bunny2Entity extends LovelyRobot {

    // -- Constructor --

    public Bunny2Entity(EntityType<? extends LovelyRobot> entityType, Level level) {
        super(entityType, level, NativeEntityType.BUNNY2);
    } // Bunny2Entity

    // -- Inherited Methods --

    @Override
    public ItemStack setDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // setDropItem

    // -- Attribute Creation --

    /**
     * Creates attribute supplier for Bunny2 robot.
     *
     * @return attribute supplier with Bunny2 stats
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.BUNNY2.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.BUNNY2.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.BUNNY2.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.BUNNY2.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.BUNNY2.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.BUNNY2.getArmourToughness())
                .build();
    } // createAttributes

} // Class: Bunny2Entity
