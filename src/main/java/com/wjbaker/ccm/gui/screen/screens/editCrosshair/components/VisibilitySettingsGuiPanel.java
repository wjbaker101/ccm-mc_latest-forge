package com.wjbaker.ccm.gui.screen.screens.editCrosshair.components;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.gui.component.components.CheckBoxGuiComponent;
import com.wjbaker.ccm.gui.component.components.HeadingGuiComponent;
import com.wjbaker.ccm.gui.component.components.PanelGuiComponent;
import com.wjbaker.ccm.gui.screen.GuiScreen;
import net.minecraft.client.resources.language.I18n;

public final class VisibilitySettingsGuiPanel extends PanelGuiComponent {

    public VisibilitySettingsGuiPanel(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final int width,
        final int height) {

        super(parentGuiScreen, x, y, width, height);

        CustomCrosshair crosshair = CustomCrosshairMod.INSTANCE.properties().getCrosshair();

        var heading = new HeadingGuiComponent(this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visibility_settings"));

        var isVisibleDefaultCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_by_default"), crosshair.isVisibleDefault.get());
        isVisibleDefaultCheckBox.bind(crosshair.isVisibleDefault);

        var isVisibleHiddenGuiCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_hidden_gui"), crosshair.isVisibleHiddenGui.get());
        isVisibleHiddenGuiCheckBox.bind(crosshair.isVisibleHiddenGui);

        var isVisibleDebugCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_debug_gui"), crosshair.isVisibleDebug.get());
        isVisibleDebugCheckBox.bind(crosshair.isVisibleDebug);

        var isVisibleThirdPersonCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_third_person"), crosshair.isVisibleThirdPerson.get());
        isVisibleThirdPersonCheckBox.bind(crosshair.isVisibleThirdPerson);

        var isVisibleSpectatorCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_spectator_mode"), crosshair.isVisibleSpectator.get());
        isVisibleSpectatorCheckBox.bind(crosshair.isVisibleSpectator);

        var isVisibleHoldingRangedWeapon = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_holding_ranged_weapon"), crosshair.isVisibleHoldingRangedWeapon.get());
        isVisibleHoldingRangedWeapon.bind(crosshair.isVisibleHoldingRangedWeapon);

        var isVisibleHoldingThrowableItem = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_holding_throwable_item"), crosshair.isVisibleHoldingThrowableItem.get());
        isVisibleHoldingThrowableItem.bind(crosshair.isVisibleHoldingThrowableItem);

        var isVisibleUsingSpyglassCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.visible_using_spyglass"), crosshair.isVisibleUsingSpyglass.get());
        isVisibleUsingSpyglassCheckBox.bind(crosshair.isVisibleUsingSpyglass);

        this.addComponent(heading);
        this.addComponent(isVisibleDefaultCheckBox);
        this.addComponent(isVisibleHiddenGuiCheckBox);
        this.addComponent(isVisibleDebugCheckBox);
        this.addComponent(isVisibleThirdPersonCheckBox);
        this.addComponent(isVisibleSpectatorCheckBox);
        this.addComponent(isVisibleHoldingRangedWeapon);
        this.addComponent(isVisibleHoldingThrowableItem);
        this.addComponent(isVisibleUsingSpyglassCheckBox);
        this.pack();
    }
}