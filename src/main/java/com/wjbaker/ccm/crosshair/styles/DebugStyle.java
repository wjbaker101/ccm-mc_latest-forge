package com.wjbaker.ccm.crosshair.styles;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;

public final class DebugStyle extends BaseCrosshairStyle {

    private static final DebugScreenOverlay overlay = new DebugScreenOverlay(Minecraft.getInstance());

    public DebugStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        this.mc.gui.getDebugOverlay().render3dCrosshair(this.mc.gameRenderer.getMainCamera());
    }
}