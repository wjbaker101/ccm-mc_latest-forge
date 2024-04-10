package com.wjbaker.ccm.crosshair.style.styles;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.AbstractCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;

public final class DrawnStyle extends AbstractCrosshairStyle {

    public DrawnStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        var image = CustomCrosshairMod.INSTANCE.properties().getCustomCrosshairDrawer();
        var baseColour = computedProperties.colour();

        this.renderManager.drawImage(guiGraphics.pose(), x, y, image, baseColour, true);
    }
}