package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalAnimationVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeAnimation extends InternalAnimationVariant<NativeAnimation> {

    // -- Constructor --

    protected NativeAnimation(int id, String key) {
        super(id, key);
    } // Constructor: NativeAnimation ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("animation", key);
        return null;
    } // createTranslation ()

} // Class: NativeAnimation