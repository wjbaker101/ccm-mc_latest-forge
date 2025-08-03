package com.wjbaker.ccm.rendering;

import com.wjbaker.ccm.crosshair.custom.CustomCrosshairDrawer;
import com.wjbaker.ccm.gui.types.GuiBounds;
import com.wjbaker.ccm.gui.types.IDrawInsideWindowCallback;
import com.wjbaker.ccm.rendering.types.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix3x2f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public final class RenderManager {

    private void setGlProperty(final int property, final boolean isEnabled) {
        if (isEnabled)
            GL11.glEnable(property);
        else
            GL11.glDisable(property);
    }

    public void drawLines(final GuiGraphics guiGraphics, Float[] points, final float thickness, final RGBA colour) {
        this.drawLines(guiGraphics, points, thickness, colour, false);
    }

    public void drawLines(
        final GuiGraphics guiGraphics,
        Float[] points,
        final float thickness,
        final RGBA colour,
        final boolean isBlendEnabled) {

        guiGraphics.getRenderState().submitGuiElement(new ModLinesGuiElementRenderState(new Matrix3x2f(guiGraphics.pose()), points, colour));
    }

    public void drawFilledShape(final GuiGraphics guiGraphics, final float[] points, final RGBA colour) {
        this.drawFilledShape(guiGraphics, points, colour, false);
    }

    public void drawFilledShape(
        final GuiGraphics guiGraphics,
        final float[] points,
        final RGBA colour,
        final boolean isBlendEnabled) {

        guiGraphics.getRenderState().submitGuiElement(new ModFilledGuiElementRenderState(new Matrix3x2f(guiGraphics.pose()), points, colour));
    }

    public void drawLine(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final float thickness,
        final RGBA colour) {

        this.drawLines(guiGraphics, new Float[] {
            x1, y1,
            x2, y2
        }, thickness, colour);
    }

    public void drawRectangle(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final float thickness,
        final RGBA colour) {

        this.drawLines(guiGraphics, new Float[] {
            x1, y1,
            x2, y1,
            x2, y2,
            x1, y2,
            x1, y1
        }, thickness, colour);
    }

    public void drawFilledRectangle(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final RGBA colour) {

        this.drawFilledRectangle(guiGraphics, x1, y1, x2, y2, colour, false);
    }

    public void drawFilledRectangle(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final RGBA colour,
        final boolean isBlendEnabled) {

        this.drawFilledShape(guiGraphics, new float[] {
            x1, y1,
            x1, y2,
            x2, y2,
            x2, y1
        }, colour, isBlendEnabled);
    }

    public void drawBorderedRectangle(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final float borderThickness,
        final RGBA borderColour,
        final RGBA fillColour) {

        this.drawBorderedRectangle(guiGraphics, x1, y1, x2, y2, borderThickness, borderColour, fillColour, false);
    }

    public void drawBorderedRectangle(
        final GuiGraphics guiGraphics,
        final float x1, final float y1,
        final float x2, final float y2,
        final float borderThickness,
        final RGBA borderColour,
        final RGBA fillColour,
        final boolean isBlendEnabled) {

        this.drawFilledRectangle(guiGraphics, x1, y1, x2, y2, fillColour, isBlendEnabled);
        this.drawRectangle(guiGraphics, x1, y1, x2, y2, borderThickness, borderColour);
    }

    public void drawPartialCircle(
        final GuiGraphics guiGraphics,
        final float x, final float y,
        final float radius,
        final int startAngleAt,
        final int endAngleAt,
        final float thickness,
        final RGBA colour) {

        var startAngle = Math.max(0, Math.min(startAngleAt, endAngleAt));
        var endAngle = Math.min(360, Math.max(startAngleAt, endAngleAt));
        var ratio = (float)Math.PI / 180.F;

        var points = new ArrayList<Float>();
        for (var i = startAngle; i <= endAngle; ++i) {
            var radians = (i - 90) * ratio;

            points.add(x + (float)Math.cos(radians) * radius);
            points.add(y + (float)Math.sin(radians) * radius);
        }

        guiGraphics.getRenderState().submitGuiElement(new ModLinesGuiElementRenderState(new Matrix3x2f(guiGraphics.pose()), points.toArray(new Float[0]), colour));
    }

    public void drawCircle(
        final GuiGraphics guiGraphics,
        final float x, final float y,
        final float radius,
        final float thickness,
        final RGBA colour) {

        this.drawPartialCircle(guiGraphics, x, y, radius, 0, 360, thickness, colour);
        this.drawPartialCircle(guiGraphics, x, y, radius, 0, 360, thickness, colour);
    }

    public void drawImage(
        final GuiGraphics guiGraphics,
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

                    this.drawFilledRectangle(guiGraphics, drawX, drawY, drawX + 1, drawY + 1, colour);
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

        matrixStack.pushMatrix();
        matrixStack.scale(0.5F, 0.5F);

        this.drawText(guiGraphics, text, x * 2, y * 2, colour, hasShadow);
        matrixStack.popMatrix();
    }

    public void drawBigText(final GuiGraphics guiGraphics, final String text, final int x, final int y, final RGBA colour, final boolean hasShadow) {
        var matrixStack = guiGraphics.pose();

        matrixStack.pushMatrix();
        matrixStack.scale(1.5F, 1.5F);

        this.drawText(guiGraphics, text, (int)(x * 0.666F), (int)(y * 0.666F), colour, hasShadow);
        matrixStack.popMatrix();
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
            bounds.x() * scale,
            window.getHeight() - (bounds.y() * scale) - (bounds.height() * scale),
            bounds.width() * scale,
            bounds.height() * scale);

        callback.draw();

        this.setGlProperty(GL11.GL_SCISSOR_TEST, false);
    }
}