package com.wjbaker.ccm.crosshair.types;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.rendering.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public abstract class BaseCrosshairStyle {

    protected final CustomCrosshair crosshair;
    protected final RenderManager renderManager;
    protected final Minecraft mc;

    public BaseCrosshairStyle(final CustomCrosshair crosshair) {
        this.crosshair = crosshair;
        this.renderManager = new RenderManager();
        this.mc = Minecraft.getInstance();
    }

    public abstract void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties);
}