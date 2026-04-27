package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalModelVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeModel extends InternalModelVariant<NativeModel> {

    // -- Constructor --

    protected NativeModel(int id, String key) {
        super(id, key);
    } // Constructor: NativeModel ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("model", key);
        return null;
    } // createTranslation ()

} // Class: NativeModel