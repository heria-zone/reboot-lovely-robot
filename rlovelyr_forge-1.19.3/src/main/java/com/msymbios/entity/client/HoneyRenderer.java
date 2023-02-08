package com.msymbios.entity.client;

import com.msymbios.entity.custom.HoneyEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HoneyRenderer extends GeoEntityRenderer<HoneyEntity>  {

    // -- Constructor --
    public HoneyRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new HoneyModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(HoneyEntity instance) {
        return instance.getCurrentTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(HoneyEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    } // getRenderType ()

} // Class HoneyRenderer