package net.heriazone.lovelylib.api.entity.base;

import net.heriazone.lovelylib.common.entity.NativeEntityType;
import net.heriazone.lovelylib.common.entity.common.LovelyRobotEntity;
import net.heriazone.lovelylib.common.entity.enums.EntityVariant;
import net.heriazone.lovelylib.utils.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Base class for robot entity implementations across different loaders.
 * <p>
 * <b>Architecture:</b> Provides common robot behavior while allowing loader-specific
 * customization for GeckoLib integration, item handling, and platform-specific APIs.
 * <p>
 * <b>Design Decision:</b> Abstract class rather than interface to provide default
 * implementations while requiring loader-specific GeckoLib integration. Keeps
 * animation logic in loaders to respect GeckoLib isolation constraint.
 * <p>
 * <b>Loader Responsibilities:</b> Subclasses must implement GeckoLib interfaces
 * (GeoEntity), animation controllers, and platform-specific item resolution.
 */
public abstract class BaseRobotEntity extends LovelyRobotEntity {

    // -- Constructor --

    public BaseRobotEntity(EntityType<? extends LovelyRobotEntity> entityType, Level level, NativeEntityType nativeEntity) {
        super(entityType, level, nativeEntity);
    } // Constructor: BaseRobotEntity()

    // -- Abstract Methods (Loader-Specific) --

    /**
     * Gets the item that should be dropped when robot dies.
     * <p>
     * <b>Loader Variation:</b> Different loaders may have different item access
     * patterns (direct reference vs Supplier.get()) or different core items.
     *
     * @return ItemStack to drop on death
     */
    @Override
    public abstract ItemStack getDropItem();

    /**
     * Gets the pickup item for this robot variant.
     * <p>
     * <b>Loader Variation:</b> Fabric uses constructor-injected item, while
     * Forge/NeoForge use variant-based lookup with Supplier.get() calls.
     *
     * @return Item that can be used to pick up this robot
     */
    @Override
    public abstract Item getPickupItem();

    // -- Common Helper Methods --

    /**
     * Resolves pickup item based on entity variant using switch expression.
     * <p>
     * <b>Usage:</b> Provides common variant-to-item mapping logic that can be
     * used by Forge/NeoForge implementations. Fabric can override with injected item.
     * <p>
     * <b>Extensibility:</b> Subclasses can override to provide loader-specific
     * item resolution or add custom variants.
     *
     * @param variant the robot variant to get pickup item for
     * @return Item corresponding to the variant, or null if unknown
     */
    protected Item getPickupItemForVariant(EntityVariant variant) {
        // Default implementation returns null - subclasses should override
        // with loader-specific item resolution logic
        return null;
    } // getPickupItemForVariant()

    /**
     * Gets the robot's entity variant from its native entity type.
     * <p>
     * <b>Usage:</b> Provides consistent variant resolution across loaders
     * for pickup item determination and other variant-specific logic.
     *
     * @return EntityVariant for this robot, or null if not found
     */
    protected EntityVariant getRobotVariant() {
        return EntityVariant.byName(this.nativeEntity.getKey());
    } // getRobotVariant()

    /**
     * Validates that the robot variant is supported.
     * <p>
     * <b>Safety:</b> Ensures robot has valid variant before attempting
     * variant-specific operations like pickup item resolution.
     *
     * @return true if robot has valid variant, false otherwise
     */
    protected boolean hasValidVariant() {
        return getRobotVariant() != null;
    } // hasValidVariant()

    // -- Common Behavior Methods --

    /**
     * Handles robot initialization after spawning.
     * <p>
     * <b>Common Logic:</b> Performs standard robot setup that applies to all
     * loaders: registry registration, data validation, and initial state setup.
     * <p>
     * <b>Usage:</b> Call from loader-specific spawn handling after entity creation
     * and before applying custom data.
     */
    protected void handlePostSpawnInitialization() {
        // Registry registration is handled by the base entity during normal lifecycle
        // ensureRegistered() is called automatically during tick() and data loading

        // Validate entity data consistency
        EntitySpawnHelper.validateEntityData(this);
    } // handlePostSpawnInitialization()

    /**
     * Handles robot cleanup before removal.
     * <p>
     * <b>Common Logic:</b> Performs standard cleanup that applies to all loaders:
     * registry cleanup, resource cleanup, and state finalization.
     * <p>
     * <b>Usage:</b> Call from loader-specific removal handling or death events.
     */
    protected void handlePreRemovalCleanup() {
        // Registry cleanup is handled by the base LovelyRobotEntity
        // Additional cleanup can be added here if needed
    } // handlePreRemovalCleanup()

} // Class: BaseRobotEntity