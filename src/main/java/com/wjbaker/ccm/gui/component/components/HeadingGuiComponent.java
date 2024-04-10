package com.wjbaker.ccm.gui.component.components;

import com.wjbaker.ccm.gui.component.GuiComponent;
import com.wjbaker.ccm.gui.screen.GuiScreen;
import net.minecraft.client.gui.GuiGraphics;

public final class HeadingGuiComponent extends GuiComponent {

    private final String label;

    public HeadingGuiComponent(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final String label) {

        super(parentGuiScreen, x, y, -1, -1);

        this.label = label;

        this.width = this.renderManager.textWidth(this.label) * 2;
        this.height = 12;
    }

    @Override
    public void draw(final GuiGraphics guiGraphics) {
        super.draw(guiGraphics);

        this.renderManager.drawBigText(guiGraphics, this.label, this.x, this.y, this.currentTextColour, true);
    }
}