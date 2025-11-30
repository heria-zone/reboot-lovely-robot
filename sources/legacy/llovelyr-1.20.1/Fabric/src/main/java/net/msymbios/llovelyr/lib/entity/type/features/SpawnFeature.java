package net.msymbios.llovelyr.lib.entity.type.features;

import net.minecraft.registry.RegistryKey;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    // -- Fields --
    
    private final int weight;
    private final int minGroupSize;
    private final int maxGroupSize;
    private final List<RegistryKey<Biome>> biomes;

    // -- Constructors --
    
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
    @SafeVarargs
    public SpawnFeature(int weight, int minGroup, int maxGroup, RegistryKey<Biome>... biomes) {
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
    }

    // -- Accessors --
    
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
    }

    /**
     * Returns minimum group size.
     * <p>
     * <b>Spawn System:</b> Minimum number of entities spawned together in a group.
     * 
     * @return minimum group size (>= 0)
     */
    public int getMinGroupSize() {
        return this.minGroupSize;
    }

    /**
     * Returns maximum group size.
     * <p>
     * <b>Spawn System:</b> Maximum number of entities spawned together in a group.
     * 
     * @return maximum group size (>= minGroupSize)
     */
    public int getMaxGroupSize() {
        return this.maxGroupSize;
    }

    /**
     * Returns unmodifiable list of biomes where entity can spawn.
     * <p>
     * <b>Immutability:</b> Returns unmodifiable view to prevent external modification
     * of spawn rules.
     * 
     * @return unmodifiable list of biome registry keys
     */
    public List<RegistryKey<Biome>> getBiomes() {
        return Collections.unmodifiableList(this.biomes);
    }

} // Class: SpawnFeature
