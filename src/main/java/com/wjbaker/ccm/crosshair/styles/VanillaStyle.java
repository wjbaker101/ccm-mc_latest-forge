package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class VanillaStyle extends BaseCrosshairStyle {

    private static final Identifier CROSSHAIR_SPRITE = Identifier.withDefaultNamespace("hud/crosshair");

    public VanillaStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, final int x, final int y, final ComputedProperties computedProperties) {
        var crosshairSize = 15;
        var textureSize = 15;

        graphics.blitSprite(
            RenderPipelines.CROSSHAIR,
            CROSSHAIR_SPRITE,
            x - Math.round(crosshairSize / 2f), y - Math.round(crosshairSize / 2f),
            textureSize, textureSize);
    }
}