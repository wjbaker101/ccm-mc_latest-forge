package com.wjbaker.ccm.crosshair.style.styles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;
import com.wjbaker.ccm.crosshair.style.AbstractCrosshairStyle;

public final class DebugStyle extends AbstractCrosshairStyle {

    public DebugStyle(final CustomCrosshair crosshair) {
        super(crosshair);
    }

    @Override
    public void draw(final PoseStack matrixStack, final int x, final int y, final ComputedProperties computedProperties) {
        var camera = this.mc.gameRenderer.getMainCamera();

        var renderMatrixStack = RenderSystem.getModelViewStack();
        renderMatrixStack.pushPose();
        renderMatrixStack.mulPose(Vector3f.XN.rotationDegrees(camera.getXRot()));
        renderMatrixStack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
        renderMatrixStack.scale(-1, -1, -1);

        RenderSystem.applyModelViewMatrix();

        RenderSystem.defaultBlendFunc();
        RenderSystem.renderCrosshair(10);

        renderMatrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
