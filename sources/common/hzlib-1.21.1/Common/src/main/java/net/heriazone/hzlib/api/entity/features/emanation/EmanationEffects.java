package net.heriazone.hzlib.api.entity.features.emanation;

import net.heriazone.hzlib.api.entity.util.EntityUtils;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * <p>Static factory for common {@link EmanationEffect} implementations.<p>
 * <p>
 * <b>Usage:</b> Pass results directly into {@link EmanationRule.Builder#effect}:
 * <pre>{@code
 * EmanationRule.builder()
 *     .trigger(EmanationTrigger.ON_ATTACK)
 *     .effect(EmanationEffects.igniteTarget(4))
 *     .effect(EmanationEffects.applyToSelf(MobEffects.REGENERATION, 60, 0))
 *     .build()
 * }</pre>
 */
public final class EmanationEffects {

    private EmanationEffects() {} // non-instantiable

    // -- Target effects (ON_ATTACK) --

    /**
     * Sets the attack target on fire for {@code seconds} seconds.
     * <p>
     * <b>Version isolation:</b> Delegates to {@link EntityUtils#ignite} — the
     * underlying Minecraft API differs between versions. See {@link EntityUtils}
     * for the backport guide.
     */
    public static EmanationEffect igniteTarget(int seconds) {
        return ctx -> { if (ctx.target != null) EntityUtils.ignite(ctx.target, seconds); };
    } // igniteTarget ()

    /**
     * Adds {@code ticks} freeze ticks to the target, capped at the freeze threshold.
     * <p>
     * <b>Powder snow immunity:</b> The Chilly effect on the holder drains freeze ticks
     * each tick via {@code applyEffectTick} — this effect is the offensive counterpart.
     */
    public static EmanationEffect freezeTarget(int ticks) {
        return ctx -> {
            if (ctx.target == null) return;
            int current = ctx.target.getTicksFrozen();
            int max     = ctx.target.getTicksRequiredToFreeze();
            ctx.target.setTicksFrozen(Math.min(current + ticks, max));
        };
    } // freezeTarget ()

    /** Applies a mob effect to the attack target. */
    public static EmanationEffect applyToTarget(Holder<MobEffect> effect, int durationTicks, int amplifier) {
        return ctx -> {
            if (ctx.target != null)
                ctx.target.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        };
    } // applyToTarget ()

    /**
     * Deals bonus damage equal to {@code fraction} of the target's max health.
     * <p>
     * <b>Soul Wanderer undead bonus:</b> {@code dealBonusDamage(0.7f)} deals 70% of
     * the target's max health as additional direct damage. The damage source is
     * {@code mobAttack} from the emanating entity.
     */
    public static EmanationEffect dealBonusDamage(float fraction) {
        return ctx -> {
            if (ctx.target == null) return;
            float bonus = ctx.target.getMaxHealth() * fraction;
            DamageSource source = ctx.getLevel().damageSources().mobAttack(ctx.getEntity());
            ctx.target.hurt(source, bonus);
        };
    } // dealBonusDamage ()

    // -- Attacker effects (ON_HURT) --

    /** Applies a mob effect to the entity that dealt damage. */
    public static EmanationEffect applyToAttacker(Holder<MobEffect> effect, int durationTicks, int amplifier) {
        return ctx -> {
            if (ctx.attacker != null)
                ctx.attacker.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        };
    } // applyToAttacker ()

    // -- Giver effects (ON_GIFT) --

    /**
     * Applies a mob effect to the player who gave the item — the blessing.
     * <p>
     * <b>Design note:</b> This is not a trade. The entity was made happy; her
     * happiness flows outward as a benevolent emanation. The giver receives it
     * because they are the closest living thing to that outward flow.
     */
    public static EmanationEffect applyToGiver(Holder<MobEffect> effect, int durationTicks, int amplifier) {
        return ctx -> {
            if (ctx.giver != null)
                ctx.giver.addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
        };
    } // applyToGiver ()

    // -- Self effects (any trigger) --

    /** Applies a mob effect to the emanating entity herself. */
    public static EmanationEffect applyToSelf(Holder<MobEffect> effect, int durationTicks, int amplifier) {
        return ctx -> ctx.getEntity().addEffect(new MobEffectInstance(effect, durationTicks, amplifier));
    } // applyToSelf ()

    /** Heals the entity by a flat amount, capped at her max health. */
    public static EmanationEffect healSelf(float amount) {
        return ctx -> ctx.getEntity().heal(amount);
    } // healSelf ()

    // -- Area of effect --

    /**
     * Applies a set of inner effects to all living entities within {@code radius} blocks
     * of the entity, excluding herself.
     * <p>
     * <b>Context remapping:</b> Each nearby entity is placed in the {@code target} slot
     * of a derived context so inner effects using {@code ctx.target} resolve correctly.
     * <p>
     * <b>Mandrake scream:</b> The canonical use — one {@code aoe()} wraps multiple
     * {@code applyToTarget()} effects that together form the scream burst.
     *
     * @param radius  search radius in blocks
     * @param effects inner effects applied to each entity found within radius
     */
    public static EmanationEffect aoe(float radius, EmanationEffect... effects) {
        return ctx -> {
            List<LivingEntity> nearby = ctx.getLevel().getEntitiesOfClass(
                    LivingEntity.class,
                    ctx.getEntity().getBoundingBox().inflate(radius),
                    e -> e != ctx.getEntity() && e.isAlive()
            );
            for (LivingEntity entity : nearby) {
                EmanationContext aoeCtx = EmanationContext.onAttack(ctx.getEntity(), entity);
                for (EmanationEffect effect : effects) {
                    effect.apply(aoeCtx);
                }
            }
        };
    } // aoe ()

} // Class: EmanationEffects
