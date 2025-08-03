package com.wjbaker.ccm.rendering;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.wjbaker.ccm.rendering.types.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

@OnlyIn(Dist.CLIENT)
public record ModLinesGuiElementRenderState(Matrix3x2f pose, Float[] points, RGBA colour) implements GuiElementRenderState {

    @Override
    public RenderPipeline pipeline() {
        return RenderPipelines.DEBUG_LINE_STRIP;
    }

    @Override
    public void buildVertices(final VertexConsumer vertices, final float depth) {
        for (var i = 0; i < points.length; i += 2) {
            vertices
                .addVertexWith2DPose(this.pose, points[i], points[i + 1], depth)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
        }

        vertices
            .addVertexWith2DPose(this.pose, points[points.length - 2], points[points.length - 1], depth)
            .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
    }

    @Override
    public @NotNull TextureSetup textureSetup() {
        return TextureSetup.noTexture();
    }

    @Override
    public @Nullable ScreenRectangle scissorArea() {
        var minX = points[0];
        var minY = points[1];
        var maxX = points[0];
        var maxY = points[1];

        for (int i = 2; i < points.length; i++) {
            if (i % 2 == 0) {
                minX = Math.min(minX, points[i]);
                maxX = Math.max(maxX, points[i]);
            }
            else {
                minY = Math.min(minY, points[i]);
                maxY = Math.max(maxY, points[i]);
            }
        }

        var window = Minecraft.getInstance().getWindow();
        return new ScreenRectangle(-minX.intValue(), -minY.intValue(), window.getGuiScaledWidth() + maxX.intValue(), window.getGuiScaledHeight() + maxY.intValue());
    }

    @Override
    public ScreenRectangle bounds() {
        var window = Minecraft.getInstance().getWindow();
        return new ScreenRectangle(0, 0, window.getGuiScaledWidth(), window.getGuiScaledHeight()).transformMaxBounds(pose);
    }
}