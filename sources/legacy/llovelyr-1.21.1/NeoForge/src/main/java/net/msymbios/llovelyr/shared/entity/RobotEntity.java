package net.msymbios.llovelyr.shared.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.msymbios.llovelyr.common.entity.NativeEntityType;
import net.msymbios.llovelyr.common.entity.enums.EntityVariant;
import net.msymbios.llovelyr.lib.entity.InternalAnimation;
import net.msymbios.llovelyr.source.LovelyItems;
import net.msymbios.llovelyr.common.entity.common.LovelyRobotEntity;
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

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobotEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
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
        return new ItemStack(LovelyItems.ROBOT_CORE.get(), 1);
    } // getDropItem ()

    @Override
    public Item getPickupItem() {
        EntityVariant variant = EntityVariant.byName(this.nativeEntity.getKey());
        assert variant != null;
        return switch (variant) {
            case Bunny -> LovelyItems.BUNNY_SPAWN.get();
            case Bunny2 -> LovelyItems.BUNNY2_SPAWN.get();
            case Dragon -> LovelyItems.DRAGON_SPAWN.get();
            case Honey -> LovelyItems.HONEY_SPAWN.get();
            case Kitsune -> LovelyItems.KITSUNE_SPAWN.get();
            case Neko -> LovelyItems.NEKO_SPAWN.get();
            case Vanilla -> LovelyItems.VANILLA_SPAWN.get();
            default -> getDropItem().getItem();
        };
    } // getPickupItem ()

} // Class: RobotEntity
