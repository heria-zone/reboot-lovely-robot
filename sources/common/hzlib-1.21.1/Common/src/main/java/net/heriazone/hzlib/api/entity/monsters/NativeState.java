package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalStateVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeState extends InternalStateVariant<NativeState> {

    // -- Constructor --

    protected NativeState(int id, String key) {
        super(id, key);
    } // Constructor: NativeState ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("state", key);
        return null;
    } // createTranslation ()

} // Class: NativeState