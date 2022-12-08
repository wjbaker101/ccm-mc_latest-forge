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
import com.wjbaker.ccm.crosshair.style.ICrosshairStyle;
import com.wjbaker.ccm.render.ModTheme;
import com.wjbaker.ccm.render.RenderManager;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public final class CrosshairRenderManager {

    private final RenderManager renderManager;
    private final CrosshairStyleFactory crosshairStyleFactory;

    private final Set<Item> itemCooldownItems = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.CHORUS_FRUIT
    );

    public CrosshairRenderManager() {
        this.renderManager = new RenderManager();
        this.crosshairStyleFactory = new CrosshairStyleFactory();
    }

    public void draw(final CustomCrosshair crosshair, final int x, final int y) {
        ComputedProperties computedProperties = new ComputedProperties(crosshair);

        if (!computedProperties.isVisible())
            return;

        PoseStack matrixStack = new PoseStack();

        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Pre(
            Minecraft.getInstance().getWindow(),
            matrixStack,
            1.0F,
            VanillaGuiOverlay.CROSSHAIR.type()));

        var calculatedStyle = Minecraft.getInstance().options.renderDebug && crosshair.isKeepDebugEnabled.get()
            ? CrosshairStyle.DEBUG
            : crosshair.style.get();

        ICrosshairStyle style = this.crosshairStyleFactory.from(calculatedStyle, crosshair);
        boolean isItemCooldownEnabled = crosshair.isItemCooldownEnabled.get();
        boolean isDotEnabled = crosshair.isDotEnabled.get();

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(matrixStack, crosshair, computedProperties, x, y);

        if (isDotEnabled && crosshair.style.get() != CrosshairStyle.DEFAULT)
            this.renderManager.drawCircle(matrixStack, x, y, 0.5F, 1.0F, crosshair.dotColour.get());

        this.drawDefaultAttackIndicator(matrixStack, computedProperties, x, y);

        var transformMatrixStack = calculatedStyle == CrosshairStyle.DEBUG
            ? RenderSystem.getModelViewStack()
            : matrixStack;

        var renderX = x + crosshair.offsetX.get();
        var renderY = y + crosshair.offsetY.get();

        this.drawToolDamageIndicator(transformMatrixStack, crosshair, computedProperties, renderX, renderY);

        this.preTransformation(transformMatrixStack, crosshair, renderX, renderY);

        style.draw(transformMatrixStack, 0, 0, computedProperties);

        this.postTransformation(transformMatrixStack);

        MinecraftForge.EVENT_BUS.post(new RenderGuiOverlayEvent.Post(
            Minecraft.getInstance().getWindow(),
            matrixStack,
            1.0F,
            VanillaGuiOverlay.CROSSHAIR.type()));
    }

    private void preTransformation(
        final PoseStack matrixStack,
        final CustomCrosshair crosshair,
        final int x,
        final int y) {

        var rotation = crosshair.rotation.get();
        var scale = crosshair.scale.get() - 2;
        var windowScaling = (float)Minecraft.getInstance().getWindow().getGuiScale() / 2.0F;

        matrixStack.pushPose();
        matrixStack.translate(x, y, 0.0D);
        matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation));
        matrixStack.scale(scale / 100.0F / windowScaling, scale / 100.0F / windowScaling, 1.0F);

        RenderSystem.applyModelViewMatrix();
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

        Player player = Minecraft.getInstance().player;

        if (player == null)
            return;

        RGBA colour = crosshair.itemCooldownColour.get();

        int width = crosshair.width.get();
        int height = crosshair.height.get();
        int maxSize = Math.max(width, height);
        int offset = 3;

        for (final Item item : this.itemCooldownItems) {
            float cooldown = player.getCooldowns().getCooldownPercent(item, 0.0F);

            if (cooldown == 0.0F)
                continue;

            int progress = Math.round(360 - (360 * cooldown));

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
        final PoseStack matrixStack,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        RenderSystem.setShaderTexture(0, Gui.GUI_ICONS_LOCATION);

        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
            GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
            GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);

        var mc = Minecraft.getInstance();

        if (mc.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR && mc.player != null) {
            float f = mc.player.getAttackStrengthScale(0.0F);
            boolean flag = false;

            if (mc.crosshairPickEntity instanceof LivingEntity && f < 1.0F) {
                flag = mc.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                flag = flag & mc.crosshairPickEntity.isAlive();
            }

            int j = mc.getWindow().getGuiScaledHeight() / 2 - 7 + 16;
            int k = mc.getWindow().getGuiScaledWidth() / 2 - 8;

            if (flag) {
                GuiComponent.blit(matrixStack, k, j, 0, 68, 94, 16, 16, 256, 256);
            }
            else if (f < 1.0F) {
                int l = (int)(f * 17.0F);
                GuiComponent.blit(matrixStack, k, j, 0, 36, 94, 16, 4, 256, 256);
                GuiComponent.blit(matrixStack, k, j, 0, 52, 94, l, 4, 256, 256);
            }
        }
    }

    private void drawToolDamageIndicator(
        final PoseStack matrixStackP,
        final CustomCrosshair crosshair,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        if (!crosshair.isToolDamageEnabled.get())
            return;

        var drawX = x + crosshair.gap.get() + 5;
        var drawY = y + crosshair.gap.get() + 5;

        var mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

        var tool = mc.player.getMainHandItem();
        if (!tool.isDamageableItem())
            return;

        var remainingDamage = tool.getMaxDamage() - tool.getDamageValue();

        if (remainingDamage > 10)
            return;

        var itemRenderer = mc.getItemRenderer();
        var model = itemRenderer.getModel(tool, null, null, 0);

        mc.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        var matrixStack = RenderSystem.getModelViewStack();
        matrixStack.pushPose();
        matrixStack.translate(x, y, (100.0F + 1));
        matrixStack.translate(8.0D, 8.0D, 0.0D);
        matrixStack.scale(1.0F, -1.0F, 1.0F);
        matrixStack.scale(8F, 8F, 8F);
        RenderSystem.applyModelViewMatrix();

        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Lighting.setupForFlatItems();

        itemRenderer.render(tool, ItemTransforms.TransformType.GUI, false, new PoseStack(), bufferSource, 15728880, OverlayTexture.NO_OVERLAY, model);
        bufferSource.endBatch();

        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();

        matrixStack.popPose();

        this.renderManager.drawSmallText(matrixStackP, "" + remainingDamage, drawX + 6, drawY, ModTheme.WHITE, true);
    }
}
