package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntitySound {

    // -- Registry --

    public static final List<NativeSound> SOUNDS = new ArrayList<>();

    // -- Values --

    public static final NativeSound ATTACK = create(0, "attack");
    public static final NativeSound DEFAULT = create(1, "default");
    public static final NativeSound DEATH = create(2, "death");
    public static final NativeSound HURT = create(3, "hurt");
    public static final NativeSound HUFF = create(4, "huff");
    public static final NativeSound LAUGH = create(5, "laugh");
    public static final NativeSound PUFF = create(6, "puff");
    public static final NativeSound SPECIAL = create(7, "special");

    // -- Methods --

    protected static NativeSound create(int id, String name) {
        NativeSound sound = new NativeSound(id, name);
        SOUNDS.add(sound);
        return sound;
    } // create ()

} // Class: EntitySound