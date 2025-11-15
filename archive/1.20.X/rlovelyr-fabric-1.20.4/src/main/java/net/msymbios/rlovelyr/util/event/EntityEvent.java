package net.msymbios.rlovelyr.util.event;

import net.minecraft.world.entity.Mob;

/**
 * An event relating to an entity.
 */
public class EntityEvent<T extends Mob> extends HandleableEvent {

    // -- Variables --

    /**
     * The entity.
     */
    public final T entity;

    // -- Methods --
    /**
     * @param entity the entity.
     */
    public EntityEvent(T entity)
    {
        this.entity = entity;
    }

} // Class EntityEvent