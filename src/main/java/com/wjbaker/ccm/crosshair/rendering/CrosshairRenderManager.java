package com.wjbaker.ccm.crosshair.rendering;

import com.google.common.collect.ImmutableSet;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputedProperties;
import com.wjbaker.ccm.crosshair.styles.*;
import com.wjbaker.ccm.crosshair.types.BaseCrosshairStyle;
import com.wjbaker.ccm.rendering.ModTheme;
import com.wjbaker.ccm.rendering.RenderManager;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import org.joml.Matrix3x2fStack;

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

    public void draw(final CustomCrosshair crosshair, final GuiGraphics guiGraphics, final int x, final int y) {
        var computedProperties = new ComputedProperties(crosshair);

        if (!computedProperties.isVisible())
            return;

        var calculatedStyle = Minecraft.getInstance().getDebugOverlay().showDebugScreen() && crosshair.isKeepDebugEnabled.get()
            ? BaseCrosshairStyle.Styles.DEBUG
            : crosshair.style.get();

        var style = mapCrosshairStyle(calculatedStyle, crosshair);
        var isItemCooldownEnabled = crosshair.isItemCooldownEnabled.get();
        var isDotEnabled = crosshair.isDotEnabled.get();

        var matrices = guiGraphics.pose();

        var renderX = x + crosshair.offsetX.get();
        var renderY = y + crosshair.offsetY.get();

        this.drawIndicators(guiGraphics, crosshair, computedProperties, renderX, renderY);

        this.preTransformation(matrices, crosshair, renderX, renderY);

        this.drawDefaultAttackIndicator(guiGraphics);

        if (isDotEnabled && crosshair.style.get() != BaseCrosshairStyle.Styles.VANILLA)
            this.renderManager.drawCircle(guiGraphics, 0, 0, 0.5F, 1.0F, crosshair.dotColour.get());

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(guiGraphics, crosshair, computedProperties);

        style.draw(guiGraphics, 0, 0, computedProperties);

        this.postTransformation(matrices, crosshair);
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
        final Matrix3x2fStack matrices,
        final CustomCrosshair crosshair,
        final int x,
        final int y) {

        if (crosshair.style.get() == BaseCrosshairStyle.Styles.DEBUG) {
            return;
        }

        matrices.pushMatrix();

        var rotation = crosshair.rotation.get();
        var scale = crosshair.scale.get() - 2;

        matrices.scale(scale / 100.0F, scale / 100.0F);
        matrices.rotateLocal(rotation);

        matrices.translateLocal(x, y);
    }

    private void postTransformation(final Matrix3x2fStack matrixStack, final CustomCrosshair crosshair) {
        if (crosshair.style.get() == BaseCrosshairStyle.Styles.DEBUG) {
            return;
        }

        matrixStack.popMatrix();
    }

    private void drawItemCooldownIndicator(
        final GuiGraphics guiGraphics,
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
                guiGraphics,
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
                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, drawX, drawY, 16, 16);
            }
            else if (f < 1.0F) {
                var l = (int)(f * 17.0F);
                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, drawX, drawY, 16, 4);
                guiGraphics.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, drawX, drawY, l, 4);
            }
        }
    }

    private void drawIndicators(
        final GuiGraphics guiGraphics,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        var drawX = x + crosshair.gap.get() + 5;
        var drawY = y + crosshair.gap.get() + 5;

        var indicatorItems = computedProperties.indicatorItems();

        var matrixStack = guiGraphics.pose();

        for (var indicatorItem : indicatorItems) {
            matrixStack.scale(0.5F, 0.5F);
            guiGraphics.renderItem(indicatorItem.icon(), drawX * 2 - 8, drawY * 2 - 8);
            matrixStack.scale(2F, 2F);

            this.renderManager.drawSmallText(guiGraphics, indicatorItem.text(), drawX + 5, drawY, ModTheme.WHITE, true);

            drawX += 15;
        }
    }
}