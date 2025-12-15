package net.msymbios.llovelyr.common.entity.internal;

import net.minecraft.util.Identifier;
import net.msymbios.llovelyr.source.entity.common.LovelyRobotEntity;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

/**
 * Base GeckoLib model class for robot entity rendering.
 * <p>
 * <b>Architecture:</b> Abstracts GeckoLib's model system to automatically retrieve
 * model, texture, and animation resources from robot entity data. Eliminates need
 * for robot-specific model classes when using standard resource patterns.
 * <p>
 * <b>Design Decision:</b> Resource locations are retrieved from entity rather than
 * hardcoded, allowing dynamic model/texture switching based on entity state (armed
 * vs unarmed, different color variants).
 */
public abstract class InternalModel<T extends LovelyRobotEntity & GeoEntity> extends GeoModel<T> {

    // -- Resource Resolution --

    /**
     * Retrieves model resource location from entity's current state.
     * <p>
     * <b>Architecture:</b> Model can change based on entity state (e.g., armed vs
     * unarmed). Entity determines appropriate model, renderer just displays it.
     * 
     * @param animatable robot entity being rendered
     * @return Identifier pointing to .geo.json model file
     */
    @Override
    public Identifier getModelResource(T animatable) {
        return animatable.getCurrentModel();
    } // getModelResource()

    /**
     * Retrieves texture resource location from entity's variant and color.
     * <p>
     * <b>Architecture:</b> Texture determined by entity's variant type and selected
     * color. Allows 16+ color variants per robot type without separate model classes.
     * 
     * @param animatable robot entity being rendered
     * @return Identifier pointing to texture PNG file
     */
    @Override
    public Identifier getTextureResource(T animatable) {
        return animatable.getTexture();
    } // getTextureResource()

    /**
     * Retrieves animation resource location from entity's animator configuration.
     * <p>
     * <b>Architecture:</b> Animation controllers defined in .animation.json files.
     * Entity specifies which animator to use, enabling different animation sets per
     * robot type.
     * 
     * @param animatable robot entity being rendered
     * @return Identifier pointing to .animation.json file
     */
    @Override
    public Identifier getAnimationResource(T animatable) {
        return animatable.getAnimator();
    } // getAnimationResource()

    // -- Custom Animations --

    /**
     * Applies custom bone transformations for natural head tracking.
     * <p>
     * <b>Architecture:</b> Called every render frame after standard animations apply.
     * Allows procedural bone manipulation for effects like head tracking, tail physics,
     * or breathing animations.
     * <p>
     * <b>Performance:</b> Runs every frame, keep transformations lightweight. Heavy
     * calculations should be cached in entity tick logic.
     * 
     * @param animatable robot entity being rendered
     * @param instanceId unique instance identifier for this render
     * @param event animation state with entity data and tick information
     */
    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event) {
        InternalAnimation.headAnimation(this, event);
    } // setCustomAnimations()

} // Class: InternalModel
