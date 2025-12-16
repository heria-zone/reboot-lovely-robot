package net.heriazone.lovelylib.hzlib.framework.entity.data;

import java.util.*;

/**
 * <p>Generic container for mapping keys to resources with optimized random selection.<p>
 * <p>
 * <b>Architecture:</b> Pure Java collection wrapper with zero Minecraft dependencies.
 * Provides type-safe resource storage with performance-optimized random access through
 * key caching.
 * <p>
 * <b>Performance:</b> O(1) for put/get/has operations. getRandom() is O(1) after initial
 * cache build, avoiding repeated ArrayList creation. Cache invalidates on put() to maintain
 * consistency.
 * <p>
 * <b>Design Decision:</b> Lazy cache invalidation rather than eager updates. Random selection
 * is common in rendering paths, so caching provides significant benefit despite occasional
 * cache rebuild cost.
 * <p>
 * <b>Thread Safety:</b> Not thread-safe. Callers must synchronize access if shared across threads.
 *
 * @param <K> key type for resource lookup
 * @param <V> value type for stored resources
 */
public class ResourceMap<K, V> {

    // -- Variables --

    private final Map<K, V> resources;
    private List<K> cachedKeys;
    private final Random random;

    // -- Constructors --

    /**
     * Creates empty resource map with default capacity.
     */
    public ResourceMap() {
        this.resources = new HashMap<>();
        this.cachedKeys = null;
        this.random = new Random();
    } // Constructor: ResourceMap ()

    /**
     * Creates empty resource map with specified initial capacity.
     *
     * @param initialCapacity initial capacity for underlying map
     */
    public ResourceMap(int initialCapacity) {
        this.resources = new HashMap<>(initialCapacity);
        this.cachedKeys = null;
        this.random = new Random();
    } // Constructor: ResourceMap ()

    // -- Custom Methods --

    /**
     * Associates specified value with specified key.
     * <p>
     * <b>State Impact:</b> Invalidates cached key list, forcing rebuild on next getRandom() call.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     */
    public void put(K key, V value) {
        resources.put(key, value);
        cachedKeys = null; // Invalidate cache
    } // put ()

    /**
     * Returns value associated with specified key, or null if not present.
     *
     * @param key key whose associated value is to be returned
     * @return value associated with key, or null if not present
     */
    public V get(K key) {
        return resources.get(key);
    } // get ()

    /**
     * Returns true if this map contains mapping for specified key.
     *
     * @param key key whose presence is to be tested
     * @return true if map contains mapping for key
     */
    public boolean has(K key) {
        return resources.containsKey(key);
    } // has ()

    /**
     * Returns set of all keys in this map.
     * <p>
     * <i>Note:</i> Returns view of underlying map's key set. Modifications to returned
     * set affect this map.
     *
     * @return set view of keys contained in this map
     */
    public Set<K> keys() {
        return resources.keySet();
    } // keys ()

    /**
     * Returns randomly selected value from this map.
     * <p>
     * <b>Performance:</b> O(1) after initial cache build. Cache rebuilds only when
     * invalidated by put() operations.
     * <p>
     * <b>Failure Mode:</b> Returns null if map is empty rather than throwing exception,
     * allowing graceful degradation in rendering paths.
     *
     * @return randomly selected value, or null if map is empty
     */
    public V getRandom() {
        if (resources.isEmpty()) {
            return null;
        }

        // Lazy cache rebuild
        if (cachedKeys == null) {
            cachedKeys = new ArrayList<>(resources.keySet());
        }

        K randomKey = cachedKeys.get(random.nextInt(cachedKeys.size()));
        return resources.get(randomKey);
    } // getRandom ()

    /**
     * Returns number of key-value mappings in this map.
     *
     * @return number of mappings
     */
    public int size() {
        return resources.size();
    } // size ()

    /**
     * Returns true if this map contains no key-value mappings.
     *
     * @return true if map is empty
     */
    public boolean isEmpty() {
        return resources.isEmpty();
    } // isEmpty ()

    /**
     * Removes all mappings from this map.
     * <p>
     * <b>State Impact:</b> Invalidates cached key list.
     */
    public void clear() {
        resources.clear();
        cachedKeys = null;
    } // clear ()

} // Class: ResourceMap