package com.msymbios.entity.client;

import com.msymbios.entity.custom.Bunny2Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class Bunny2Renderer extends GeoEntityRenderer<Bunny2Entity>  {

    // -- Constructor --
    public Bunny2Renderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new Bunny2Model());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(Bunny2Entity instance) {
        return instance.getCurrentTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(Bunny2Entity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    } // getRenderType ()

} // Class Bunny2Renderer