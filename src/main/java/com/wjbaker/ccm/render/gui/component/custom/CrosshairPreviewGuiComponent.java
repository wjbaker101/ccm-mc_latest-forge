package com.wjbaker.ccm.render.gui.component.custom;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.render.CrosshairRenderManager;
import com.wjbaker.ccm.render.ModTheme;
import com.wjbaker.ccm.render.gui.component.GuiComponent;
import com.wjbaker.ccm.render.gui.screen.GuiScreen;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.gui.GuiGraphics;

public final class CrosshairPreviewGuiComponent extends GuiComponent {

    private final CrosshairRenderManager crosshairRenderManager;
    private final CustomCrosshair crosshair;

    public CrosshairPreviewGuiComponent(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final CustomCrosshair crosshair) {

        super(parentGuiScreen, x, y, 150, 150);
        this.crosshair = crosshair;

        this.crosshairRenderManager = new CrosshairRenderManager(true);
    }

    @Override
    public void draw(final GuiGraphics guiGraphics) {
        super.draw(guiGraphics);

        int gridCount = 30;
        int gridSize = this.width / gridCount;

        for (int gridX = 0; gridX < gridCount; ++gridX) {
            for (int gridY = 0; gridY < gridCount; ++gridY) {
                RGBA gridColour = (gridX % 2 == 0 && gridY % 2 == 0) || (gridX % 2 != 0 && gridY % 2 != 0)
                    ? ModTheme.WHITE.setOpacity(140)
                    : ModTheme.DARK_GREY.setOpacity(140);

                this.renderManager.drawFilledRectangle(
                    guiGraphics.pose(),
                    this.x + gridSize * gridX,
                    this.y + gridSize * gridY,
                    this.x + gridSize * gridX + gridSize,
                    this.y + gridSize * gridY + gridSize,
                    gridColour);
            }
        }

        this.renderManager.drawRectangle(
            guiGraphics.pose(),
            this.x, this.y,
            this.x + this.width, this.y + this.height,
            2.0F,
            ModTheme.PRIMARY);

        this.crosshairRenderManager.draw(this.crosshair, this.x + (this.width / 2), this.y + (this.height / 2));

        if (!CustomCrosshairMod.INSTANCE.properties().getIsModEnabled().get())
            this.renderManager.drawSmallText(
                guiGraphics,
                "Mod is disabled, re-enable to see this crosshair!",
                this.x + 5,
                this.y + this.height - 8,
                new RGBA(200, 40, 40, 255),
                false);
    }
}