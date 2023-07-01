package com.wjbaker.ccm.render.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.wjbaker.ccm.render.gui.event.IKeyboardEvents;
import com.wjbaker.ccm.render.gui.event.IMouseEvents;
import net.minecraft.client.gui.GuiGraphics;

public interface IGuiScreen extends IMouseEvents, IKeyboardEvents {

    void update();
    void close();

    void draw(final GuiGraphics guiGraphics);
}