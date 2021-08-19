package com.wjbaker.ccm.crosshair.render;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Map;
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

    private final Map<Item, Float> usageItemsDurations = ImmutableMap.of(
        Items.BOW, 20.0F,
        Items.TRIDENT, 10.0F,
        Items.CROSSBOW, 0.0F
    );

    public ComputedProperties(final CustomCrosshair crosshair) {
        this.mc = Minecraft.getInstance();
        this.crosshair = crosshair;

        this.gap = this.calculateGap();
        this.colour = this.calculateColour();
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

    private int calculateGap() {
        int baseGap = this.crosshair.gap.get();

        if (this.mc.player == null)
            return baseGap;

        boolean isSpectator = this.mc.player.isSpectator();

        ItemStack mainHandItem = this.mc.player.getMainHandItem();

        boolean isHoldingItem = !mainHandItem.isEmpty()
            || !this.mc.player.getOffhandItem().isEmpty();

        boolean isDynamicBowEnabled = this.crosshair.isDynamicBowEnabled.get();
        boolean isDynamicAttackIndicatorEnabled = this.crosshair.isDynamicAttackIndicatorEnabled.get();

        if (isSpectator || !isHoldingItem || (!isDynamicAttackIndicatorEnabled && !isDynamicBowEnabled))
            return baseGap;

        int gapModifier = 2;
        Float usageItemDuration = this.usageItemsDurations.get(this.mc.player.getUseItem().getItem());

        if (isDynamicBowEnabled && usageItemDuration != null) {
            float progress;

            if (this.mc.player.getUseItem().getItem() == Items.CROSSBOW) {
                usageItemDuration = (float) CrossbowItem.getChargeDuration(this.mc.player.getUseItem());
                progress = Math.min(usageItemDuration, usageItemDuration - this.mc.player.getTicksUsingItem());
            }
            else {
                progress = Math.min(usageItemDuration, this.mc.player.getTicksUsingItem() + this.mc.player.getUseItemRemainingTicks());
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

            float currentAttackUsage = this.mc.player.getAttackStrengthScale(0.0F);
            float maxAttackUsage = 1.0F;

            if (this.mc.player.getCurrentItemAttackStrengthDelay() > 5.0F && currentAttackUsage < maxAttackUsage)
                return baseGap + Math.round((maxAttackUsage - currentAttackUsage) * gapModifier * 20);
        }

        return baseGap;
    }

    private RGBA calculateColour() {
        Entity target = this.mc.crosshairPickEntity;

        boolean isHighlightPlayersEnabled = this.crosshair.isHighlightPlayersEnabled.get();
        if (isHighlightPlayersEnabled && target instanceof Player) {
            return this.crosshair.highlightPlayersColour.get();
        }

        boolean isHighlightHostilesEnabled = this.crosshair.isHighlightHostilesEnabled.get();
        if (isHighlightHostilesEnabled && target instanceof Enemy) {
            return this.crosshair.highlightHostilesColour.get();
        }

        boolean isHighlightPassivesEnabled = this.crosshair.isHighlightPassivesEnabled.get();
        if (isHighlightPassivesEnabled && target instanceof LivingEntity) {
            return this.crosshair.highlightPassivesColour.get();
        }

        if (this.crosshair.isRainbowEnabled.get())
            return this.getRainbowColour();

        return this.crosshair.colour.get();
    }

    private RGBA getRainbowColour() {
        int ticks = this.crosshair.rainbowTicks.get() + 1;

        if (ticks > 125000)
            ticks = 0;

        this.crosshair.rainbowTicks.set(ticks);

        int opacity = this.crosshair.colour.get().getOpacity();
        int speed = this.crosshair.rainbowSpeed.get();

        return new RGBA(255, 255, 255, opacity)
            .setRed(this.getRainbowColourComponent(ticks, 0.0F, speed))
            .setGreen(this.getRainbowColourComponent(ticks, 2.0F, speed))
            .setBlue(this.getRainbowColourComponent(ticks, 4.0F, speed));
    }

    private int getRainbowColourComponent(final int ticks, final float offset, final int speed) {
        return (int)(Math.sin((ticks * speed / 20000.0F) + offset) * 127 + 128);
    }

    private boolean calculateIsVisible() {
        if (this.mc.player == null)
            return false;

        if (!this.crosshair.isVisibleDefault.get())
            return false;

        if (!this.crosshair.isVisibleHiddenGui.get() && this.mc.options.hideGui)
            return false;

        boolean isThirdPerson = !this.mc.options.getCameraType().isFirstPerson();
        if (!this.crosshair.isVisibleThirdPerson.get() && isThirdPerson)
            return false;

        if (!this.crosshair.isVisibleDebug.get() && this.mc.options.renderDebug)
            return false;

        if (!this.crosshair.isVisibleSpectator.get() && this.mc.player.isSpectator())
            return false;

        if (!this.crosshair.isVisibleHoldingRangedWeapon.get() && this.isHoldingItem(this.mc.player, this.rangedWeapons))
            return false;

        if (!this.crosshair.isVisibleHoldingThrowableItem.get() && this.isHoldingItem(this.mc.player, this.throwableItems))
            return false;

        return true;
    }

    private boolean isHoldingItem(final Player player, final Set<Item> items) {
        ItemStack mainHandItem = player.getMainHandItem();

        boolean isMainHand = items.contains(mainHandItem.getItem());
        boolean isOffhand = items.contains(player.getOffhandItem().getItem());

        return isMainHand || (isOffhand && mainHandItem.isEmpty());
    }
}
