package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityAnimation {

    // -- Registry --

    public static final List<NativeAnimation> ANIMATIONS = new ArrayList<>();

    // -- Values --

    public static final NativeAnimation IDLE = create(0, "idle");
    public static final NativeAnimation WALK = create(1, "walk");
    public static final NativeAnimation ATTACK = create(2, "attack");
    public static final NativeAnimation INTERACT = create(3, "interact");
    public static final NativeAnimation REST = create(4,"rest");
    public static final NativeAnimation WAVE = create(5,"wave");
    public static final NativeAnimation HURT = create(5,"hurt");

    // -- Methods --

    protected static NativeAnimation create(int id, String name) {
        NativeAnimation animation = new NativeAnimation(id, name);
        ANIMATIONS.add(animation);
        return animation;
    } // create ()

} // Class: EntityAnimation