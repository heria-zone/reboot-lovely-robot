package net.heriazone.hzlib.api.entity;

import net.heriazone.hzlib.api.entity.features.BoneRule;
import net.heriazone.hzlib.api.entity.features.BoneVisibilityFeature;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
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
public abstract class NativeModel<T extends NativeEntity & GeoEntity> extends GeoModel<T> {

    // -- Resource Resolution --

    /**
     * Retrieves model resource location from entity's current state.
     * <p>
     * <b>Architecture:</b> Model can change based on entity state (e.g., armed vs
     * unarmed). Entity determines appropriate model, renderer just displays it.
     *
     * @param animatable robot entity being rendered
     * @return ResourceLocation pointing to .geo.json model file
     */
    @Override
    public ResourceLocation getModelResource(T animatable) {
        return animatable.getCurrentModel();
    } // getModelResource()

    /**
     * Retrieves texture resource location from entity's variant and color.
     * <p>
     * <b>Architecture:</b> Texture determined by entity's variant type and selected
     * color. Allows 16+ color variants per robot type without separate model classes.
     *
     * @param animatable robot entity being rendered
     * @return ResourceLocation pointing to texture PNG file
     */
    @Override
    public ResourceLocation getTextureResource(T animatable) {
        return animatable.getCurrentTexture();
    } // getTextureResource()

    /**
     * Retrieves animation resource location from entity's animator configuration.
     * <p>
     * <b>Architecture:</b> Animation controllers defined in .animation.json files.
     * Entity specifies which animator to use, enabling different animation sets per
     * robot type.
     *
     * @param animatable robot entity being rendered
     * @return ResourceLocation pointing to .animation.json file
     */
    @Override
    public ResourceLocation getAnimationResource(T animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource()

    // -- Custom Animations --

    /**
     * Applies custom bone transformations for head tracking and declarative bone visibility.
     * <p>
     * <b>Architecture:</b> Two concerns handled in sequence each render frame:
     * <ol>
     *   <li><b>Head tracking</b> — delegates to {@link NativeAnimation#headAnimation} using
     *       the head bone name resolved from the entity's family descriptor via
     *       {@link #resolveHeadBoneName}. Defaults to {@code "head"} for families that
     *       do not override {@link net.heriazone.hzlib.api.entity.NativeEntityFamily#headBone}.</li>
     *   <li><b>Bone visibility</b> — evaluates the {@link BoneVisibilityFeature} declared on the
     *       entity's family, if present. Each {@link BoneRule} is tested and the corresponding
     *       {@code GeoBone.setHidden()} call is made here in the loader where GeckoLib types are
     *       available. No reflection — conditions are pure-Java lambdas declared in Common.</li>
     * </ol>
     * <p>
     * <b>Performance:</b> Runs every render frame. Both operations are O(n) over small lists
     * (typically &lt;10 rules). Conditions must be allocation-free; see {@link BoneRule}.
     *
     * @param animatable robot entity being rendered
     * @param instanceId unique instance identifier for this render
     * @param event      animation state with entity data and tick information
     */
    @Override
    public void setCustomAnimations(T animatable, long instanceId, AnimationState<T> event) {
        NativeAnimation.headAnimation(this, event, resolveHeadBoneName(animatable));
        if (animatable.nativeEntity != null) {
            animatable.nativeEntity.getFeature(BoneVisibilityFeature.class)
                    .ifPresent(feature -> applyBoneVisibility(feature, animatable));
        }
    } // setCustomAnimations()

    // -- Head Bone Resolution --

    /**
     * Resolves the head bone name for the given entity from its family descriptor.
     * <p>
     * Reads {@link net.heriazone.hzlib.api.entity.NativeEntityFamily#getHeadBoneName()}
     * when the entity has a family reference, and falls back to {@code "head"} otherwise.
     * The fallback covers edge cases where {@code nativeEntity} is null before the entity
     * is fully initialised.
     *
     * @param animatable the entity being rendered
     * @return bone name string; never {@code null}
     */
    private String resolveHeadBoneName(T animatable) {
        return (animatable.nativeEntity != null)
                ? animatable.nativeEntity.getHeadBoneName()
                : "head";
    } // resolveHeadBoneName()

    // -- Bone Visibility --

    /**
     * Evaluates every {@link BoneRule} in the given feature and applies the resulting
     * hide/show state to the corresponding {@link GeoBone} in this model.
     * <p>
     * <b>Null-safety:</b> If a bone name declared in a rule does not exist in the loaded
     * {@code .geo.json} model, {@code getBone()} returns {@code null} and that rule is
     * silently skipped. This allows families to declare rules for bones that only exist
     * in some model variants.
     * <p>
     * <b>Polarity:</b> A rule with {@code hideWhenTrue = true} hides the bone when the
     * condition passes and shows it when the condition fails. A rule with
     * {@code hideWhenTrue = false} shows the bone when the condition passes and hides it
     * when the condition fails.
     *
     * @param feature    the bone visibility feature retrieved from the entity's family
     * @param animatable the entity being rendered, passed through to each condition
     */
    private void applyBoneVisibility(BoneVisibilityFeature feature, T animatable) {
        for (BoneRule rule : feature.getRules()) {
            GeoBone bone = getAnimationProcessor().getBone(rule.boneName());
            if (bone == null) continue;
            boolean conditionMet = rule.condition().test(animatable);
            // hideWhenTrue=true  → setHidden(true)  when conditionMet; setHidden(false) when not
            // hideWhenTrue=false → setHidden(false) when conditionMet; setHidden(true)  when not
            bone.setHidden(rule.hideWhenTrue() == conditionMet);
        }
    } // applyBoneVisibility()

} // Class: NativeModel