package net.heriazone.hzlib.api.entity.features;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>Declares what an entity can plant, where, and how often.<p>
 * <p>
 * <b>Responsibility:</b> Pure data declaration. Contains no tick logic — the entity
 * reads this feature and drives planting behavior itself, persisting the enabled flag
 * and cooldown state per-instance in NBT.
 * <p>
 * <b>Cooldown resolution:</b> Each {@link PlantItem} can declare its own cooldown.
 * If omitted, it inherits the feature-level {@code globalCooldown}. This allows one
 * mushroom species to plant more frequently than another without separate features.
 * <p>
 * <b>Planting logic (entity-side):</b>
 * <ol>
 *   <li>Check enabled flag (from NBT)</li>
 *   <li>Check cooldown (from NBT)</li>
 *   <li>Try up to {@code maxAttempts} random blocks within {@code searchRadius}</li>
 *   <li>Each candidate: block beneath must be in {@code allowedBlocks}, block above must be air</li>
 *   <li>If all attempts fail, try the block the entity is standing on</li>
 *   <li>Reset cooldown after attempting</li>
 * </ol>
 */
public class PlantingFeature {

    // -- Defaults --

    public static final int   DEFAULT_MAX_ATTEMPTS  = 5;
    public static final int   DEFAULT_SEARCH_RADIUS = 4;
    public static final int   DEFAULT_COOLDOWN_TICKS = 1200; // 60 seconds

    // -- Fields --

    private final int globalCooldown;
    private final int maxAttempts;
    private final int searchRadius;
    private final List<PlantItem> entries;

    // -- Constructor --

    private PlantingFeature(Builder builder) {
        this.globalCooldown = builder.globalCooldown;
        this.maxAttempts    = builder.maxAttempts;
        this.searchRadius   = builder.searchRadius;
        this.entries        = Collections.unmodifiableList(new ArrayList<>(builder.entries));
    } // Constructor: PlantingFeature ()

    // -- Accessors --

    /** Fallback cooldown (ticks) for any {@link PlantItem} without its own cooldown. */
    public int getGlobalCooldown() { return globalCooldown; }

    /** Maximum random position attempts before falling back to standing block. */
    public int getMaxAttempts()    { return maxAttempts;    }

    /** Block radius around the entity to search for valid planting positions. */
    public int getSearchRadius()   { return searchRadius;   }

    /** All plantable entries declared for this entity. */
    public List<PlantItem> getEntries() { return entries; }

    /**
     * Resolves the effective cooldown for a given entry.
     * Returns the entry's own cooldown if set, otherwise the global cooldown.
     *
     * @param entry the plant entry to resolve cooldown for
     * @return effective cooldown in ticks
     */
    public int resolveCooldown(PlantItem entry) {
        int base = entry.getCooldown() >= 0 ? entry.getCooldown() : globalCooldown;
        int min  = (int)(base * 0.75f);
        return min + ThreadLocalRandom.current().nextInt(base - min + 1);
    } // resolveCooldown ()

    // -- Builder --

    public static Builder builder() {
        return new Builder();
    } // builder ()

    /**
     * <p>Fluent builder for {@link PlantingFeature}.<p>
     * <pre>{@code
     * PlantingFeature.builder()
     *     .globalCooldown(1200)
     *     .maxAttempts(5)
     *     .searchRadius(4)
     *     .entry(PlantItem.of(Blocks.BROWN_MUSHROOM)
     *         .allowedBlocks(Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.MYCELIUM)
     *         .build())
     *     .build();
     * }</pre>
     */
    public static class Builder {

        private int globalCooldown = DEFAULT_COOLDOWN_TICKS;
        private int maxAttempts   = DEFAULT_MAX_ATTEMPTS;
        private int searchRadius  = DEFAULT_SEARCH_RADIUS;
        private final List<PlantItem> entries = new ArrayList<>();

        private Builder() {} // Constructor: Builder ()

        /** Overrides the global cooldown fallback (ticks). */
        public Builder globalCooldown(int ticks) {
            this.globalCooldown = Math.max(0, ticks);
            return this;
        } // globalCooldown ()

        /** Overrides the maximum random position attempts before falling back. */
        public Builder maxAttempts(int attempts) {
            this.maxAttempts = Math.max(1, attempts);
            return this;
        } // maxAttempts ()

        /** Overrides the block search radius around the entity. */
        public Builder searchRadius(int radius) {
            this.searchRadius = Math.max(1, radius);
            return this;
        } // searchRadius ()

        /** Adds a plantable entry. */
        public Builder entry(PlantItem entry) {
            if (entry != null) entries.add(entry);
            return this;
        } // entry ()

        public PlantingFeature build() {
            return new PlantingFeature(this);
        } // build ()

    } // Class: Builder

    // =========================================================================
    // PlantItem — inner class
    // =========================================================================

    /**
     * <p>Declares a single plantable item — what block to place, where it is valid,
     * and an optional per-entry cooldown override.<p>
     * <p>
     * <b>Validity check (entity-side):</b> The block beneath the target position must
     * be in {@code allowedBlocks}, and the target position itself must be air.
     */
    public static class PlantItem {

        // -- Fields --

        private final Block plant;
        private final Set<Block> allowedBlocks;
        private final int cooldown; // -1 = inherit from PlantingFeature.globalCooldown

        // -- Constructor --

        private PlantItem(EntryBuilder builder) {
            this.plant         = builder.plant;
            this.allowedBlocks = Collections.unmodifiableSet(new HashSet<>(builder.allowedBlocks));
            this.cooldown      = builder.cooldown;
        } // Constructor: PlantItem ()

        // -- Accessors --

        /** The block this entry places when planting. */
        public Block getPlant() { return plant; }

        /** Returns true if the given block state is a valid surface for planting. */
        public boolean isValidSurface(BlockState state) {
            return allowedBlocks.contains(state.getBlock());
        } // isValidSurface ()

        /**
         * Returns this entry's cooldown in ticks, or {@code -1} if it should
         * inherit from {@link PlantingFeature#getGlobalCooldown()}.
         */
        public int getCooldown() { return cooldown; }

        // -- Builder --

        public static EntryBuilder of(Block plant) {
            return new EntryBuilder(plant);
        } // of ()

        /**
         * Fluent builder for {@link PlantItem}.
         */
        public static class EntryBuilder {

            private final Block plant;
            private final Set<Block> allowedBlocks = new HashSet<>();
            private int cooldown = -1; // inherit by default

            private EntryBuilder(Block plant) {
                this.plant = Objects.requireNonNull(plant, "Plant block cannot be null");
            } // Constructor: EntryBuilder ()

            /** Declares valid surface blocks beneath the planting position. */
            public EntryBuilder allowedBlocks(Block... blocks) {
                if (blocks != null) Collections.addAll(allowedBlocks, blocks);
                return this;
            } // allowedBlocks ()

            /**
             * Sets a per-entry cooldown override (ticks).
             * If not called, this entry inherits the feature's global cooldown.
             */
            public EntryBuilder cooldown(int ticks) {
                this.cooldown = Math.max(0, ticks);
                return this;
            } // cooldown ()

            public PlantItem build() {
                if (allowedBlocks.isEmpty())
                    throw new IllegalStateException("PlantItem must have at least one allowed block");
                return new PlantItem(this);
            } // build ()

        } // Class: EntryBuilder

    } // Class: PlantItem

} // Class: PlantingFeature