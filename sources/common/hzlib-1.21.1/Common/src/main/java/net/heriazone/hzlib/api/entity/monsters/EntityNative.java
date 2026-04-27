package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityNative {

    // -- Registry --

    public static final List<NativeEntity> NATIVES = new ArrayList<>();

    // -- Values --

    public static final NativeEntity BEE = create(0, "bee");
    public static final NativeEntity GOURDRAGORA = create(1, "gourdragora");
    public static final NativeEntity MANDRAGORA = create(3, "mandragora");
    public static final NativeEntity MUSHROOM = create(4, "mushroom");
    public static final NativeEntity SLIME = create(5, "slime");
    public static final NativeEntity SPOOK = create(6, "spook");
    public static final NativeEntity WISP = create(7, "wisp");

    // -- Methods --

    protected static NativeEntity create(int id, String name) {
        NativeEntity natives = new NativeEntity(id, name);
        NATIVES.add(natives);
        return natives;
    } // create ()

} // Class: EntityNative