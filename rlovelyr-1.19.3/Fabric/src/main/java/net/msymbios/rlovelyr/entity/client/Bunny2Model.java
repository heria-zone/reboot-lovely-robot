package net.msymbios.rlovelyr.entity.client;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.msymbios.rlovelyr.entity.custom.Bunny2Entity;
import net.msymbios.rlovelyr.entity.custom.BunnyEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class Bunny2Model extends GeoModel<Bunny2Entity> {

    // -- Methods --
    @Override
    public Identifier getModelResource(Bunny2Entity animatable) {
        return animatable.getCurrentModel();
    } // getModelResource ()

    @Override
    public Identifier getTextureResource(Bunny2Entity animatable) {
        return animatable.getTexture();
    } // getTextureResource ()

    @Override
    public Identifier getAnimationResource(Bunny2Entity animatable) {
        return animatable.getCurrentAnimator();
    } // getAnimationResource ()

    @Override
    public void setCustomAnimations(Bunny2Entity animatable, long instanceId, AnimationState<Bunny2Entity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");
        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    } // setCustomAnimations ()

} // Class Bunny2Model