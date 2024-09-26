package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.msymbios.rlovelyr.common.entity.InternalLogic;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.EMPYRIUM_SPAWN;

public class EmpyriumEntity extends RobotEntity  {

    // -- Constructor --

    public EmpyriumEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.EMPYRIUM;
    } // Constructor EmpyriumEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(EMPYRIUM_SPAWN.get(), 1);
    } // setDropItem ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if (stack.is(Items.BLAZE_ROD)) return false;
        return super.canInteractWithItems(stack);
    } // canInteractWithItems ()

    @Override
    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.EMPYRIUM.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.EMPYRIUM.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.EMPYRIUM.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.EMPYRIUM.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.EMPYRIUM.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.EMPYRIUM.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class EmpyriumEntity