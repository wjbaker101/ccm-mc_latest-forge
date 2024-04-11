package com.wjbaker.ccm.events;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {

    public static final KeyMapping EDIT_CROSSHAIR = new KeyMapping(
        "keybind.custom_crosshair_mod.open_edit_crosshair_gui",
        GLFW.GLFW_KEY_GRAVE_ACCENT,
        "gui.custom_crosshair_mod.mod_keybinds_category");
}