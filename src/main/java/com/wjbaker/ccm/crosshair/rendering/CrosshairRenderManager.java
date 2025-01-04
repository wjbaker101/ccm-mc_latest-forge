package com.wjbaker.ccm.crosshair.rendering;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.styles.*;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import com.wjbaker.ccm.rendering.ModTheme;
import com.wjbaker.ccm.rendering.RenderManager;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Set;

public final class CrosshairRenderManager {

    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background");
    private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress");

    private final RenderManager renderManager;

    private final Set<Item> itemCooldownItems = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.CHORUS_FRUIT
    );

    public CrosshairRenderManager() {
        this.renderManager = new RenderManager();
    }

    public void draw(final CustomCrosshair crosshair, final int x, final int y) {
        var computedProperties = new ComputedProperties(crosshair);

        if (!computedProperties.isVisible())
            return;

        var guiGraphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());

        var calculatedStyle = Minecraft.getInstance().getDebugOverlay().showDebugScreen() && crosshair.isKeepDebugEnabled.get()
            ? BaseCrosshairStyle.Styles.DEBUG
            : crosshair.style.get();

        var style = mapCrosshairStyle(calculatedStyle, crosshair);
        var isItemCooldownEnabled = crosshair.isItemCooldownEnabled.get();
        var isDotEnabled = crosshair.isDotEnabled.get();

        var transformMatrixStack = guiGraphics.pose();

        var renderX = x + crosshair.offsetX.get();
        var renderY = y + crosshair.offsetY.get();
        var scale = crosshair.scale.get() - 2;
        var windowScaling = (float)Minecraft.getInstance().getWindow().getGuiScale() / 2.0F;

        this.preTransformation(transformMatrixStack, crosshair, renderX, renderY);

        this.drawIndicators(guiGraphics, crosshair, computedProperties, renderX, renderY);

        transformMatrixStack.mulPose(Axis.ZP.rotationDegrees(crosshair.rotation.get()));
        transformMatrixStack.scale(scale / 100.0F / windowScaling, scale / 100.0F / windowScaling, 1.0F);

        this.drawDefaultAttackIndicator(guiGraphics);

        if (isDotEnabled && crosshair.style.get() != BaseCrosshairStyle.Styles.VANILLA)
            this.renderManager.drawCircle(guiGraphics.pose(), 0, 0, 0.5F, 1.0F, crosshair.dotColour.get());

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(guiGraphics.pose(), crosshair, computedProperties);

        style.draw(guiGraphics, 0, 0, computedProperties);

        this.postTransformation(transformMatrixStack);
    }

    private BaseCrosshairStyle mapCrosshairStyle(final BaseCrosshairStyle.Styles style, final CustomCrosshair crosshair) {
        return switch (style) {
            case VANILLA -> new VanillaStyle(crosshair);
            case CIRCLE -> new CircleStyle(crosshair);
            case SQUARE -> new SquareStyle(crosshair);
            case TRIANGLE -> new TriangleStyle(crosshair);
            case ARROW -> new ArrowStyle(crosshair);
            case DEBUG -> new DebugStyle(crosshair);
            case DRAWN -> new DrawnStyle(crosshair);

            default -> new CrossStyle(crosshair);
        };
    }

    private void preTransformation(
        final PoseStack matrixStack,
        final CustomCrosshair crosshair,
        final int x,
        final int y) {

        matrixStack.pushPose();
        matrixStack.translate(x, y, 0);
    }

    private void postTransformation(final PoseStack matrixStack) {
        matrixStack.popPose();
    }

    private void drawItemCooldownIndicator(
        final PoseStack matrixStack,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties) {

        var player = Minecraft.getInstance().player;
        if (player == null)
            return;

        var colour = crosshair.itemCooldownColour.get();

        var width = crosshair.width.get();
        var height = crosshair.height.get();
        var maxSize = Math.max(width, height);
        var offset = 3;

        for (final Item item : this.itemCooldownItems) {
            var cooldown = player.getCooldowns().getCooldownPercent(item.getDefaultInstance(), 0.0F);
            if (cooldown == 0.0F)
                continue;

            var progress = Math.round(360 - (360 * cooldown));

            this.renderManager.drawPartialCircle(
                matrixStack,
                0, 0,
                computedProperties.gap() + maxSize + offset,
                0,
                progress,
                2.0F,
                colour);

            offset += 3;
        }
    }

    private void drawDefaultAttackIndicator(final GuiGraphics guiGraphics) {
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
            GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        var mc = Minecraft.getInstance();

        if (mc.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR && mc.player != null) {
            var f = mc.player.getAttackStrengthScale(0.0F);
            var flag = false;

            if (mc.crosshairPickEntity instanceof LivingEntity && f < 1.0F) {
                flag = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag = flag & mc.crosshairPickEntity.isAlive();
            }

            var drawX = -8;
            var drawY = -7 + 16;

            if (flag) {
                guiGraphics.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, drawX, drawY, 16, 16);
            } else if (f < 1.0F) {
                int l = (int)(f * 17.0F);
                guiGraphics.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, drawX, drawY, 16, 4);
                guiGraphics.blitSprite(RenderType::crosshair, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, drawX, drawY, l, 4);
            }
        }
    }

    private void drawIndicators(
        final GuiGraphics guiGraphics,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        var drawX = crosshair.gap.get() + 5;
        var drawY = crosshair.gap.get() + 5;

        var indicatorItems = computedProperties.indicatorItems();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupForFlatItems();

        var matrixStack = guiGraphics.pose();

        for (var indicatorItem : indicatorItems) {
            matrixStack.scale(0.5F, 0.5F, 1F);
            guiGraphics.renderItem(indicatorItem.icon(), drawX * 2 - 8, drawY * 2 - 8);
            matrixStack.scale(2F, 2F, 1F);

            this.renderManager.drawSmallText(guiGraphics, indicatorItem.text(), drawX + 5, drawY, ModTheme.WHITE, true);

            drawX += 15;
        }

        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
    }
}