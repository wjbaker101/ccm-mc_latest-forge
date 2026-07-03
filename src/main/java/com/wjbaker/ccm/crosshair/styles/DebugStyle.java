package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.DebugCrosshairRenderer;

public final class DebugStyle extends BaseCrosshairStyle {

    private final DebugCrosshairRenderer debugCrosshairRenderer = new DebugCrosshairRenderer();

    public DebugStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, final int x, final int y, final ComputedProperties computedProperties) {
        this.debugCrosshairRenderer.render(
            this.mc.gameRenderer.gameRenderState().levelRenderState.cameraRenderState,
            this.mc.gameRenderer.gameRenderState().windowRenderState.guiScale);
    }
}