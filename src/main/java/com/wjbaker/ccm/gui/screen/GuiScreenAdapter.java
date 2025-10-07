package com.wjbaker.ccm.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class GuiScreenAdapter extends Screen implements IGuiScreen {

    protected GuiScreenAdapter(final String title) {
        super(Component.literal(title));
    }

    @Override
    public void tick() {
        this.update();
        super.tick();
    }

    @Override
    public void onClose() {
        this.close();
        super.onClose();
    }

    @Override
    public void render(final @NotNull GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.draw(guiGraphics);
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent click, final boolean doubled) {
        this.onMouseDown((int)click.x(), (int)click.y(), click.button());
        return super.mouseClicked(click, doubled);
    }

    @Override
    public boolean mouseReleased(final MouseButtonEvent click) {
        this.onMouseUp((int)click.x(), (int)click.y(), click.button());
        return super.mouseReleased(click);
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        this.onMouseMove((int)mouseX, (int)mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(final MouseButtonEvent click, double offsetX, double offsetY) {
        this.onMouseDrag((int)click.x(), (int)click.y(), (int)(click.x() + offsetX), (int)(click.y() + offsetY));
        return super.mouseDragged(click, offsetX, offsetY);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double amountX, final double amountY) {
        if (amountY > 0)
            this.onMouseScrollUp();
        else
            this.onMouseScrollDown();

        return super.mouseScrolled(mouseX, mouseY, amountX, amountY);
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        this.onKeyDown(event.key());
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(final KeyEvent event) {
        this.onKeyUp(event.key());
        return super.keyReleased(event);
    }
}