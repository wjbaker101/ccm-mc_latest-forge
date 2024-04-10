package com.wjbaker.ccm.gui.screen.screens.editCrosshair.components;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.gui.component.components.CheckBoxGuiComponent;
import com.wjbaker.ccm.gui.component.components.PanelGuiComponent;
import com.wjbaker.ccm.gui.component.components.ColourPickerGuiComponent;
import com.wjbaker.ccm.gui.component.components.HeadingGuiComponent;
import com.wjbaker.ccm.gui.screen.GuiScreen;

public final class DotSettingsGuiPanel extends PanelGuiComponent {

    public DotSettingsGuiPanel(
        final GuiScreen parentGuiScreen,
        final int x,
        final int y,
        final int width,
        final int height) {

        super(parentGuiScreen, x, y, width, height);

        CustomCrosshair crosshair = CustomCrosshairMod.INSTANCE.properties().getCrosshair();

        HeadingGuiComponent heading = new HeadingGuiComponent(this.parentGuiScreen, -1, -1, "Dot Settings");

        CheckBoxGuiComponent isDotEnabledCheckBox = new CheckBoxGuiComponent(
            this.parentGuiScreen, -1, -1, "Enable Dot", crosshair.isDotEnabled.get());
        isDotEnabledCheckBox.bind(crosshair.isDotEnabled);

        ColourPickerGuiComponent dotColourColourPicker = new ColourPickerGuiComponent(
            this.parentGuiScreen,
            crosshair,
            -1,
            -1,
            "Dot Colour");
        dotColourColourPicker.bind(crosshair.dotColour);

        this.addComponent(heading);
        this.addComponent(isDotEnabledCheckBox);
        this.addComponent(dotColourColourPicker);
        this.pack();
    }
}
