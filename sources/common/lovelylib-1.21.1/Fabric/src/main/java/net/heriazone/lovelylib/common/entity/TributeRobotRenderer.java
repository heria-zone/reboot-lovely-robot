package net.heriazone.lovelylib.common.entity;

import net.heriazone.hzlib.api.entity.NativeRenderer;
import net.heriazone.hzlib.api.layer.BaseTextureLayer;
import net.heriazone.hzlib.api.layer.HeadphoneOverlayLayer;
import net.heriazone.hzlib.framework.entity.enums.EntityState;
import net.heriazone.lovelylib.common.configs.SharedConfigs;
import net.heriazone.lovelylib.common.shared.LovelyResource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

/**
 * Renderer for Tribute robot variants — base texture and mode overlay only.
 * <p>
 * <b>Architecture:</b> Identical layer stack to {@link NativeRobotRenderer} except
 * the {@code DynamicColorLayer} health collar is intentionally omitted. Tribute is a
 * faithful recreation of the original LovelyRobot mod, which had no health collar.
 * <p>
 * <b>Layer stack:</b> Base texture → headphone mode overlay (defense / auto-attack).
 * No health-indicator collar ring.
 */
public class TributeRobotRenderer extends NativeRenderer<NativeRobotEntity> {

    // -- Constructor --

    public TributeRobotRenderer(EntityRendererProvider.Context context) {
        super(context, new NativeRobotModel(), SharedConfigs.Client.ShadowRadius);

        addLayer(new BaseTextureLayer<>(this));

        // Mode overlay — defence indicator > auto-attack indicator > empty
        addLayer(new HeadphoneOverlayLayer<>(this, LovelyResource.GENERAL_LAYER_EMPTY)
                .addConditionalTexture(
                        entity -> entity.getCurrentState() == EntityState.Defense,
                        LovelyResource.GENERAL_LAYER_BASE_DEFENSE
                )
                .addConditionalTexture(
                        RobotEntity::getAutoAttack,
                        LovelyResource.GENERAL_LAYER_AUTO_ATTACK
                )
        );
    } // Constructor: TributeRobotRenderer()

} // Class: TributeRobotRenderer
