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
import net.msymbios.rlovelyr.common.entity.InternalLogic;
import net.msymbios.rlovelyr.entity.internal.NativeEntityType;
import net.msymbios.rlovelyr.entity.internal.RobotEntity;
import net.msymbios.rlovelyr.entity.internal.enums.EntityTexture;

import static net.msymbios.rlovelyr.item.LovelyRobotItems.PRIME_SPAWN;

public class PrimeEntity extends RobotEntity  {

    // -- Constructor --

    public PrimeEntity(EntityType<? extends RobotEntity> entityType, World level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.PRIME;
    } // Constructor PrimeEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(PRIME_SPAWN, 1);
    } // setDropItem ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if (stack.isOf(Items.BLAZE_ROD)) return false;
        return super.canInteractWithItems(stack);
    } // canInteractWithItems ()

    @Override
    protected ActionResult handleItemInteraction (ItemStack stack, PlayerEntity player) {
        InternalLogic.displayInfo(this, "Method Called: handleItemInteraction ()", false);
        if(handleTexture(stack, player)) return ActionResult.SUCCESS;
        return ActionResult.PASS;
    } // handleItemInteraction ()

    @Override
    protected boolean handleTexture(ItemStack stack, PlayerEntity player) {
        InternalLogic.displayInfo(this, "Method Called: handleTexture ()", false);
        boolean result = false;
        var oldTexture = getTextureID();
        int textureID = this.getTextureID();

        if (stack.isOf(Items.BLAZE_ROD)) {
            InternalLogic.displayInfo(this, "Item Used: [Blaze Rod]", false);
            if (textureID >= EntityTexture.DARK_MATTER.getId() && textureID <= EntityTexture.HESTIA.getId()) {
                for (int i = textureID + 1; i <= EntityTexture.HESTIA.getId(); i++) {
                    InternalLogic.displayInfo(this, "Texture Check: [" + EntityTexture.byId(i).getName() + "]", false);
                    if (!nativeEntity.checkTexture(EntityTexture.byId(i))) continue;
                    InternalLogic.displayInfo(this, "Texture Chosen: [" + EntityTexture.byId(i).getName() + "]", false);
                    textureID = i;
                    break;
                }
            }
        }

        if (this.getTextureID() != textureID) setTexture(EntityTexture.byId(textureID));
        if (oldTexture == getTextureID()) setTexture(EntityTexture.DARK_MATTER);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().creativeMode)
                stack.decrement(1);
            result = true;
        }
        return result;
    } // handleTexture ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static DefaultAttributeContainer.Builder createAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, NativeEntityType.PRIME.getMaxHealth())
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, NativeEntityType.PRIME.getAttackDamage())
                .add(EntityAttributes.GENERIC_ATTACK_SPEED, NativeEntityType.PRIME.getAttackSpeed())
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, NativeEntityType.PRIME.getMoveSpeed())
                .add(EntityAttributes.GENERIC_ARMOR, NativeEntityType.PRIME.getArmour())
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, NativeEntityType.PRIME.getArmourToughness());
    } // createAttributes ()

} // Class PrimeEntity