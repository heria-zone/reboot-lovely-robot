package net.heriazone.hzlib.api.entity.monsters;

import net.heriazone.hzlib.api.entity.dynamic.InternalTextureVariant;
import net.minecraft.network.chat.MutableComponent;

public class NativeTexture extends InternalTextureVariant<NativeTexture> {

    // -- Constructor --

    protected NativeTexture(int id, String key) {
        super(id, key);
    } // Constructor: NativeTexture ()

    // -- Inherited Methods --

    @Override
    protected MutableComponent createTranslation(String key) {
        //MonstersIdentifier.getTranslation("texture", key);
        return null;
    } // createTranslation ()

} // Class: NativeTexture