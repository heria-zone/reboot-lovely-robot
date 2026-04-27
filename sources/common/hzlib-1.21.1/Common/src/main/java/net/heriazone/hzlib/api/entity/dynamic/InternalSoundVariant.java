package net.heriazone.hzlib.api.entity.dynamic;

public abstract class InternalSoundVariant<T extends InternalSoundVariant<T>> extends InternalVariant<InternalSoundVariant<?>> {

    // -- Constructors --

    protected InternalSoundVariant(int id, String key) {
        super(id, key);
    } // Constructor: InternalSoundVariant ()

} // Class: InternalSoundVariant