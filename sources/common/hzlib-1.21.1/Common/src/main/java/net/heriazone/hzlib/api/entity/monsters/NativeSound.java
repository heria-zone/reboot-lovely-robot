package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalSoundVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeSound extends InternalSoundVariant<NativeSound> {

    // -- Constructor --

    protected NativeSound(int id, String key) {
        super(id, key);
    } // Constructor: NativeSound ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("sound", key);
        return null;
    } // createTranslation ()

} // Class: NativeSound