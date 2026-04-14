package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import com.wjbaker.ccm.rendering.types.RGBA;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class CrossStyle extends BaseCrosshairStyle {

    public CrossStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphicsExtractor graphics, final int x, final int y, final ComputedProperties computedProperties) {
        boolean isOutlineEnabled = this.crosshair.isOutlineEnabled.get();
        RGBA baseColour = computedProperties.colour();
        int gap = computedProperties.gap();
        float thickness = this.crosshair.thickness.get();
        int width = this.crosshair.width.get();
        int height  = this.crosshair.height.get();
        var isAdaptiveColourEnabled = this.crosshair.isAdaptiveColourEnabled.get();

        // Order of the orientation of the bars:
        // Left
        // Bottom
        // Right
        // Left

        if (isOutlineEnabled) {
            RGBA outlineColour = this.crosshair.outlineColour.get();
            float adjustedWidth = width + 1.0F;
            float adjustedHeight = height + 1.0F;
            float adjustedGap = gap - 0.5F;

            this.renderManager.drawBorderedRectangle(graphics, x - thickness, y - adjustedGap - adjustedHeight, x + thickness, y - adjustedGap, 2.0F, outlineColour, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawBorderedRectangle(graphics, x - thickness, y + adjustedGap, x + thickness, y + adjustedGap + adjustedHeight, 2.0F, outlineColour, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawBorderedRectangle(graphics, x - adjustedGap - adjustedWidth, y - thickness, x - adjustedGap, y + thickness, 2.0F, outlineColour, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawBorderedRectangle(graphics, x + adjustedGap, y - thickness, x + adjustedGap + adjustedWidth, y + thickness, 2.0F, outlineColour, baseColour, isAdaptiveColourEnabled);
        }
        else {
            float adjustedThickness = thickness - 0.5F;

            this.renderManager.drawFilledRectangle(graphics, x - adjustedThickness, y - gap - height, x + adjustedThickness, y - gap, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawFilledRectangle(graphics, x - adjustedThickness, y + gap, x + adjustedThickness, y + gap + height, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawFilledRectangle(graphics, x - gap - width, y - adjustedThickness, x - gap, y + adjustedThickness, baseColour, isAdaptiveColourEnabled);
            this.renderManager.drawFilledRectangle(graphics, x + gap, y - adjustedThickness, x + gap + width, y + adjustedThickness, baseColour, isAdaptiveColourEnabled);
        }
    }
}