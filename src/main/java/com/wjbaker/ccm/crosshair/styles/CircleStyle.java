package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import com.wjbaker.ccm.rendering.types.RGBA;
import net.minecraft.client.gui.GuiGraphics;

public final class CircleStyle extends BaseCrosshairStyle {

    public CircleStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        boolean isOutlineEnabled = this.crosshair.isOutlineEnabled.get();
        int thickness = this.crosshair.thickness.get();

        if (isOutlineEnabled) {
            RGBA outlineColour = this.crosshair.outlineColour.get();

            this.renderManager.drawCircle(guiGraphics.pose(), x, y, computedProperties.gap() + 0.5F + thickness, 2.0F, outlineColour);
            this.renderManager.drawCircle(guiGraphics.pose(), x, y, computedProperties.gap() - 0.5F, 2.0F, outlineColour);
        }

        this.renderManager.drawTorus(
            guiGraphics.pose(),
            x, y,
            computedProperties.gap(),
            computedProperties.gap() + thickness,
            computedProperties.colour());
    }
}