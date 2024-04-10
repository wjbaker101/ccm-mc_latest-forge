package com.wjbaker.ccm.crosshair.style.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;

public final class ArrowStyle extends BaseCrosshairStyle {

    public ArrowStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        int width = this.crosshair.width.get();
        int height = this.crosshair.height.get();
        int thickness = this.crosshair.thickness.get();
        var isAdaptiveColourEnabled = this.crosshair.isAdaptiveColourEnabled.get();

        this.renderManager.drawLines(guiGraphics.pose(), new float[] {
            x - width, y + height, x, y,
            x, y, x + width, y + height
        }, thickness, computedProperties.colour(), isAdaptiveColourEnabled);
    }
}