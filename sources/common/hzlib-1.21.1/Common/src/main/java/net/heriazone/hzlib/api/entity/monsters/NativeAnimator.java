package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalAnimatorVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeAnimator extends InternalAnimatorVariant<NativeAnimator> {

    // -- Constructor --

    protected NativeAnimator(int id, String key) {
        super(id, key);
    } // Constructor: NativeAnimator ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("animator", key);
        return null;
    } // createTranslation ()

} // Class: NativeAnimator