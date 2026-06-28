package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * <p>Typed NBT field handle — the single source of truth for one entity data field.<p>
 * <p>
 * <b>Design:</b> The NBT key is encapsulated here. Call sites reference the static
 * constant directly ({@code RobotFields.LEVEL}) — no string literals outside the
 * field declaration. A rename means changing one constant; the compiler rejects
 * every stale reference.
 * <p>
 * <b>No migrator field:</b> Per ADR 019, migration is a cross-field concern owned
 * exclusively by {@link MigrationChain}. A per-field migrator cannot handle cases
 * like belly/texture disambiguation that require reading two fields simultaneously.
 * <p>
 * <b>Validation:</b> On read, values that fail the optional predicate fall back to
 * {@code defaultValue} and log a warning — corrupt NBT never crashes the entity.
 *
 * @param <T> the Java type this field serialises to
 */
public final class DataField<T> {

    // -- Fields --

    private final String        key;
    private final DataType<T>   type;
    private final T             defaultValue;
    @Nullable
    private final Predicate<T>  validator;

    // -- Constructors --

    private DataField(@NotNull String key, @NotNull DataType<T> type,
                      @NotNull T defaultValue, @Nullable Predicate<T> validator) {
        this.key          = Objects.requireNonNull(key,          "Field key cannot be null");
        this.type         = Objects.requireNonNull(type,         "DataType cannot be null");
        this.defaultValue = Objects.requireNonNull(defaultValue, "Default value cannot be null");
        this.validator    = validator;
    } // Constructor: DataField ()

    // -- Factory Methods --

    /** Creates a field with no validation — any read value is accepted as-is. */
    @NotNull
    public static <T> DataField<T> of(@NotNull String key, @NotNull DataType<T> type,
                                      @NotNull T defaultValue) {
        return new DataField<>(key, type, defaultValue, null);
    } // of ()

    /** Creates a field with a validation predicate. Invalid reads fall back to {@code defaultValue}. */
    @NotNull
    public static <T> DataField<T> of(@NotNull String key, @NotNull DataType<T> type,
                                      @NotNull T defaultValue, @NotNull Predicate<T> validator) {
        return new DataField<>(key, type, defaultValue, validator);
    } // of ()

    // -- Pipeline Operations --

    /**
     * Reads this field from the compound.
     * <p>
     * If the key is absent, returns {@code defaultValue}. If the key is present but
     * the value fails the validator, returns {@code defaultValue} and logs a warning
     * — corrupt NBT never propagates into entity state.
     *
     * @param compound the {@link DataCompound} to read from
     * @return valid field value, or {@code defaultValue}
     */
    @NotNull
    public T readFrom(@NotNull DataCompound compound) {
        if (!compound.has(key)) return defaultValue;

        T value = type.reader.read(compound, key);
        if (value == null) return defaultValue;

        if (validator != null && !validator.test(value)) {
            LoggerFactory.getLogger("HZLib-DataField").warn(
                "[HZLib] Field '{}' value '{}' failed validation — using default '{}'",
                key, value, defaultValue);
            return defaultValue;
        }
        return value;
    } // readFrom ()

    /**
     * Writes {@code value} to the compound under this field's key.
     *
     * @param compound the {@link DataCompound} to write to
     * @param value    the value to write
     */
    public void writeTo(@NotNull DataCompound compound, @NotNull T value) {
        type.writer.write(compound, key, value);
    } // writeTo ()

    // -- Accessors --

    /**
     * Returns the NBT key this field is stored under.
     * <p>
     * Package-private intentionally — pipeline code ({@link EntityDataSchema},
     * {@link MigrationChain}) reads the key for format detection. Call sites must
     * reference the {@link DataField} constant, never the raw key string.
     */
    @NotNull
    String getKey() { return key; } // getKey ()

    /** Returns the {@link DataType} governing read/write behaviour. */
    @NotNull
    public DataType<T> getType() { return type; } // getType ()

    /** Returns the value used when the key is absent or fails validation. */
    @NotNull
    public T getDefaultValue() { return defaultValue; } // getDefaultValue ()

} // Class: DataField
