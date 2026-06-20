package net.heriazone.hzlib.api.entity.util;

import net.minecraft.tags.*;
import net.minecraft.world.entity.*;

/**
 * <p>Version-agnostic utility methods for entity queries and mutations.<p>
 * <p>
 * <b>Purpose:</b> Isolates Minecraft API calls that change between versions behind
 * a stable, version-agnostic Java interface. Feature code ({@code EmanationConditions},
 * {@code EmanationEffects}, etc.) calls these methods exclusively — never the
 * underlying Minecraft API directly. When backporting, only this class changes,
 * not every call site across the feature system.
 * <p>
 * <b>Version history of wrapped calls:</b>
 * <ul>
 *   <li>{@link #isUndead} — 1.21+: entity type tag {@code #minecraft:undead}.
 *       Pre-1.21: {@code getMobType() == MobType.UNDEAD} (removed in 1.21).</li>
 *   <li>{@link #isAquatic} — 1.21+: entity type tag {@code #minecraft:aquatic}.
 *       Pre-1.21: {@code getMobType() == MobType.WATER}.</li>
 *   <li>{@link #isArthropod} — 1.21+: entity type tag {@code #minecraft:arthropod}.
 *       Pre-1.21: {@code getMobType() == MobType.ARTHROPOD}.</li>
 *   <li>{@link #isIllager} — 1.21+: entity type tag {@code #minecraft:illager}.
 *       Pre-1.21: {@code getMobType() == MobType.ILLAGER}.</li>
 *   <li>{@link #ignite} — 1.21+: {@code igniteForSeconds(n)}.
 *       Pre-1.21: {@code setSecondsOnFire(n)}.</li>
 * </ul>
 * <p>
 * <b>Custom tags:</b> Since the check is tag-based, any entity type can be added to
 * (or removed from) the undead/aquatic/etc. set via datapack JSON without touching
 * Java code. Custom mod-specific tags (e.g. {@code monsters_girls:fey}) follow the
 * same pattern and can be checked with {@link #hasTag}.
 * <p>
 * <b>Backport guide:</b> When porting to a version where any of these methods breaks:
 * <ol>
 *   <li>Change only this class.</li>
 *   <li>All feature conditions and effects continue to compile and work unchanged.</li>
 *   <li>Document the version-specific implementation in the method Javadoc.</li>
 * </ol>
 */
public final class EntityUtils {

    private EntityUtils() {} // non-instantiable

    // -------------------------------------------------------------------------
    // Entity type classification (formerly MobType / EnumCreatureAttribute)
    // -------------------------------------------------------------------------

    /**
     * Returns true if the entity is undead.
     * <p>
     * <b>1.21.1+:</b> Checks entity type tag {@code #minecraft:undead} — the same
     * tag that drives the Smite enchantment. Includes zombies, skeletons, phantoms,
     * wither, wither skeletons, drowned, husks, strays, zombie villagers, zombie
     * piglins, and zoglins.
     * <p>
     * <b>Pre-1.21:</b> Replace with:
     * {@code entity.getMobType() == net.minecraft.world.entity.MobType.UNDEAD}
     *
     * @param entity the entity to test
     * @return true if the entity is classified as undead
     */
    public static boolean isUndead(LivingEntity entity) {
        return entity.getType().is(EntityTypeTags.UNDEAD);
    } // isUndead ()

    /**
     * Returns true if the entity is aquatic.
     * <p>
     * <b>1.21.1+:</b> Checks entity type tag {@code #minecraft:aquatic}.
     * <p>
     * <b>Pre-1.21:</b> Replace with:
     * {@code entity.getMobType() == net.minecraft.world.entity.MobType.WATER}
     *
     * @param entity the entity to test
     * @return true if the entity is classified as aquatic
     */
    public static boolean isAquatic(LivingEntity entity) {
        return entity.getType().is(EntityTypeTags.AQUATIC);
    } // isAquatic ()

    /**
     * Returns true if the entity is an arthropod.
     * <p>
     * <b>1.21.1+:</b> Checks entity type tag {@code #minecraft:arthropod}.
     * <p>
     * <b>Pre-1.21:</b> Replace with:
     * {@code entity.getMobType() == net.minecraft.world.entity.MobType.ARTHROPOD}
     *
     * @param entity the entity to test
     * @return true if the entity is classified as an arthropod
     */
    public static boolean isArthropod(LivingEntity entity) {
        return entity.getType().is(EntityTypeTags.ARTHROPOD);
    } // isArthropod ()

    /**
     * Returns true if the entity is an illager.
     * <p>
     * <b>1.21.1+:</b> Checks entity type tag {@code #minecraft:illager}.
     * <p>
     * <b>Pre-1.21:</b> Replace with:
     * {@code entity.getMobType() == net.minecraft.world.entity.MobType.ILLAGER}
     *
     * @param entity the entity to test
     * @return true if the entity is classified as an illager
     */
    public static boolean isIllager(LivingEntity entity) {
        return entity.getType().is(EntityTypeTags.ILLAGER);
    } // isIllager ()

    /**
     * Returns true if the entity's type belongs to the given entity type tag.
     * <p>
     * <b>Custom tags:</b> Pass any {@code TagKey<EntityType<?>>} — vanilla or mod-defined.
     * Custom tags are registered via {@code data/<namespace>/tags/entity_type/<name>.json}.
     * <pre>{@code
     * // Define a custom tag key
     * public static final TagKey<EntityType<?>> FEY = TagKey.create(
     *     Registries.ENTITY_TYPE,
     *     ResourceLocation.fromNamespaceAndPath("monsters_girls", "fey"));
     *
     * // Use it in a condition
     * EmanationConditions.targetHasTag(MonstersEntityTags.FEY)
     * }</pre>
     *
     * @param entity the entity to test
     * @param tag    the entity type tag to check membership for
     * @return true if the entity's type is in the tag
     */
    public static boolean hasTag(LivingEntity entity,
            TagKey<EntityType<?>> tag) {
        return entity.getType().is(tag);
    } // hasTag ()

    // -------------------------------------------------------------------------
    // Combat mutations
    // -------------------------------------------------------------------------

    /**
     * Sets the entity on fire for the given number of seconds.
     * <p>
     * <b>1.21.1+:</b> {@code entity.igniteForSeconds(seconds)}.
     * <p>
     * <b>1.20.x and earlier:</b> Replace with:
     * {@code entity.setSecondsOnFire(seconds)}
     *
     * @param entity  the entity to ignite
     * @param seconds duration in seconds
     */
    public static void ignite(LivingEntity entity, int seconds) {
        entity.igniteForSeconds(seconds);
    } // ignite ()

} // Class: EntityUtils
