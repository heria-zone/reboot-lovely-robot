package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.framework.utils.Version;
import net.heriazone.hzlib.utils.IReadWriteNBT;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;

/**
 * <p>Defines item conversion when entity is picked up or captured.</p>
 * <p>
 * <b>Architecture:</b> Decouples pickup mechanics from entity implementation,
 * enabling different entity types to convert into different items while
 * preserving entity state through NBT data transfer.
 * <p>
 * <b>Design Decision:</b> Entity is responsible for interpreting NBT data,
 * while this feature only defines which item to create and how to package
 * the entity's state for transfer.
 * <p>
 * <b>Use Cases:</b>
 * <ul>
 * <li>Robots convert to spawn items with preserved stats</li>
 * <li>Custom entities convert to capture items</li>
 * <li>Instance-level overrides for special entities</li>
 * </ul>
 */
public class PickupFeature {

    // -- Fields --

    private final Item pickupItem;
    private final Function<IReadWriteNBT, CompoundTag> nbtPackager;
    private final boolean preserveStats;

    // -- Constructors --

    /**
     * Creates pickup feature with specified item and default NBT packaging.
     * <p>
     * <b>Default Behavior:</b> Preserves all entity stats using entity's
     * writeToNBT() method.
     *
     * @param pickupItem item to create when entity is picked up
     * @throws NullPointerException if pickupItem is null
     */
    public PickupFeature(@NotNull Item pickupItem) {
        this(pickupItem, true);
    } // Constructor: PickupFeature ()

    /**
     * Creates pickup feature with specified item and stat preservation flag.
     * <p>
     * <b>Stat Preservation:</b> When true, uses entity's writeToNBT() to
     * package all data. When false, creates empty NBT (fresh entity on spawn).
     *
     * @param pickupItem item to create when entity is picked up
     * @param preserveStats whether to preserve entity stats in item NBT
     * @throws NullPointerException if pickupItem is null
     */
    public PickupFeature(@NotNull Item pickupItem, boolean preserveStats) {
        this(pickupItem, preserveStats ? PickupFeature::defaultNbtPackager : entity -> new CompoundTag());
    } // Constructor: PickupFeature ()

    /**
     * Creates pickup feature with custom NBT packaging logic.
     * <p>
     * <b>Use Case:</b> Enables selective stat preservation or custom data
     * transformation during pickup (e.g., reset health but keep level).
     *
     * @param pickupItem item to create when entity is picked up
     * @param nbtPackager function to create NBT from entity state
     * @throws NullPointerException if pickupItem or nbtPackager is null
     */
    public PickupFeature(@NotNull Item pickupItem, @NotNull Function<IReadWriteNBT, CompoundTag> nbtPackager) {
        this.pickupItem = Objects.requireNonNull(pickupItem, "Pickup item cannot be null");
        this.nbtPackager = Objects.requireNonNull(nbtPackager, "NBT packager cannot be null");
        this.preserveStats = true; // Custom packager implies preservation intent
    } // Constructor: PickupFeature ()

    // -- Public Methods --

    /**
     * Gets the item to create when entity is picked up.
     *
     * @return pickup item
     */
    public Item getPickupItem() {
        return pickupItem;
    } // getPickupItem ()

    /**
     * Checks if entity stats should be preserved in pickup item.
     *
     * @return true if stats are preserved, false for fresh entity
     */
    public boolean shouldPreserveStats() {
        return preserveStats;
    } // shouldPreserveStats ()

    /**
     * Creates pickup item with entity data packaged in DataComponents.
     * <p>
     * <b>Architecture:</b> Delegates NBT creation to configured packager,
     * enabling flexible data transfer strategies per entity type.
     * <p>
     * <b>State Transfer:</b> Entity's current state is serialized into
     * item's DataComponents.CUSTOM_DATA, allowing full restoration on spawn.
     * <p>
     * <b>1.21.1 Pattern:</b> Uses DataComponents.CUSTOM_DATA instead of
     * legacy NBT tags for Minecraft 1.21.1+ compatibility.
     *
     * @param entity entity being picked up
     * @return ItemStack with entity data in DataComponents
     * @throws NullPointerException if entity is null
     */
    public ItemStack createPickupItem(@NotNull IReadWriteNBT entity) {
        Objects.requireNonNull(entity, "Entity cannot be null");

        ItemStack stack = new ItemStack(pickupItem);
        CompoundTag nbt = nbtPackager.apply(entity);

        if (!nbt.isEmpty()) {
            // Use DataComponents.CUSTOM_DATA for 1.21.1+
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        }

        return stack;
    } // createPickupItem ()

    /**
     * Applies pickup item's data to entity.
     * <p>
     * <b>Architecture:</b> Entity is responsible for interpreting NBT data
     * through its readFromNBT() method. This feature only extracts the data
     * from item and passes it to entity.
     * <p>
     * <b>Design Decision:</b> Separation of concerns - feature handles item
     * interaction, entity handles data interpretation.
     * <p>
     * <b>1.21.1 Pattern:</b> Reads from DataComponents.CUSTOM_DATA instead of
     * legacy NBT tags for Minecraft 1.21.1+ compatibility.
     *
     * @param stack pickup item with data
     * @param entity entity to receive data
     * @throws NullPointerException if stack or entity is null
     */
    public void applyPickupData(@NotNull ItemStack stack, @NotNull IReadWriteNBT entity, Version version) {
        Objects.requireNonNull(stack, "ItemStack cannot be null");
        Objects.requireNonNull(entity, "Entity cannot be null");

        // Use DataComponents.CUSTOM_DATA for 1.21.1+
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null && !customData.isEmpty()) {
            CompoundTag entityData = customData.copyTag();
            // Entity interprets its own data through readFromNBT
            entity.readFromNBT(entityData, version);
        }
    } // applyPickupData ()

    // -- Default NBT Packager --

    /**
     * Default NBT packaging strategy using entity's writeToNBT().
     * <p>
     * <b>Behavior:</b> Captures complete entity state including stats,
     * protections, enchantments, and custom data.
     *
     * @param entity entity to package
     * @return CompoundTag with entity data
     */
    private static CompoundTag defaultNbtPackager(@NotNull IReadWriteNBT entity) {
        CompoundTag nbt = new CompoundTag();
        return entity.writeToNBT(nbt);
    } // defaultNbtPackager ()

} // Class: PickupFeature
