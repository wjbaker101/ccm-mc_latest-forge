package com.wjbaker.ccm.rendering;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wjbaker.ccm.crosshair.custom.CustomCrosshairDrawer;
import com.wjbaker.ccm.gui.types.GuiBounds;
import com.wjbaker.ccm.gui.types.IDrawInsideWindowCallback;
import com.wjbaker.ccm.rendering.types.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import org.lwjgl.opengl.GL11;

public final class RenderManager {

    private void setGlProperty(final int property, final boolean isEnabled) {
        if (isEnabled)
            GL11.glEnable(property);
        else
            GL11.glDisable(property);
    }

    public void drawLines(final PoseStack matrixStack, float[] points, final float thickness, final RGBA colour) {
        this.drawLines(matrixStack, points, thickness, colour, false);
    }

    public void drawLines(
        final PoseStack matrixStack,
        float[] points,
        final float thickness,
        final RGBA colour,
        final boolean isBlendEnabled) {

        this.setGlProperty(GL11.GL_LINE_SMOOTH, false);

        var bufferBuilders = Minecraft.getInstance().renderBuffers();
        var immediate = bufferBuilders.bufferSource();
        var vertexConsumer = immediate.getBuffer(RenderType.debugLineStrip(thickness));

        for (var i = 0; i < points.length; i += 2) {
            vertexConsumer
                .addVertex(matrixStack.last(), points[i], points[i + 1], 0.0F)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
        }

        immediate.endBatch();
    }

    public void drawFilledShape(final PoseStack matrixStack, final float[] points, final RGBA colour) {
        this.drawFilledShape(matrixStack, points, colour, false);
    }

    public void drawFilledShape(
        final PoseStack matrixStack,
        final float[] points,
        final RGBA colour,
        final boolean isBlendEnabled) {

        this.setGlProperty(GL11.GL_LINE_SMOOTH, false);

        var bufferBuilders = Minecraft.getInstance().renderBuffers();
        var immediate = bufferBuilders.bufferSource();
        var vertexConsumer = immediate.getBuffer(RenderType.gui());

        for (var i = 0; i < points.length; i += 2) {
            vertexConsumer
                .addVertex(matrixStack.last(), points[i], points[i + 1], 0.0F)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
        }

        immediate.endBatch();
    }

    public void drawLine(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final float thickness,
        final RGBA colour) {

        this.drawLines(matrixStack, new float[] {
            x1, y1,
            x2, y2
        }, thickness, colour);
    }

    public void drawRectangle(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final float thickness,
        final RGBA colour) {

        this.drawLines(matrixStack, new float[] {
            x1, y1,
            x2, y1,
            x2, y2,
            x1, y2,
            x1, y1
        }, thickness, colour);
    }

    public void drawFilledRectangle(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final RGBA colour) {

        this.drawFilledRectangle(matrixStack, x1, y1, x2, y2, colour, false);
    }

    public void drawFilledRectangle(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final RGBA colour,
        final boolean isBlendEnabled) {

        this.drawFilledShape(matrixStack, new float[] {
            x1, y1,
            x1, y2,
            x2, y2,
            x2, y1
        }, colour, isBlendEnabled);
    }

    public void drawBorderedRectangle(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final float borderThickness,
        final RGBA borderColour,
        final RGBA fillColour) {

        this.drawBorderedRectangle(matrixStack, x1, y1, x2, y2, borderThickness, borderColour, fillColour, false);
    }

    public void drawBorderedRectangle(
        final PoseStack matrixStack,
        final float x1, final float y1,
        final float x2, final float y2,
        final float borderThickness,
        final RGBA borderColour,
        final RGBA fillColour,
        final boolean isBlendEnabled) {

        this.drawFilledRectangle(matrixStack, x1, y1, x2, y2, fillColour, isBlendEnabled);
        this.drawRectangle(matrixStack, x1, y1, x2, y2, borderThickness, borderColour);
    }

    public void drawPartialCircle(
        final PoseStack matrixStack,
        final float x, final float y,
        final float radius,
        final int startAngleAt,
        final int endAngleAt,
        final float thickness,
        final RGBA colour) {

        this.setGlProperty(GL11.GL_LINE_SMOOTH, true);

        var startAngle = Math.max(0, Math.min(startAngleAt, endAngleAt));
        var endAngle = Math.min(360, Math.max(startAngleAt, endAngleAt));

        RenderSystem.lineWidth(thickness);

        var ratio = (float)Math.PI / 180.F;

        var bufferBuilders = Minecraft.getInstance().renderBuffers();
        var immediate = bufferBuilders.bufferSource();
        var vertexConsumer = immediate.getBuffer(RenderType.debugLineStrip(thickness));

        for (var i = startAngle; i <= endAngle; ++i) {
            var radians = (i - 90) * ratio;

            vertexConsumer
                .addVertex(matrixStack.last().pose(), x + (float)Math.cos(radians) * radius, y + (float)Math.sin(radians) * radius, 0.0F)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity());
        }

        immediate.endBatch();
    }

    public void drawCircle(
        final PoseStack matrixStack,
        final float x, final float y,
        final float radius,
        final float thickness,
        final RGBA colour) {

        this.drawPartialCircle(matrixStack, x, y, radius, 0, 360, thickness, colour);
    }

    public void drawTorus(
        final PoseStack matrixStack,
        final int x, final int y,
        final int innerRadius,
        final int outerRadius,
        final RGBA colour) {

        this.setGlProperty(GL11.GL_LINE_SMOOTH, true);

        var ratio = (float)Math.PI / 180.F;

        var bufferBuilders = Minecraft.getInstance().renderBuffers();
        var immediate = bufferBuilders.bufferSource();
        var vertexConsumer = immediate.getBuffer(RenderType.gui());

        for (var i = 0; i <= 360; ++i) {
            var radians = (i - 90) * ratio;

            vertexConsumer
                .addVertex(matrixStack.last().pose(), x + (float)Math.cos(radians) * innerRadius, y + (float)Math.sin(radians) * innerRadius, 0.0F)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity())
                .setNormal(matrixStack.last(), 0.0F, 0.0F, 0.0F);

            vertexConsumer
                .addVertex(matrixStack.last().pose(), x + (float)Math.cos(radians) * outerRadius, y + (float)Math.sin(radians) * outerRadius, 0.0F)
                .setColor(colour.getRed(), colour.getGreen(), colour.getBlue(), colour.getOpacity())
                .setNormal(matrixStack.last(), 0.0F, 0.0F, 0.0F);
        }

        immediate.endBatch();
    }

    public void drawImage(
        final PoseStack matrixStack,
        final int x, final int y,
        final CustomCrosshairDrawer image,
        final RGBA colour,
        final boolean isCentered) {

        var offsetX = isCentered ? image.getWidth() / 2.0F : 0;
        var offsetY = isCentered ? image.getHeight() / 2.0F : 0;

        var width = image.getWidth();
        var height = image.getHeight();

        for (int imageX = 0; imageX < width; ++imageX) {
            for (int imageY = 0; imageY < height; ++imageY) {
                if (image.getAt(imageX, imageY) == 1) {
                    var drawX = x + imageX - offsetX;
                    var drawY = y + imageY - offsetY;

                    this.drawFilledRectangle(matrixStack, drawX, drawY, drawX + 1, drawY + 1, colour);
                }
            }
        }
    }

    public void drawText(final GuiGraphics guiGraphics, final String text, final int x, final int y, final RGBA colour, final boolean hasShadow) {
        var colourAsInt = this.rgbaAsInt(colour);

        if (hasShadow)
            guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, colourAsInt, true);
        else
            guiGraphics.drawString(Minecraft.getInstance().font, text, x, y, colourAsInt);
    }

    public void drawSmallText(final GuiGraphics guiGraphics, final String text, final int x, final int y, final RGBA colour, final boolean hasShadow) {
        var matrixStack = guiGraphics.pose();

        matrixStack.pushPose();
        matrixStack.scale(0.5F, 0.5F, 1.0F);

        this.drawText(guiGraphics, text, x * 2, y * 2, colour, hasShadow);
        matrixStack.popPose();
    }

    public void drawBigText(final GuiGraphics guiGraphics, final String text, final int x, final int y, final RGBA colour, final boolean hasShadow) {
        var matrixStack = guiGraphics.pose();

        matrixStack.pushPose();
        matrixStack.scale(1.5F, 1.5F, 1.0F);

        this.drawText(guiGraphics, text, (int)(x * 0.666F), (int)(y * 0.666F), colour, hasShadow);
        matrixStack.popPose();
    }

    private int rgbaAsInt(final RGBA colour) {
        return (colour.getOpacity() << 24) + (colour.getRed() << 16) + (colour.getGreen() << 8) + colour.getBlue();
    }

    public int textWidth(final String text) {
        return Minecraft.getInstance().font.width(text);
    }

    public void drawInsideBounds(final GuiBounds bounds, final IDrawInsideWindowCallback callback) {
        this.setGlProperty(GL11.GL_SCISSOR_TEST, true);

        var window = Minecraft.getInstance().getWindow();
        var scale = window.getGuiScale();

        GL11.glScissor(
            (int)Math.round(bounds.x() * scale),
            (int)Math.round(window.getHeight() - (bounds.y() * scale) - (bounds.height() * scale)),
            (int)Math.round(bounds.width() * scale),
            (int)Math.round(bounds.height() * scale));

        callback.draw();

        this.setGlProperty(GL11.GL_SCISSOR_TEST, false);
    }
}