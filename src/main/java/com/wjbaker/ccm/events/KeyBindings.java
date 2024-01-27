package com.wjbaker.ccm.events;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class KeyBindings {

    public static final KeyMapping EDIT_CROSSHAIR = create("edit_crosshair", GLFW.GLFW_KEY_GRAVE_ACCENT, "category");

    private static KeyMapping create(final String type, final int key, final String category) {
        return new KeyMapping(identifier(type), key, identifier(category));
    }

    private static String identifier(final String value) {
        return "key.custom-crosshair-mod." + value;
    }
}