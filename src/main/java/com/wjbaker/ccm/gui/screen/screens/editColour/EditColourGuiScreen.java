package com.wjbaker.ccm.gui.screen.screens.editColour;

import com.wjbaker.ccm.CustomCrosshairMod;
import com.wjbaker.ccm.crosshair.CustomCrosshair;
import com.wjbaker.ccm.crosshair.property.IntegerProperty;
import com.wjbaker.ccm.crosshair.property.RgbaProperty;
import com.wjbaker.ccm.gui.component.components.*;
import com.wjbaker.ccm.gui.component.custom.ColourPreviewGuiComponent;
import com.wjbaker.ccm.gui.component.custom.CrosshairPreviewGuiComponent;
import com.wjbaker.ccm.gui.component.event.IOnClickEvent;
import com.wjbaker.ccm.gui.component.event.IOnValueChangedEvent;
import com.wjbaker.ccm.gui.component.type.PanelOrientation;
import com.wjbaker.ccm.gui.screen.GuiScreen;
import com.wjbaker.ccm.gui.screen.screens.editCrosshair.EditCrosshairGuiScreen;
import com.wjbaker.ccm.rendering.ModTheme;
import com.wjbaker.ccm.type.RGBA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

public final class EditColourGuiScreen extends GuiScreen {

    private final int panelWidth;
    private final RgbaProperty colour;
    private final ScrollPanelGuiComponent mainPanel;
    private final CrosshairPreviewGuiComponent crosshairPreviewPanel;

    public EditColourGuiScreen(final CustomCrosshair crosshair, final RgbaProperty colour) {
        super("Edit Colour");

        this.panelWidth = 300;
        this.colour = colour;

        HeadingGuiComponent titleHeading = new HeadingGuiComponent(this, -1, -1, "Edit Colour");

        IntegerProperty red = new IntegerProperty("fake_red", this.colour.get().getRed());
        IntegerSliderGuiComponent redSlider = new IntegerSliderGuiComponent(this, -1, -1, 260, "Red", 0, 255, red.get());
        redSlider.setBaseThumbColour(new RGBA(240, 20, 20, 255));
        redSlider.setHoverThumbColour(new RGBA(210, 40, 40, 255));
        redSlider.bind(red);
        redSlider.addEvent(IOnValueChangedEvent.class, () -> {
            this.colour.set(this.colour.get().setRed(redSlider.getValue()));
        });

        IntegerProperty green = new IntegerProperty("fake_green", this.colour.get().getGreen());
        IntegerSliderGuiComponent greenSlider = new IntegerSliderGuiComponent(this, -1, -1, 260, "Green", 0, 255, green.get());
        greenSlider.setBaseThumbColour(new RGBA(20, 240, 20, 255));
        greenSlider.setHoverThumbColour(new RGBA(40, 210, 40, 255));
        greenSlider.bind(green);
        greenSlider.addEvent(IOnValueChangedEvent.class, () -> {
            this.colour.set(this.colour.get().setGreen(greenSlider.getValue()));
        });

        IntegerProperty blue = new IntegerProperty("fake_blue", this.colour.get().getBlue());
        IntegerSliderGuiComponent blueSlider = new IntegerSliderGuiComponent(this, -1, -1, 260, "Blue", 0, 255, blue.get());
        blueSlider.setBaseThumbColour(new RGBA(20, 20, 240, 255));
        blueSlider.setHoverThumbColour(new RGBA(40, 40, 210, 255));
        blueSlider.bind(blue);
        blueSlider.addEvent(IOnValueChangedEvent.class, () -> {
            this.colour.set(this.colour.get().setBlue(blueSlider.getValue()));
        });

        IntegerProperty opacity = new IntegerProperty("fake_opacity", this.colour.get().getOpacity());
        IntegerSliderGuiComponent opacitySlider = new IntegerSliderGuiComponent(this, -1, -1, 260, "Opacity", 0, 255, opacity.get());
        opacitySlider.setBaseThumbColour(new RGBA(250, 250, 250, 255));
        opacitySlider.setHoverThumbColour(new RGBA(240, 240, 240, 255));
        opacitySlider.bind(opacity);
        opacitySlider.addEvent(IOnValueChangedEvent.class, () -> {
            this.colour.set(this.colour.get().setOpacity(opacitySlider.getValue()));
        });

        ButtonGuiComponent doneButton = new ButtonGuiComponent(this, -1, -1, 50, 35, "Done");
        doneButton.addEvent(IOnClickEvent.class, () -> {
            Minecraft.getInstance().setScreen(new EditCrosshairGuiScreen(crosshair));
        });

        ColourPreviewGuiComponent colourPreview = new ColourPreviewGuiComponent(this, -1, -1, this.colour);

        PanelGuiComponent donePanel = new PanelGuiComponent(this, -1, -1, 200, -1, PanelOrientation.HORIZONTAL);
        donePanel.setBaseBorderColour(ModTheme.TRANSPARENT);
        donePanel.setHoverBorderColour(ModTheme.TRANSPARENT);
        donePanel.addComponent(doneButton);
        donePanel.addComponent(colourPreview);
        donePanel.pack();

        PanelGuiComponent colourPickerPanel = new PanelGuiComponent(this, -1, -1, this.panelWidth, -1);
        colourPickerPanel.addComponent(titleHeading);
        colourPickerPanel.addComponent(redSlider);
        colourPickerPanel.addComponent(greenSlider);
        colourPickerPanel.addComponent(blueSlider);
        colourPickerPanel.addComponent(opacitySlider);
        colourPickerPanel.addComponent(donePanel);
        colourPickerPanel.pack();

        this.mainPanel = new ScrollPanelGuiComponent(this, 0, this.headerHeight + 1, -1, -1);
        this.mainPanel.addComponent(colourPickerPanel);
        this.mainPanel.pack();

        this.components.add(this.mainPanel);

        this.crosshairPreviewPanel = new CrosshairPreviewGuiComponent(
            this,
            -1, -1,
            CustomCrosshairMod.INSTANCE.properties().getCrosshair());
    }

    @Override
    public void update() {
        super.update();

        this.mainPanel.setSize(this.width - 1, this.height - this.headerHeight - 1);

        if (this.width > this.panelWidth + this.crosshairPreviewPanel.getWidth() + 50) {
            this.crosshairPreviewPanel.setPosition(
                this.panelWidth + ((this.width - this.panelWidth) / 2) + 15 - (this.crosshairPreviewPanel.getWidth() / 2),
                this.headerHeight + ((this.height - this.headerHeight) / 2) + 7 - (this.crosshairPreviewPanel.getHeight() / 2));
        }
        else {
            this.crosshairPreviewPanel.setPosition(
                this.width - this.crosshairPreviewPanel.getWidth() - 20,
                this.headerHeight + 7);
        }
    }

    @Override
    public void draw(final GuiGraphics guiGraphics) {
        super.draw(guiGraphics);

        this.crosshairPreviewPanel.draw(guiGraphics);
    }
}