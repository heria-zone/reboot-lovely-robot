package net.msymbios.rlovelyr.entity.custom;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RobotEntity extends TameableEntity {

    // -- Variables --
    public boolean isSitting;
    public RobotState currentState = RobotState.Standby;
    public RobotState debugState = RobotState.Standby;

    // -- Constructor --
    protected RobotEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    } // Constructor RobotEntity ()

    // -- Methods --
    @Override
    protected void initGoals() {
        //this.goalSelector.add(8, new WanderAroundFarGoal(this, 1.0));
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(1, new LookAroundGoal(this));
    } // initGoals ()

    protected void initDataTracker() {
        super.initDataTracker();
    } // initDataTracker ()

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);

        if (this.world.isClient) {
            if(hand == Hand.MAIN_HAND) {
                ToggleSitState(itemStack);

                StandbyMode(itemStack);
                TrackMode(itemStack);

                HuntMode(itemStack);
                GuardMode(itemStack);

                if(debugState != currentState) {
                    player.sendMessage(Text.literal("State: " + this.currentState.toString()));
                    debugState = currentState;
                }
            }
        }

        return ActionResult.SUCCESS;
    } // interactMob ()

    @Nullable @Override
    public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
        return null;
    } // createChild

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_DOLPHIN_AMBIENT;
    } // getAmbientSound ()

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_DOLPHIN_HURT;
    } // getHurtSound ()

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_PIG_DEATH;
    } // getDeathSound ()

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.ENTITY_PIG_STEP, 0.15f, 1.0f);
    } // playStepSound ()


    public void ToggleSitState (ItemStack itemStack) {
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE)) return;
        isSitting = InvertBoolean(isSitting);
    } // ToggleSitState ()

    public void StandbyMode(ItemStack itemStack){
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE)) return;
        if(isSitting) currentState = RobotState.Standby;
    } // StandbyMode ()

    public void TrackMode(ItemStack itemStack){
        if(itemStack.isOf(Items.PINK_DYE) || itemStack.isOf(Items.YELLOW_DYE) || itemStack.isOf(Items.LIGHT_BLUE_DYE) || itemStack.isOf(Items.BLACK_DYE)) return;
        if(!isSitting) currentState = RobotState.Track;
    } // TrackMode ()

    public void HuntMode(ItemStack itemStack){
        if(itemStack.isOf(Items.WOODEN_SWORD) || itemStack.isOf(Items.STONE_SWORD) || itemStack.isOf(Items.IRON_SWORD) || itemStack.isOf(Items.GOLDEN_SWORD) || itemStack.isOf(Items.DIAMOND_SWORD) || itemStack.isOf(Items.NETHERITE_SWORD)) {
            isSitting = false;
            currentState = RobotState.Hunt;
        }
    } // HuntMode ()

    public void GuardMode(ItemStack itemStack){
        if(!itemStack.isOf(Items.COMPASS) && !itemStack.isOf(Items.RECOVERY_COMPASS)) return;
        isSitting = false;
        currentState = RobotState.Guard;
    } // GuardMode ()

    public static int getRandomNumber(int number) {
        return Random.createLocal().nextInt(number);
    } // getRandomNumber ()

    public boolean InvertBoolean(boolean value) {
        return value = value ^ true;
    } // InvertBoolean ()

    public enum RobotState {
        Standby,
        Guard,
        Track,
        Hunt
    } // Enum RobotState

} // Class RobotEntity