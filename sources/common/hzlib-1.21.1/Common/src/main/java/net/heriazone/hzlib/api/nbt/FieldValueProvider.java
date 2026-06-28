package net.heriazone.hzlib.api.nbt;

/**
 * <p>Entity-side write accessor: maps a {@link DataField} to its current live value.<p>
 * <p>
 * <b>Contract:</b> Called once per field during {@link EntityDataSchema#writeTo}.
 * Implementations must return a non-null value matching the field's {@link DataType}.
 * Return the field's {@link DataField#getDefaultValue()} if the entity has no
 * meaningful value for that field.
 * <p>
 * <b>No string keys:</b> The method receives the typed {@link DataField} constant —
 * not a string key. Mismatched fields fail at compile time, not silently at runtime.
 */
@FunctionalInterface
public interface FieldValueProvider {

    /**
     * Returns the entity's current value for {@code field}.
     *
     * @param <T>   the field's value type
     * @param field typed handle identifying the field
     * @return current value — must not be null
     */
    <T> T provide(DataField<T> field);

} // Interface: FieldValueProvider
