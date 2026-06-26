package net.heriazone.hzlib.api.entity.features;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * Declares ranged attack capability for an entity family.
 * <p>
 * <b>Architecture:</b> Attached to a {@link net.heriazone.hzlib.api.entity.NativeEntityFamily}
 * via {@code withFeature(RangedAttackFeature.class, ...)}. When present,
 * {@code WildTamableEntity.registerGoals()} automatically wires a {@code RangedAttackGoal}
 * and a {@code NearestAttackableTargetGoal<Monster>} — no entity-class override needed.
 * <p>
 * <b>Loader isolation:</b> The projectile factory accepts an {@code EntityType} and a
 * {@code Level} and returns a {@link Projectile}. The actual type reference
 * (e.g. {@code MonstersEntities.MUSHROOM_SNOWBALL_PROJECTILE}) lives at the call site in
 * the loader module — this class has no loader dependency.
 * <p>
 * <b>throw arc:</b> The entity class ({@code WildTamableEntity}) owns the throw trajectory
 * logic (arc correction, sound). The factory only creates the projectile instance; what
 * happens on impact is the projectile class's concern. This keeps the three concerns
 * cleanly separated: feature (data), entity (throw), projectile (impact).
 * <p>
 * <b>Usage example:</b>
 * <pre>{@code
 * withFeature(RangedAttackFeature.class, RangedAttackFeature
 *         .of(MonstersEntities.MUSHROOM_SNOWBALL_PROJECTILE,
 *             (type, level) -> new MushroomSnowball(type, level))
 *         .interval(20, 40)
 *         .range(15.0f))
 * }</pre>
 */
public class RangedAttackFeature {

    // -- Fields --

    private final EntityType<? extends Projectile>                       projectileType;
    private final BiFunction<EntityType<? extends Projectile>, Level, ? extends Projectile> factory;
    private int   minInterval  = 20;  // ticks
    private int   maxInterval  = 40;  // ticks
    private float attackRange  = 15f; // blocks

    // -- Constructor --

    private RangedAttackFeature(EntityType<? extends Projectile>                       type,
                                BiFunction<EntityType<? extends Projectile>, Level, ? extends Projectile> factory) {
        this.projectileType = Objects.requireNonNull(type,    "Projectile EntityType cannot be null");
        this.factory        = Objects.requireNonNull(factory, "Projectile factory cannot be null");
    } // Constructor: RangedAttackFeature ()

    // -- Factory --

    /**
     * Creates a new {@code RangedAttackFeature} with the given projectile type and factory.
     *
     * @param type    registered {@code EntityType} for the projectile — passed to the factory
     *                so the projectile can call {@code super(type, level)}
     * @param factory {@code (EntityType, Level) → Projectile} — constructs a fresh projectile
     *                instance; the thrower is set by the entity after construction
     * @param <P>     projectile type
     * @return new feature instance for chaining
     */
    public static <P extends Projectile> RangedAttackFeature of(
            EntityType<P> type,
            BiFunction<EntityType<P>, Level, P> factory) {
        // Widen the type so the field can store any factory without unchecked warnings at call sites.
        @SuppressWarnings("unchecked")
        BiFunction<EntityType<? extends Projectile>, Level, ? extends Projectile> widened =
                (t, l) -> factory.apply((EntityType<P>) t, l);
        return new RangedAttackFeature(type, widened);
    } // of ()

    // -- Fluent Builder --

    /**
     * Sets the throw interval range in ticks.
     * <p>
     * {@code RangedAttackGoal} uses {@code minInterval} as the fastest possible cadence
     * and {@code maxInterval} when the target is at maximum range.
     *
     * @param min minimum ticks between throws
     * @param max maximum ticks between throws
     * @return this instance for chaining
     */
    public RangedAttackFeature interval(int min, int max) {
        this.minInterval = Math.max(1, min);
        this.maxInterval = Math.max(minInterval, max);
        return this;
    } // interval ()

    /**
     * Sets the maximum engagement range in blocks.
     * <p>
     * The {@code RangedAttackGoal} will not activate unless a valid target is within
     * this distance. Beyond this range the entity may still chase the target.
     *
     * @param blocks attack range in blocks (positive)
     * @return this instance for chaining
     */
    public RangedAttackFeature range(float blocks) {
        this.attackRange = Math.max(1f, blocks);
        return this;
    } // range ()

    // -- Accessors --

    /** @return registered {@code EntityType} passed to the projectile factory */
    public EntityType<? extends Projectile> getProjectileType() { return projectileType; }

    /**
     * Creates a fresh projectile instance for a throw.
     * <p>
     * The returned projectile has no owner or position set — the calling entity
     * is responsible for calling {@code setOwner()} and {@code setPos()}.
     *
     * @param level world instance
     * @return new projectile instance
     */
    public Projectile createProjectile(Level level) {
        return factory.apply(projectileType, level);
    } // createProjectile ()

    /** @return minimum ticks between throws */
    public int getMinInterval()  { return minInterval;  }

    /** @return maximum ticks between throws */
    public int getMaxInterval()  { return maxInterval;  }

    /** @return maximum engagement range in blocks */
    public float getAttackRange() { return attackRange; }

} // Class: RangedAttackFeature
