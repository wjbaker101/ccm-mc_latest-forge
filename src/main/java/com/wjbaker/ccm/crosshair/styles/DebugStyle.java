package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class DebugStyle extends BaseCrosshairStyle {

    public DebugStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, final int x, final int y, final ComputedProperties computedProperties) {
        var gameRenderState = this.mc.gameRenderer.getGameRenderState();

        this.mc.gui.getDebugOverlay().render3dCrosshair(gameRenderState.levelRenderState.cameraRenderState, gameRenderState.windowRenderState.guiScale);
    }
}