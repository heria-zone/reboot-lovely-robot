package net.heriazone.hzlib.api.entity.dynamic;

public abstract class InternalModelVariant<T extends InternalModelVariant<T>> extends InternalVariant<InternalModelVariant<?>> {

    // -- Constructors --

    protected InternalModelVariant(int id, String key) {
        super(id, key);
    } // Constructor: InternalModelVariant ()

} // Class: InternalModelVariant