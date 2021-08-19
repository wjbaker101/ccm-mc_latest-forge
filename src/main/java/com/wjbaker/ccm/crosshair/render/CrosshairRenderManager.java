package com.wjbaker.ccm.crosshair.render;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.style.CrosshairStyle;
import com.wjbaker.ccm.crosshair.style.CrosshairStyleFactory;
import com.wjbaker.ccm.crosshair.style.ICrosshairStyle;
import com.wjbaker.ccm.render.RenderManager;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.Minecraft;
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

        this.preTransformation(matrixStack, x, y);

        style.draw(matrixStack, 0, 0, computedProperties);

        this.postTransformation(matrixStack);

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.PostLayer(
            matrixStack,
            eventParent,
            ForgeIngameGui.CROSSHAIR_ELEMENT));
    }

    private void preTransformation(final PoseStack matrixStack, final int x, final int y) {
        var rotation = this.crosshair.rotation.get();
        var scale = this.crosshair.scale.get() - 2;
        var windowScaling = (float)Minecraft.getInstance().getWindow().getGuiScale() / 2.0F;

        matrixStack.translate(x, y, 0.0D);
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(rotation));
        matrixStack.scale(scale / 100.0F / windowScaling, scale / 100.0F / windowScaling, 1.0F);
    }

    private void postTransformation(final PoseStack matrixStack) {
        matrixStack.popPose();
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
}
