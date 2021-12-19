package com.wjbaker.ccm.crosshair.render;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.style.CrosshairStyle;
import com.wjbaker.ccm.crosshair.style.CrosshairStyleFactory;
import com.wjbaker.ccm.crosshair.style.ICrosshairStyle;
import com.wjbaker.ccm.render.RenderManager;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.common.MinecraftForge;

import java.util.Set;

public final class CrosshairRenderManager {

    private final CustomCrosshair crosshair;
    private final RenderManager renderManager;
    private final CrosshairStyleFactory crosshairStyleFactory;

    private final Set<Item> itemCooldownItems = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.CHORUS_FRUIT
    );

    public CrosshairRenderManager(final CustomCrosshair crosshair) {
        this.crosshair = crosshair;
        this.renderManager = new RenderManager();
        this.crosshairStyleFactory = new CrosshairStyleFactory();
    }

    public void draw(final int x, final int y) {
        ComputedProperties computedProperties = new ComputedProperties(this.crosshair);

        if (!computedProperties.isVisible())
            return;

        PoseStack matrixStack = new PoseStack();

        RenderGameOverlayEvent eventParent = new RenderGameOverlayEvent(
            matrixStack,
            1.0F,
            Minecraft.getInstance().getWindow());

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.PreLayer(
            matrixStack,
            eventParent,
            ForgeIngameGui.CROSSHAIR_ELEMENT));

        ICrosshairStyle style = this.crosshairStyleFactory.from(this.crosshair.style.get(), this.crosshair);
        boolean isItemCooldownEnabled = this.crosshair.isItemCooldownEnabled.get();
        boolean isDotEnabled = this.crosshair.isDotEnabled.get();

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(matrixStack, computedProperties, x, y);

        if (isDotEnabled && this.crosshair.style.get() != CrosshairStyle.DEFAULT)
            this.renderManager.drawCircle(matrixStack, x, y, 0.5F, 1.0F, this.crosshair.dotColour.get());

        this.drawDefaultAttackIndicator(matrixStack, computedProperties, x, y);

        var transformMatrixStack = this.crosshair.style.get() == CrosshairStyle.DEBUG
            ? RenderSystem.getModelViewStack()
            : matrixStack;

        this.preTransformation(transformMatrixStack, x, y);

        style.draw(transformMatrixStack, 0, 0, computedProperties);

        this.postTransformation(transformMatrixStack);

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.PostLayer(
            matrixStack,
            eventParent,
            ForgeIngameGui.CROSSHAIR_ELEMENT));
    }

    private void preTransformation(final PoseStack matrixStack, final int x, final int y) {
        var rotation = this.crosshair.rotation.get();
        var scale = this.crosshair.scale.get() - 2;
        var windowScaling = (float)Minecraft.getInstance().getWindow().getGuiScale() / 2.0F;

        matrixStack.pushPose();
        matrixStack.translate(x, y, 0.0D);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        matrixStack.scale(scale / 100.0F / windowScaling, scale / 100.0F / windowScaling, 1.0F);

        RenderSystem.applyModelViewMatrix();
    }

    private void postTransformation(final PoseStack matrixStack) {
        matrixStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

    private void drawItemCooldownIndicator(
        final PoseStack matrixStack,
        final ComputedProperties computedProperties,
        final int x, final int y) {

        Player player = Minecraft.getInstance().player;

        if (player == null)
            return;

        RGBA colour = this.crosshair.itemCooldownColour.get();

        int width = this.crosshair.width.get();
        int height = this.crosshair.height.get();
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

        if (mc.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR && mc.player != null) {
            float f = mc.player.getAttackStrengthScale(0.0F);
            boolean flag = false;

            if (mc.crosshairPickEntity instanceof LivingEntity && f >= 1.0F) {
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
}
