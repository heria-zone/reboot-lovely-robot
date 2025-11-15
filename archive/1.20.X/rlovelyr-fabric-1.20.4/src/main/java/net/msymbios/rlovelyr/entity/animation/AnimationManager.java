package net.msymbios.rlovelyr.entity.animation;

import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.msymbios.rlovelyr.entity.animation.internal.Priority;
import net.msymbios.rlovelyr.entity.internal.InternalEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.Animation;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public final class AnimationManager {

    // -- Variables --
    private static AnimationManager MANAGER;
    private final ReferenceArrayList<AnimationStatus>[] data = new ReferenceArrayList[Priority.LOWEST + 1];

    // -- Constructor --
    public AnimationManager() {
        for (int i = 0; i < data.length; i++) {
            data[i] = new ReferenceArrayList<>(6);
        }
    } // Constructor AnimationManager ()

    // -- Methods --

    public static AnimationManager getInstance() {
        if (MANAGER == null)
            MANAGER = new AnimationManager();
        return MANAGER;
    } // getInstance ()

    public void register(AnimationStatus state) {
        data[state.getPriority()].add(state);
    } // register ()

    private static <P extends GeoAnimatable> PlayState playAnimation(AnimationState<P> event, String animationName, Animation.LoopType loopType) {
        event.getController().setAnimation(RawAnimation.begin().then(animationName, loopType));
        return PlayState.CONTINUE;
    } // playAnimation ()

    private static <P extends GeoAnimatable> PlayState playAnimation(AnimationState<P> event, String animationName) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay(animationName));
        return PlayState.CONTINUE;
    } // playAnimation ()

    public static <P extends GeoAnimatable> PlayState playLoopAnimation(AnimationState<P> event, String animationName) {
        return playAnimation(event, animationName, Animation.LoopType.LOOP);
    } // playLoopAnimation ()


    private boolean checkSwingAndUse(InternalEntity entity, Hand hand) {
        if (entity.handSwinging && entity.getActiveHand() == hand) return false;
        return !entity.isUsingItem() || entity.getActiveHand() != hand;
    } // checkSwingAndUse ()

    private boolean isSameItem(InternalEntity entity, ItemStack itemStack, Hand hand) {
        ItemStack preItem = entity.handItemsForAnimation[hand.ordinal()];
        if (preItem.isDamaged())
            return ItemStack.areItemsEqual(itemStack, preItem);
        return ItemStack.areEqual(itemStack, preItem);
    } // isSameItem ()


    public PlayState predicateParallel(AnimationState<? extends GeoAnimatable> event, String animationName) {
        if (MinecraftClient.getInstance().isPaused())
            return PlayState.STOP;
        return playLoopAnimation(event, animationName);
    } // predicateParallel ()

    public PlayState predicateMain(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity entity = (InternalEntity)event.getAnimatable();
        if (entity == null) return PlayState.STOP;

        for (int i = Priority.HIGHEST; i <= Priority.LOWEST; i++) {
            for (AnimationStatus state : data[i]) {
                if (state.getPredicate().test(entity, event)) {
                    String animationName = state.getAnimationName();
                    Animation.LoopType loopType = state.getLoopType();
                    return playAnimation(event, animationName, loopType);
                }
            }
        }
        return PlayState.STOP;
    } // predicateMain ()

    public PlayState predicateSwing(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity entity = (InternalEntity)event.getAnimatable();
        if (entity == null) return PlayState.STOP;

        if (entity.handSwinging && !entity.isSleeping()) {
            if (entity.handSwingTicks == 0) playAnimation(event, "attack", Animation.LoopType.PLAY_ONCE);

            /*Identifier id = entity.getAnimator();
            ConditionalSwing conditionalSwing = (entity.swingingArm == InteractionHand.MAIN_HAND) ? ConditionManager.getSwingMainhand(id) : ConditionManager.getSwingOffhand(id);
            if (conditionalSwing != null) {
                String name = conditionalSwing.doTest(entity, entity.swingingArm);
                if (StringUtils.isNoneBlank(name)) {
                    return playAnimation(event, name, ILoopType.EDefaultLoopTypes.PLAY_ONCE);
                }
            }*/
            //String defaultSwing = (entity.swingingArm == InteractionHand.MAIN_HAND) ? "swing_hand" : "swing_offhand";
            return playAnimation(event, "attack", Animation.LoopType.PLAY_ONCE);
        }
        return PlayState.CONTINUE;
    } // predicateSwing ()

    public PlayState predicateUse(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity entity = (InternalEntity)event.getAnimatable();
        if (entity == null) return PlayState.STOP;

        if (entity.isUsingItem() && !entity.isSleeping()) {
            if (entity.getItemUseTime() == 1) playAnimation(event, "empty", Animation.LoopType.PLAY_ONCE);

            if (entity.getActiveHand() == Hand.MAIN_HAND) {
                /*Identifier id = ((InternalEntity) event.getAnimatable()).getAnimator();
                ConditionalUse conditionalUse = ConditionManager.getUseMainhand(id);
                if (conditionalUse != null) {
                    String name = conditionalUse.doTest(entity, Hand.MAIN_HAND);
                    if (StringUtils.isNoneBlank(name)) {
                        return playAnimation(event, name, ILoopType.EDefaultLoopTypes.LOOP);
                    }
                }*/
                return playAnimation(event, "use_mainhand", Animation.LoopType.LOOP);
            } else {
                /*Identifier id = ((InternalEntity) event.getAnimatable()).getAnimator();
                ConditionalUse conditionalUse = ConditionManager.getUseOffhand(id);
                if (conditionalUse != null) {
                    String name = conditionalUse.doTest(entity, Hand.OFF_HAND);
                    if (StringUtils.isNoneBlank(name)) {
                        return playAnimation(event, name, Animation.LoopType.LOOP);
                    }
                }*/
                return playAnimation(event, "use_offhand", Animation.LoopType.LOOP);
            }
        }
        return PlayState.STOP;
    } // predicateUse ()

    public PlayState predicateBeg(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity entity = (InternalEntity)event.getAnimatable();
        if (entity == null) return PlayState.STOP;
        if (entity.isSitting()) return playAnimation(event, "sit", Animation.LoopType.LOOP);
        return PlayState.STOP;
    } // predicateBeg ()

    public PlayState predicateArmor(AnimationState<? extends GeoAnimatable> event, EquipmentSlot slot) {
        InternalEntity entity = (InternalEntity)event.getAnimatable();
        if (entity == null) return PlayState.STOP;

        ItemStack itemBySlot = entity.getEquippedStack(slot);
        if (itemBySlot.isEmpty()) return PlayState.STOP;

        /*Identifier id = ((InternalEntity) event.getAnimatable()).getAnimator();
        ConditionArmor conditionArmor = ConditionManager.getArmor(id);
        if (conditionArmor != null) {
            String name = conditionArmor.doTest(entity, slot);
            if (StringUtils.isNoneBlank(name)) {
                return playAnimation(event, name, ILoopType.EDefaultLoopTypes.LOOP);
            }
        }

        Identifier animation = ((InternalEntity) event.getAnimatable()).getAnimator();
        String defaultName = slot.getName() + ":default";
        if (GeckoLibCache.getInstance().getAnimations().get(animation).animations().containsKey(defaultName))
            return playAnimation(event, defaultName, ILoopType.EDefaultLoopTypes.LOOP);*/

        return PlayState.STOP;
    }

    /*public PlayState predicateOffhandHold(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity maid = (InternalEntity)event.getAnimatable();
        if (maid == null) return PlayState.STOP;

        if (!maid.isAttacking() && !maid.isUsingItem()) {
            ItemStack offhandItem = maid.getOffHandStack();
            if (offhandItem.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(offhandItem)) {
                return playAnimation(event, "hold_offhand:charged_crossbow", Animation.LoopType.LOOP);
            }
        }

        if (checkSwingAndUse(maid, Hand.OFF_HAND)) {
            ItemStack offhandItem = maid.getOffHandStack();
            if (!isSameItem(maid, offhandItem, Hand.OFF_HAND)) {
                maid.handItemsForAnimation[Hand.OFF_HAND.ordinal()] = offhandItem;
                playAnimation(event, "attack", Animation.LoopType.LOOP);
            }

            Identifier id = ((InternalEntity) event.getAnimatable()).getAnimator();
            ConditionalHold conditionalHold = ConditionManager.getHoldOffhand(id);
            if (conditionalHold != null) {
                String name = conditionalHold.doTest(maid, Hand.OFF_HAND);
                if (StringUtils.isNoneBlank(name)) {
                    return playAnimation(event, name, Animation.LoopType.LOOP);
                }
            }
        }
        return PlayState.STOP;
    } // predicateOffhandHold ()

    public PlayState predicateMainhandHold(AnimationState<? extends GeoAnimatable> event) {
        InternalEntity maid = (InternalEntity)event.getAnimatable();
        if (maid == null) return PlayState.STOP;

        if (!maid.handSwinging && !maid.isUsingItem()) {
            ItemStack mainHandItem = maid.getMainHandStack();
            if (mainHandItem.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(mainHandItem)) {
                return playAnimation(event, "hold_mainhand:charged_crossbow", Animation.LoopType.LOOP);
            }
        }

        if (checkSwingAndUse(maid, Hand.MAIN_HAND)) {
            ItemStack mainHandItem = maid.getMainHandStack();
            if (!isSameItem(maid, mainHandItem, Hand.MAIN_HAND)) {
                maid.handItemsForAnimation[Hand.MAIN_HAND.ordinal()] = mainHandItem;
                playAnimation(event, "attack", Animation.LoopType.LOOP);
            }

            Identifier id = ((InternalEntity) event.getAnimatable()).getAnimator();
            ConditionalHold conditionalHold = ConditionManager.getHoldMainhand(id);
            if (conditionalHold != null) {
                String name = conditionalHold.doTest(maid, Hand.MAIN_HAND);
                if (StringUtils.isNoneBlank(name)) {
                    return playAnimation(event, name, Animation.LoopType.LOOP);
                }
            }
        }
        return PlayState.STOP;
    } // predicateMainhandHold ()*/

} // Class AnimationManager