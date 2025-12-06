package net.msymbios.llovelyr.shared.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
import net.msymbios.llovelyr.lib.entity.InternalAnimation;
import net.msymbios.llovelyr.source.LovelyItems;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Unified entity implementation for all robot variants.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via NativeEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 */
public class RobotEntity extends LovelyRobotEntity implements GeoEntity {

    // -- Variables --

    private final Item pickupItem;

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobotEntity> entityType, Level level, NativeEntityType nativeEntity, Item item) {
        super(entityType, level, nativeEntity);
        pickupItem = item;
    } // Constructor: RobotEntity()

    // -- Inherited Methods --

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegister) {
        controllerRegister.add(InternalAnimation.locomotionAnimation(this));
        controllerRegister.add(InternalAnimation.attackAnimation(this));
    } // registerControllers ()

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return cache; } // getAnimatableInstanceCache ()

    @Override
    public ItemStack getDropItem() {
        return new ItemStack(LovelyItems.ROBOT_CORE, 1);
    } // getDropItem ()

    @Override
    public Item getPickupItem() {
        return pickupItem;
        //EntityVariant variant = EntityVariant.byName(this.nativeEntity.getKey());
        //assert variant != null;
        //return switch (variant) {
        //    case Bunny -> LovelyItems.BUNNY_SPAWN;
        //    case Bunny2 -> LovelyItems.BUNNY2_SPAWN;
        //    case Dragon -> LovelyItems.DRAGON_SPAWN;
        //    case Honey -> LovelyItems.HONEY_SPAWN;
        //    case Kitsune -> LovelyItems.KITSUNE_SPAWN;
        //    case Neko -> LovelyItems.NEKO_SPAWN;
        //    case Vanilla -> LovelyItems.VANILLA_SPAWN;
        //    default -> getDropItem().getItem();
        //};
    } // getPickupItem ()

} // Class: RobotEntity