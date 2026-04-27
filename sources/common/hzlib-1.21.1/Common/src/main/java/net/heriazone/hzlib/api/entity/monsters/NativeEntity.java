package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalEntityVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeEntity extends InternalEntityVariant<NativeEntity> {

    // -- Constructor --

    protected NativeEntity(int id, String key) {
        super(id, key);
    } // Constructor: NativeEntity ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("entity", key);
        return null;
    } // createTranslation ()

} // Class: NativeEntity