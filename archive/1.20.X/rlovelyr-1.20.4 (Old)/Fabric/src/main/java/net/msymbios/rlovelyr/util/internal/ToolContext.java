package net.msymbios.rlovelyr.util.internal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.msymbios.rlovelyr.util.enums.ToolType;

/**
 * Represents the context in which a tool is being used
 */
public class ToolContext {

    // -- Variables --

    /**
     * The type of tool.
     */
    public final ToolType toolType;

    /**
     * The target entity (should be set if tool type is a weapon).
     */
    public final LivingEntity entity;

    /**
     * The target block state (should be set if tool type is not a weapon).
     */
    public final BlockState blockState;

    // -- Constructor --

    /**
     * @param toolType the type of tool.
     * @param entity   the target entity (should be set if tool type is a weapon).
     */
    public ToolContext(ToolType toolType, LivingEntity entity) {
        this(toolType, entity, null);
    } // Constructor ToolContext ()

    /**
     * @param toolType   the type of tool.
     * @param blockState the target block state (should be set if tool type is not a weapon).
     */
    public ToolContext(ToolType toolType, BlockState blockState) {
        this(toolType, null, blockState);
    } // Constructor ToolContext ()

    /**
     * @param toolType   the type of tool.
     * @param entity     the target entity (should be set if tool type is a weapon).
     * @param blockState the target block state (should be set if tool type is not a weapon).
     */
    private ToolContext(ToolType toolType, LivingEntity entity, BlockState blockState) {
        this.toolType = toolType;
        this.entity = entity;
        this.blockState = blockState;
    } // Constructor ToolContext ()

} // Class ToolContext