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

import static net.msymbios.rlovelyr.item.LovelyRobotItems.HYPERION_SPAWN;

public class HyperionEntity extends RobotEntity  {

    // -- Constructor --

    public HyperionEntity(EntityType<? extends RobotEntity> entityType, Level level) {
        super(entityType, level);
        this.nativeEntity = NativeEntityType.HYPERION;
    } // Constructor HyperionEntity ()

    // -- Inherited Methods --
    
    @Override
    public ItemStack setDropItem() {
        return new ItemStack(HYPERION_SPAWN.get(), 1);
    } // setDropItem ()

    @Override
    protected boolean canInteractWithItems(ItemStack stack) {
        if (stack.is(Items.BLAZE_ROD)) return false;
        return super.canInteractWithItems(stack);
    } // canInteractWithItems ()

    @Override
    protected InteractionResult handleItemInteraction (ItemStack stack, Player player) {
        InternalLogic.displayInfo(this, "Method Called: handleItemInteraction ()", false);
        if(handleTexture(stack, player)) return InteractionResult.SUCCESS;
        return InteractionResult.PASS;
    } // handleItemInteraction ()

    @Override
    protected boolean handleTexture(ItemStack stack, Player player) {
        InternalLogic.displayInfo(this, "Method Called: handleTexture ()", false);
        boolean result = false;
        var oldTexture = getTextureID();
        int textureID = this.getTextureID();

        if (stack.is(Items.BLAZE_ROD)) {
            InternalLogic.displayInfo(this, "Item Used: [Blaze Rod]", false);
            if (textureID >= EntityTexture.COMMANDER.getId() && textureID <= EntityTexture.VALKYRIE.getId()) {
                for (int i = textureID + 1; i <= EntityTexture.VALKYRIE.getId(); i++) {
                    InternalLogic.displayInfo(this, "Texture Check: [" + EntityTexture.byId(i).getName() + "]", false);
                    if (!nativeEntity.checkTexture(EntityTexture.byId(i))) continue;
                    InternalLogic.displayInfo(this, "Texture Chosen: [" + EntityTexture.byId(i).getName() + "]", false);
                    textureID = i;
                    break;
                }
            }
        }

        if (this.getTextureID() != textureID) setTexture(EntityTexture.byId(textureID));
        if (oldTexture == getTextureID()) setTexture(EntityTexture.COMMANDER);

        if(oldTexture != getTextureID()) {
            if (!player.getAbilities().instabuild)
                stack.shrink(1);
            result = true;
        }
        return result;
    } // handleTexture ()

    // -- Custom Methods --

    /**
     * @return the additional attributes to add to the entity.
     */
    public static AttributeSupplier createAttributes() {
        return Animal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, NativeEntityType.HYPERION.getMaxHealth())
                .add(Attributes.ATTACK_DAMAGE, NativeEntityType.HYPERION.getAttackDamage())
                .add(Attributes.ATTACK_SPEED, NativeEntityType.HYPERION.getAttackSpeed())
                .add(Attributes.MOVEMENT_SPEED, NativeEntityType.HYPERION.getMoveSpeed())
                .add(Attributes.ARMOR, NativeEntityType.HYPERION.getArmour())
                .add(Attributes.ARMOR_TOUGHNESS, NativeEntityType.HYPERION.getArmourToughness())
                .build();
    } // createAttributes ()

} // Class HyperionEntity