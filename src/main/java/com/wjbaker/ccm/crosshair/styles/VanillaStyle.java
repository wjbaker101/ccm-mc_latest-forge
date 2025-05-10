package com.wjbaker.ccm.crosshair.styles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public final class VanillaStyle extends BaseCrosshairStyle {

    private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");

    public VanillaStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var crosshairSize = 15;

        guiGraphics.blitSprite(
            RenderType::crosshair,
            CROSSHAIR_SPRITE,
            x - Math.round(crosshairSize / 2f),
            y - Math.round(crosshairSize / 2f),
            crosshairSize,
            crosshairSize);
    }
}