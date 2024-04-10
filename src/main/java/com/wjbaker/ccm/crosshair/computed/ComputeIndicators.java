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
        var indicators = new ArrayList<IndicatorItem>();

        mutateForToolDamage(indicators, crosshair);
        mutateForProjectiles(indicators, crosshair);

        return indicators;
    }

    private static void mutateForToolDamage(final List<IndicatorItem> indicators, final CustomCrosshair crosshair) {
        if (!crosshair.isToolDamageEnabled.get())
            return;

        if (mc.player == null)
            return;

        var tool = mc.player.getMainHandItem();
        if (!tool.isDamageableItem())
            return;

        var remainingDamage = tool.getMaxDamage() - tool.getDamageValue();
        if (remainingDamage > 10)
            return;

        indicators.add(new IndicatorItem("" + remainingDamage, tool));
    }

    private static void mutateForProjectiles(final List<IndicatorItem> indicators, final CustomCrosshair crosshair) {
        if (!crosshair.isProjectileIndicatorEnabled.get() || mc.player == null)
            return;

        var tool = mc.player.getMainHandItem();

        var projectile = mc.player.getProjectile(tool);
        if (projectile == ItemStack.EMPTY)
            return;

        indicators.add(new IndicatorItem("" + projectile.getCount(), projectile));
    }
}