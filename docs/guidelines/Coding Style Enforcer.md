# Java Coding Style Guide

This document establishes comprehensive coding standards for Java development in the LovelyRobot project, emphasizing maintainability, readability, and architectural clarity.

## File Organization

### File Structure
- One public class per file with matching filename
- Package-private helper classes may coexist when tightly coupled
- Organize files in logical package hierarchies reflecting domain boundaries

### Package Declaration

```java
package com.lovelyrobot.core.entities;

// Code implementation

// Package: com.lovelyrobot.core.entities
```

- Use reverse domain notation for package naming
- Include closing comment indicating package scope
- Align package structure with architectural layers

## Class Structure

### Class Declaration

```java
/**
 * <p>Coordinates robot entity lifecycle with behavioral state management.<p>
 * <p>
 * <b>Architecture:</b> Serves as the bridge between Minecraft's entity system
 * and the robot's AI decision-making layer. Translates game events (damage, interaction,
 * tick updates) into behavioral triggers while maintaining state consistency across
 * client-server boundaries.
 * <p>
 * <b>State Management:</b> Maintains behavioral state separate from physical state,
 * allowing personality and decision-making to persist independently of health,
 * position, or animation state. This separation enables behavioral continuity
 * during respawns and dimension transfers.
 * <p>
 * <b>Thread Safety:</b> Entity ticks occur on server thread, but behavioral
 * processing may spawn async tasks. All state mutations are synchronized to
 * prevent race conditions during concurrent interaction processing.
 */
public class RobotEntity extends LivingEntity implements IRobotBehavior {

    // -- Constants --

    // -- Fields --

    // -- Properties --

    // -- Constructors --

    // -- Lifecycle Methods --

    // -- Public Methods --

    // -- Protected Methods --

    // -- Private Methods --

} // Class: RobotEntity
```

- Focus JavaDoc on architectural role and system impact
- Explain coordination responsibilities and state management
- Use section headers to organize code logically
- Include closing comment with class name

### Interface Declaration

```java
/**
 * <p>Establishes behavioral contracts for robot decision-making systems.<p>
 * <p>
 * <b>Design Intent:</b> Decouples behavioral logic from entity implementation,
 * enabling multiple robot types to share behavioral patterns while maintaining
 * distinct personalities. This abstraction allows behavioral systems to evolve
 * independently of entity mechanics.
 * <p>
 * <b>Implementation Requirements:</b> Implementers must ensure behavioral
 * consistency across state transitions and maintain personality coherence
 * during interaction processing. Behavioral responses should be deterministic
 * for identical inputs to enable predictable testing and debugging.
 * <p>
 * <b>Extension Strategy:</b> New behavioral patterns can be added without
 * modifying existing implementations through composition rather than inheritance.
 */
public interface IRobotBehavior {

    // -- Behavioral Contracts --

    // -- State Management --

    // -- Event Handling --

} // Interface: IRobotBehavior
```

- Emphasize contract purpose and implementation expectations
- Explain extensibility and consistency requirements
- Group methods by functional responsibility

### Enum Declaration

```java
/**
 * <p>Defines robot operational states with behavioral and resource implications.<p>
 * <p>
 * <b>State Hierarchy:</b> States form a capability hierarchy where transitions
 * must be validated to prevent invalid behavioral combinations. Moving from
 * SHUTDOWN to ACTIVE requires initialization checks that IDLE to ACTIVE skips.
 * <p>
 * <b>Resource Management:</b> Each state has different computational overhead.
 * ACTIVE runs full AI processing, IDLE maintains awareness with reduced tick
 * frequency, SHUTDOWN releases all behavioral resources.
 * <p>
 * <b>Persistence:</b> State survives world reloads but not entity death. Respawned
 * robots default to IDLE regardless of pre-death state to ensure clean initialization.
 */
public enum RobotState {
    /**
     * <p>Full operational mode with complete behavioral processing.<p>
     * <p>
     * <b>Capabilities:</b> All AI goals active, full interaction processing,
     * autonomous decision-making enabled.
     * <p>
     * <b>Performance:</b> Highest computational cost - runs every tick.
     */
    ACTIVE(true, true),
    
    /**
     * <p>Standby mode maintaining situational awareness.<p>
     * <p>
     * <b>Capabilities:</b> Passive observation only, responds to direct interaction
     * but doesn't initiate behaviors.
     * <p>
     * <b>Performance:</b> Reduced cost - processes every 5 ticks.
     */
    IDLE(true, false),
    
    /**
     * <p>Powered down with all systems suspended.<p>
     * <p>
     * <b>Capabilities:</b> No processing, no interaction response, purely decorative.
     * <p>
     * <b>Performance:</b> Minimal cost - only renders, no logic processing.
     */
    SHUTDOWN(false, false);

    private final boolean isAware;
    private final boolean canAct;

    RobotState(boolean isAware, boolean canAct) {
        this.isAware = isAware;
        this.canAct = canAct;
    }

    public boolean isAware() { return isAware; }
    public boolean canAct() { return canAct; }

} // Enum: RobotState
```

- Explain behavioral implications of each state
- Document state transition constraints
- Include functional properties that affect system behavior

## Member Organization

### Fields and Constants

```java
// -- Constants --

private static final int DEFAULT_INTERACTION_RANGE = 16;
private static final String ROBOT_DATA_KEY = "robot_data";
private static final Logger LOGGER = LoggerFactory.getLogger(RobotEntity.class);

// -- Fields --

private final UUID m_robotId;
private final RobotConfiguration m_configuration;
private volatile RobotState m_currentState;
private final AtomicReference<BehaviorContext> m_behaviorContext;
```

- Use SCREAMING_SNAKE_CASE for constants
- Group related fields by functional area
- Use appropriate concurrency primitives for shared state
- Initialize immutable fields as final

### Properties (Getters/Setters)

```java
// -- Properties --

/**
 * Retrieves the robot's current operational state.
 * 
 * State changes trigger behavioral recalibration and may affect
 * interaction availability and response patterns.
 */
public RobotState getCurrentState() {
    return m_currentState;
}

/**
 * Updates the robot's operational state with validation.
 * 
 * Validates state transitions to prevent invalid behavioral combinations
 * and ensures proper cleanup of previous state resources.
 */
public void setState(RobotState newState) {
    if (!isValidTransition(m_currentState, newState)) {
        throw new IllegalStateException(
            String.format("Invalid state transition from %s to %s", m_currentState, newState)
        );
    }
    
    RobotState previousState = this.currentState;
    this.currentState = newState;
    onStateChanged(previousState, newState);
}
```

- Explain the broader system impact of property access
- Document validation logic and constraints
- Describe side effects and state synchronization

### Constructors

```java
// -- Constructors --

/**
 * Creates a robot entity with specified configuration and behavioral parameters.
 * 
 * Initializes core systems and establishes behavioral baselines that will
 * influence all subsequent interactions and decision-making processes.
 * 
 * @param configuration defines behavioral parameters and interaction rules
 * @param initialState sets the starting operational mode
 * @throws IllegalArgumentException if configuration is invalid or incompatible
 */
public RobotEntity(RobotConfiguration configuration, RobotState initialState) {
    this.robotId = UUID.randomUUID();
    this.configuration = Objects.requireNonNull(configuration, "Configuration cannot be null");
    this.currentState = Objects.requireNonNull(initialState, "Initial state cannot be null");
    this.behaviorContext = new AtomicReference<>(createBehaviorContext());
    
    validateConfiguration();
    initializeBehavioralSystems();
} // Constructor: RobotEntity()
```

- Explain initialization impact on system behavior
- Document parameter relationships and constraints
- Use Objects.requireNonNull for validation
- Include closing comment with parentheses

### Methods

```java
// -- Public Methods --

/**
 * Processes interaction requests and generates appropriate behavioral responses.
 * 
 * Evaluates interaction context against current behavioral state and generates
 * responses that maintain personality consistency while adapting to situational
 * requirements. May trigger state transitions based on interaction outcomes.
 * 
 * @param interaction the interaction context requiring processing
 * @return response strategy tailored to current behavioral state
 * @throws InteractionException if interaction cannot be processed in current state
 */
public InteractionResponse processInteraction(InteractionContext interaction) {
    Objects.requireNonNull(interaction, "Interaction context cannot be null");
    
    if (!currentState.canAct()) {
        throw new InteractionException("Robot cannot process interactions in state: " + currentState);
    }
    
    BehaviorContext context = behaviorContext.get();
    InteractionResponse response = context.evaluateInteraction(interaction);
    
    updateBehavioralState(interaction, response);
    return response;
} // processInteraction()
```

- Focus on method's role in larger system workflows
- Explain decision-making processes and state impacts
- Document exception conditions and recovery strategies
- Include closing comment with method name and parentheses: `} // methodName()`

## Code Style and Formatting

### Braces and Indentation

```java
public void processRobotUpdate() {
    if (shouldUpdateBehavior()) {
        updateBehavioralPatterns();
    } else {
        maintainCurrentBehavior();
    }
    
    while (hasPendingInteractions()) {
        processNextInteraction();
    }
}
```

- Use K&R style bracing (opening brace on same line)
- Use 4-space indentation consistently
- Add space before opening braces
- Align closing braces with opening statement

### Single Line Statements

```java
// Simple conditional - braces optional
if (isActive()) updateLastActivity();

// Multi-line or complex logic - always use braces
if (requiresComplexProcessing()) {
    performComplexCalculation();
    updateInternalState();
}
```

- Omit braces only for simple single-line statements
- Always use braces for multi-statement blocks
- Consider readability over brevity

### Spacing and Line Length

```java
// Method parameters and operators
public void configureRobot(String name, RobotType type, boolean isActive) {
    for (int i = 0; i < configurations.length; i++) {
        if (configurations[i].matches(type)) {
            applyConfiguration(configurations[i]);
        }
    }
}

// Long parameter lists
public RobotBuilder createRobot(
        String name,
        RobotType type,
        BehaviorProfile profile,
        Map<String, Object> customProperties) {
    // Implementation
}
```

- Maintain 120-character line limit
- Add spaces around operators and after commas
- Indent wrapped parameters to align with opening parenthesis
- Break long method chains at logical points

## Documentation Standards

### Philosophy: Insightful Over Descriptive, Concise Over Verbose

Documentation should reveal **why** code exists and **how** it fits into the broader architecture, not merely describe **what** it does. However, **be concise** - every sentence must add value. Focus on:

- **Architectural Role**: How this component coordinates with others
- **Design Decisions**: Why this approach was chosen over alternatives
- **System Impact**: What happens when this code executes (only if non-obvious)
- **Trade-offs**: What was sacrificed for what benefit (only significant ones)
- **Failure Modes**: How the system degrades when things go wrong (only if critical)

**Key Principle**: If it's obvious from the code or method name, don't document it. Document the non-obvious insights.

**Bad Example** (Merely Descriptive):
```java
/**
 * Processes an interaction.
 * 
 * @param interaction the interaction to process
 * @return the response
 */
public InteractionResponse processInteraction(InteractionContext interaction) {
```

**Bad Example** (Too Verbose):
```java
/**
 * <p>Coordinates behavioral response generation through multi-stage evaluation.<p>
 * <p>
 * <b>Decision Flow:</b> Interaction requests pass through compatibility filtering,
 * priority assessment, and behavioral alignment checks before response generation.
 * This staged approach prevents inappropriate responses while maintaining personality
 * consistency across varying interaction contexts. The filtering stage ensures that
 * only valid interactions proceed to evaluation. The priority assessment determines
 * which interactions should be processed first. The behavioral alignment ensures
 * responses match the robot's personality profile.
 * <p>
 * <b>State Impact:</b> Successful interactions may trigger behavioral adaptation,
 * updating response patterns based on outcome feedback. Failed interactions are
 * logged but don't affect core behavioral models to prevent corruption from
 * malformed requests. The adaptation process is asynchronous to avoid blocking.
 * <p>
 * <b>Performance:</b> Response generation is O(1) for cached patterns, O(n) for
 * novel interactions requiring full behavioral evaluation. Caching is implemented
 * using a LRU cache with a maximum size of 1000 entries.
 * 
 * @param interaction context containing request type, source, and environmental factors
 * @return response strategy with behavioral tone, content, and follow-up actions
 * @throws InteractionException if interaction violates behavioral constraints
 */
public InteractionResponse processInteraction(InteractionContext interaction) {
```

**Good Example** (Concise and Insightful):
```java
/**
 * Coordinates behavioral response through multi-stage evaluation.
 * <p>
 * <b>Architecture:</b> Filters compatibility, assesses priority, validates behavioral
 * alignment before generation. Prevents inappropriate responses while maintaining
 * personality consistency.
 * <p>
 * <b>State Impact:</b> Successful interactions trigger adaptation; failures are logged
 * but don't corrupt behavioral models.
 * <p>
 * <b>Performance:</b> O(1) for cached patterns, O(n) for novel interactions.
 * 
 * @param interaction context with request type, source, and environmental factors
 * @return response strategy with behavioral tone and follow-up actions
 * @throws InteractionException if interaction violates behavioral constraints
 */
public InteractionResponse processInteraction(InteractionContext interaction) {
```

### JavaDoc Formatting for Readability

Use HTML tags to structure documentation for better visual hierarchy, but **keep it concise**:

```java
/**
 * Orchestrates robot behavioral adaptation based on environmental feedback.
 * <p>
 * <b>Architecture:</b> Monitors interaction outcomes and environmental changes to refine
 * responses. Balances consistency (dampens rapid changes) with flexibility (adapts to
 * persistent patterns).
 * <p>
 * <b>Performance:</b> Runs asynchronously to avoid blocking. Results cached for 30 minutes.
 * <p>
 * <i>Note:</i> Uses exponential moving average with configurable sensitivity thresholds.
 * 
 * @param environmentData current context (time, location, nearby entities)
 * @param interactionHistory recent outcomes (last 50) for pattern analysis
 * @return adaptation strategy with behavioral adjustments and confidence levels
 * @throws AdaptationException if environmental data is incompatible with behavioral model
 */
public AdaptationStrategy adaptBehavior(
        EnvironmentData environmentData,
        List<InteractionOutcome> interactionHistory) throws AdaptationException {
    // Implementation
}
```

**Formatting Guidelines:**
- Use `<p>` tags to separate logical sections (improves readability in IDEs)
- Use `<b>Section Headers:</b>` to create visual anchors for key concepts
- Use `<i>Note:</i>` for technical details (keep brief)
- Use `<code>methodName()</code>` for inline code references
- **Avoid redundancy** - don't repeat what's obvious from code or method names
- **Be selective** - only include sections that add non-obvious insights

**Common Section Headers** (use sparingly, only when adding value):
- `<b>Architecture:</b>` - How it fits in the system
- `<b>Design Decision:</b>` - Why this approach over alternatives
- `<b>State Impact:</b>` - What changes (only if non-obvious)
- `<b>Performance:</b>` - Complexity notes (only if critical)
- `<b>Failure Mode:</b>` - Error handling (only if non-standard)
- `<i>Note:</i>` - Brief technical details

**Real-World Example** (EntityState enum):
```java
/**
 * Coordinates robot behavioral priorities through discrete operational modes.
 * <p>
 * <b>Architecture:</b> States sit above the goal system as the top-level decision layer.
 * State changes trigger goal set recalculation, allowing owner commands to alter behavior
 * without modifying individual AI goals.
 * <p>
 * <b>Design Decision:</b> Three states balance behavioral variety with player comprehension.
 * More granular options (Patrol, Guard, Escort) were rejected to avoid subtle distinctions.
 * <p>
 * <b>Serialization:</b> ID-based with O(1) CODEC array lookup. Invalid IDs default to
 * Follow rather than crashing, ensuring robots survive corrupted save data.
 */
public enum EntityState {
    /**
     * Active companion: FollowOwnerGoal prioritized, combat goals active.
     * Default state for exploration and combat.
     */
    Follow(0),
    
    /**
     * Territorial guardian: FollowOwnerGoal disabled, BaseDefenseGoal anchors to location.
     * Used for base defense and preventing robots from following into danger.
     */
    Defense(1),
    
    /**
     * Passive observer: All movement/combat goals disabled except sitting.
     * Used for decoration or keeping robots non-threatening to mobs.
     */
    Standby(2);
}
```

### Section Headers and Organization

```java
// -- Core Behavioral Systems --

// -- Interaction Processing --

// -- State Management --

// -- Utility Methods --
```

- Use descriptive section headers that reflect functional areas
- Group methods by architectural responsibility
- Order sections from most important to supporting functionality

## Error Handling and Validation

### Exception Handling

```java
try {
    BehaviorResponse response = processBehavioralRequest(request);
    return response.generateOutput();
} catch (BehaviorException e) {
    LOGGER.warn("Behavioral processing failed for request: {}, falling back to default behavior", 
                request.getId(), e);
    return generateDefaultBehavior(request);
} catch (Exception e) {
    LOGGER.error("Unexpected error during behavioral processing", e);
    throw new RobotSystemException("Critical behavioral system failure", e);
} finally {
    cleanupBehavioralResources();
}
```

- Catch specific exceptions before general ones
- Log context information that aids debugging
- Provide meaningful fallback behaviors when possible
- Wrap unexpected exceptions with domain-specific types

### Parameter Validation

```java
public void updateRobotBehavior(RobotId robotId, BehaviorUpdate update) {
    Objects.requireNonNull(robotId, "Robot ID is required for behavior updates");
    Objects.requireNonNull(update, "Behavior update cannot be null");
    
    if (!update.isValid()) {
        throw new IllegalArgumentException("Behavior update failed validation: " + update.getValidationErrors());
    }
    
    if (!robotExists(robotId)) {
        throw new RobotNotFoundException("No robot found with ID: " + robotId);
    }
    
    // Method implementation
}
```

- Use Objects.requireNonNull for null checks
- Validate business rules with descriptive error messages
- Use domain-specific exceptions for business rule violations
- Perform validation before any state changes

## Naming Conventions

- **Packages**: `lowercase.separated.by.dots` (`com.lovelyrobot.core.entities`)
- **Classes**: `PascalCase` (`RobotEntity`, `BehaviorProcessor`)
- **Interfaces**: `IPascalCase` with 'I' prefix (`IRobotBehavior`, `IInteractionHandler`)
- **Methods**: `camelCase` (`processInteraction`, `updateBehavior`)
- **Variables**: `camelCase` (`robotId`, `behaviorContext`)
- **Constants**: `SCREAMING_SNAKE_CASE` (`DEFAULT_INTERACTION_RANGE`, `MAX_RETRY_ATTEMPTS`)
- **Enums**: `PascalCase` for enum and values (`RobotState.ACTIVE`)
- **Generics**: `T` or descriptive (`T`, `TEntity`, `TResponse`)

## Language Features and Best Practices

### Optional and Null Safety

```java
/**
 * Retrieves robot configuration with fallback to system defaults.
 * 
 * Provides configuration isolation while ensuring system stability through
 * intelligent fallback mechanisms when custom configurations are unavailable.
 */
public Optional<RobotConfiguration> getRobotConfiguration(RobotId robotId) {
    return Optional.ofNullable(configurationCache.get(robotId))
                  .filter(config -> config.isValid())
                  .or(() -> loadDefaultConfiguration());
}

// Usage with proper null safety
getRobotConfiguration(robotId)
    .ifPresentOrElse(
        config -> applyConfiguration(config),
        () -> LOGGER.warn("No valid configuration found for robot: {}", robotId)
    );
```

- Use Optional for methods that may not return a value
- Chain Optional operations for clean null handling
- Avoid Optional for fields and parameters

### Stream API and Functional Programming

```java
/**
 * Filters and prioritizes robot interactions based on behavioral compatibility.
 * 
 * Evaluates interaction requests against current behavioral state and capacity
 * constraints, ensuring optimal resource allocation while maintaining responsive
 * interaction patterns.
 */
public List<InteractionRequest> prioritizeInteractions(List<InteractionRequest> requests) {
    return requests.stream()
                  .filter(this::isCompatibleWithCurrentState)
                  .filter(request -> request.getPriority().ordinal() >= minimumPriority.ordinal())
                  .sorted(Comparator.comparing(InteractionRequest::getPriority)
                                   .thenComparing(InteractionRequest::getTimestamp))
                  .limit(maxConcurrentInteractions)
                  .collect(Collectors.toList());
}
```

- Use streams for data transformation and filtering
- Prefer method references when they improve readability
- Break complex stream operations across multiple lines
- Use appropriate collectors for result types

### Concurrency and Thread Safety

```java
/**
 * Manages concurrent robot state updates with consistency guarantees.
 * 
 * Ensures atomic state transitions while preventing race conditions that
 * could lead to inconsistent behavioral states or resource conflicts.
 */
private final ReadWriteLock stateLock = new ReentrantReadWriteLock();
private volatile RobotState currentState = RobotState.IDLE;

public void updateState(RobotState newState) {
    stateLock.writeLock().lock();
    try {
        if (isValidTransition(currentState, newState)) {
            RobotState previousState = this.currentState;
            this.currentState = newState;
            notifyStateChange(previousState, newState);
        }
    } finally {
        stateLock.writeLock().unlock();
    }
}

public RobotState getCurrentState() {
    stateLock.readLock().lock();
    try {
        return currentState;
    } finally {
        stateLock.readLock().unlock();
    }
}
```

- Use appropriate synchronization mechanisms for shared state
- Prefer concurrent collections over synchronized wrappers
- Document thread safety guarantees in class-level JavaDoc
- Use volatile for simple state flags

### Builder Pattern Implementation

```java
/**
 * Constructs robot entities with validated configuration and behavioral setup.
 * 
 * Provides fluent configuration while ensuring all required behavioral
 * components are properly initialized and validated before robot activation.
 */
public static class RobotBuilder {
    private String name;
    private RobotType type;
    private BehaviorProfile behaviorProfile;
    private Map<String, Object> customProperties = new HashMap<>();

    /**
     * Configures robot behavioral parameters that influence interaction patterns.
     * 
     * Behavioral profiles define personality traits and response tendencies
     * that will guide decision-making throughout the robot's lifecycle.
     */
    public RobotBuilder withBehavior(BehaviorProfile profile) {
        this.behaviorProfile = Objects.requireNonNull(profile, "Behavior profile cannot be null");
        return this;
    }

    /**
     * Constructs the robot entity with comprehensive validation.
     * 
     * Validates configuration compatibility and initializes all behavioral
     * systems before returning a fully operational robot instance.
     */
    public RobotEntity build() {
        validateRequiredFields();
        validateConfigurationCompatibility();
        return new RobotEntity(this);
    }

    private void validateRequiredFields() {
        if (name == null || type == null || behaviorProfile == null) {
            throw new IllegalStateException("Name, type, and behavior profile are required");
        }
    }
}
```

- Use builder pattern for complex object construction
- Validate configuration at build time
- Provide fluent interface with method chaining
- Document the impact of each configuration option

## Architectural Patterns

### Dependency Injection

```java
/**
 * Coordinates robot behavioral systems with external service dependencies.
 * 
 * Manages service lifecycle and ensures proper resource cleanup while
 * maintaining loose coupling between behavioral components and infrastructure.
 */
@Component
public class RobotBehaviorCoordinator {

    private final InteractionService interactionService;
    private final BehaviorAnalyzer behaviorAnalyzer;
    private final ConfigurationManager configurationManager;

    /**
     * Initializes coordinator with required service dependencies.
     * 
     * Establishes service relationships that enable behavioral processing
     * while maintaining architectural boundaries between system layers.
     */
    public RobotBehaviorCoordinator(
            InteractionService interactionService,
            BehaviorAnalyzer behaviorAnalyzer,
            ConfigurationManager configurationManager) {
        
        this.interactionService = Objects.requireNonNull(interactionService);
        this.behaviorAnalyzer = Objects.requireNonNull(behaviorAnalyzer);
        this.configurationManager = Objects.requireNonNull(configurationManager);
    }
}
```

- Use constructor injection for required dependencies
- Validate injected dependencies
- Document service relationships and architectural boundaries
- Prefer interfaces over concrete implementations

### Factory Methods and Abstract Factories

```java
/**
 * Creates robot instances optimized for specific operational contexts.
 * 
 * Encapsulates robot construction complexity while ensuring proper
 * initialization of context-specific behavioral and interaction systems.
 */
public class RobotFactory {

    /**
     * Creates a companion robot optimized for player interaction and assistance.
     * 
     * Configures behavioral patterns that prioritize responsiveness and
     * helpfulness while maintaining appropriate social boundaries.
     */
    public static RobotEntity createCompanionRobot(String name, Player owner) {
        return new RobotBuilder()
                .withName(name)
                .withType(RobotType.COMPANION)
                .withBehavior(BehaviorProfiles.FRIENDLY_ASSISTANT)
                .withOwner(owner)
                .withInteractionRange(COMPANION_INTERACTION_RANGE)
                .build();
    }

    /**
     * Creates a utility robot focused on task execution and resource management.
     * 
     * Optimizes behavioral patterns for efficiency and task completion while
     * minimizing social interaction overhead.
     */
    public static RobotEntity createUtilityRobot(String name, TaskConfiguration tasks) {
        return new RobotBuilder()
                .withName(name)
                .withType(RobotType.UTILITY)
                .withBehavior(BehaviorProfiles.TASK_FOCUSED)
                .withTaskConfiguration(tasks)
                .withInteractionRange(UTILITY_INTERACTION_RANGE)
                .build();
    }
}
```

- Use factory methods for context-specific object creation
- Document the optimization focus and behavioral implications
- Encapsulate complex configuration logic
- Provide meaningful method names that indicate purpose

## Performance and Resource Management

### Resource Management

```java
/**
 * Manages robot behavioral processing with automatic resource cleanup.
 * 
 * Ensures proper resource lifecycle management while maintaining behavioral
 * processing performance through efficient resource pooling and cleanup.
 */
public class BehaviorProcessor implements AutoCloseable {

    private final ExecutorService behaviorExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private volatile boolean isShutdown = false;

    public BehaviorProcessor() {
        this.behaviorExecutor = Executors.newCachedThreadPool(
            new ThreadFactoryBuilder()
                .setNameFormat("behavior-processor-%d")
                .setDaemon(true)
                .build()
        );
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
    }

    @Override
    public void close() {
        isShutdown = true;
        
        behaviorExecutor.shutdown();
        scheduledExecutor.shutdown();
        
        try {
            if (!behaviorExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                behaviorExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            behaviorExecutor.shutdownNow();
            scheduledExecutor.shutdownNow();
        }
    }
}
```

- Implement AutoCloseable for resource management
- Use try-with-resources for automatic cleanup
- Provide graceful shutdown with timeouts
- Handle interruption properly in cleanup code

### Memory and Performance Optimization

```java
/**
 * Caches behavioral analysis results with memory-conscious eviction policies.
 * 
 * Balances analysis performance with memory usage through intelligent caching
 * strategies that prioritize frequently accessed behavioral patterns while
 * preventing memory exhaustion.
 */
private final Cache<BehaviorKey, BehaviorAnalysis> analysisCache = 
    CacheBuilder.newBuilder()
               .maximumSize(1000)
               .expireAfterWrite(Duration.ofMinutes(30))
               .recordStats()
               .build();

/**
 * Retrieves or computes behavioral analysis with caching optimization.
 * 
 * Leverages cached results for performance while ensuring analysis accuracy
 * through appropriate cache invalidation and refresh strategies.
 */
public BehaviorAnalysis analyzeBehavior(BehaviorContext context) {
    BehaviorKey key = BehaviorKey.from(context);
    
    return analysisCache.get(key, () -> {
        LOGGER.debug("Computing behavioral analysis for context: {}", context.getId());
        return performBehavioralAnalysis(context);
    });
}
```

- Use appropriate caching strategies for performance
- Configure cache limits to prevent memory issues
- Document performance trade-offs and optimization strategies
- Monitor cache effectiveness through metrics

## Testing Considerations

### Test Structure and Documentation

```java
/**
 * Validates robot behavioral consistency across different interaction scenarios.
 * 
 * Ensures behavioral systems maintain personality coherence while adapting
 * appropriately to varying environmental and social contexts.
 */
class RobotBehaviorTest {

    /**
     * Verifies that repeated similar interactions produce consistent behavioral responses.
     * 
     * Behavioral consistency is crucial for user trust and predictable robot
     * personality, while still allowing for appropriate contextual adaptation.
     */
    @Test
    void shouldMaintainBehavioralConsistency() {
        // Given: A robot with established behavioral patterns
        RobotEntity robot = createTestRobot(BehaviorProfiles.FRIENDLY_ASSISTANT);
        InteractionContext greeting = createGreetingInteraction();
        
        // When: Processing the same interaction multiple times
        InteractionResponse firstResponse = robot.processInteraction(greeting);
        InteractionResponse secondResponse = robot.processInteraction(greeting);
        
        // Then: Responses should be behaviorally consistent
        assertThat(firstResponse.getBehavioralTone())
            .isEqualTo(secondResponse.getBehavioralTone());
        assertThat(firstResponse.getResponseCategory())
            .isEqualTo(secondResponse.getResponseCategory());
    }
}
```

- Focus test documentation on behavioral expectations and system impact
- Use descriptive test method names that explain the scenario
- Structure tests with Given-When-Then for clarity
- Validate behavioral consistency and system integration

## Code Organization Guidelines

1. **Package Structure**: Organize by architectural layers and domain boundaries
2. **Class Ordering**: Constants, fields, constructors, public methods, protected methods, private methods
3. **Method Grouping**: Group by functional responsibility and call frequency
4. **Import Organization**: Static imports first, then standard library, then third-party, then project imports
5. **Documentation Focus**: Emphasize system impact, architectural role, and behavioral implications
6. **Error Handling**: Provide meaningful error messages and appropriate fallback behaviors
7. **Resource Management**: Use try-with-resources and implement AutoCloseable where appropriate
8. **Thread Safety**: Document concurrency guarantees and use appropriate synchronization
9. **Performance**: Consider memory usage and processing efficiency in design decisions
10. **Testing**: Focus on behavioral validation and system integration scenarios

This coding style guide emphasizes the architectural and behavioral aspects of code, ensuring that documentation provides insight into system design and operational impact rather than merely describing implementation details.