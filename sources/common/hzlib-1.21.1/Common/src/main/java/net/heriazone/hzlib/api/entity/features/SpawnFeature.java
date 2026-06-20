package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.features.variants.TextureVariantFeature;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.*;

/**
 * <p>Manages spawn configuration for entity world generation.</p>
 * <p>
 * <b>Architecture:</b> Provides composable spawn configuration that can be attached to any entity type
 * through the feature system. Stores spawn weight, group size constraints, and biome restrictions.
 * <p>
 * <b>Design Decision:</b> Immutable data storage after construction to prevent accidental modification
 * of spawn rules during runtime. All fields are final and collections are defensively copied.
 * <p>
 * <b>Use Case:</b> Enables entities to have configurable spawn rules without hardcoding spawn
 * logic in entity classes. Integrates with Minecraft's spawn system.
 */
public class SpawnFeature {

    // -- Variables --

    private MobCategory category;
    private int weight;
    private int minGroupSize;
    private int maxGroupSize;
    private List<ResourceKey<Biome>> biomes;

    private boolean hasPlacement;
    private SpawnPlacementType placement;
    private Heightmap.Types heightmap;
    private SpawnPlacements.SpawnPredicate<? extends Mob> spawnPredicate;

    // -- Constructors --

    public SpawnFeature() {} // Constructor: SpawnFeature ()

    // -- Custom Methods --

    /**
     * Creates spawn feature with specified configuration.
     * <p>
     * <b>Validation:</b> Weight and group sizes are clamped to non-negative values.
     * If minGroupSize > maxGroupSize, they are swapped to maintain valid range.
     * <p>
     * <b>Immutability:</b> Biome list is defensively copied to prevent external modification.
     *
     * @param weight spawn weight (higher = more common, clamped to >= 0)
     * @param minGroup minimum entities per spawn group (clamped to >= 0)
     * @param maxGroup maximum entities per spawn group (clamped to >= 0)
     * @param biomes biomes where entity can spawn (varargs)
     */
    public SpawnFeature withModifications(MobCategory category, int weight, int minGroup, int maxGroup, ResourceKey<Biome>... biomes) {
        this.category = category;
        this.weight = Math.max(0, weight);

        // Ensure min <= max by swapping if necessary
        int validMin = Math.max(0, Math.min(minGroup, maxGroup));
        int validMax = Math.max(0, Math.max(minGroup, maxGroup));
        this.minGroupSize = validMin;
        this.maxGroupSize = validMax;

        // Defensive copy of biome list
        this.biomes = biomes != null && biomes.length > 0
                ? new ArrayList<>(Arrays.asList(biomes))
                : new ArrayList<>();

        return this;
    } // withModifications ()

    public SpawnFeature withPlacements(SpawnPlacementType placement, Heightmap.Types heightmap, SpawnPlacements.SpawnPredicate<? extends Mob> spawnPredicate) {
        this.hasPlacement = true;
        this.placement = placement;
        this.heightmap = heightmap;
        this.spawnPredicate = spawnPredicate;
        return this;
    } // withPlacements ()

    // -- Accessors --

    public MobCategory getCategory() {
        return this.category;
    } // getCategory ()

    /**
     * Returns spawn weight.
     * <p>
     * <b>Spawn System:</b> Higher weight increases spawn probability relative to other entities.
     * Weight of 0 effectively disables spawning.
     *
     * @return spawn weight (>= 0)
     */
    public int getWeight() {
        return this.weight;
    } // getWeight ()

    /**
     * Returns minimum group size.
     * <p>
     * <b>Spawn System:</b> Minimum number of entities spawned together in a group.
     *
     * @return minimum group size (>= 0)
     */
    public int getMinGroupSize() {
        return this.minGroupSize;
    } // getMinGroupSize ()

    /**
     * Returns maximum group size.
     * <p>
     * <b>Spawn System:</b> Maximum number of entities spawned together in a group.
     *
     * @return maximum group size (>= minGroupSize)
     */
    public int getMaxGroupSize() {
        return this.maxGroupSize;
    } // getMaxGroupSize ()

    /**
     * Returns unmodifiable list of biomes where entity can spawn.
     * <p>
     * <b>Immutability:</b> Returns unmodifiable view to prevent external modification
     * of spawn rules.
     *
     * @return unmodifiable list of biome registry keys
     */
    public List<ResourceKey<Biome>> getBiomes() {
        return Collections.unmodifiableList(this.biomes);
    } // getBiomes ()

    public boolean hasPlacement() {
        return this.hasPlacement;
    } // getPlacement ()

    public SpawnPlacementType getPlacement() {
        return this.placement;
    } // getPlacement ()

    public Heightmap.Types getHeightmap() {
        return this.heightmap;
    } // getHeightmap ()

    public SpawnPlacements.SpawnPredicate<? extends Mob> getSpawnPredicate() {
        return this.spawnPredicate;
    } // getSpawnPredicate ()

} // Class: SpawnFeature