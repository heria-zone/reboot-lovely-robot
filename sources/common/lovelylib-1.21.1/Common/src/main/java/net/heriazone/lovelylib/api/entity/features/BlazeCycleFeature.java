package net.heriazone.lovelylib.api.entity.features;

import net.heriazone.lovelylib.common.entity.enums.EntityTexture;

import java.util.List;
import java.util.Objects;

/**
 * Blaze Rod advances the robot's texture through a fixed palette in order, wrapping
 * at the end.
 * <p>
 * <b>Design:</b> Restricted-palette robots (Prime, Hyperion) cannot use the standard
 * dye-reaction handler because their texture IDs are not mapped to dye colors.
 * This feature replaces that handler for Blaze Rod only — the dispatch guard in
 * {@code RobotEntity.handleItemInteraction()} checks for this feature first, runs the
 * cycle, and returns early so the default handler never fires.
 * <p>
 * <b>Palette ownership:</b> The palette list is the same one declared in
 * {@code RobotEntityDefinition.Builder.palette()} — this feature holds a reference to it
 * so the cycle stays in sync with what the entity can actually render.
 * <p>
 * <b>Blaze Rod consumption:</b> The dispatch site (RobotEntity) is responsible for
 * consuming one Blaze Rod unless the player is in creative mode.
 */
public class BlazeCycleFeature {

    // -- Fields --

    private final EntityTexture       firstTexture;
    private final EntityTexture       lastTexture;
    private final List<EntityTexture> palette;

    // -- Constructor --

    /**
     * @param palette      the restricted palette in display order — must not be empty
     * @param firstTexture palette entry to wrap back to after lastTexture
     * @param lastTexture  palette entry after which cycling wraps to firstTexture
     */
    public BlazeCycleFeature(List<EntityTexture> palette,
                              EntityTexture firstTexture,
                              EntityTexture lastTexture) {
        this.palette      = Objects.requireNonNull(palette, "palette");
        this.firstTexture = Objects.requireNonNull(firstTexture, "firstTexture");
        this.lastTexture  = Objects.requireNonNull(lastTexture, "lastTexture");
    } // Constructor: BlazeCycleFeature()

    // -- Cycle Logic --

    /**
     * Returns the next texture in the cycle after {@code current}.
     * Wraps from {@code lastTexture} back to {@code firstTexture}.
     * If {@code current} is not in the palette, returns {@code firstTexture}.
     *
     * @param current the robot's active texture
     * @return the next texture to apply
     */
    public EntityTexture next(EntityTexture current) {
        int idx = palette.indexOf(current);
        if (idx < 0 || current == lastTexture) return firstTexture;
        return palette.get(idx + 1);
    } // next()

    // -- Accessors --

    public EntityTexture       getFirstTexture() { return firstTexture; }
    public EntityTexture       getLastTexture()  { return lastTexture; }
    public List<EntityTexture> getPalette()      { return palette; }

} // Class: BlazeCycleFeature
