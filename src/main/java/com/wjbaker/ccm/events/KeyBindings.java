package com.wjbaker.ccm.events;

import com.mojang.blaze3d.platform.InputConstants;
import cpw.mods.util.Lazy;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {

    public static final Lazy<KeyMapping> EDIT_CROSSHAIR = Lazy.of(() -> new KeyMapping(
        "keybind.custom_crosshair_mod.open_edit_crosshair_gui",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_GRAVE_ACCENT,
        new KeyMapping.Category(Identifier.fromNamespaceAndPath("custom_crosshair_mod", "key_bindings"))));
}