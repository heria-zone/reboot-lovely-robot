package net.heriazone.hzlib.api.entity.features.emanation;

import net.heriazone.hzlib.api.entity.conditions.EntityContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Immutable snapshot of the world at the moment an emanation fires.
 * <p>
 * <b>Architecture:</b> Extends {@link EntityContext} — the four shared accessors
 * ({@code getDimension}, {@code getBiome}, {@code getDayTime}, {@code isServerSide})
 * are inherited. {@link #getLevel()} is overridden with a covariant {@link ServerLevel}
 * return — emanation always runs server-side, so callers get the narrower type for free.
 * <p>
 * Fields irrelevant to a given trigger are null. Use the static factories rather
 * than the constructor — they encode which fields are populated per trigger type.
 */
public final class EmanationContext extends EntityContext {

    // -- Domain fields --

    /** ON_ATTACK: the entity that was struck. Null for other triggers. */
    @Nullable public final LivingEntity target;

    /** ON_HURT: the entity that dealt the damage. Null for other triggers or non-entity sources. */
    @Nullable public final LivingEntity attacker;

    /** ON_GIFT: the player who gave the item. Null for other triggers. */
    @Nullable public final Player giver;

    /** ON_GIFT: the item stack being offered. Null for other triggers. */
    @Nullable public final ItemStack gift;

    /** The trigger that produced this context. Always non-null. */
    public final EmanationTrigger trigger;

    // -- Constructor --

    private EmanationContext(LivingEntity self, @Nullable LivingEntity target,
                              @Nullable LivingEntity attacker, @Nullable Player giver,
                              @Nullable ItemStack gift, EmanationTrigger trigger) {
        super(self);
        this.target   = target;
        this.attacker = attacker;
        this.giver    = giver;
        this.gift     = gift;
        this.trigger  = trigger;
    } // Constructor: EmanationContext ()

    // -- Covariant level accessor --

    /**
     * Returns the server level in which this emanation occurs.
     * <p>
     * <b>Design Decision:</b> Covariant override of {@link EntityContext#getLevel()} —
     * emanations are server-only so the cast is always safe, and callers avoid a
     * manual cast when they need {@code ServerLevel} APIs.
     */
    @Override
    public ServerLevel getLevel() {
        return (ServerLevel) super.getLevel();
    } // getLevel ()

    // -- Static factories --

    /** Creates a context for an ON_ATTACK moment — entity struck a target. */
    public static EmanationContext onAttack(LivingEntity self, LivingEntity target) {
        return new EmanationContext(self, target, null, null, null, EmanationTrigger.ON_ATTACK);
    } // onAttack ()

    /** Creates a context for an ON_HURT moment — entity took damage. */
    public static EmanationContext onHurt(LivingEntity self, @Nullable LivingEntity attacker) {
        return new EmanationContext(self, null, attacker, null, null, EmanationTrigger.ON_HURT);
    } // onHurt ()

    /** Creates a context for an ON_GIFT moment — player gave the entity an item. */
    public static EmanationContext onGift(LivingEntity self, Player giver, ItemStack gift) {
        return new EmanationContext(self, null, null, giver, gift, EmanationTrigger.ON_GIFT);
    } // onGift ()

    /** Creates a context for an ON_THRESHOLD moment — health crossed a threshold. */
    public static EmanationContext onThreshold(LivingEntity self) {
        return new EmanationContext(self, null, null, null, null, EmanationTrigger.ON_THRESHOLD);
    } // onThreshold ()

} // Class: EmanationContext
