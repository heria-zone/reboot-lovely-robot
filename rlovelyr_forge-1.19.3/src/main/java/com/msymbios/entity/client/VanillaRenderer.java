package com.msymbios.entity.client;

import com.msymbios.entity.custom.VanillaEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class VanillaRenderer extends GeoEntityRenderer<VanillaEntity>  {

    // -- Constructor --
    public VanillaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new VanillaModel());
        this.shadowRadius = 0.4f;
    } // Constructor Bunny2Renderer ()

    // -- Methods --
    @Override
    public ResourceLocation getTextureLocation(VanillaEntity instance) {
        return instance.getCurrentTexture();
    } // getTextureLocation ()

    @Override
    public RenderType getRenderType(VanillaEntity animatable, ResourceLocation texture, @Nullable MultiBufferSource bufferSource, float partialTick) {
        return super.getRenderType(animatable, texture, bufferSource, partialTick);
    } // getRenderType ()

} // Class VanillaRenderer