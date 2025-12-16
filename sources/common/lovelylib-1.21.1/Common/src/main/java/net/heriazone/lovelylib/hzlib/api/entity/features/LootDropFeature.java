package net.heriazone.lovelylib.hzlib.api.entity.features;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Predicate;

/**
 * <p>Defines additional loot drops on entity death.</p>
 * <p>
 * <b>Architecture:</b> Provides flexible loot system with per-item configuration
 * for quantity ranges, drop chances, and conditional drops. Supports both
 * type-level defaults and instance-level overrides.
 * <p>
 * <b>Design Decision:</b> Loot is separate from core drops (DropFeature).
 * Core represents essential item, loot represents additional rewards.
 * <p>
 * <b>Use Cases:</b>
 * <ul>
 * <li>Level-based loot (high-level robots drop rare items)</li>
 * <li>Conditional drops (only drop if certain stats met)</li>
 * <li>Varied loot tables per robot type</li>
 * <li>Instance overrides for special entities</li>
 * </ul>
 */
public class LootDropFeature {

    // -- Fields --

    private final List<LootEntry> lootTable;
    private final int maxVariety;

    // -- Constructors --

    /**
     * Creates empty loot drop feature.
     * <p>
     * <b>Use Case:</b> Starting point for builder pattern or entities with
     * no loot drops.
     */
    public LootDropFeature() {
        this(new ArrayList<>(), Integer.MAX_VALUE);
    } // Constructor: LootDropFeature ()

    /**
     * Creates loot drop feature with specified loot table.
     * <p>
     * <b>Variety Limit:</b> No limit on number of different items that can drop.
     *
     * @param lootTable list of loot entries
     * @throws NullPointerException if lootTable is null
     */
    public LootDropFeature(@NotNull List<LootEntry> lootTable) {
        this(lootTable, Integer.MAX_VALUE);
    } // Constructor: LootDropFeature ()

    /**
     * Creates loot drop feature with loot table and variety limit.
     * <p>
     * <b>Variety Limit:</b> Maximum number of different item types that can
     * drop simultaneously. Useful for preventing loot explosion.
     *
     * @param lootTable list of loot entries
     * @param maxVariety maximum different item types to drop
     * @throws NullPointerException if lootTable is null
     */
    public LootDropFeature(@NotNull List<LootEntry> lootTable, int maxVariety) {
        this.lootTable = new ArrayList<>(Objects.requireNonNull(lootTable, "Loot table cannot be null"));
        this.maxVariety = Math.max(1, maxVariety);
    } // Constructor: LootDropFeature ()

    // -- Builder Methods --

    /**
     * Adds loot entry to table.
     * <p>
     * <b>Fluent API:</b> Returns this for method chaining.
     *
     * @param entry loot entry to add
     * @return this instance
     * @throws NullPointerException if entry is null
     */
    public LootDropFeature addLoot(@NotNull LootEntry entry) {
        Objects.requireNonNull(entry, "Loot entry cannot be null");
        this.lootTable.add(entry);
        return this;
    } // addLoot ()

    /**
     * Adds simple loot entry with default settings.
     * <p>
     * <b>Defaults:</b> 100% drop chance, 1-1 count range, no conditions.
     *
     * @param item item to drop
     * @return this instance
     * @throws NullPointerException if item is null
     */
    public LootDropFeature addLoot(@NotNull Item item) {
        return addLoot(new LootEntry(item));
    } // addLoot ()

    /**
     * Adds loot entry with specified count range.
     * <p>
     * <b>Defaults:</b> 100% drop chance, no conditions.
     *
     * @param item item to drop
     * @param minCount minimum drop count
     * @param maxCount maximum drop count
     * @return this instance
     * @throws NullPointerException if item is null
     */
    public LootDropFeature addLoot(@NotNull Item item, int minCount, int maxCount) {
        return addLoot(new LootEntry(item, minCount, maxCount));
    } // addLoot ()

    /**
     * Adds loot entry with count range and drop chance.
     *
     * @param item item to drop
     * @param minCount minimum drop count
     * @param maxCount maximum drop count
     * @param dropChance probability of drop (0.0 to 1.0)
     * @return this instance
     * @throws NullPointerException if item is null
     */
    public LootDropFeature addLoot(@NotNull Item item, int minCount, int maxCount, float dropChance) {
        return addLoot(new LootEntry(item, minCount, maxCount, dropChance));
    } // addLoot ()

    /**
     * Sets maximum variety of items that can drop.
     * <p>
     * <b>Use Case:</b> Limit loot explosion for entities with large loot tables.
     *
     * @param maxVariety maximum different item types
     * @return this instance
     */
    public LootDropFeature withMaxVariety(int maxVariety) {
        return new LootDropFeature(this.lootTable, maxVariety);
    } // withMaxVariety ()

    // -- Public Methods --

    /**
     * Gets immutable view of loot table.
     *
     * @return unmodifiable list of loot entries
     */
    public List<LootEntry> getLootTable() {
        return Collections.unmodifiableList(lootTable);
    } // getLootTable ()

    /**
     * Gets maximum variety limit.
     *
     * @return max different item types that can drop
     */
    public int getMaxVariety() {
        return maxVariety;
    } // getMaxVariety ()

    /**
     * Checks if loot table is empty.
     *
     * @return true if no loot entries defined
     */
    public boolean isEmpty() {
        return lootTable.isEmpty();
    } // isEmpty ()

    /**
     * Generates loot drops based on configured table and random chance.
     * <p>
     * <b>Algorithm:</b>
     * <ol>
     * <li>Filter entries by condition predicates</li>
     * <li>Roll drop chance for each entry</li>
     * <li>Generate random count within min/max range</li>
     * <li>Limit to maxVariety different items</li>
     * </ol>
     * <p>
     * <b>Performance:</b> O(n) where n is loot table size. Variety limiting
     * uses first-come-first-served (no sorting).
     *
     * @param random random source for drop calculations
     * @return list of ItemStacks to drop
     * @throws NullPointerException if random is null
     */
    public List<ItemStack> generateDrops(@NotNull RandomSource random) {
        Objects.requireNonNull(random, "Random source cannot be null");

        List<ItemStack> drops = new ArrayList<>();
        int varietyCount = 0;

        for (LootEntry entry : lootTable) {
            // Check variety limit
            if (varietyCount >= maxVariety) {
                break;
            }

            // Check conditions
            if (!entry.shouldDrop()) {
                continue;
            }

            // Roll drop chance
            if (random.nextFloat() > entry.getDropChance()) {
                continue;
            }

            // Generate count
            int count = entry.generateCount(random);
            if (count <= 0) {
                continue;
            }

            // Create drop
            ItemStack stack = new ItemStack(entry.getItem(), count);
            drops.add(stack);
            varietyCount++;
        }

        return drops;
    } // generateDrops ()

    // -- Loot Entry Class --

    /**
     * <p>Represents single loot entry with item, count range, and drop conditions.</p>
     * <p>
     * <b>Design Decision:</b> Immutable after construction to prevent accidental
     * modification of shared loot tables.
     */
    public static class LootEntry {

        // -- Fields --

        private final Item item;
        private final int minCount;
        private final int maxCount;
        private final float dropChance;
        private final Predicate<Void> condition;

        // -- Constructors --

        /**
         * Creates loot entry with default settings.
         * <p>
         * <b>Defaults:</b> 1-1 count, 100% drop chance, no conditions.
         *
         * @param item item to drop
         * @throws NullPointerException if item is null
         */
        public LootEntry(@NotNull Item item) {
            this(item, 1, 1, 1.0f, null);
        } // Constructor: LootEntry ()

        /**
         * Creates loot entry with count range.
         * <p>
         * <b>Defaults:</b> 100% drop chance, no conditions.
         *
         * @param item item to drop
         * @param minCount minimum drop count
         * @param maxCount maximum drop count
         * @throws NullPointerException if item is null
         */
        public LootEntry(@NotNull Item item, int minCount, int maxCount) {
            this(item, minCount, maxCount, 1.0f, null);
        } // Constructor: LootEntry ()

        /**
         * Creates loot entry with count range and drop chance.
         * <p>
         * <b>Defaults:</b> No conditions.
         *
         * @param item item to drop
         * @param minCount minimum drop count
         * @param maxCount maximum drop count
         * @param dropChance probability of drop (0.0 to 1.0)
         * @throws NullPointerException if item is null
         */
        public LootEntry(@NotNull Item item, int minCount, int maxCount, float dropChance) {
            this(item, minCount, maxCount, dropChance, null);
        } // Constructor: LootEntry ()

        /**
         * Creates loot entry with full configuration.
         *
         * @param item item to drop
         * @param minCount minimum drop count
         * @param maxCount maximum drop count
         * @param dropChance probability of drop (0.0 to 1.0)
         * @param condition predicate for conditional drops (null = always drop)
         * @throws NullPointerException if item is null
         */
        public LootEntry(@NotNull Item item, int minCount, int maxCount, float dropChance, Predicate<Void> condition) {
            this.item = Objects.requireNonNull(item, "Item cannot be null");
            this.minCount = Math.max(0, minCount);
            this.maxCount = Math.max(this.minCount, maxCount);
            this.dropChance = Math.max(0.0f, Math.min(1.0f, dropChance));
            this.condition = condition;
        } // Constructor: LootEntry ()

        // -- Getters --

        /**
         * @return item to drop
         */
        public Item getItem() {
            return item;
        } // getItem ()

        /**
         * @return minimum drop count
         */
        public int getMinCount() {
            return minCount;
        } // getMinCount ()

        /**
         * @return maximum drop count
         */
        public int getMaxCount() {
            return maxCount;
        } // getMaxCount ()

        /**
         * @return drop chance (0.0 to 1.0)
         */
        public float getDropChance() {
            return dropChance;
        } // getDropChance ()

        // -- Drop Logic --

        /**
         * Checks if drop conditions are met.
         * <p>
         * <b>Behavior:</b> Returns true if no condition set, otherwise
         * evaluates condition predicate.
         *
         * @return true if should attempt drop
         */
        public boolean shouldDrop() {
            return condition == null || condition.test(null);
        } // shouldDrop ()

        /**
         * Generates random drop count within configured range.
         *
         * @param random random source
         * @return count between minCount and maxCount (inclusive)
         * @throws NullPointerException if random is null
         */
        public int generateCount(@NotNull RandomSource random) {
            Objects.requireNonNull(random, "Random source cannot be null");

            if (minCount == maxCount) {
                return minCount;
            }

            return minCount + random.nextInt(maxCount - minCount + 1);
        } // generateCount ()

    } // Class: LootEntry

} // Class: LootDropFeature