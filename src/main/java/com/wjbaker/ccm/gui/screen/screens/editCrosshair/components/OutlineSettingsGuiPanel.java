package com.wjbaker.ccm.gui.screen.screens.editCrosshair.components;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.gui.component.components.CheckBoxGuiComponent;
import com.wjbaker.ccm.gui.component.components.ColourPickerGuiComponent;
import com.wjbaker.ccm.gui.component.components.HeadingGuiComponent;
import com.wjbaker.ccm.gui.component.components.PanelGuiComponent;
import com.wjbaker.ccm.gui.screen.GuiScreen;
import net.minecraft.client.resources.language.I18n;

public final class OutlineSettingsGuiPanel extends PanelGuiComponent {

    public OutlineSettingsGuiPanel(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final int width,
        final int height) {

        super(parentGuiScreen, x, y, width, height);

        CustomCrosshair crosshair = CustomCrosshairMod.INSTANCE.properties().getCrosshair();

        var heading = new HeadingGuiComponent(this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.outline_settings"));

        var isOutlineEnabledCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.enable_outline"), crosshair.isOutlineEnabled.get());
        isOutlineEnabledCheckBox.bind(crosshair.isOutlineEnabled);

        var outlineColourColourPicker = new ColourPickerGuiComponent(this.parentGuiScreen, crosshair, -1, -1, I18n.get("custom_crosshair_mod.screen.edit_crosshair.outline_colour"));
        outlineColourColourPicker.bind(crosshair.outlineColour);

        this.addComponent(heading);
        this.addComponent(isOutlineEnabledCheckBox);
        this.addComponent(outlineColourColourPicker);
        this.pack();
    }
}