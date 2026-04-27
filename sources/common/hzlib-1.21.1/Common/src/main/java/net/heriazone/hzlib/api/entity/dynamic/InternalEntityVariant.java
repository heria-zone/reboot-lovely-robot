package net.heriazone.hzlib.api.entity.dynamic;

public abstract class InternalEntityVariant<T extends InternalEntityVariant<T>> extends InternalVariant<InternalEntityVariant<?>> {

    // -- Constructors --

    protected InternalEntityVariant(int id, String key) {
        super(id, key);
    } // Constructor: InternalEntityVariant ()

} // Class: InternalEntityVariant