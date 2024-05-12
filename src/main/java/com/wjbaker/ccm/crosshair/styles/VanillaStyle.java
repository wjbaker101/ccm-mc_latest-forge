package com.wjbaker.ccm.crosshair.styles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class VanillaStyle extends BaseCrosshairStyle {

    private static final ResourceLocation CROSSHAIR_SPRITE = new ResourceLocation("hud/crosshair");

    public VanillaStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
            GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var crosshairSize = 15;

        guiGraphics.blitSprite(
            CROSSHAIR_SPRITE,
            x - Math.round(crosshairSize / 2f),
            y - Math.round(crosshairSize / 2f),
            crosshairSize,
            crosshairSize);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}