package net.heriazone.hzlib.api.entity.monsters;

import java.util.ArrayList;
import java.util.List;

public class EntityModel {

    // -- Registry --

    public static final List<NativeModel> MODELS = new ArrayList<>();

    // -- Values --

    public static final NativeModel DEFAULT = create(0, "default");

    // -- Methods --

    protected static NativeModel create(int id, String name) {
        NativeModel model = new NativeModel(id, name);
        MODELS.add(model);
        return model;
    } // create ()

} // Class: EntityModel