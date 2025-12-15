package net.msymbios.llovelyr.framework.registry;

import java.lang.ref.WeakReference;
import java.util.UUID;

/**
 * <p>Represents a single robot entry in the owner registry.<p>
 * <p>
 * <b>Design Decision:</b> Uses WeakReference to LivingEntity instead of
 * cached data, enabling live position/health/stats access without
 * synchronization overhead.
 * <p>
 * <b>Memory Safety:</b> Weak references allow garbage collection of
 * unloaded entities, preventing memory leaks.
 */
public class RobotRegistryEntry {
    
    // -- Fields --
    
    private final UUID robotId;
    private final UUID ownerId;
    private final WeakReference<Object> entityRef; // Object to avoid Minecraft dependency
    private String robotType;
    private long lastUpdate;
    
    // -- Constructors --
    
    /**
     * Creates a registry entry for a robot.
     * 
     * @param robotId unique robot identifier
     * @param ownerId owner player identifier
     * @param entity the living entity (will be wrapped in WeakReference)
     * @param robotType robot type key
     */
    public RobotRegistryEntry(UUID robotId, UUID ownerId, Object entity, String robotType) {
        if (robotId == null) {
            throw new IllegalArgumentException("Robot ID cannot be null");
        }
        if (ownerId == null) {
            throw new IllegalArgumentException("Owner ID cannot be null");
        }
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null");
        }
        if (robotType == null || robotType.isEmpty()) {
            throw new IllegalArgumentException("Robot type cannot be null or empty");
        }
        
        this.robotId = robotId;
        this.ownerId = ownerId;
        this.entityRef = new WeakReference<>(entity);
        this.robotType = robotType;
        this.lastUpdate = System.currentTimeMillis();
    }
    
    // -- Public Methods --
    
    /**
     * Gets the robot's unique identifier.
     * 
     * @return robot ID
     */
    public UUID getRobotId() {
        return robotId;
    }
    
    /**
     * Gets the owner's unique identifier.
     * 
     * @return owner ID
     */
    public UUID getOwnerId() {
        return ownerId;
    }
    
    /**
     * Gets the entity reference (may be null if garbage collected).
     * 
     * @return entity or null
     */
    public Object getEntity() {
        return entityRef.get();
    }
    
    /**
     * Checks if the entity reference is still valid.
     * <p>
     * <i>Note:</i> This method should be extended in the Lib layer to check
     * entity.isAlive() and !entity.isRemoved() when Minecraft types are available.
     * 
     * @return true if entity reference exists
     */
    public boolean isEntityValid() {
        return entityRef.get() != null;
    }
    
    /**
     * Gets the robot type key.
     * 
     * @return robot type
     */
    public String getRobotType() {
        return robotType;
    }
    
    /**
     * Sets the robot type key.
     * 
     * @param robotType new robot type
     */
    public void setRobotType(String robotType) {
        if (robotType == null || robotType.isEmpty()) {
            throw new IllegalArgumentException("Robot type cannot be null or empty");
        }
        this.robotType = robotType;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return timestamp in milliseconds
     */
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    /**
     * Updates the timestamp to current time.
     */
    public void updateTimestamp() {
        this.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Sets the timestamp to a specific value.
     * 
     * @param timestamp timestamp in milliseconds
     */
    public void setLastUpdate(long timestamp) {
        this.lastUpdate = timestamp;
    }
    
} // Class: RobotRegistryEntry
