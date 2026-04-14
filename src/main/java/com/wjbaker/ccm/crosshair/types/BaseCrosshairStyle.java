package com.wjbaker.ccm.crosshair.types;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.rendering.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class BaseCrosshairStyle {

    public enum Styles {
        VANILLA,
        CROSS,
        CIRCLE,
        SQUARE,
        TRIANGLE,
        ARROW,
        DEBUG,
        DRAWN,
    }

    protected final CustomCrosshair crosshair;
    protected final RenderManager renderManager;
    protected final Minecraft mc;

    public BaseCrosshairStyle(final CustomCrosshair crosshair) {
        this.crosshair = crosshair;
        this.renderManager = new RenderManager();
        this.mc = Minecraft.getInstance();
    }

    public abstract void draw(final GuiGraphicsExtractor graphics, final int x, final int y, final ComputedProperties computedProperties);
}