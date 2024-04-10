package com.wjbaker.ccm.crosshair.computed;

import com.wjbaker.ccm.crosshair.CustomCrosshair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ComputeIndicators {

    public record IndicatorItem(String text, ItemStack icon) {}

    private static final Minecraft mc = Minecraft.getInstance();

    private ComputeIndicators() {}

    public static List<IndicatorItem> getIndicatorItems(final CustomCrosshair crosshair) {
        var indicatorItems = new ArrayList<IndicatorItem>();

        if (crosshair.isToolDamageEnabled.get()) {
            if (mc.player != null) {
                var tool = mc.player.getMainHandItem();
                if (tool.isDamageableItem()) {
                    var remainingDamage = tool.getMaxDamage() - tool.getDamageValue();
                    if (remainingDamage <= 10) {
                        indicatorItems.add(new IndicatorItem("" + remainingDamage, tool));
                    }
                }
            }
        }

        if (crosshair.isProjectileIndicatorEnabled.get() && mc.player != null) {
            var tool = mc.player.getMainHandItem();
            var projectile = mc.player.getProjectile(tool);
            if (projectile != ItemStack.EMPTY) {
                indicatorItems.add(new IndicatorItem("" + projectile.getCount(), projectile));
            }
        }

        return indicatorItems;
    }
}