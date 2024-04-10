package com.wjbaker.ccm.crosshair.rendering;

import com.google.common.collect.ImmutableSet;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.computed.ComputeColour;
import com.wjbaker.ccm.crosshair.computed.ComputeGap;
import com.wjbaker.ccm.crosshair.computed.ComputeIndicators;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;
import java.util.Set;

public final class ComputedProperties {

    private final Minecraft mc;
    private final CustomCrosshair crosshair;

    private final int gap;
    private final RGBA colour;
    private final boolean isVisible;

    private final Set<Item> rangedWeapons = ImmutableSet.of(
        Items.BOW,
        Items.TRIDENT,
        Items.CROSSBOW
    );

    private final Set<Item> throwableItems = ImmutableSet.of(
        Items.ENDER_PEARL,
        Items.ENDER_EYE
    );

    public ComputedProperties(final CustomCrosshair crosshair) {
        this.mc = Minecraft.getInstance();
        this.crosshair = crosshair;

        this.gap = ComputeGap.compute(crosshair);
        this.colour = ComputeColour.compute(crosshair);
        this.isVisible = this.calculateIsVisible();
    }

    public int gap() {
        return this.gap;
    }

    public RGBA colour() {
        return this.colour;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    private boolean calculateIsVisible() {
        if (this.mc.player == null)
            return false;

        if (!this.crosshair.isVisibleDefault.get())
            return false;

        if (!this.crosshair.isVisibleHiddenGui.get() && this.mc.options.hideGui)
            return false;

        var isThirdPerson = !this.mc.options.getCameraType().isFirstPerson();
        if (!this.crosshair.isVisibleThirdPerson.get() && isThirdPerson)
            return false;

        if (!this.crosshair.isVisibleDebug.get() && this.mc.getDebugOverlay().showDebugScreen())
            return false;

        if (!this.crosshair.isVisibleSpectator.get() && this.mc.player.isSpectator())
            return false;

        if (!this.crosshair.isVisibleHoldingRangedWeapon.get() && this.isHoldingItem(this.mc.player, this.rangedWeapons))
            return false;

        if (!this.crosshair.isVisibleHoldingThrowableItem.get() && this.isHoldingItem(this.mc.player, this.throwableItems))
            return false;

        if (!this.crosshair.isVisibleUsingSpyglass.get() && this.mc.player.isScoping())
            return false;

        return true;
    }

    private boolean isHoldingItem(final Player player, final Set<Item> items) {
        var mainHandItem = player.getMainHandItem();

        var isMainHand = items.contains(mainHandItem.getItem());
        var isOffhand = items.contains(player.getOffhandItem().getItem());

        return isMainHand || (isOffhand && mainHandItem.isEmpty());
    }

    public List<ComputeIndicators.IndicatorItem> getIndicatorItems() {
        return ComputeIndicators.getIndicatorItems(this.crosshair);
    }
}