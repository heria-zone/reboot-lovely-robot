package net.heriazone.hzlib.api.entity.dynamic;

public abstract class InternalStateVariant<T extends InternalStateVariant<T>> extends InternalVariant<InternalStateVariant<?>> {

    // -- Constructors --

    protected InternalStateVariant(int id, String key) {
        super(id, key);
    } // Constructor: InternalStateVariant ()

} // Class: InternalStateVariant