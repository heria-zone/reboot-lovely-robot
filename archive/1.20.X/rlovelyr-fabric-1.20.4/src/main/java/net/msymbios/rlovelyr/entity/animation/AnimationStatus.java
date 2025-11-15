package net.msymbios.rlovelyr.entity.animation;

import net.minecraft.util.math.MathHelper;
import net.msymbios.rlovelyr.entity.animation.internal.Priority;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;

import java.util.function.BiPredicate;

public class AnimationStatus {

    // -- Variables --
    private final String animationName;
    private final Animation.LoopType loopType;
    private final int priority;
    private final BiPredicate<InternalEntity, AnimationState<? extends GeoAnimatable>> predicate;

    // -- Constructor --
    public AnimationStatus(String animationName, Animation.LoopType loopType, int priority, BiPredicate<InternalEntity, AnimationState<? extends GeoAnimatable>> predicate) {
        this.animationName = animationName;
        this.loopType = loopType;
        this.priority = MathHelper.clamp(priority, Priority.HIGHEST, Priority.LOWEST);
        this.predicate = predicate;
    } // Constructor AnimationStatus ()

    // -- Methods --

    public BiPredicate<InternalEntity, AnimationState<? extends GeoAnimatable>> getPredicate() {
        return predicate;
    } // getPredicate ()

    public String getAnimationName() {
        return animationName;
    }

    public Animation.LoopType getLoopType() {
        return loopType;
    }

    public int getPriority() {
        return priority;
    }

} // Class AnimationStatus