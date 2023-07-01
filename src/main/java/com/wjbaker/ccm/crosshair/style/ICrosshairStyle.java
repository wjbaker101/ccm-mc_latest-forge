package com.wjbaker.ccm.crosshair.style;

import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import net.minecraft.client.gui.GuiGraphics;

public interface ICrosshairStyle {

    void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties);
}