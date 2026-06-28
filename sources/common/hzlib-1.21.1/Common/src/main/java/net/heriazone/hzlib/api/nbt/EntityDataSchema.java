package net.heriazone.hzlib.api.nbt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>Ordered registry of {@link DataField} handles for one entity family.<p>
 * <p>
 * <b>Architecture:</b> Declared once on the family descriptor via
 * {@link net.heriazone.hzlib.api.entity.NativeEntityFamily#configureSchema()}.
 * The entity's {@code addAdditionalSaveData} and {@code readAdditionalSaveData}
 * delegate all field I/O to this schema — no field key literals appear anywhere
 * in entity code.
 * <p>
 * <b>Write path:</b> {@link #writeTo} iterates the registered fields in declaration
 * order and calls the entity's {@link FieldValueProvider} for each. The provider
 * returns the current live value by field handle — zero string keys at the call site.
 * <p>
 * <b>Read path:</b> {@link #readFrom} iterates the same fields, reads and validates
 * each value, then delivers it to the entity's {@link FieldValueConsumer}. Invalid
 * values fall back to their declared defaults (see {@link DataField}).
 * <p>
 * <b>Version headers:</b> The schema writes {@code SchemaVersion} and {@code McVersion}
 * headers into the compound before any field. These are written here, not by the entity,
 * so the entity cannot accidentally omit them.
 */
public final class EntityDataSchema {

    // -- Constants --

    static final String KEY_SCHEMA_VERSION = "SchemaVersion";
    static final String KEY_MC_VERSION     = "McVersion";

    // -- Fields --

    private final List<DataField<?>> fields;
    private final String             schemaVersion;

    // -- Constructor --

    private EntityDataSchema(@NotNull List<DataField<?>> fields, @NotNull String schemaVersion) {
        this.fields        = Collections.unmodifiableList(new ArrayList<>(fields));
        this.schemaVersion = schemaVersion;
    } // Constructor: EntityDataSchema ()

    // -- Pipeline Operations --

    /**
     * Writes all registered fields and the two version headers to {@code entityDataCompound}.
     * <p>
     * Call from {@code NativeEntity.addAdditionalSaveData} after wrapping the root tag and
     * obtaining (or creating) the {@code "EntityData"} sub-compound via
     * {@link DataCompound#getOrCreate}.
     *
     * @param entityDataCompound the {@code "EntityData"} sub-compound, not the root
     * @param provider           entity-side value supplier (receives each field handle)
     */
    public void writeTo(@NotNull DataCompound entityDataCompound,
                        @NotNull FieldValueProvider provider) {
        Objects.requireNonNull(entityDataCompound, "EntityData compound cannot be null");
        Objects.requireNonNull(provider, "FieldValueProvider cannot be null");

        entityDataCompound.putString(KEY_SCHEMA_VERSION, schemaVersion);
        entityDataCompound.putString(KEY_MC_VERSION, McVersionProvider.current());

        for (DataField<?> field : fields) {
            writeField(entityDataCompound, field, provider);
        }
    } // writeTo ()

    /**
     * Reads all registered fields from {@code entityDataCompound} and delivers them
     * to the entity's consumer.
     * <p>
     * Missing keys use the field's declared default. Values that fail validation use
     * the default and log a warning (see {@link DataField#readFrom}).
     *
     * @param entityDataCompound the {@code "EntityData"} sub-compound, not the root
     * @param consumer           entity-side value receiver (receives field + validated value)
     */
    public void readFrom(@NotNull DataCompound entityDataCompound,
                         @NotNull FieldValueConsumer consumer) {
        Objects.requireNonNull(entityDataCompound, "EntityData compound cannot be null");
        Objects.requireNonNull(consumer, "FieldValueConsumer cannot be null");

        for (DataField<?> field : fields) {
            readField(entityDataCompound, field, consumer);
        }
    } // readFrom ()

    // -- Private Helpers --

    @SuppressWarnings("unchecked")
    private <T> void writeField(DataCompound compound, DataField<T> field,
                                 FieldValueProvider provider) {
        T value = provider.provide(field);
        if (value != null) field.writeTo(compound, value);
    } // writeField ()

    @SuppressWarnings("unchecked")
    private <T> void readField(DataCompound compound, DataField<T> field,
                                FieldValueConsumer consumer) {
        T value = field.readFrom(compound);
        consumer.consume(field, value);
    } // readField ()

    // -- Accessors --

    /** Returns the schema version string written into every save. */
    @NotNull
    public String getVersion() { return schemaVersion; } // getVersion ()

    /** Returns an unmodifiable view of all registered fields in declaration order. */
    @NotNull
    public List<DataField<?>> getFields() { return fields; } // getFields ()

    /**
     * Returns {@code true} if the given compound contains both version headers —
     * used by {@link MigrationChain} to distinguish the current format from legacy.
     */
    public static boolean isCurrentFormat(@NotNull DataCompound entityDataCompound) {
        return entityDataCompound.has(KEY_SCHEMA_VERSION)
                && entityDataCompound.has(KEY_MC_VERSION);
    } // isCurrentFormat ()

    /**
     * Returns {@code true} if the compound has a {@code SchemaVersion} but no
     * {@code McVersion} — indicates a pre-McVersion save that still uses the
     * structured format introduced before McVersion was added.
     */
    public static boolean isPreMcVersionFormat(@NotNull DataCompound entityDataCompound) {
        return entityDataCompound.has(KEY_SCHEMA_VERSION)
                && !entityDataCompound.has(KEY_MC_VERSION);
    } // isPreMcVersionFormat ()

    // -- Builder --

    /** Returns a new builder for composing a schema. */
    @NotNull
    public static Builder builder() { return new Builder(); } // builder ()

    /**
     * Fluent builder for {@link EntityDataSchema}.
     * <p>
     * Register fields in the order they should be written to NBT.
     * Declare {@code .version()} once — it is written as a header before all fields.
     */
    public static final class Builder {

        private final List<DataField<?>> fields        = new ArrayList<>();
        private String                   schemaVersion = "1.0.0";

        private Builder() {}

        /**
         * Registers a field in the schema.
         * Fields are written and read in the order they are registered.
         *
         * @param field typed field handle
         * @return this builder
         */
        public Builder register(@NotNull DataField<?> field) {
            Objects.requireNonNull(field, "DataField cannot be null");
            fields.add(field);
            return this;
        } // register ()

        /**
         * Sets the schema version string written as the {@code SchemaVersion} header.
         * Default is {@code "1.0.0"}.
         *
         * @param version schema version string
         * @return this builder
         */
        public Builder version(@NotNull String version) {
            this.schemaVersion = Objects.requireNonNull(version, "Version cannot be null");
            return this;
        } // version ()

        /** Builds the immutable {@link EntityDataSchema}. */
        @NotNull
        public EntityDataSchema build() {
            return new EntityDataSchema(fields, schemaVersion);
        } // build ()

    } // Class: Builder

} // Class: EntityDataSchema
