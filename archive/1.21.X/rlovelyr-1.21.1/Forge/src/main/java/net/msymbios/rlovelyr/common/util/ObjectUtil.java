package net.msymbios.rlovelyr.common.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Contains utility functions for objects.
 */
public class ObjectUtil {

    // -- Methods --

    /**
     * @param obj1 the object to try to return.
     * @param obj2 the object to return if the first is null.
     * @return the first object if it is not null, otherwise the second.
     */
    public static <T> T coalesce(@Nullable T obj1, @NotNull T obj2) {
        return obj1 == null ? obj2 : obj1;
    } // coalesce ()

} // Class ObjectUtil