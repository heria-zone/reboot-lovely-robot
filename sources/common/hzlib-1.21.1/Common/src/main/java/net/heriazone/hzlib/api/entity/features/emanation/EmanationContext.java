package net.heriazone.hzlib.api.entity.features.emanation;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * <p>Immutable snapshot of the world at the moment an emanation fires.<p>
 * <p>
 * <b>Architecture:</b> Constructed once per trigger event and passed to every
 * condition and effect in the matching rule. Never null — always fully populated
 * for the trigger type it represents. Fields irrelevant to a given trigger are null.
 * <p>
 * <b>Use static factories</b> ({@link #onAttack}, {@link #onHurt}, {@link #onGift},
 * {@link #onThreshold}) rather than the constructor directly.
 */
public final class EmanationContext {

    // -- Fields --

    /** The entity whose nature is emanating. Always non-null. */
    public final LivingEntity self;

    /** ON_ATTACK: the entity that was struck. Null for other triggers. */
    @Nullable public final LivingEntity target;

    /** ON_HURT: the entity that dealt the damage. Null for other triggers or non-entity sources. */
    @Nullable public final LivingEntity attacker;

    /** ON_GIFT: the player who gave the item. Null for other triggers. */
    @Nullable public final Player giver;

    /** ON_GIFT: the item stack being offered. Null for other triggers. */
    @Nullable public final ItemStack gift;

    /** The level in which this emanation occurs. Always non-null. */
    public final ServerLevel level;

    /** The trigger that produced this context. Always non-null. */
    public final EmanationTrigger trigger;

    // -- Constructor --

    private EmanationContext(LivingEntity self, @Nullable LivingEntity target,
                              @Nullable LivingEntity attacker, @Nullable Player giver,
                              @Nullable ItemStack gift, ServerLevel level,
                              EmanationTrigger trigger) {
        this.self     = self;
        this.target   = target;
        this.attacker = attacker;
        this.giver    = giver;
        this.gift     = gift;
        this.level    = level;
        this.trigger  = trigger;
    } // Constructor: EmanationContext ()

    // -- Static Factories --

    /** Creates a context for an ON_ATTACK moment — entity struck a target. */
    public static EmanationContext onAttack(LivingEntity self, LivingEntity target) {
        return new EmanationContext(self, target, null, null, null,
                (ServerLevel) self.level(), EmanationTrigger.ON_ATTACK);
    } // onAttack ()

    /** Creates a context for an ON_HURT moment — entity took damage. */
    public static EmanationContext onHurt(LivingEntity self, @Nullable LivingEntity attacker) {
        return new EmanationContext(self, null, attacker, null, null,
                (ServerLevel) self.level(), EmanationTrigger.ON_HURT);
    } // onHurt ()

    /** Creates a context for an ON_GIFT moment — player gave the entity an item. */
    public static EmanationContext onGift(LivingEntity self, Player giver, ItemStack gift) {
        return new EmanationContext(self, null, null, giver, gift,
                (ServerLevel) self.level(), EmanationTrigger.ON_GIFT);
    } // onGift ()

    /** Creates a context for an ON_THRESHOLD moment — health crossed a threshold. */
    public static EmanationContext onThreshold(LivingEntity self) {
        return new EmanationContext(self, null, null, null, null,
                (ServerLevel) self.level(), EmanationTrigger.ON_THRESHOLD);
    } // onThreshold ()

} // Class: EmanationContext
