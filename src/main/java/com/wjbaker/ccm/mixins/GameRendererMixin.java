package com.wjbaker.ccm.mixins;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.rendering.CrosshairRenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public final class GameRendererMixin {

    private final CrosshairRenderManager crosshairRenderManager = new CrosshairRenderManager();

    @Inject(
        at = @At(
            value = "CONSTANT",
            args = "stringValue=gui"
        ),
        method = "render"
    )
    private void render(final CallbackInfo callbackInfo) {
        if (!CustomCrosshairMod.INSTANCE.properties().getIsModEnabled().get())
            return;

        var mc = Minecraft.getInstance();
        var window = mc.getWindow();
        var x = window.getGuiScaledWidth() / 2;
        var y = window.getGuiScaledHeight() / 2;

        var crosshair = CustomCrosshairMod.INSTANCE.properties().getCrosshair();
        this.crosshairRenderManager.draw(crosshair, x, y);
    }
}