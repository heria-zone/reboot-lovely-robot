package net.msymbios.rlovelyr.entity.internal;

import net.msymbios.rlovelyr.LovelyRobot;
import net.msymbios.rlovelyr.client.gui.LovelyRobotGuiHandler;
import net.msymbios.rlovelyr.entity.attribute.BlocklingAttributes;
import net.msymbios.rlovelyr.entity.skill.BlocklingSkills;
import net.msymbios.rlovelyr.util.ObjectUtil;
import net.msymbios.rlovelyr.util.interfaces.IReadWriteNBT;
import net.msymbios.rlovelyr.util.internal.Version;

public abstract class InterfaceEntity extends InternalEntity implements IReadWriteNBT {

    // -- Variables --
    /**
     * Handles opening screens and containers.
     */
    public final LovelyRobotGuiHandler guiHandler = new LovelyRobotGuiHandler(this);

    /**
     * Used to track whether the player has released crouch after interacting (changing blockling type).
     * This stops a player picking up a blockling immediately after changing its type by accident.
     * Should be replaced with a capability on the player to tell when they have stopped using an item.
     */
    private boolean hasPlayerResetCrouchBetweenInteractions = true;

    /**
     * The blockling's attribute manager (called stats because attributes is already thing in vanilla).
     */
    private final BlocklingAttributes stats = new BlocklingAttributes(this);

    /**
     * The blockling's skills manager.
     */
    private final BlocklingSkills skills = new BlocklingSkills(this);

    // -- Constructor --

    protected InterfaceEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);

        stats.initUpdateCallbacks();

        // Set up any values that are determined randomly here.
        // So that we can sync them up using read/writeSpawnData.
        if (!getWorld().isClient()) {
            stats.init();
        }

    } // Constructor InterfaceEntity ()

    // -- Inherited Methods --

    @Override
    public void tick() {
        super.tick();

        if (!getWorld().isClient) {
            if (!hasPlayerResetCrouchBetweenInteractions) {
                hasPlayerResetCrouchBetweenInteractions = !isTamed() || (getOwner() != null && !getOwner().isCrawling());
            }
        }

        skills.tick();

    } // tick ()

    @Override
    public void handleInteract(PlayerEntity player) {
        if (!getWorld().isClient()) {
            if (hasPlayerResetCrouchBetweenInteractions) guiHandler.openGui(player);
        }
    } // handleInteract ()

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putString("rlovelyr_version", LovelyRobot.VERSION.toString());
        writeToNBT(nbt);
        return super.writeNbt(nbt);
    } // writeNbt ()

    @Override
    public void readNbt(NbtCompound nbt) {
        Version version = ObjectUtil.coalesce(new Version(nbt.getString("rlovelyr_version")), LovelyRobot.VERSION);
        readFromNBT(nbt, version);
        super.readNbt(nbt);
    } // readNbt ()

    // IReadWriteNBT

    @Override
    public NbtCompound writeToNBT(NbtCompound nbt) {
        nbt.put("attributes", stats.writeToNBT());
        nbt.put("skills", skills.writeToNBT());

        return nbt;
    } // writeToNBT ()

    @Override
    public void readFromNBT(NbtCompound nbt, Version version) {
        NbtCompound statsTag = nbt.getCompound("attributes");
        if (statsTag != null) stats.readFromNBT(statsTag, version);

        NbtCompound skillsTag = nbt.getCompound("skills");
        if (skillsTag != null) skills.readFromNBT(skillsTag, version);
        //stats.updateTypeBonuses(false);
    } // readFromNBT ()

    // -- Custom Methods --

    /**
     * @return the blockling's attribute manager.
     */
    public BlocklingAttributes getStats() {
        return stats;
    } // getStats ()

    /**
     * @return the blockling's skill manager.
     */
    public BlocklingSkills getSkills() {
        return skills;
    } // getSkills ()

} // InterfaceEntity