package com.wjbaker.ccm.crosshair.computed;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class ComputeIndicators {

    public record IndicatorItem(String text, ItemStack icon) {}

    private static final Minecraft mc = Minecraft.getInstance();

    private final CustomCrosshair crosshair;

    public ComputeIndicators(final CustomCrosshair crosshair) {
        this.crosshair = crosshair;
    }

    public List<IndicatorItem> getIndicatorItems() {
        var indicatorItems = new ArrayList<IndicatorItem>();

        if (crosshair.isToolDamageEnabled.get()) {
            if (this.mc.player != null) {
                var tool = mc.player.getMainHandItem();
                if (tool.isDamageableItem()) {
                    var remainingDamage = tool.getMaxDamage() - tool.getDamageValue();
                    if (remainingDamage <= 10) {
                        indicatorItems.add(new IndicatorItem("" + remainingDamage, tool));
                    }
                }
            }
        }

        if (crosshair.isProjectileIndicatorEnabled.get() && this.mc.player != null) {
            var tool = mc.player.getMainHandItem();
            var projectile = this.mc.player.getProjectile(tool);
            if (projectile != ItemStack.EMPTY) {
                indicatorItems.add(new IndicatorItem("" + projectile.getCount(), projectile));
            }
        }

        return indicatorItems;
    }
}