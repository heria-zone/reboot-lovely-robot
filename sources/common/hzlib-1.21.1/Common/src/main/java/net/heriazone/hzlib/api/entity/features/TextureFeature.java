package net.heriazone.hzlib.api.entity.features;

import net.heriazone.hzlib.api.entity.dynamic.InternalTextureVariant;
import net.heriazone.hzlib.framework.entity.data.ResourceMap;
import net.minecraft.resources.ResourceLocation;

public class TextureFeature {

    // -- Variables --

    protected final ResourceMap<InternalTextureVariant<?>, ResourceLocation> textures;

    // -- Constructor --

    public TextureFeature() {
        this.textures = new ResourceMap<>();
    } // Constructor: TextureFeature ()

    public TextureFeature(ResourceMap<InternalTextureVariant<?>, ResourceLocation> textures) {
        this.textures = textures;
    } // Constructor: TextureFeature ()

    // -- Methods --



} // Class: TextureFeature