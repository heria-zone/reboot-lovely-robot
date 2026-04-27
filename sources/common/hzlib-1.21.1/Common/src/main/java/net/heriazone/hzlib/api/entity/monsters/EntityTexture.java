package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityTexture {

    // -- Registry --

    public static final List<NativeTexture> TEXTURES = new ArrayList<>();

    // -- Values --

    public static final NativeTexture SLIM = create(0, "slim");
    public static final NativeTexture DEFAULT = create(1, "default");
    public static final NativeTexture TUMMY = create(2, "tummy");
    public static final NativeTexture INFLATED = create(3, "inflated");
    public static final NativeTexture CHUNKY = create(4, "chunky");

    // -- Methods --

    protected static NativeTexture create(int id, String name) {
        NativeTexture texture = new NativeTexture(id, name);
        TEXTURES.add(texture);
        return texture;
    } // create ()

} // Class: EntityTexture