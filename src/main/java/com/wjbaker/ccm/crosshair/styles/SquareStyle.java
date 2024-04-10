package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.gui.GuiGraphics;

public final class SquareStyle extends BaseCrosshairStyle {

    public SquareStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        boolean isOutlineEnabled = this.crosshair.isOutlineEnabled.get();
        int width = this.crosshair.width.get();
        int height = this.crosshair.height.get();
        int thickness = this.crosshair.thickness.get();
        int gap = computedProperties.gap();
        RGBA colour = computedProperties.colour();
        var isAdaptiveColourEnabled = this.crosshair.isAdaptiveColourEnabled.get();

        if (isOutlineEnabled) {
            RGBA outlineColour = this.crosshair.outlineColour.get();

            // Inner
            this.renderManager.drawRectangle(
                guiGraphics.pose(),
                x - width - gap + 0.5F, y - height - gap + 0.5F,
                x + width + gap - 0.5F, y + height + gap - 0.5F,
                2.0F,
                outlineColour);

            // Outer
            this.renderManager.drawRectangle(
                guiGraphics.pose(),
                x - width - thickness - gap - 0.5F, y - height - thickness - gap - 0.5F,
                x + width + thickness + gap + 0.5F, y + height + thickness + gap + 0.5F,
                2.0F,
                outlineColour);
        }

        // Top
        this.renderManager.drawFilledRectangle(
            guiGraphics.pose(),
            x - width - thickness - gap, y - height - thickness - gap,
            x + width + thickness + gap, y - height - gap,
            colour, isAdaptiveColourEnabled);

        // Bottom
        this.renderManager.drawFilledRectangle(
            guiGraphics.pose(),
            x - width - thickness - gap, y + height + gap,
            x + width + thickness + gap, y + height + thickness + gap,
            colour, isAdaptiveColourEnabled);

        // Left
        this.renderManager.drawFilledRectangle(
            guiGraphics.pose(),
            x - width - thickness - gap, y - gap - height,
            x - width - gap, y + gap + height,
            colour, isAdaptiveColourEnabled);

        // Right
        this.renderManager.drawFilledRectangle(
            guiGraphics.pose(),
            x + width + gap, y - gap - height,
            x + width + thickness + gap, y + gap + height,
            colour, isAdaptiveColourEnabled);
    }
}