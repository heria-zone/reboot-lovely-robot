package net.heriazone.lovelylib.hzlib.api.entity.features;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.IntSupplier;

/**
 * <p>Defines core item drop when entity dies.</p>
 * <p>
 * <b>Architecture:</b> Separates drop logic from entity implementation,
 * enabling different entity types to drop different core items with
 * configurable quantities.
 * <p>
 * <b>Design Decision:</b> Core drop is distinct from loot drops. Core
 * represents the entity's essential item (e.g., robot core), while loot
 * represents additional rewards (handled by LootDropFeature).
 * <p>
 * <b>Use Cases:</b>
 * <ul>
 * <li>Robots drop robot cores with preserved stats</li>
 * <li>Custom entities drop their defining items</li>
 * <li>Dynamic drop counts based on entity state</li>
 * </ul>
 */
public class DropFeature {

    // -- Fields --

    private final Item dropItem;
    private final IntSupplier dropCountSupplier;
    private final boolean preserveStats;

    // -- Constructors --

    /**
     * Creates drop feature with specified item and default count (1).
     * <p>
     * <b>Default Behavior:</b> Drops 1 item with stats preserved.
     *
     * @param dropItem item to drop on death
     * @throws NullPointerException if dropItem is null
     */
    public DropFeature(@NotNull Item dropItem) {
        this(dropItem, 1, true);
    } // Constructor: DropFeature ()

    /**
     * Creates drop feature with specified item, count, and stat preservation.
     * <p>
     * <b>Stat Preservation:</b> When true, dropped item contains entity's
     * NBT data. When false, dropped item is fresh (no stats).
     *
     * @param dropItem item to drop on death
     * @param dropCount number of items to drop
     * @param preserveStats whether to preserve entity stats in dropped item
     * @throws NullPointerException if dropItem is null
     */
    public DropFeature(@NotNull Item dropItem, int dropCount, boolean preserveStats) {
        this(dropItem, () -> Math.max(0, dropCount), preserveStats);
    } // Constructor: DropFeature ()

    /**
     * Creates drop feature with dynamic drop count supplier.
     * <p>
     * <b>Use Case:</b> Enables drop count based on entity state (e.g., level,
     * health, or custom conditions).
     *
     * @param dropItem item to drop on death
     * @param dropCountSupplier function providing drop count
     * @param preserveStats whether to preserve entity stats in dropped item
     * @throws NullPointerException if dropItem or dropCountSupplier is null
     */
    public DropFeature(@NotNull Item dropItem, @NotNull IntSupplier dropCountSupplier, boolean preserveStats) {
        this.dropItem = Objects.requireNonNull(dropItem, "Drop item cannot be null");
        this.dropCountSupplier = Objects.requireNonNull(dropCountSupplier, "Drop count supplier cannot be null");
        this.preserveStats = preserveStats;
    } // Constructor: DropFeature ()

    // -- Public Methods --

    /**
     * Gets the item to drop on death.
     *
     * @return drop item
     */
    public Item getDropItem() {
        return dropItem;
    } // getDropItem ()

    /**
     * Gets the number of items to drop.
     * <p>
     * <b>Dynamic Evaluation:</b> Calls supplier each time, allowing count
     * to vary based on current conditions.
     *
     * @return drop count (clamped to non-negative)
     */
    public int getDropCount() {
        return Math.max(0, dropCountSupplier.getAsInt());
    } // getDropCount ()

    /**
     * Checks if entity stats should be preserved in dropped item.
     *
     * @return true if stats are preserved, false for fresh item
     */
    public boolean shouldPreserveStats() {
        return preserveStats;
    } // shouldPreserveStats ()

    /**
     * Creates drop item stack with appropriate count.
     * <p>
     * <b>Architecture:</b> Returns ItemStack ready for world spawn. NBT data
     * is not included here - caller must use PickupFeature.createPickupItem()
     * if stat preservation is needed.
     *
     * @return ItemStack with configured drop count
     */
    public ItemStack createDropStack() {
        int count = getDropCount();
        if (count <= 0) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(dropItem, count);
    } // createDropStack ()

} // Class: DropFeature