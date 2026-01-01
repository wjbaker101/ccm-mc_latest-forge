package com.wjbaker.ccm.mixins;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.rendering.CrosshairRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public final class GameRendererMixin {

    @Shadow
    private GuiRenderState guiRenderState;

    private final CrosshairRenderManager crosshairRenderManager = new CrosshairRenderManager();

    @Inject(
        at = @At(
            value = "CONSTANT",
            args = "stringValue=guiExtraction"
        ),
        method = "render"
    )
    private void render(final CallbackInfo callbackInfo) {
        if (!CustomCrosshairMod.INSTANCE.properties().getIsModEnabled().get())
            return;

        var mc = Minecraft.getInstance();

        if (mc.screen != null) {
            return;
        }

        var window = mc.getWindow();
        var x = window.getGuiScaledWidth() / 2;
        var y = window.getGuiScaledHeight() / 2;

        var mouseX = (int)mc.mouseHandler.getScaledXPos(window);
        var mouseY = (int)mc.mouseHandler.getScaledYPos(window);

        var crosshair = CustomCrosshairMod.INSTANCE.properties().getCrosshair();
        var guiGraphics = new GuiGraphics(mc, this.guiRenderState, mouseX, mouseY);

        this.crosshairRenderManager.draw(crosshair, guiGraphics, x, y);
    }
}