package com.wjbaker.ccm.crosshair.render;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.style.CrosshairStyle;
import com.wjbaker.ccm.crosshair.style.CrosshairStyleFactory;
import com.wjbaker.ccm.crosshair.style.ICrosshairStyle;
import com.wjbaker.ccm.render.RenderManager;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

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

        MatrixStack matrixStack = new MatrixStack();

        RenderGameOverlayEvent eventParent = new RenderGameOverlayEvent(
            matrixStack,
            1.0F,
            Minecraft.getInstance().getMainWindow());

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Pre(
            matrixStack,
            eventParent,
            RenderGameOverlayEvent.ElementType.CROSSHAIRS));

        ICrosshairStyle style = this.crosshairStyleFactory.from(this.crosshair.style.get(), this.crosshair);
        boolean isItemCooldownEnabled = this.crosshair.isItemCooldownEnabled.get();
        boolean isDotEnabled = this.crosshair.isDotEnabled.get();

        if (isItemCooldownEnabled)
            this.drawItemCooldownIndicator(computedProperties, x, y);

        if (isDotEnabled && this.crosshair.style.get() != CrosshairStyle.DEFAULT)
            this.renderManager.drawCircle(x, y, 0.5F, 1.0F, this.crosshair.dotColour.get());

        this.preRotation(x, y);

        style.draw(x, y, computedProperties);

        this.postRotation();

        MinecraftForge.EVENT_BUS.post(new RenderGameOverlayEvent.Post(
            matrixStack,
            eventParent,
            RenderGameOverlayEvent.ElementType.CROSSHAIRS));
    }

    private void preRotation(final int x, final int y) {
        int rotation = this.crosshair.rotation.get();

        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0);
        GL11.glRotatef(rotation, x, y, 8000);
        GL11.glTranslatef(-x, -y, 0);
    }

    private void postRotation() {
        GL11.glPopMatrix();
    }

    private void drawItemCooldownIndicator(
        final ComputedProperties computedProperties,
        final int x, final int y) {

        ClientPlayerEntity player = Minecraft.getInstance().player;

        if (player == null)
            return;

        RGBA colour = this.crosshair.itemCooldownColour.get();

        int width = this.crosshair.width.get();
        int height = this.crosshair.height.get();
        int maxSize = Math.max(width, height);
        int offset = 3;

        for (final Item item : this.itemCooldownItems) {
            float cooldown = player.getCooldownTracker().getCooldown(item, 0.0F);

            if (cooldown == 0.0F)
                continue;

            int progress = Math.round(360 - (360 * cooldown));

            this.renderManager.drawPartialCircle(
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
