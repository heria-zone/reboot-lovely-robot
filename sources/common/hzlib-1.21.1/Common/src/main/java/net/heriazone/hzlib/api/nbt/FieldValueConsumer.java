package net.heriazone.hzlib.api.nbt;

/**
 * <p>Entity-side read accessor: receives a loaded field value and applies it to entity state.<p>
 * <p>
 * <b>Contract:</b> Called once per field during {@link EntityDataSchema#readFrom}.
 * Implementations must apply the value to both the entity's logical state and its
 * corresponding {@code SynchedEntityData} accessor where one exists — NBT is
 * authoritative on load, not {@code SynchedEntityData}.
 * <p>
 * <b>No string keys:</b> The method receives the typed {@link DataField} constant.
 * Mismatched fields fail at compile time.
 */
@FunctionalInterface
public interface FieldValueConsumer {

    /**
     * Applies the loaded {@code value} for {@code field} to the entity.
     *
     * @param <T>   the field's value type
     * @param field typed handle identifying the field
     * @param value loaded and validated value from NBT
     */
    <T> void consume(DataField<T> field, T value);

} // Interface: FieldValueConsumer
