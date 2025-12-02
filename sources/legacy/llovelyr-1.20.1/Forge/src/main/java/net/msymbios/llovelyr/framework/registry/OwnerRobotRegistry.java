package net.msymbios.llovelyr.framework.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>Manages robot registration per owner with spawn limit enforcement.<p>
 * <p>
 * <b>Thread Safety:</b> Uses ConcurrentHashMap for thread-safe access
 * from multiple server threads.
 * <p>
 * <b>Performance:</b> O(1) lookup by owner or robot ID, O(n) for cleanup.
 */
public class OwnerRobotRegistry {
    
    // -- Fields --
    
    private final Map<UUID, Set<RobotRegistryEntry>> ownerToRobots;
    private final Map<UUID, RobotRegistryEntry> robotIdToEntry;
    
    // -- Constructors --
    
    /**
     * Creates a new robot registry.
     */
    public OwnerRobotRegistry() {
        this.ownerToRobots = new ConcurrentHashMap<>();
        this.robotIdToEntry = new ConcurrentHashMap<>();
    }
    
    // -- Public Methods --
    
    /**
     * Checks if an owner can spawn another robot.
     * 
     * @param ownerId owner's unique identifier
     * @param maxRobots maximum robots allowed per owner
     * @return true if spawn is allowed
     */
    public boolean canSpawnRobot(UUID ownerId, int maxRobots) {
        if (ownerId == null) {
            return false;
        }
        
        Set<RobotRegistryEntry> robots = ownerToRobots.get(ownerId);
        if (robots == null) {
            return true;
        }
        
        // Count only valid entries
        long validCount = robots.stream()
                               .filter(RobotRegistryEntry::isEntityValid)
                               .count();
        
        return validCount < maxRobots;
    }
    
    /**
     * Registers a robot in the registry.
     * 
     * @param entry registry entry to add
     * @throws IllegalArgumentException if entry is null or already registered
     */
    public void registerRobot(RobotRegistryEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Entry cannot be null");
        }
        
        UUID robotId = entry.getRobotId();
        UUID ownerId = entry.getOwnerId();
        
        if (robotIdToEntry.containsKey(robotId)) {
            throw new IllegalArgumentException("Robot already registered: " + robotId);
        }
        
        // Add to robot ID map
        robotIdToEntry.put(robotId, entry);
        
        // Add to owner map
        ownerToRobots.computeIfAbsent(ownerId, k -> ConcurrentHashMap.newKeySet())
                    .add(entry);
    }
    
    /**
     * Unregisters a robot from the registry.
     * 
     * @param robotId robot's unique identifier
     * @return true if robot was found and removed
     */
    public boolean unregisterRobot(UUID robotId) {
        if (robotId == null) {
            return false;
        }
        
        RobotRegistryEntry entry = robotIdToEntry.remove(robotId);
        if (entry == null) {
            return false;
        }
        
        // Remove from owner map
        Set<RobotRegistryEntry> robots = ownerToRobots.get(entry.getOwnerId());
        if (robots != null) {
            robots.remove(entry);
            
            // Clean up empty sets
            if (robots.isEmpty()) {
                ownerToRobots.remove(entry.getOwnerId());
            }
        }
        
        return true;
    }
    
    /**
     * Gets all robots for a specific owner.
     * 
     * @param ownerId owner's unique identifier
     * @return list of registry entries (may be empty)
     */
    public List<RobotRegistryEntry> getRobotsForOwner(UUID ownerId) {
        if (ownerId == null) {
            return Collections.emptyList();
        }
        
        Set<RobotRegistryEntry> robots = ownerToRobots.get(ownerId);
        if (robots == null) {
            return Collections.emptyList();
        }
        
        return new ArrayList<>(robots);
    }
    
    /**
     * Gets a robot entry by robot ID.
     * 
     * @param robotId robot's unique identifier
     * @return registry entry or null if not found
     */
    public RobotRegistryEntry getRobotById(UUID robotId) {
        if (robotId == null) {
            return null;
        }
        return robotIdToEntry.get(robotId);
    }
    
    /**
     * Gets the count of robots for a specific owner.
     * 
     * @param ownerId owner's unique identifier
     * @return number of registered robots
     */
    public int getRobotCount(UUID ownerId) {
        if (ownerId == null) {
            return 0;
        }
        
        Set<RobotRegistryEntry> robots = ownerToRobots.get(ownerId);
        return robots != null ? robots.size() : 0;
    }
    
    /**
     * Gets the count of valid robots for a specific owner.
     * <p>
     * <i>Note:</i> This counts only entries with valid entity references.
     * 
     * @param ownerId owner's unique identifier
     * @return number of valid robots
     */
    public int getValidRobotCount(UUID ownerId) {
        if (ownerId == null) {
            return 0;
        }
        
        Set<RobotRegistryEntry> robots = ownerToRobots.get(ownerId);
        if (robots == null) {
            return 0;
        }
        
        return (int) robots.stream()
                          .filter(RobotRegistryEntry::isEntityValid)
                          .count();
    }
    
    /**
     * Removes all entries with invalid entity references.
     * <p>
     * <b>Performance:</b> O(n) where n is total number of registered robots.
     * Should be called periodically to prevent memory leaks.
     * 
     * @return number of entries removed
     */
    public int cleanupInvalidEntries() {
        int removedCount = 0;
        
        // Collect invalid robot IDs
        List<UUID> invalidIds = robotIdToEntry.entrySet().stream()
            .filter(e -> !e.getValue().isEntityValid())
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // Remove invalid entries
        for (UUID robotId : invalidIds) {
            if (unregisterRobot(robotId)) {
                removedCount++;
            }
        }
        
        return removedCount;
    }
    
    /**
     * Clears all entries from the registry.
     */
    public void clear() {
        ownerToRobots.clear();
        robotIdToEntry.clear();
    }
    
    /**
     * Gets the total number of registered robots across all owners.
     * 
     * @return total robot count
     */
    public int getTotalRobotCount() {
        return robotIdToEntry.size();
    }
    
    /**
     * Gets the number of owners with registered robots.
     * 
     * @return owner count
     */
    public int getOwnerCount() {
        return ownerToRobots.size();
    }
    
    /**
     * Gets all owners with their registered robots.
     * <p>
     * <b>Use Case:</b> Administrative commands that need to list all robot owners
     * and their robot counts.
     * <p>
     * <b>Performance:</b> O(n) where n is number of owners. Returns immutable
     * view to prevent external modification.
     * 
     * @return map of owner UUIDs to their robot entry lists
     */
    public Map<UUID, List<RobotRegistryEntry>> getAllOwners() {
        Map<UUID, List<RobotRegistryEntry>> result = new HashMap<>();
        
        ownerToRobots.forEach((ownerId, robotSet) -> {
            result.put(ownerId, new ArrayList<>(robotSet));
        });
        
        return Collections.unmodifiableMap(result);
    }
    
} // Class: OwnerRobotRegistry
