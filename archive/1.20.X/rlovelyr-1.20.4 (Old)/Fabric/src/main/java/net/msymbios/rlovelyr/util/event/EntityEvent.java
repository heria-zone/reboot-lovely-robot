package net.msymbios.rlovelyr.util.event;

import net.minecraft.entity.mob.MobEntity;

/**
 * An event relating to an entity.
 */
public class EntityEvent<T extends MobEntity> extends HandleableEvent {

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