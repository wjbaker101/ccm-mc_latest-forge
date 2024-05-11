package com.wjbaker.ccm.crosshair.styles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import net.minecraft.client.gui.GuiGraphics;

public final class DebugStyle extends BaseCrosshairStyle {

    public DebugStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics, final int x, final int y, final ComputedProperties computedProperties) {
        var camera = this.mc.gameRenderer.getMainCamera();

        var renderMatrixStack = RenderSystem.getModelViewStack();
        renderMatrixStack.pushMatrix();
        renderMatrixStack.rotateX(-camera.getXRot() * 0.017453292F);
        renderMatrixStack.rotateY(camera.getYRot() * 0.017453292F);
        renderMatrixStack.scale(-1, -1, -1);

        RenderSystem.applyModelViewMatrix();

        RenderSystem.defaultBlendFunc();
        RenderSystem.renderCrosshair(10);

        renderMatrixStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }
}