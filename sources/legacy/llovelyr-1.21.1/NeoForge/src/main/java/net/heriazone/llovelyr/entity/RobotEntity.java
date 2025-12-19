package net.heriazone.llovelyr.entity;

import net.heriazone.llovelyr.source.LegacyItems;
import net.heriazone.lovelylib.api.entity.base.BaseRobotEntity;
import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.LovelyRobotEntity;
import net.heriazone.lovelylib.common.entity.enums.EntityVariant;
import net.heriazone.lovelylib.hzlib.api.entity.InternalAnimation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.instance.SingletonAnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;

/**
 * Forge-specific robot entity implementation with GeckoLib integration.
 * <p>
 * <b>Architecture:</b> Extends BaseRobotEntity for common behavior while implementing
 * GeoEntity for Forge-specific GeckoLib animation system integration.
 * <p>
 * <b>Design Decision:</b> Composition over inheritance - single entity class with
 * behavior configured via NativeEntityType instead of separate classes per variant.
 * Reduces code duplication and simplifies variant addition.
 * <p>
 * <b>Forge Specifics:</b> Uses variant-based pickup item resolution with Supplier.get()
 * calls for deferred registry object access.
 */
public class RobotEntity extends BaseRobotEntity implements GeoEntity {

    // -- Variables --

    private final AnimatableInstanceCache cache = new SingletonAnimatableInstanceCache(this);

    // -- Constructor --

    public RobotEntity(EntityType<? extends LovelyRobotEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
        handlePostSpawnInitialization();
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
        return new ItemStack(LegacyItems.ROBOT_CORE.get(), 1);
    } // getDropItem ()

    @Override
    public Item getPickupItem() {
        Item variantItem = getPickupItemForVariant(getRobotVariant());
        return variantItem != null ? variantItem : getDropItem().getItem();
    } // getPickupItem ()

    @Override
    protected Item getPickupItemForVariant(EntityVariant variant) {
        if (variant == null) return null;

        return switch (variant) {
            case Bunny -> LegacyItems.BUNNY_SPAWN.get();
            case Bunny2 -> LegacyItems.BUNNY2_SPAWN.get();
            case Dragon -> LegacyItems.DRAGON_SPAWN.get();
            case Honey -> LegacyItems.HONEY_SPAWN.get();
            case Kitsune -> LegacyItems.KITSUNE_SPAWN.get();
            case Neko -> LegacyItems.NEKO_SPAWN.get();
            case Vanilla -> LegacyItems.VANILLA_SPAWN.get();
            default -> null;
        };
    } // getPickupItemForVariant()

} // Class: RobotEntity