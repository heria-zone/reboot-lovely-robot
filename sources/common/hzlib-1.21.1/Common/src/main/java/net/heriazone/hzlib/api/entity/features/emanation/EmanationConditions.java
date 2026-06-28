package net.heriazone.hzlib.api.entity.features.emanation;

import net.heriazone.hzlib.api.entity.util.EntityUtils;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

/**
 * Static factory for {@link EmanationCondition} predicates.
 * <p>
 * <b>Architecture:</b> All conditions here are emanation-domain-specific — target
 * classification, self state, and gift checks. Environment conditions (biome, time,
 * weather, chance) are not needed in this domain and should be sourced from
 * {@link net.heriazone.hzlib.api.entity.conditions.EntityConditions} directly if
 * ever required on an emanation rule.
 * <p>
 * <b>Version isolation:</b> All Minecraft API calls that vary between versions
 * (entity type classification) are delegated to {@link EntityUtils}. Only
 * {@link EntityUtils} needs updating on a backport — this class stays untouched.
 * <p>
 * Compose freely:
 * <pre>{@code
 * EmanationConditions.targetIsUndead().and(EmanationConditions.isTamed())
 * }</pre>
 */
public final class EmanationConditions {

    private EmanationConditions() {} // non-instantiable

    // -- Target conditions --

    /**
     * Passes when the attack target is an undead mob.
     * <p>
     * <b>1.21.1+:</b> Uses entity type tag {@code #minecraft:undead} via
     * {@link EntityUtils#isUndead}. <b>Pre-1.21:</b> {@code MobType.UNDEAD} — see
     * {@link EntityUtils} for the backport swap.
     */
    public static EmanationCondition targetIsUndead() {
        return ctx -> ctx.target != null && EntityUtils.isUndead(ctx.target);
    } // targetIsUndead ()

    /**
     * Passes when the attack target is an aquatic mob.
     * <p>
     * <b>1.21.1+:</b> {@code #minecraft:aquatic}. <b>Pre-1.21:</b> {@code MobType.WATER}.
     */
    public static EmanationCondition targetIsAquatic() {
        return ctx -> ctx.target != null && EntityUtils.isAquatic(ctx.target);
    } // targetIsAquatic ()

    /**
     * Passes when the attack target is an arthropod.
     * <p>
     * <b>1.21.1+:</b> {@code #minecraft:arthropod}. <b>Pre-1.21:</b> {@code MobType.ARTHROPOD}.
     */
    public static EmanationCondition targetIsArthropod() {
        return ctx -> ctx.target != null && EntityUtils.isArthropod(ctx.target);
    } // targetIsArthropod ()

    /**
     * Passes when the attack target belongs to the specified entity type tag.
     * <p>
     * Supports any tag — vanilla or custom mod-defined. Custom tags require only a
     * JSON file under {@code data/<namespace>/tags/entity_type/}, no Java changes.
     *
     * @param tag the entity type tag to check
     */
    public static EmanationCondition targetHasTag(TagKey<EntityType<?>> tag) {
        return ctx -> ctx.target != null && EntityUtils.hasTag(ctx.target, tag);
    } // targetHasTag ()

    /** Passes when the attack target is a player. */
    public static EmanationCondition targetIsPlayer() {
        return ctx -> ctx.target instanceof Player;
    } // targetIsPlayer ()

    /** Passes when the attacker (on ON_HURT) is a player. */
    public static EmanationCondition attackerIsPlayer() {
        return ctx -> ctx.attacker instanceof Player;
    } // attackerIsPlayer ()

    // -- Self state --

    /** Passes when the entity is tamed. */
    public static EmanationCondition isTamed() {
        return ctx -> ctx.getEntity() instanceof TamableAnimal t && t.isTame();
    } // isTamed ()

    /**
     * Passes when the entity's current health is at or below {@code value}.
     * <p>
     * <b>ON_THRESHOLD use:</b> Always pair with a rule {@code cooldown} — without it
     * the condition fires every tick while health stays below the threshold.
     */
    public static EmanationCondition healthAtMost(float value) {
        return ctx -> ctx.getEntity().getHealth() <= value;
    } // healthAtMost ()

    /** Passes when the entity's current health is at or above {@code value}. */
    public static EmanationCondition healthAtLeast(float value) {
        return ctx -> ctx.getEntity().getHealth() >= value;
    } // healthAtLeast ()

    // -- Gift conditions --

    /** Passes when the gifted item matches the specified item exactly. */
    public static EmanationCondition giftIs(Item item) {
        return ctx -> ctx.gift != null && ctx.gift.is(item);
    } // giftIs ()

    /** Passes when the gifted item belongs to the specified item tag. */
    public static EmanationCondition giftIsIn(TagKey<Item> tag) {
        return ctx -> ctx.gift != null && ctx.gift.is(tag);
    } // giftIsIn ()

    /** Passes when the giver is the entity's owner. */
    public static EmanationCondition giverIsOwner() {
        return ctx -> ctx.giver != null
                && ctx.getEntity() instanceof TamableAnimal t
                && t.isOwnedBy(ctx.giver);
    } // giverIsOwner ()

} // Class: EmanationConditions
