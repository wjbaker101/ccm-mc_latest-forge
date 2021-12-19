package com.wjbaker.ccm.crosshair.style;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.render.RenderManager;
import net.minecraft.client.Minecraft;

public abstract class AbstractCrosshairStyle implements ICrosshairStyle {

    protected final CustomCrosshair crosshair;
    protected final RenderManager renderManager;
    protected final Minecraft mc;

    public AbstractCrosshairStyle(final CustomCrosshair crosshair) {
        this.crosshair = crosshair;
        this.renderManager = new RenderManager();
        this.mc = Minecraft.getInstance();
    }
}
