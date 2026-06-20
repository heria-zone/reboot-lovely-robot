package net.heriazone.hzlib.api.entity.internal;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

/**
 * Static utilities for attribute mutation and owner message display.
 * <p>
 * Centralises the two concerns that appear repeatedly across entity tiers:
 * pushing recalculated stat values onto live Minecraft attributes, and sending
 * status messages to the entity's owner. Neither concern belongs on a specific
 * entity class — extracting them here keeps entity code free of boilerplate.
 */
public class EntityLogic { // TODO: Replace handleLevel with CombatLevelFeature and ProtectionFeature

    // -- Attribute Mutation --

    /**
     * Pushes recalculated stat values onto the entity's live attributes.
     * Call after any stat change that should take effect immediately.
     *
     * @param entity        the entity to update
     * @param hp            new max health
     * @param attack        new attack damage
     * @param armor         new armor value
     * @param armorToughness new armor toughness value
     */
    public static void handleLevel(LivingEntity entity, int hp, int attack, double armor, double armorToughness) {
        updateEntityAttribute(entity, Attributes.MAX_HEALTH, hp);
        updateEntityAttribute(entity, Attributes.ATTACK_DAMAGE, attack);
        updateEntityAttribute(entity, Attributes.ARMOR, armor);
        updateEntityAttribute(entity, Attributes.ARMOR_TOUGHNESS, armorToughness);
    } // handleLevel ()

    // int and double overloads — Minecraft's AttributeInstance.setBaseValue takes a double,
    // but call sites often produce ints; both avoid a cast at every call site.

    private static void updateEntityAttribute(LivingEntity entity, Holder<Attribute> attribute, int value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        assert instance != null;
        instance.setBaseValue(value);
    } // updateEntityAttribute ()

    private static void updateEntityAttribute(LivingEntity entity, Holder<Attribute> attribute, double value) {
        AttributeInstance instance = entity.getAttribute(attribute);
        assert instance != null;
        instance.setBaseValue(value);
    } // updateEntityAttribute ()

    // -- Owner Display --

    /**
     * Sends a message to the entity's owner.
     *
     * @param entity  the tameable entity whose owner receives the message
     * @param message the message component
     * @param overlay {@code true} to show on the action bar, {@code false} for chat
     */
    public static void displayInfo(TamableAnimal entity, Component message, boolean overlay) {
        if (entity.getOwner() instanceof Player player) {
            player.displayClientMessage(message, overlay);
        }
    } // displayInfo ()

    /** Convenience overload that wraps a plain string into a {@link Component}. */
    public static void displayInfo(TamableAnimal entity, String message, boolean overlay) {
        displayInfo(entity, Component.nullToEmpty(message), overlay);
    } // displayInfo ()

} // Class: EntityLogic