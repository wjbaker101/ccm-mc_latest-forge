package com.wjbaker.ccm.crosshair.computed;

import com.google.common.collect.ImmutableSet;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Set;

public abstract class ComputeVisibility {

    private static final Minecraft mc = Minecraft.getInstance();

    private static final Set<Item> RANGED_WEAPONS = ImmutableSet.of(
        Items.BOW,
        Items.TRIDENT,
        Items.CROSSBOW
    );

    private static final Set<Item> THROWABLE_ITEMS = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.ENDER_EYE
    );

    private ComputeVisibility() {}

    public static boolean compute(final CustomCrosshair crosshair) {
        if (mc.player == null)
            return false;

        if (!crosshair.isVisibleDefault.get())
            return false;

        if (!crosshair.isVisibleHiddenGui.get() && mc.options.hideGui)
            return false;

        var isThirdPerson = !mc.options.getCameraType().isFirstPerson();
        if (!crosshair.isVisibleThirdPerson.get() && isThirdPerson)
            return false;

        if (!crosshair.isVisibleDebug.get() && mc.getDebugOverlay().showDebugScreen())
            return false;

        if (!crosshair.isVisibleSpectator.get() && mc.player.isSpectator())
            return false;

        if (!crosshair.isVisibleHoldingRangedWeapon.get() && isHoldingItem(mc.player, RANGED_WEAPONS))
            return false;

        if (!crosshair.isVisibleHoldingThrowableItem.get() && isHoldingItem(mc.player, THROWABLE_ITEMS))
            return false;

        if (!crosshair.isVisibleUsingSpyglass.get() && mc.player.isScoping())
            return false;

        return true;
    }

    private static boolean isHoldingItem(final Player player, final Set<Item> items) {
        var mainHandItem = player.getMainHandItem();

        var isMainHand = items.contains(mainHandItem.getItem());
        var isOffhand = items.contains(player.getOffhandItem().getItem());

        return isMainHand || (isOffhand && mainHandItem.isEmpty());
    }
}