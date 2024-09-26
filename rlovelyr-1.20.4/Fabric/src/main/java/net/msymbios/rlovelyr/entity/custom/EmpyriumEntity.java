package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.EMPYRIUM_SPAWN;

public class EmpyriumEntity extends RobotEntity  {

    // -- Constructor --

    public EmpyriumEntity(EntityType<? extends RobotEntity> entityType, World level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.EMPYRIUM;
    } // Constructor EmpyriumEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(EMPYRIUM_SPAWN, 1);
    } // setDropItem ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if (stack.isOf(Items.BLAZE_ROD)) return false;
        return super.canInteractWithItems(stack);
    } // canInteractWithItems ()

    @Override
    protected ActionResult handleItemInteraction (ItemStack stack, PlayerEntity player) {
        return ActionResult.PASS;
    } // handleItemInteraction ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.EMPYRIUM.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.EMPYRIUM.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.EMPYRIUM.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.EMPYRIUM.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.EMPYRIUM.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.EMPYRIUM.getArmourToughness());
    } // createAttributes ()

} // Class EmpyriumEntity