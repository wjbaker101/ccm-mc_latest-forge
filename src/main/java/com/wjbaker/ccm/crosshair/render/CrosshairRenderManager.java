package com.wjbaker.ccm.crosshair.render;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.style.CrosshairStyle;
import com.wjbaker.ccm.crosshair.style.CrosshairStyleFactory;
import com.wjbaker.ccm.render.ModTheme;
import com.wjbaker.ccm.render.RenderManager;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public final class CrosshairRenderManager {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation("textures/gui/icons.png");

    private final boolean isForGui;
    private final RenderManager renderManager;
    private final CrosshairStyleFactory crosshairStyleFactory;

    private final Set<Item> itemCooldownItems = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.CHORUS_FRUIT
    );

    public CrosshairRenderManager(final boolean isForGui) {
        this.isForGui = isForGui;
        this.renderManager = new RenderManager();
        this.crosshairStyleFactory = new CrosshairStyleFactory();
    }

    public void draw(final CustomCrosshair crosshair, final int x, final int y) {
        var computedProperties = new ComputedProperties(crosshair);

        if (!computedProperties.isVisible())
            return;

        var guiGraphics = new GuiGraphics(Minecraft.getInstance(), Minecraft.getInstance().renderBuffers().bufferSource());

        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Pre(
            Minecraft.getInstance().getWindow(),
            guiGraphics,
            1.0F,
            VanillaGuiOverlay.CROSSHAIR.type()));

        var calculatedStyle = Minecraft.getInstance().getDebugOverlay().showDebugScreen() && crosshair.isKeepDebugEnabled.get()
            ? CrosshairStyle.DEBUG
            : crosshair.style.get();

        var style = this.crosshairStyleFactory.from(calculatedStyle, crosshair);
        var isItemCooldownEnabled = crosshair.isItemCooldownEnabled.get();
        var isDotEnabled = crosshair.isDotEnabled.get();

        var transformMatrixStack = calculatedStyle == CrosshairStyle.DEBUG
            ? RenderSystem.getModelViewStack()
            : guiGraphics.pose();

        var renderX = x + crosshair.offsetX.get();
        var renderY = y + crosshair.offsetY.get();
        var scale = crosshair.scale.get() - 2;
        var windowScaling = (float)Minecraft.getInstance().getWindow().getGuiScale() / 2.0F;

        this.preTransformation(transformMatrixStack, crosshair, renderX, renderY);

        this.drawToolDamageIndicator(guiGraphics, crosshair, computedProperties, renderX, renderY);

        transformMatrixStack.mulPose(Axis.ZP.rotationDegrees(crosshair.rotation.get()));
        transformMatrixStack.scale(scale / 100.0F / windowScaling, scale / 100.0F / windowScaling, 1.0F);
        RenderSystem.applyModelViewMatrix();

        this.drawDefaultAttackIndicator(guiGraphics, computedProperties, 0, 0);

        if (isDotEnabled && crosshair.style.get() != CrosshairStyle.DEFAULT)
            this.renderManager.drawCircle(guiGraphics.pose(), 0, 0, 0.5F, 1.0F, crosshair.dotColour.get());

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(guiGraphics.pose(), crosshair, computedProperties, 0, 0);

        style.draw(guiGraphics, 0, 0, computedProperties);

        this.postTransformation(transformMatrixStack);

        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(
            Minecraft.getInstance().getWindow(),
            guiGraphics,
            1.0F,
            VanillaGuiOverlay.CROSSHAIR.type()));
    }

    private void preTransformation(
        final PoseStack matrixStack,
        final CustomCrosshair crosshair,
        final int x,
        final int y) {

        var z = this.isForGui ? 0.0F : 1000F - ForgeHooksClient.getGuiFarPlane();

        matrixStack.pushPose();
        matrixStack.translate(x, y, z);
    }

    private void postTransformation(final PoseStack matrixStack) {
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private void drawItemCooldownIndicator(
        final PoseStack matrixStack,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties,
        final int x,
        final int y) {

        var player = Minecraft.getInstance().player;
        if (player == null)
            return;

        var colour = crosshair.itemCooldownColour.get();

        var width = crosshair.width.get();
        var height = crosshair.height.get();
        var maxSize = Math.max(width, height);
        var offset = 3;

        for (final Item item : this.itemCooldownItems) {
            var cooldown = player.getCooldowns().getCooldownPercent(item, 0.0F);
            if (cooldown == 0.0F)
                continue;

            var progress = Math.round(360 - (360 * cooldown));

            this.renderManager.drawPartialCircle(
                matrixStack,
                x, y,
                computedProperties.gap() + maxSize + offset,
                0,
                progress,
                2.0F,
                colour);

            offset += 3;
        }
    }

    private void drawDefaultAttackIndicator(
        final GuiGraphics guiGraphics,
        final ComputedProperties computedProperties,
        final int x, final int y) {

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

            var drawX = x - 8;
            var drawY = y - 7 + 16;

            if (flag) {
                guiGraphics.blit(GUI_ICONS_LOCATION, drawX, drawY, 0, 68, 94, 16, 16, 256, 256);
            }
            else if (f < 1.0F) {
                int l = (int)(f * 17.0F);
                guiGraphics.blit(GUI_ICONS_LOCATION ,drawX, drawY, 0, 36, 94, 16, 4, 256, 256);
                guiGraphics.blit(GUI_ICONS_LOCATION, drawX, drawY, 0, 52, 94, l, 4, 256, 256);
            }
        }
    }

    private void drawToolDamageIndicator(
        final GuiGraphics guiGraphics,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        var drawX = crosshair.gap.get() + 5;
        var drawY = crosshair.gap.get() + 5;

        var mc = Minecraft.getInstance();
        var itemRenderer = mc.getItemRenderer();
        var indicatorItems = computedProperties.getIndicatorItems();

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupForFlatItems();

        var matrixStack = guiGraphics.pose();
        var bufferSource = guiGraphics.bufferSource();

        for (var indicatorItem : indicatorItems) {
            matrixStack.pushPose();
            matrixStack.translate(drawX, drawY, 0.0D);
            matrixStack.scale(1.0F, -1.0F, 1.0F);
            matrixStack.scale(8F, 8F, 8F);

            var model = itemRenderer.getModel(indicatorItem.getIcon(), null, null, 0);
            itemRenderer.render(indicatorItem.getIcon(), ItemDisplayContext.GUI, false, matrixStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, model);

            bufferSource.endBatch();
            matrixStack.popPose();

            this.renderManager.drawSmallText(guiGraphics, indicatorItem.getText(), drawX + 5, drawY, ModTheme.WHITE, true);

            drawX += 15;
        }

        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
    }
}