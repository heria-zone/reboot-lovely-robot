package net.heriazone.hzlib.framework.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides null-safe object coalescing for defensive programming patterns.
 * <p>
 * <b>Design Intent:</b> Simplifies null-handling logic by providing a concise
 * alternative to ternary operators, improving code readability in entity
 * initialization and configuration fallback scenarios.
 */
public class ObjectUtil {

    // -- Methods --

    /**
     * Returns first non-null object, falling back to guaranteed non-null default.
     * <p>
     * <b>Usage Pattern:</b> Commonly used for configuration defaults and optional
     * parameter handling where a fallback value must always be available.
     *
     * @param obj1 the object to try to return
     * @param obj2 the object to return if the first is null
     * @return the first object if it is not null, otherwise the second
     */
    public static <T> T coalesce(@Nullable T obj1, @NotNull T obj2) {
        return obj1 == null ? obj2 : obj1;
    } // coalesce ()

} // Class: ObjectUtil
