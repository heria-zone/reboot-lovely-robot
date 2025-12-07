package net.msymbios.llovelyr.lib.registry;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.msymbios.llovelyr.framework.registry.OwnerRobotRegistry;
import net.msymbios.llovelyr.framework.registry.RobotRegistryEntry;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * <p>Persists robot registry data to disk using Minecraft's SavedData system.</p>
 * <p>
 * <b>Architecture:</b> Wraps OwnerRobotRegistry with SavedData persistence,
 * ensuring robot tracking survives server restarts and world reloads.
 * <p>
 * <b>Design Decision:</b> Stores only essential data (robot UUID, owner UUID, type).
 * Entity references are rebuilt from loaded entities on world load.
 * <p>
 * <b>Thread Safety:</b> SavedData is accessed only from server thread, ensuring
 * thread-safe registry operations.
 */
public class RobotRegistrySavedData extends SavedData {

    // -- Constants --
    
    private static final String DATA_NAME = "lovely_robot_registry";
    
    // -- Fields --
    
    private final OwnerRobotRegistry registry;
    
    // -- Constructor --
    
    /**
     * Creates new saved data with empty registry.
     */
    public RobotRegistrySavedData() {
        this.registry = new OwnerRobotRegistry();
    } // Constructor: RobotRegistrySavedData ()
    
    /**
     * Creates saved data with existing registry.
     *
     * @param registry existing registry to wrap
     */
    public RobotRegistrySavedData(OwnerRobotRegistry registry) {
        this.registry = registry;
    } // Constructor: RobotRegistrySavedData ()
    
    // -- Public Methods --
    
    /**
     * Gets the wrapped robot registry.
     *
     * @return robot registry instance
     */
    @NotNull
    public OwnerRobotRegistry getRegistry() {
        return registry;
    } // getRegistry ()
    
    /**
     * Loads or creates robot registry saved data for specified level.
     * <p>
     * <b>Architecture:</b> Uses Minecraft's DimensionDataStorage to persist
     * registry data per dimension. Automatically loads from disk if exists.
     *
     * @param level server level to get saved data for
     * @return saved data instance (loaded or newly created)
     */
    @NotNull
    public static RobotRegistrySavedData get(@NotNull ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<RobotRegistrySavedData>(
                        RobotRegistrySavedData::new,
                        RobotRegistrySavedData::load,
                        null
                ),
                DATA_NAME
        );
    } // get ()
    
    // -- SavedData Implementation --
    
    /**
     * Saves registry data to NBT.
     * <p>
     * <b>Data Format:</b>
     * <pre>
     * {
     *   "robots": [
     *     {
     *       "robotId": "uuid-string",
     *       "ownerId": "uuid-string",
     *       "type": "entity-type-string"
     *     },
     *     ...
     *   ]
     * }
     * </pre>
     * <p>
     * <b>Design Decision:</b> Entity references are NOT saved (would cause memory leaks).
     * They are rebuilt from loaded entities when world loads.
     *
     * @param tag NBT tag to save to
     * @param registries holder lookup provider
     * @return the modified NBT tag
     */
    @Override
    @NotNull
    public CompoundTag save(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        ListTag robotsList = new ListTag();
        
        // Save all registry entries
        for (RobotRegistryEntry entry : registry.getAllRobots()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID("robotId", entry.getRobotId());
            entryTag.putUUID("ownerId", entry.getOwnerId());
            entryTag.putString("type", entry.getRobotType());
            robotsList.add(entryTag);
        }
        
        tag.put("robots", robotsList);
        return tag;
    } // save ()
    
    /**
     * Loads registry data from NBT.
     * <p>
     * <b>Architecture:</b> Reconstructs registry from saved data. Entity references
     * are null initially and will be populated when entities load and call ensureRegistered().
     * <p>
     * <b>Validation:</b> Skips entries with invalid UUIDs or missing data.
     *
     * @param tag NBT tag to load from
     * @param registries holder lookup provider
     * @return loaded saved data instance
     */
    @NotNull
    public static RobotRegistrySavedData load(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        RobotRegistrySavedData data = new RobotRegistrySavedData();
        
        if (tag.contains("robots", Tag.TAG_LIST)) {
            ListTag robotsList = tag.getList("robots", Tag.TAG_COMPOUND);
            
            for (int i = 0; i < robotsList.size(); i++) {
                CompoundTag entryTag = robotsList.getCompound(i);
                
                try {
                    UUID robotId = entryTag.getUUID("robotId");
                    UUID ownerId = entryTag.getUUID("ownerId");
                    String type = entryTag.getString("type");
                    
                    // Create entry without entity reference (will be populated by ensureRegistered)
                    RobotRegistryEntry entry = new RobotRegistryEntry(
                            robotId,
                            ownerId,
                            null, // Entity reference will be set when entity loads
                            type
                    );
                    
                    data.registry.registerRobot(entry);
                } catch (Exception e) {
                    // Skip invalid entries
                    // Log error but don't fail entire load
                }
            }
        }
        
        return data;
    } // load ()
    
} // Class: RobotRegistrySavedData
