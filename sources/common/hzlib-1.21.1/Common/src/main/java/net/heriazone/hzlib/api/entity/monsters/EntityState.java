package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityState {

    // -- Registry --

    public static final List<NativeState> STATES = new ArrayList<>();

    // -- Values --

    public static final NativeState MOVE = create(0, "move");
    public static final NativeState REST = create(1, "rest");

    // -- Methods --

    protected static NativeState create(int id, String name) {
        NativeState state = new NativeState(id, name);
        STATES.add(state);
        return state;
    } // create ()

} // Class: EntityState