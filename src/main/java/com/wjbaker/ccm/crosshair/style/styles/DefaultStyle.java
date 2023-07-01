package com.wjbaker.ccm.crosshair.style.styles;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.crosshair.style.AbstractCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;

public final class DefaultStyle extends AbstractCrosshairStyle {

    private final ResourceLocation guiIconsLocation;

    public DefaultStyle(final CustomCrosshair crosshair) {
        super(crosshair);

        this.guiIconsLocation = new ResourceLocation("textures/gui/icons.png");
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
        RenderSystem.setShaderTexture(0, this.guiIconsLocation);

        int crosshairSize = 15;
        int textureSize = 256;

        guiGraphics.blit(
            this.guiIconsLocation,
            x - Math.round(crosshairSize / 2.0F),
            y - Math.round(crosshairSize / 2.0F),
            0, 0,
            crosshairSize, crosshairSize,
            textureSize, textureSize);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}