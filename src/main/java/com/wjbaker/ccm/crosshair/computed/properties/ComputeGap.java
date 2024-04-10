package com.wjbaker.ccm.crosshair.computed.properties;

import com.google.common.collect.ImmutableMap;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Map;

public abstract class ComputeGap {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final Map<Item, Float> ITEM_DURATIONS = ImmutableMap.of(
        Items.BOW, 20.0F,
        Items.TRIDENT, 10.0F,
        Items.CROSSBOW, 0.0F
    );

    private ComputeGap() {}

    public static int compute(final CustomCrosshair crosshair) {
        var baseGap = crosshair.gap.get();

        if (mc.player == null)
            return baseGap;

        var isSpectator = mc.player.isSpectator();

        var mainHandItem = mc.player.getMainHandItem();

        var isHoldingItem = !mainHandItem.isEmpty()
            || !mc.player.getOffhandItem().isEmpty();

        var isDynamicBowEnabled = crosshair.isDynamicBowEnabled.get();
        var isDynamicAttackIndicatorEnabled = crosshair.isDynamicAttackIndicatorEnabled.get();

        if (isSpectator || !isHoldingItem || (!isDynamicAttackIndicatorEnabled && !isDynamicBowEnabled))
            return baseGap;

        var gapModifier = 2;
        var usageItemDuration = ITEM_DURATIONS.get(mc.player.getUseItem().getItem());

        if (isDynamicBowEnabled && usageItemDuration != null) {
            float progress;

            if (mc.player.getUseItem().getItem() == Items.CROSSBOW) {
                usageItemDuration = (float) CrossbowItem.getChargeDuration(mc.player.getUseItem());
                progress = Math.min(usageItemDuration, mc.player.getTicksUsingItem());
            }
            else {
                progress = Math.min(usageItemDuration, mc.player.getTicksUsingItem());
            }

            return baseGap + Math.round((usageItemDuration - progress) * gapModifier);
        }
        else if (isDynamicAttackIndicatorEnabled) {
            var hasAttackSpeedModifier = mainHandItem
                .getItem()
                .getAttributeModifiers(EquipmentSlot.MAINHAND, mainHandItem)
                .entries()
                .stream()
                .anyMatch(x -> x.getKey().equals(Attributes.ATTACK_SPEED));

            if (!hasAttackSpeedModifier)
                return baseGap;

            var currentAttackUsage = mc.player.getAttackStrengthScale(0.0F);
            var maxAttackUsage = 1.0F;

            if (mc.player.getCurrentItemAttackStrengthDelay() > 5.0F && currentAttackUsage < maxAttackUsage)
                return baseGap + Math.round((maxAttackUsage - currentAttackUsage) * gapModifier * 20);
        }

        return baseGap;
    }
}