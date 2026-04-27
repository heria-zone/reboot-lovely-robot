package net.heriazone.hzlib.api.entity.dynamic;

public abstract class InternalAnimationVariant<T extends InternalAnimationVariant<T>> extends InternalVariant<InternalAnimationVariant<?>> {

    // -- Constructors --

    protected InternalAnimationVariant(int id, String key) {
        super(id, key);
    } // Constructor: InternalAnimationVariant ()

} // Class: InternalAnimationVariant