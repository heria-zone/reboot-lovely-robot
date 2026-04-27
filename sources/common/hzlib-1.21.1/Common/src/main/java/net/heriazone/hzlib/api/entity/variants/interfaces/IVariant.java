package net.heriazone.hzlib.api.entity.variants.interfaces;

import net.minecraft.resources.ResourceLocation;

public interface IVariant {

    // -- Methods --

    String getKey();

    String getDisplay();

    ResourceLocation getResource(String key);

    int getPriority(); // For ordering/selection

    boolean isAvailable(String key);

} // Interface: IVariant