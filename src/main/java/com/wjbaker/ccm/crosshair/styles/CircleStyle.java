package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;

public final class CircleStyle extends BaseCrosshairStyle {

    public CircleStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        this.renderManager.drawCircle(guiGraphics, x, y, computedProperties.gap(), 2.0F, computedProperties.colour());
    }
}