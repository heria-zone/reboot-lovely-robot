package net.heriazone.hzlib.api.nbt;

import java.util.UUID;

/**
 * <p>Typed token identifying how a {@link DataField} is stored and read back.<p>
 * <p>
 * <b>Design:</b> Each constant carries the Java class it serialises to and the
 * {@link DataCompound} read/write operations for that type — keeping the type
 * dispatch in one place rather than scattered across every field declaration.
 * <p>
 * <b>UUID:</b> Delegates to {@link DataCompound#putUUID}/{@link DataCompound#getUUID},
 * which handle the version-specific two-long vs. IntArray encoding internally.
 * Callers never branch on MC version to persist a UUID.
 */
public final class DataType<T> {

    // -- Constants --

    public static final DataType<Integer> INT     = new DataType<>(Integer.class,
            DataCompound::putInt, (c, k) -> c.getInt(k, 0));

    public static final DataType<Float>   FLOAT   = new DataType<>(Float.class,
            DataCompound::putFloat, (c, k) -> c.getFloat(k, 0f));

    public static final DataType<Boolean> BOOLEAN = new DataType<>(Boolean.class,
            DataCompound::putBoolean, (c, k) -> c.getBoolean(k, false));

    public static final DataType<String>  STRING  = new DataType<>(String.class,
            DataCompound::putString, (c, k) -> c.getString(k, ""));

    /** UUID — uses MC-version-appropriate storage via {@link DataCompound}. */
    public static final DataType<UUID>    UUID    = new DataType<>(java.util.UUID.class,
            (c, k, v) -> c.putUUID(k, v), (c, k) -> {
                java.util.UUID u = c.getUUID(k);
                return u; // null when absent — DataField uses its defaultValue in that case
            });

    // -- Fields --

    private final Class<T>  type;
    final Writer<T>         writer;
    final Reader<T>         reader;

    // -- Constructor --

    private DataType(Class<T> type, Writer<T> writer, Reader<T> reader) {
        this.type   = type;
        this.writer = writer;
        this.reader = reader;
    } // Constructor: DataType ()

    // -- Accessor --

    /** Returns the Java class this type serialises to. */
    public Class<T> getType() { return type; } // getType ()

    // -- Internal SAM interfaces --

    @FunctionalInterface
    interface Writer<T> {
        void write(DataCompound compound, String key, T value);
    }

    @FunctionalInterface
    interface Reader<T> {
        T read(DataCompound compound, String key);
    }

} // Class: DataType
