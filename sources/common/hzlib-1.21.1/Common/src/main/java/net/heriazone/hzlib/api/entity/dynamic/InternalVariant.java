package net.heriazone.hzlib.api.entity.dynamic;

import net.minecraft.network.chat.MutableComponent;

import java.util.Objects;

public abstract class InternalVariant<T extends InternalVariant<T>> implements IInternalVariant {

    // -- Variables --

    protected final int id;
    protected final String key;
    protected final MutableComponent name;

    // -- Constructors --

    protected InternalVariant(int id, String key) {
        this.id = id;
        this.key = Objects.requireNonNull(key, "Key cannot be null");
        this.name = createTranslation(key);
    } // Constructor: InternalVariant ()

    // -- Abstract Methods --

    protected abstract MutableComponent createTranslation(String key);

    // -- Public Accessors --

    public int getId() {
        return id;
    } // getKey ()

    public String getKey() {
        return key;
    } // getKey ()

    public MutableComponent getName() {
        return name;
    } // getName ()

} // Class: InternalVariant