package com.wjbaker.ccm.crosshair.style.styles;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.crosshair.style.AbstractCrosshairStyle;
import com.wjbaker.ccm.type.RGBA;

public final class ArrowStyle extends AbstractCrosshairStyle {

    public ArrowStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final PoseStack matrixStack, final int x, final int y, final ComputedProperties computedProperties) {
        int width = this.crosshair.width.get();
        int height = this.crosshair.height.get();
        int thickness = this.crosshair.thickness.get();
        var isAdaptiveColourEnabled = this.crosshair.isAdaptiveColourEnabled.get();

        this.renderManager.drawLines(matrixStack, new float[] {
            x - width, y + height, x, y,
            x, y, x + width, y + height
        }, thickness, computedProperties.colour(), isAdaptiveColourEnabled);
    }
}
