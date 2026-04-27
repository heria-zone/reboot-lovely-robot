package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityAnimator {

    // -- Registry --

    public static final List<NativeAnimator> ANIMATORS = new ArrayList<>();

    // -- Values --

    public static final NativeAnimator DEFAULT = create(0, "default");

    // -- Methods --

    protected static NativeAnimator create(int id, String name) {
        NativeAnimator animator = new NativeAnimator(id, name);
        ANIMATORS.add(animator);
        return animator;
    } // create ()

} // Class: EntityAnimator