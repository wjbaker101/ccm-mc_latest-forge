package com.wjbaker.ccm.crosshair.style;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wjbaker.ccm.crosshair.render.ComputedProperties;

public interface ICrosshairStyle {

    void draw(final PoseStack matrixStack, final int x, final int y, final ComputedProperties computedProperties);
}
